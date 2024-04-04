import java.awt.Color;
import java.util.*;

/**
 * Pawn piece represents a Pawn in a chess game, it has a location, color, and a value of 1, 
 * it can move ahead or diagonally to capture
 * @author Harrison Chen
 * @version 3/27/23
 */
public class Pawn extends Piece
{
    /**
     * Constructs a Pawn piece with a color and file name of a picture of it
     * @param col the color of the Pawn (black/white)
     * @param fileName the file name of the Pawn icon
     */
    public Pawn(Color col, String fileName)
    {
        super(col, fileName, 1);
    }

    /**
     * Returns all the valid locations this Pawn can move to (forward 1-2, capture diagonally)
     * @return an ArrayList of valid locations
     */
    public ArrayList<Location> destinations()
    {
        ArrayList<Location> validDests = new ArrayList<Location>();
        int y = -1;
        int startRow = 6;
        if(getColor().equals(Color.BLACK)) 
        {
            y = 1;
            startRow = 1;
        }
        Location check = new Location(getLocation().getRow()+y, getLocation().getCol());
        if(getBoard().isValid(check) && getBoard().get(check)==null)
            validDests.add(check);
        if(getLocation().getRow() == startRow)
        {
            check = new Location(getLocation().getRow()+y*2, getLocation().getCol());
            if(getBoard().isValid(check) && getBoard().get(check)==null)
                validDests.add(check);
        }
        int[] diagonals = {-1,1};
        for(int x : diagonals)
        {
            check = new Location(getLocation().getRow()+y, getLocation().getCol()+x);
            if(getBoard().isValid(check) && getBoard().get(check)!=null 
                    && getBoard().get(check).getColor()!=getColor())
                    validDests.add(check);
        }
        return validDests;
    }
}
