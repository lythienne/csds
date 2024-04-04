import java.awt.Color;
import java.util.*;

/**
 * King piece represents a king in a chess game, it has a location, color, and a value of 1000, 
 * it can move to any of its adjacent 8 squares.
 * @author Harrison Chen
 * @version 3/27/23
 */
public class King extends Piece
{
    /**
     * Constructs a King piece with a color and file name of a picture of it
     * @param col the color of the king (black/white)
     * @param fileName the file name of the king icon
     */
    public King(Color col, String fileName)
    {
        super(col, fileName, 1000);
    }

    /**
     * Returns all the valid locations this king can move to (8 adjacent squares)
     * @return an ArrayList of valid locations
     */
    public ArrayList<Location> destinations()
    {
        ArrayList<Location> validDests = new ArrayList<Location>();
        int[] y = {1, 1, 1, 0, 0, -1, -1, -1};
        int[] x = {-1, 0, 1, 1, -1, -1, 0, 1};
        for(int i=0; i<8; i++)
        {
            Location check = new Location(getLocation().getRow()+y[i], getLocation().getCol()+x[i]);
            if(isValidDestination(check))
                validDests.add(check);
        }
        return validDests;
    }
}
