package com.aq.sbj;

import java.util.EnumSet;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by amq102 on 6/5/2015.
 */
public class Table extends InstantObservable{
     public static final int MAX_SPLITS = 4;
    public static final int NO_OF_HANDS = MAX_SPLITS * 2;
    public static final Level LEVEL = Level.INFO;
    public static final ConsoleHandler CONSOLE_HANDLER = new ConsoleHandler();
    static Logger tracer;

    static {
        tracer = Logger.getLogger(Table.class.getPackage().toString());
        tracer.setLevel(Table.LEVEL);
        CONSOLE_HANDLER.setLevel(Table.LEVEL);
        tracer.addHandler(CONSOLE_HANDLER);
    }

    private final int DEFAULT_BET = 10;
    public int activeHandIndex;
    public Bet[] bets;
    public EnumSet<OP> ops;
    public BankRoll bankRoll;
    public Hand dealer;
    public Hand[] hands;
    int lastBetAmount;
    private Runnable inBetweenCards;
    private boolean dealersTurn;

    public Table() {
        this(new RandomDeck());
    }

    public Table(Deck deck) {

        bankRoll=new BankRoll(200,"Bank");
        hands = new Hand[NO_OF_HANDS];

        for (int i = 0; i < NO_OF_HANDS; i++) {
            hands[i]=( new Hand(deck));
        }
        bets = new Bet[NO_OF_HANDS];
        dealer=new Hand(deck);

        setOps(OP.NEW_HAND());
        inBetweenCards = null;
    }

    public static String CardsString(Hand hand, String separator, boolean obscure)
    {
        StringBuilder sb=new StringBuilder((2+separator.length())* Hand.INIT_HAND_SIZE);
        for (int i = 0; i < hand.size()-1; i++) {
            if (i==1 && obscure){
                sb.append("XX").append(separator);
            }

            else
            {
                sb.append(hand.get(i).toString()).append( separator);
            }
        }
        if (hand.size()==2 && obscure) {
                sb.append("XX");
        } else {
            sb.append(hand.get(hand.size() - 1));
        }
        return sb.toString();
    }

    public static String HandToString(Hand hand, boolean obscure) {
        if (hand.size()>0) {
            return ((hand.isSoft() && !obscure)? "SOFT":"    ")+ " "+((obscure)?("  "):(String.format("%1$2s",hand.getTotal()))+ " : ")+ CardsString(hand," ", obscure);
        } else {
            return "";
        }
    }

    private static  boolean isTen(Card card)
    {
        int rank=card.getRank();
        return ((rank>=10) && (rank<=13));
    }

    public boolean isDealersTurn() {
        return dealersTurn;
    }

    public void setDealersTurn(boolean dealersTurn) {
        this.dealersTurn = dealersTurn;
        setChanged();
        notifyObservers();
    }

    public EnumSet<OP> cloneOps() {
        return ops.clone();
    }

    public void setOps(EnumSet<OP> ops) {
        this.ops=ops;
        setChanged();
        notifyObservers(TableChange.OPs);
    }

    public void startGame(BankRoll bankRoll, int bet) {
        for (int i = 0; i < NO_OF_HANDS; i++) {
            hands[i].newHand();
            bets[i]=null;
        }
        dealer.newHand();
        setDealersTurn(false);
        //bankRoll.Bet(bet);
        bets[0] = new Bet(bankRoll, bet, 0);
        bets[MAX_SPLITS] = new Bet(bankRoll, bet, MAX_SPLITS);
        hands[0].deal();
        hands[MAX_SPLITS].deal();
        dealer.deal();
        hands[0].deal();
        hands[MAX_SPLITS].deal();
        dealer.deal();
        setOps(OP.AFTER_DEAL());
        if(isBj(dealer)                )
        {//bj process
            setDealersTurn(true);
            forAllActive(new DoToOne() {
                public void doToOne(int i) {
                    if (!isBj(hands[i])) {
                        bets[i].Loser();

                    } else {
                        bets[i].Push();

                    }

                }
            });
            setOps(OP.NEW_HAND());
        }
        else
        {//no bj
            boolean isOneBJ=isBj(hands[0]);
            boolean isTwoBJ=isBj(hands[MAX_SPLITS]);
            if (isOneBJ)
            {

                bets[0].Winner(1, 1);

                if (isTwoBJ){
                // both BJ

                    bets[MAX_SPLITS].Winner(1, 1);

                    StartDealerProcessing();
                }
                else
                {// only first
                    activateNextHand();
                }
            }
            else
            {
                if (isTwoBJ)
                {//only second

                    bets[MAX_SPLITS].Winner(1, 1);

                    setOps(OP.PRE_HIT());
                }
                activeHandIndex = 0;

                splitCheckAndSet();

            }









        }
        setChanged();
        notifyObservers();
    }

    public boolean isBj(Hand hand) {
        Card one= hand.get(0);
        Card two= hand.get(1);
        return (one.getRank() == 1 && isTen(two))
                || (isTen(one) && two.getRank() == 1);
    }

    private void forAllActive(DoToOne o) {
        for (int i = 0; i < NO_OF_HANDS; i++) {
            if (!isNoBetAt(i))
            {//active hand
                o.doToOne(i);
            }
        }
    }

