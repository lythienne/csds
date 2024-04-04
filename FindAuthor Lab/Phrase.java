import java.util.List;
import java.util.ArrayList;

/**
 * Phrase class represents a phrase in a sentence consisting of words and
 * ends with a phrase ender.
 * An ArrayList is used to store the tokens for efficient traversal and access while keeping
 * the order of the tokens intact
 * 
 * @author Harrison Chen
 * @version 4/14/23
 */
public class Phrase 
{
    private List<Token> tokens;

    /**
     * Constructs a new Phrase with an empty list to store tokens
     */
    public Phrase()
    {
        tokens = new ArrayList<Token>();
    }

    /**
     * Adds a token to the list of tokens of this phrase
     * @param token the token to be added
     */
    public void addToken(Token token)
    {
        tokens.add(token);
    }

    /**
     * Returns a copy of the list that holds all the tokens in this phrase
     * @return a new list with all the same values as tokens
     */
    public List<Token> phraseCopy()
    {
        List<Token> copy = new ArrayList<Token>();
        for(Token t : tokens)
            copy.add(t);
        return copy;
    }

    /**
     * Returns a String representation of this phrase consisting of all the tokens
     * in the string separated by spaces
     * @return the string representation of this phrase
     */
    public String toString()
    {
        String returnString = "";
        for(Token t :tokens)
            returnString += t+" ";
        return returnString;
    }
}
