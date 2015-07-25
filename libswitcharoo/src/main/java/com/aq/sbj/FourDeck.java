package com.aq.sbj;

/**
 * Created by amq102 on 7/25/2015.
 */
public class FourDeck implements Deck {

    private  int i;

    public FourDeck() {
        i = 0;
    }

    @Override
    public Card getCard() {
        Suit suit;
        suit=Suit.values()[i];
        i++;
        if (i>=4)
        {
            i=0;
        }
        return new Card(4, suit);
    }
}