    public void doubleDown() {
        hands[activeHandIndex].setDoubled();
        bets[activeHandIndex].doubleBet();
        hands[activeHandIndex].deal();

        if (hands[activeHandIndex].getTotal()>21)
        {
            bust();
        }else
        {
            stand();
        }

    }

    private void bust() {
        bets[activeHandIndex].Loser();

        activateNextHand();
    }

    public void hit() {
        hands[activeHandIndex].deal();
        if (hands[activeHandIndex].getTotal()>21)
        {bust();}
        else
        {
            setOps(OP.PLAY());
            setChanged();
            notifyObservers();
        }
    }

    public String textDisplay() {
        StringBuilder out = new StringBuilder();
        out.append(bankRoll.toString()).append("\n");
        out.append("Dealer: ");
        out.append(HandToString(dealer, !isDealersTurn()));
        out.append("\n\n");

        for (int i = 0; i < NO_OF_HANDS; i++) {
            if (hands[i].size() == 0) {
                out.append("\n");
            } else {
                out.append(((i==activeHandIndex)?("***"):("   "))).append(HandToString(hands[i],false));
            }
        }
        return out.toString();
    }

    public void stand() {
        activateNextHand();
    }

    /**
     * Start dealer hand if hands are done
     * if
     */
    private void activateNextHand() {
        while (true)
        {
            activeHandIndex++;
            if (activeHandIndex >= NO_OF_HANDS)
                {
                    StartDealerProcessing();
                    break;
                }
            else if (isNoBetAt(activeHandIndex))
            {
            }
            else
            {
                if (hands[activeHandIndex].size()==1)
                {
                    hands[activeHandIndex].deal();
                }
                setOps(OP.PRE_HIT());
                splitCheckAndSet();
                break;
            }

        }
        this.setChanged();
        this.notifyObservers();

    }

    private boolean isNoBetAt(int index) {
        return bets[index]==null || bets[index].isDone();
    }

    private void splitCheckAndSet() {
        tracer.finest("in splitCheck");
        Card one = hands[activeHandIndex].get(0);
        Card two = hands[activeHandIndex].get(1);
        if (
                (one.getRank()== two.getRank())
                ||
                        ( isTen(one) && isTen(two)  )
                )
        {
            if (isNoBetAt(activeHandIndex / MAX_SPLITS*MAX_SPLITS + MAX_SPLITS-1) )
            {
                EnumSet<OP> newOps= cloneOps();
                newOps.add(OP.split);
                setOps(newOps);
            }
        }
    }

    public void setInBetweenCards(Runnable inBetweenCards) {
        this.inBetweenCards = inBetweenCards;
    }

    private void StartDealerProcessing() {

        setDealersTurn(true);
        setOps(OP.NONE_OF());
        setChanged();
        notifyObservers(TableChange.DealersTurn);

        while (true)
        {
            if (dealer.getTotal()>22)
            {
                allWin();
                break;
            }
            else if (dealer.getTotal()==22)
            {
                allPush();
                break;
            }
            else if (dealer.getTotal()>17
                    || (dealer.getTotal()==17 && !dealer.isSoft()) )
            {
                standAndEval();
                break;
            }
            else {
                tracer.finest("before deal");

                if (inBetweenCards != null) {
                    tracer.finest(System.currentTimeMillis() + ":Before inbetween");
                    inBetweenCards.run();
                    tracer.finest(System.currentTimeMillis() + ":After inbetween");
                }
                dealer.deal();
                setChanged();
                notifyObservers(TableChange.DealerDealt);
            }
        }
        setOps(OP.NEW_HAND());
        setChanged();
        notifyObservers();
    }

    private void standAndEval() {
        forAllActive(new DoToOne() {
            @Override
            public void doToOne(int i) {
                if (dealer.getTotal()==hands[i].getTotal())
                {
                    bets[i].Push();

                }
                else if (dealer.getTotal()>hands[i].getTotal())
                {
                    bets[i].Loser();

                }
                else
                {

                    bets[i].Winner(1, 1);

                }
            }
        });
    }

    private void allPush() {
        forAllActive(new DoToOne() {
            @Override
            public void doToOne(int i) {
                bets[i].Push();

            }
        });
    }

    private void allWin() {
        forAllActive(new DoToOne() {
            @Override
            public void doToOne(int i) {

                bets[i].Winner(1, 1);

            }
        });
    }

    public void swap() {
        Hand.Swap(hands[0], hands[MAX_SPLITS]);
        splitCheckAndSet();
    }

    /**
     * if splitCheckAndSet works right I should never have to check
     * that a split is possible
     */
    public void split() {
        tracer.finest("in split");
        int newHandIndex=activeHandIndex;
        do {
            newHandIndex++;
            if (newHandIndex==MAX_SPLITS || newHandIndex ==NO_OF_HANDS)
            {
                throw new IllegalStateException("shouldn't be able to split here");
            }
        } while (bets[newHandIndex]!=null);
        bets[(newHandIndex)]=new Bet(bankRoll, bets[activeHandIndex].getValue(), newHandIndex);

        Card two=hands[activeHandIndex].remove(1);
        hands[(newHandIndex)].add(two);
        hands[activeHandIndex].deal();
        setOps(OP.PRE_HIT());
        splitCheckAndSet();
    }

    /**
     * syntatic sugar for doubledown
     */
    public void dd() {
         doubleDown();
    }

    public  interface DoToOne {
         void  doToOne(int i);
    }
}


