package com.aq.sbj.swing;

import com.aq.sbj.Hand;
import com.aq.sbj.Table;

import javax.swing.*;

/**
 * Created by amq102 on 6/20/2015.
 */
public class HandView extends JTextField{
    public HandView() {
        //setEditable(false);
        isDealer = false;
    }
    public Hand hand;

    public boolean isDealer() {
        return isDealer;
    }

    public void setDealer(boolean isDealer) {
        this.isDealer = isDealer;
    }
    public void update(Hand hand, Table table) {

        HandView.this.setText(Table.HandToString(hand, isDealer() && !table.isDealersTurn()));
    }
    private boolean isDealer;


}
