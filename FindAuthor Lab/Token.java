/**
 * Token class is an immutable class that has a type given by the Scanner 
 * and a String value
 * @author Harrison Chen
 * @version 4/10/23
 */
public final class Token 
{
    private final Scanner.TOKEN_TYPE type;
    private final String value;

    /**
     * Constructs a new Token with a type and a value
     * @param tokenType the type of the token
     * @param val the value of the token
     */
    public Token(Scanner.TOKEN_TYPE tokenType, String val)
    {
        type = tokenType;
        value = val;
    }

    /**
     * Returns the type of this token
     * @return type
     */
    public Scanner.TOKEN_TYPE getType()
    {
        return type;
    }

    /**
     * Returns the value of this token
     * @return value
     */
    public String getValue()
    {
        return value;
    }

    /**
     * Returns a string representation of this token
     * @return a string of type:value
     */
    public String toString()
    {
        return type.toString() + ":" + value;
    }

    /**
     * Checks whether another token is equal to this token
     * @param other the other token
     * @return true if other's type and value are equal to this token, false otherwise
     * @throws IllegalArgumentException if other is not a token
     */
    public boolean equals(Object other)
    {
        if(!(other instanceof Token))
            throw new IllegalArgumentException("Other is not a token");
        return ((Token) other).getType()== (type) && ((Token) other).getValue().equals(value);
    }

    /**
     * Returns the hashcode of this token, using the string's hashCode method
     * @return an almost-unique hashcode for this token
     */
    public int hashCode()
    {
        return value.hashCode();
    }
}