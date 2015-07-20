package com.aq.sbj;


import java.util.Random;

/**
 * Created by amq102 on 6/13/2015.
 */
public class BankRoll extends InstantObservable {


    @Override
    public  String toString()
    {
        return String.format("%s: $%s", BankName, Money());
    }


    public String BankName;
    private int money;
    //    getter
    public int Money() {
        return money;
    }
    public void setMoney(int value) {
        money = value;
        //BankEventArgs e = new BankEventArgs();
        //e.GUI = GraphicBinding;

        setChanged();
        notifyObservers();

    }

    public BankRoll()
    {
        setMoney (200);
        BankName = "BankRoll" + (new Random().nextInt());
    }
    public BankRoll(int money, String name)
    {
        setMoney(money);
        BankName = name;
    }
    public void Bet(int bet)
    {
        setMoney(Money() - bet);
    }
    public void Win(int bet)
    {
        setMoney(Money()+bet);
    }








}
/// <summary>

