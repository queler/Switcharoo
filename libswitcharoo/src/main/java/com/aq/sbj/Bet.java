package com.aq.sbj;

import java.util.logging.Logger;

/**
 * Created by amq102 on 6/13/2015.
 */
public  class Bet extends InstantObservable{

    private boolean done;

    public enum BadBetEnum {
        empty, Illegal, Funds, Done, Unchangable

    }

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
    Logger tracer= Logger.getLogger(this.getClass().getPackage().toString());


    private int value;

    public BankRoll Bank;

    public Bet(int bet, BankRoll bank) {
        value = bet;
        Bank = bank;
        // ReadyToDelete = false;
        if (isValid()) {
            Bank.Bet(value);

        } else {
            throw new BadBetException(BadBetEnum.Illegal);
        }
        done = false;
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
        int winnings = getValue() * leftOdds / rightOdds;
        //tracer.info("The bet for $%3$ of %2$ has won $%3$+$%4$=%5$", this.DisplayStringType(), this.Bank.BankName, this.getValue(), winnings, getValue() + winnings));
        Bank.Win(winnings + getValue());
        tracer.info(Bank.BankName+" went from $"+oldBank+"->"+Bank.Money());
        //this.DeleteMe(); ;  //the table will handle deletions.
        setDone();

    }

    private String DisplayStringType() {
        return "";
    }

    private void throwIfDone() {
        if (done)
        {
            throw new BadBetException(BadBetEnum.Done);
        }
    }

    private void setDone() {
        done=true;
    }

    /**
     * Table MUST clear bet after
     */
    public void Loser()
    {
        tracer.info("The "+this.DisplayStringType()+"bet of $"+this.Bank.BankName+" for "+getValue()+" has lost");
        setDone();
    }



    public void Push()
    {
        Bank.Win(getValue());

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


}
