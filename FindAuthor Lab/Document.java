import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Document class represents a document in a sentence consisting of sentences and
 * ends with the end of a file. It has a scanner to read a file, a list of sentences, and
 * the current token being processed.
 * An ArrayList is used to store the sentences for efficient traversal and access while keeping
 * the order of the sentences intact
 * 
 * @author Harrison Chen
 * @version 4/19/23
 */
public class Document 
{
    private Scanner in;
    private List<Sentence> sentences;
    private Token currToken;
    private String authorName;

    /**
     * Constructs a new Document with a scanner that reads a file, a list of the sentences
     * in the document, and the current token from the scanner (uses one-token look ahead)
     * @param fileName the name of the file of the document to read
     */
    public Document(String fileName)
    {
        try 
        {
            FileReader reader = new FileReader(new File("./MysteryText/"+fileName));
            in = new Scanner(reader);
        } 
        catch (FileNotFoundException e) 
        {
            System.out.println(fileName+" does not exist");
        }
        authorName = "";
        sentences = new ArrayList<Sentence>();
        getNextToken();
    }

    /**
     * Requests the next token from the Scanner and sets it to the currToken
     */
    private void getNextToken()
    {
        currToken = in.nextToken();
    }

    /**
     * Compares two tokens and gets the next token if they equal, otherwise throws an
     * RuntimeException
     * @param other the string to compare to
     * @throws RuntimeException if other and current token do not match
     */
    private void eat(Token other)
    {
        if(other.equals(currToken))
            getNextToken();
        else
            throw new RuntimeException("token "+other+
                    " does not match current token "+currToken);
    }

    /**
     * Parses and returns a phrase, adding words to it until it reaches a phrase, sentence, or file
     * ender
     * @return a new phrase object of all the words until reaching an end of phrase, sentence, or file
     */
    public Phrase parsePhrase()
    {
        Phrase currPhrase = new Phrase();
        while(currToken.getType() != Scanner.TOKEN_TYPE.END_OF_PHRASE &&
                currToken.getType() != Scanner.TOKEN_TYPE.END_OF_SENTENCE &&
                currToken.getType() != Scanner.TOKEN_TYPE.END_OF_FILE)
        {
            if(currToken.getType()== (Scanner.TOKEN_TYPE.WORD))
                currPhrase.addToken(currToken);

            eat(currToken);
        }

        if(currToken.getType() == Scanner.TOKEN_TYPE.END_OF_PHRASE)
            eat(currToken);

        return currPhrase;
    }

    /**
     * Parses and returns a sentence, adding phrases to it until it reaches a sentence or file ender
     * @return a new setence object of all the phrases until reaching an end of sentence or file
     */
    public Sentence parseSentence()
    {
        Sentence currSentence = new Sentence();
        while(currToken.getType() != (Scanner.TOKEN_TYPE.END_OF_SENTENCE) &&
                currToken.getType() != (Scanner.TOKEN_TYPE.END_OF_FILE))
        {
            currSentence.addPhrase(parsePhrase());
        }

        if(currToken.getType()== (Scanner.TOKEN_TYPE.END_OF_SENTENCE))
             eat(currToken);

        return currSentence;
    }

    /**
     * Parses the document, adding all the sentences in the document into the list of sentences
     * ignores all leading tokens that are not words
     */
    public void parseDocument()
    {
        /*while(!currToken.getType()== (Scanner.TOKEN_TYPE.WORD) || !currToken.getValue().equals("by"))
            eat(currToken);
        eat(currToken); //by
        while(!currToken.getType()== (Scanner.TOKEN_TYPE.WORD))
            eat(currToken);
        authorName += currToken.getValue() + " "; //first name
        eat(currToken);
        while(!currToken.getType()== (Scanner.TOKEN_TYPE.WORD))
            eat(currToken);
        authorName += currToken.getValue(); //last name
        eat(currToken);*/
        while(currToken.getType() != (Scanner.TOKEN_TYPE.WORD))
            eat(currToken);

        while(currToken.getType() != (Scanner.TOKEN_TYPE.END_OF_FILE))
        {
            sentences.add(parseSentence());
        }

        if(currToken.getType() == (Scanner.TOKEN_TYPE.END_OF_FILE))
            eat(currToken);
    }

    /**
     * Returns the list of sentences
     * @return sentences
     */
    public List<Sentence> getSentences()
    {
        return sentences;
    }

    /**
     * Returns the name of the author of this document
     * @return authorName
     */
    public String getAuthor()
    {
        return authorName;
    }
}
