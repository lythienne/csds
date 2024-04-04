import java.awt.*;
import java.util.*;

/**
 * Represesents a rectangular game board, containing Piece objects.
 * @author Harrison Chen
 * @version 3/27/23
 */
public class Board extends BoundedGrid<Piece>
{
	/**
     * Constructs a new Board with the given dimensions (8 by 8)
     */
	public Board()
	{
		super(8, 8);
	}

	/**
     * Precondition:  move has already been made on the board
	 * Postcondition: piece has moved back to its source,
	 *                and any captured piece is returned to its location
     * @param move the move to undo
     */
	public void undoMove(Move move)
	{
		Piece piece = move.getPiece();
		Location source = move.getSource();
		Location dest = move.getDestination();
		Piece victim = move.getVictim();

		piece.moveTo(source);

		if (victim != null)
			victim.putSelfInGrid(piece.getBoard(), dest);
	}
    
    /**
     * Returns a list of all legal moves that could be made by one player
     * @param color the color of the player
     * @return and arraylist of all the legal moves the player could make
     */
    public ArrayList<Move> allMoves(Color color)
    {
        ArrayList<Move> moves = possibleMoves(color);
        Iterator<Move> it = moves.iterator();
        while(it.hasNext())
        {
            Move m = it.next();
            executeMove(m);
            Color c = Color.BLACK;
            if(color == Color.BLACK) c = Color.WHITE;
            ArrayList<Move> responses = possibleMoves(c);
            boolean remove = false;
            for(Move r : responses)
                if(r.getVictim() instanceof King)
                    remove = true;
            if(remove) it.remove();
            undoMove(m);
        }
        return moves;
    }

    /**
     * Returns a list of all moves that could be made by one player (including illegal king moves)
     * @param color the color of the player
     * @return and arraylist of all the moves the player could make
     */
    private ArrayList<Move> possibleMoves(Color color)
    {
        ArrayList<Move> moves = new ArrayList<Move>();
        for(Location l : getOccupiedLocations())
        {
            Piece p = get(l);
            if(p.getColor().equals(color))
                for(Location d : p.destinations())
                    moves.add(new Move(p, d));
        }
        return moves;
    }

    /**
     * Executes a move, moving a piece to its destination
     * @param move the move to execute
     */
    public void executeMove(Move move)
    {
		move.getPiece().moveTo(move.getDestination());
    }
}