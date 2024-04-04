import java.awt.Color;
import java.util.*;

/**
 * HumanPlayer class represents a chess player, it has a chess board, color, and name that it can return
 * Its next move will be decided by the display linked to the board (chosen by a human)
 * @author Harrison Chen
 * @version 3/27/23
 */
public class HumanPlayer extends Player
{
    private BoardDisplay display;

    /**
     * Constructs a new player with a chess board, color, name, and display for the board
     * @param b the chess board
     * @param c the color of the player
     * @param n the name of the player
     * @param disp the display for the board
     */
    public HumanPlayer(Board b, Color c, String n, BoardDisplay disp)
    {
        super(b, c, n);
        display = disp;
    }

    /**
     * Gets the next move the human player will make through the display, returns the move if valid, null
     * otherwise
     * @return the move the human player makes, null if it cant make any moves
     */
    public Move nextMove()
    {
        ArrayList<Move> moves = getBoard().allMoves(getColor());
        if(moves.size()==0)
            return null;
        while(true)
        {
            Move move = display.selectMove();
            for(Move m : moves)
                if(m.equals(move))
                    return move;
        } 
    }
}
