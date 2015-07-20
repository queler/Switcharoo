package com.aq.sbj;

import java.io.IOException;
import java.util.Random;

/**
 * Created by aqueler on 6/2/2015.
 */
public class RandomDeck implements Deck{
    public RandomDeck() {
    }

    @Override
    public Card getCard()
    {
        int rank=random.nextInt(12)+1;
        Suit suit;
        switch (random.nextInt(3))
        {
            case 0: suit=Suit.HEART;break;
            case 1: suit=Suit.SPADE;break;
            case 2: suit=Suit.CLUB;break;
            case 3: suit=Suit.DIAMOND;break;
            default: throw new IllegalStateException("can't program");
        }
        return new Card(rank,suit);
    }
    Random random=new Random();
    public static void main(String [] args) throws IOException
    {
        for (int i = 0; i < 100; i++) {
            Deck deck=new RandomDeck();
            System.out.println(deck.getCard().toString());
        }
    }
}
