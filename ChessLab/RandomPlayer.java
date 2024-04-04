import java.awt.Color;
import java.util.*;
/**
 * RandomPlayer class represents a chess player, it has a chess board, color, and name that it can return
 * Its next move will be a random valid move
 * @author Harrison Chen
 * @version 3/27/23
 */
public class RandomPlayer extends Player
{
    /**
     * Constructs a new player with a chess board, color, and name
     * @param b the chess board
     * @param c the color of the player
     * @param n the name of the player
     */
    public RandomPlayer(Board b, Color c, String n)
    {
        super(b, c, n);
    }   

    /**
     * Returns a random valid move that this player will make
     */
    public Move nextMove()
    {
        ArrayList<Move> moves = getBoard().allMoves(getColor());
        if(moves.size()==0)
            return null;
        return moves.get((int) (Math.random() * moves.size()));
    }
}
