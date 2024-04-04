import java.util.List;
import java.util.ArrayList;

/**
 * Sentence class represents a sentence in a document consisting of phrases and
 * ends with a sentence ender.
 * An ArrayList is used to store the phrases for efficient traversal and access while keeping
 * the order of the phrases intact
 * 
 * @author Harrison Chen
 * @version 4/14/23
 */
public class Sentence 
{
    private List<Phrase> phrases;

    /**
     * Constructs a new Sentence with an empty list to store phrases
     */
    public Sentence()
    {
        phrases = new ArrayList<Phrase>();
    }

    /**
     * Adds a phrase to the list of phrases of this Sentence
     * @param phrase the phrase to be added
     */
    public void addPhrase(Phrase phrase)
    {
        phrases.add(phrase);
    }

    /**
     * Returns a copy of the list that holds all the phrases in this Sentence
     * @return a new list with all the same phrases
     */
    public List<Phrase> sentenceCopy()
    {
        List<Phrase> copy = new ArrayList<Phrase>();
        for(Phrase p : phrases)
            copy.add(p);
        return copy;
    }

    /**
     * Returns a String representation of this Sentence consisting of all the phrases
     * in the string separated by ,
     * @return the string representation of this Sentence
     */
    public String toString()
    {
        String returnString = "";
        for(Phrase p : phrases)
            returnString += p+"| ";
        return returnString;
    }
}
