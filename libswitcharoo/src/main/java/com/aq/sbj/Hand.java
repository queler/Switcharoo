package com.aq.sbj;

import java.util.ArrayList;
import java.util.List;

public class Hand extends InstantObservable {//implements BankRoll.Bettable{

    public static final int INIT_HAND_SIZE = 8;

    public boolean isSoft() {
        return soft;
    }

    private boolean soft;
    private int total;
    private Deck deck;
    private List<Card> cardList;

    public boolean isDoubled() {
        return doubled;
    }

    public void setDoubled(boolean doubled) {
        this.doubled = doubled;
    }
    public void setDoubled(){setDoubled(true   );}
    private boolean doubled;
/*
    public BankRoll.Bet getBet() {
        return bet;
    }

    public void setBet(BankRoll.Bet bet) {
        this.bet = bet;
    }

    private BankRoll.Bet bet;
*/
    public Hand(Deck deck) {
        this.deck = deck;
        cardList=new ArrayList<Card>(INIT_HAND_SIZE);
        this.newHand();

    }

    public Hand() {
        this(new RandomDeck());
    }

    public static void main(String[] params)
    {
        Deck deck=new RandomDeck();
        Hand one=new Hand(deck);
        Hand two=new Hand(deck);
        for (int i = 0; i < 10; i++) {
            one.deal();
            two.deal();
            one.deal();
            two.deal();
            System.out.println("Original");
            System.out.println("Hand1: " + one.toString());
            System.out.println("Hand2: " + two.toString());
            System.out.println("Swapped");
            Hand.Swap(one, two);
            System.out.println("Hand1: " + one.toString());
            System.out.println("Hand2: " + two.toString());
            System.out.println("------------------");
            one.newHand();
            two.newHand();
        }
    }

    private static void testTotal() {
        Hand hand=new Hand( );
        for (int i = 0; i < 20; i++) {
            hand.newHand();
            for (int j = 0; j < 4; j++) {
                hand.deal();
                System.out.println(hand.toString());
            }
            System.out.println();
        }
    }

    public static synchronized void Swap (Hand hand1, Hand hand2)
    {
        Card oneToTwo=hand1.get(1);
        Card twoToOne=hand2.get(1);
        hand1.set(1,twoToOne);
        hand2.set(1, oneToTwo);
    }

    public synchronized void privateDeal( Card newCard)
    {
        this.add(newCard);

    }


    public int getTotal() {
        return total;
    }

    private void calcTotal() {
        total=0;
        soft=false;
        for (int i = 0; i < this.size(); i++) {


            int rank = get(i).getRank();
            if (rank ==1)
            {
                if (soft){
                    total++;
                }
                else    {
                    total+=11;
                    soft=true;
                }
            }
            else if (rank >10)
            {
                total+=10;
            }
            else //2-9
            {
                total+=rank;
            }
        }
        if (soft && total>21) {
            total -= 10;
            soft=false;
        }
    }


    public void deal()
    {
        privateDeal(deck.getCard());
    }
    public String internalHandString(String seperator)
    {
        StringBuilder sb=new StringBuilder((2+seperator.length())* INIT_HAND_SIZE);
        for (int i = 0; i < this.size()-1; i++) {
            sb.append(this.get(i).toString()).append( seperator);
        }
        sb.append(this.get(this.size() - 1));
        return sb.toString();
    }
    @Override
    public String toString() {
        if(size()==0)
        {
            return "EMPTY";
        }else
                {return ((soft)? "SOFT":"    ")+ " "+total+ " : "+ internalHandString(" ");}
    }

    //<editor-fold desc="cardlist delegates">
    public int size() {
        return cardList.size();
    }

    public boolean isEmpty() {
        return cardList.isEmpty();
    }

    public synchronized boolean add(Card card) {

        boolean result = cardList.add(card);
        calcTotal();
        setChanged();
        notifyObservers();
        return result;
    }

    public synchronized void newHand() {
        cardList.clear();
        total=0;
        setChanged();
        notifyObservers();

    }

    public Card get(int i) {
        return cardList.get(i);
    }

    public synchronized Card set(int i, Card card) {
        Card result = cardList.set(i, card);
        calcTotal();
        setChanged();
        notifyObservers();
        return result;
    }

    public synchronized void add(int i, Card card) {
        cardList.add(i, card);
        calcTotal();
    }

    protected synchronized Card remove(int i) {
        Card result = cardList.remove(i);
        calcTotal();
        return result;
    }


    //</editor-fold>
}
