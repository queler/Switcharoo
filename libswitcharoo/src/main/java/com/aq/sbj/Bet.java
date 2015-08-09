package com.aq.sbj;

import java.util.logging.Logger;

/**
 * Created by amq102 on 6/13/2015.
 */
public  class Bet extends InstantObservable{

    private BetState state;
    public int winnings;

    public boolean isDone() {
        return state!=BetState.ACTIVE;
    }

    public BetState getState() {
        return state;
    }

    public enum BadBetEnum {
        empty, Illegal, Funds, Done, Unchangable

    }
    private int index;
    public class BadBetException extends RuntimeException {
        /**
         *
         */
        private static final long serialVersionUID = 2560537394845600049L;
        public BadBetEnum Reason;

        public BadBetException(BadBetEnum reason) {
            super();
            Reason = reason;
        }

    }
    Logger tracer= Logger.getLogger(Table.class.getPackage().toString());


    private int value;

    public BankRoll Bank;

    public Bet(BankRoll bank, int bet) {
        this(bank, bet, -1);
    }

    public Bet(BankRoll bank, int bet, int index) {
        this.index = index;
        value = bet;
        Bank = bank;
        // ReadyToDelete = false;
        if (isValid()) {
            Bank.Bet(value);

        } else {
            throw new BadBetException(BadBetEnum.Illegal);
        }
        state = BetState.ACTIVE;
    }

    /**
     * The table must remove the bet
     *
     * "Odds of return on the bet.  ex: "5:2"
     *
     * @param leftOdds
     * @param rightOdds
     *
     *
     */

    public  void Winner(int leftOdds, int rightOdds){

        throwIfDone();
        int oldBank = Bank.Money();
        winnings = getValue() * leftOdds / rightOdds;
        //tracer.info("The bet for $%3$ of %2$ has won $%3$+$%4$=%5$", this.DisplayStringType(), this.Bank.BankName, this.getValue(), winnings, getValue() + winnings));
        Bank.Win(winnings + getValue());
        tracer.info(Bank.BankName+" went from $"+oldBank+"->"+Bank.Money());
        //this.DeleteMe(); ;  //the table will handle deletions.
        setDone(BetState.WON);

    }

    private String DisplayStringType() {
        return "";
    }

    private void throwIfDone() {
        if (isDone())
        {
            throw new BadBetException(BadBetEnum.Done);
        }
    }

    private void setDone(BetState state) {
        this.state =state;
    }

    /**
     * Table MUST clear bet after
     */
    public void Loser()
    {
        tracer.info("The "+this.DisplayStringType()+"bet of $"+this.Bank.BankName+" for "+getValue()+" has lost");
        winnings=0;
        setDone(BetState.LOST);
    }



    public void Push()
    {
        Bank.Win(getValue());
        winnings=0;
        setDone(BetState.PUSHED);

        tracer.info("The bet for "+getValue()+" of "+Bank.BankName+" is given back (push)");
    }

    private String TypeOfBet() {
        return "";
    }

    /**
     * Override this if you want something other then the value of TypeOfBet shown in ToString
     */

    @Override
    public  String toString()
    {
        return String.valueOf(this.getValue());// this.Bank.BankName);
    }




    public boolean isValid(){return true;}
    //public abstract int RollId();
    //public abstract boolean Evaluate();

    public boolean isChangable() {
        return true;
    }

    public void doubleBet()
    {
        Change(getValue()*2);
    }
    private void Change(int newBet) {
        int betDelta = newBet - getValue();

        if (isChangable() && betDelta <= Bank.Money()) {
            Bank.Win(this.getValue());
            this.setValue(0);
            Bank.Bet(newBet);
            this.setValue(newBet);
        } else {
            BadBetException bbe;
            if (betDelta <= Bank.Money())// this changable must be false
            {
                bbe = new BadBetException(BadBetEnum.Unchangable);

            } else {
                bbe = new BadBetException(BadBetEnum.Funds);
            }
            throw bbe;
        }
    }


    public void setValue(int value) {
        this.value = value;
        setChanged();
        notifyObservers();

    }

    public int getValue() {
        return value;
    }


    /**
     * Created by amq102 on 8/8/2015.
     */
    public static enum BetState {
        ACTIVE,WON, LOST, PUSHED
    }
}
