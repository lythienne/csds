import java.awt.Color;

/**
 * Player class represents a chess player, it has a chess board, color, and name that it can return
 * It can also return its next move
 * @author Harrison Chen
 * @version 3/27/23
 */
public abstract class Player 
{
    private Board board;
    private Color color;
    private String name;

    /**
     * Constructs a new player with a chess board, color, and name
     * @param b the chess board
     * @param c the color of the player
     * @param n the name of the player
     */
    public Player(Board b, Color c, String n)
    {
        board = b;
        color = c;
        name = n;
    }

    /**
     * Returns this player's board
     * @return board
     */
    public Board getBoard()
    {
        return board;
    }
    /**
     * Returns this player's color
     * @return color
     */
    public Color getColor()
    {
        return color;
    }
    /**
     * Returns this player's name
     * @return name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Next move returns this player's next move
     * @return this player's next move
     */
    public abstract Move nextMove();
}
