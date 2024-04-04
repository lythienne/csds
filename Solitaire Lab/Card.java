/**
 * Card class represents a playing card, it has a suit, rank, 
 * and whether its face up or not
 * @author Harrison Chen
 * @version 11/9/22
 */
public class Card 
{
    int rank;
    String suit;
    boolean isFaceUp;

    /**
     * Constructs a card object with a suit and rank
     * sets the card to face down initially
     * @param rank the rank
     * @param suit the suit
     */
    public Card(int rank, String suit)
    {
        this.rank = rank;
        this.suit = suit;
        isFaceUp = false;
    }

    /**
     * Returns the rank of this card
     * @return rank
     */
    public int getRank()
    {return rank;}

    /**
     * Returns the suit of this card
     * @return suit
     */
    public String getSuit()
    {return suit;}

    /**
     * Returns whether this card is face up or not
     * @return true if it is face up, false otherwise
     */
    public boolean isFaceUp()
    {return isFaceUp;}

    /**
     * Returns whether this card is red or not
     * @return true if this card's suit is "d" or "h", false otherwise
     */
    public boolean isRed()
    {
        if (suit.equals("h") || suit.equals("d"))
            return true;
        return false;
    }

    //Turns this card up
    public void turnUp()
    {isFaceUp = true;}

    //Turns this card down
    public void turnDown()
    {isFaceUp = false;}

    /**
     * Returns an the file name of an image of this card based on if its
     * face up or down, its rank, and its suit
     * @return a string of the filename of this card
     */
    public String getFileName()
    {
        if (rank == 1)
            return "cards/a"+suit+".gif";
        if (rank == 10)
            return "cards/t"+suit+".gif";
        if (rank == 11)
            return "cards/j"+suit+".gif";
        if (rank == 12)
            return "cards/q"+suit+".gif";
        if (rank == 13)
            return "cards/k"+suit+".gif";
        return "cards/"+rank+suit+".gif";
    }

    /**
     * Returns a string of this card's rank and suit
     * @return rank+suit
     */
    public String toString()
    {
        return rank+suit;
    }
}
