package com.aq.sbj;

/**
 * Created by aqueler on 6/2/2015.
 */
public class Card {

    public Card(int rank, Suit suit) {
        if (rank>=1 && rank<=13) {
            this.rank = rank;

            this.suit = suit;
        }
        else
        {
            throw new IllegalArgumentException("invalid rank");
        }
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public void setSuit(Suit suit) {
        this.suit = suit;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(2);
     
        if (rank == 1) {
            str.append('A');
        } else if (rank == 11) {
            str.append('J');
        }else if (rank == 10) {
            str.append('T');
        } else if (rank == 12) {
            str.append('Q');
        } else if (rank == 13) {
            str.append('K');
        } else {
            str.append(rank);
        }
        switch (suit) {
            case DIAMOND:
                str.append('D');
                break;
            case HEART:
                str.append('H');
                break;
            case CLUB:
                str.append('C');
                break;
            case SPADE:
                str.append('S');
                break;
            default:
                throw new IllegalArgumentException("bad suit");
        }
        return str.toString();
    }

    private int rank;
    private Suit suit;

}
