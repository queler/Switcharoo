package com.aq.sbj;

/**
 * Created by aqueler on 6/2/2015.
 */
public class Card  {

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
    public static Card Parse(String s) throws IllegalArgumentException
    {
        if (s==null)
        {
            throw new NullPointerException();
        }
        else
        {
            if (s.length()==2)
            {

                    Suit suit=Suit.valueOf(s.substring(1,2).toUpperCase());
                    String rankStr=s.substring(0,1).toUpperCase();
                int rank;
                if(rankStr.equals("A"))
                    {
                        rank=1;
                    }
                else if (rankStr.equals("T"))
                {
                    rank=10;
                }
                else if (rankStr.equals("J"))
                {
                    rank=11;
                }else if (rankStr.equals("Q"))
                {
                    rank=12;
                }else if (rankStr.equals("K"))
                {
                    rank=13;
                }else
                {
                    rank=Integer.parseInt(rankStr);
                }
                return new Card(rank,suit);
            }
            else
            {
                throw new IllegalArgumentException("length!=2");
            }

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
            case D:
                str.append('D');
                break;
            case H:
                str.append('H');
                break;
            case C:
                str.append('C');
                break;
            case S:
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
