package com.aq.sbj.swing;

import com.aq.sbj.Card;
import com.aq.sbj.RandomDeck;

import javax.swing.*;

/**
 * Created by amq102 on 8/8/2015.
 */
public class SwingDebugDeck extends RandomDeck{
    @Override
    public Card getCard() {
        String res=JOptionPane.showInputDialog("Next Card? Cancel or Error will do next random");
        if (res==null) {
            return super.getCard();
        }
        else
        {

            try {
                return Card.Parse(res);
            } catch (IllegalArgumentException e) {
                return super.getCard();
            }
        }
    }
}
