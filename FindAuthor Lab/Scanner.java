import java.io.IOException;
import java.io.Reader;

/**
 * A Scanner is responsible for reading an input stream, one character at a
 * time, and separating the input into tokens.  A token is defined as:
 *  1. A 'word' which is defined as a non-empty sequence of characters that 
 *     begins with an alpha character and then consists of alpha characters, 
 *     numbers, the single quote character "'", or the hyphen character "-". 
 *  2. An 'end-of-sentence' delimiter defined as any one of the characters 
 *     ".", "?", "!".
 *  3. An end-of-file token which is returned when the scanner is asked for a
 *     token and the input is at the end-of-file.
 *  4. A phrase separator which consists of one of the characters ",",":" or
 *     ";".
 *  5. A digit.
 *  6. Any other character not defined above.
 * @author Harrison Chen
 * @author Mr. Page
 * @version 4/14/23
 */

public class Scanner
{
    private Reader in;
    private String currentChar;
    private boolean endOfFile;
    // define symbolic constants for each type of token
    public static enum TOKEN_TYPE{WORD, END_OF_SENTENCE, END_OF_FILE, 
        END_OF_PHRASE, DIGIT, UNKNOWN};
    /**
     * Constructor for Scanner objects.  The Reader object should be one of
     *  1. A StringReader
     *  2. A BufferedReader wrapped around an InputStream
     *  3. A BufferedReader wrapped around a FileReader
     *  The instance field for the Reader is initialized to the input parameter,
     *  and the endOfFile indicator is set to false.  The currentChar field is
     *  initialized by the getNextChar method.
     * @param in is the reader object supplied by the program constructing
     *        this Scanner object.
     */
    public Scanner(Reader in)
    {
        this.in = in;
        endOfFile = false;
        getNextChar();
    }
    /**
     * The getNextChar method attempts to get the next character from the input
     * stream.  It sets the endOfFile flag true if the end of file is reached on
     * the input stream.  Otherwise, it reads the next character from the stream
     * and converts it to a Java String object.
     * postcondition: The input stream is advanced one character if it is not at
     * end of file and the currentChar instance field is set to the String 
     * representation of the character read from the input stream.  The flag
     * endOfFile is set true if the input stream is exhausted.
     */
    private void getNextChar()
    {
        try
        {
            int inp = in.read();
            if(inp == -1) 
                endOfFile = true;
            else 
                currentChar = "" + (char) inp;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Compares two strings and gets the next character if they equal, otherwise throws an
     * IllegalArgumentException
     * @param other the string to compare to
     * @throws IllegalArgumentException if other and current character do not match
     */
    private void eat(String other)
    {
        if(other.equals(currentChar))
            getNextChar();
        else
            throw new RuntimeException("character "+other+
                    " does not match current character "+currentChar);
    }

    /**
     * Checks if a string is a letter
     * @precondition the string is one character long
     * @param ch the string to check
     * @return true if the string is a letter, false otherwise
     */
    private boolean isLetter(String ch)
    {
        return ch.compareTo("A")>=0 && ch.compareTo("Z")<=0
                || ch.compareTo("a")>=0 && ch.compareTo("z")<=0;
    }

    /**
     * Checks if a string is a digit
     * @precondition the string is one character long
     * @param ch the string to check
     * @return true if the string is a digit, false otherwise
     */
    private boolean isDigit(String ch)
    {
        return ch.compareTo("0")>=0 && ch.compareTo("9")<=0;
    }

    /**
     * Checks if a string is a special character
     * @precondition the string is one character long
     * @param ch the string to check
     * @return true if the string is a quote or hyphen, false otherwise
     */
    private boolean isSpecialChar(String ch)
    {
        return ch.compareTo("-")==0 || ch.compareTo("\'")==0;
    }

    /**
     * Checks if a string is a phrase ender
     * @precondition the string is one character long
     * @param ch the string to check
     * @return true if the string is a colon, comma, or semicolon, false otherwise
     */
    private boolean isPhraseEnder(String ch)
    {
        return ch.compareTo(":")==0 || ch.compareTo(",")==0
                || ch.compareTo(";")==0;
    }

    /**
     * Checks if a string is a sentence ender
     * @precondition the string is one character long
     * @param ch the string to check
     * @return true if the string is a period, question or exclamation mark, false otherwise
     */
    private boolean isSentenceEnder(String ch)
    {
        return ch.compareTo(".")==0 || ch.compareTo("?")==0
                || ch.compareTo("!")==0;
    }

    /**
     * Checks if a string is a whitespace
     * @precondition the string is one character long
     * @param ch the string to check
     * @return true if the string is a space or new line
     */
    private boolean isSpace(String ch)
    {
        return ch.compareTo(" ")==0 || ch.compareTo("\n")==0
            || ch.compareTo("\t")==0 || ch.compareTo("\r")==0;
    }

    /**
     * Checks if input stream is at end of file
     * @return true if the input stream has a next token, false otherwise
     */
    public boolean hasNextToken()
    {
        return !endOfFile;
    }

    /**
     * Creates and returns the next token in the file, skipping white spaces. If it is a letter, 
     * finds the whole word, otherwise returns the single character with its token type
     * @return a new Token of the type of the character/word and the character/word
     */
    public Token nextToken()
    {
        Token nextToken;
        while(!endOfFile && isSpace(currentChar))
        {
            eat(currentChar);
        }
        if(endOfFile)
        {
            nextToken = new Token(TOKEN_TYPE.END_OF_FILE, currentChar);
            eat(currentChar);
        }
        else if(isDigit(currentChar))
        {
            nextToken = new Token(TOKEN_TYPE.DIGIT, currentChar);
            eat(currentChar);
        }
        else if(isPhraseEnder(currentChar))
        {
            nextToken = new Token(TOKEN_TYPE.END_OF_PHRASE, currentChar);
            eat(currentChar);
        }
        else if(isSentenceEnder(currentChar))
        {
            nextToken = new Token(TOKEN_TYPE.END_OF_SENTENCE, currentChar);
            eat(currentChar);
        }
        else if(isLetter(currentChar))
        {
            String word = "";
            while(!endOfFile && (isLetter(currentChar) || isDigit(currentChar) || isSpecialChar(currentChar)))
            {
                word += currentChar;
                eat(currentChar);
            }
            nextToken = new Token(TOKEN_TYPE.WORD, word.toLowerCase());
        }
        else
        {
            nextToken = new Token(TOKEN_TYPE.UNKNOWN, currentChar);
            eat(currentChar);
        }
        return nextToken;
    }
}