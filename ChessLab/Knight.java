import java.awt.Color;
import java.util.*;

/**
 * Knight piece represents a Knight in a chess game, it has a location, color, and a value of 3, 
 * it can move two squares in one horizontal/vertical direction and one in the other.
 * @author Harrison Chen
 * @version 3/27/23
 */
public class Knight extends Piece
{
    /**
     * Constructs a Knight piece with a color and file name of a picture of it
     * @param col the color of the knight (black/white)
     * @param fileName the file name of the knight icon
     */
    public Knight(Color col, String fileName)
    {
        super(col, fileName, 3);
    }

    /**
     * Returns all the valid locations this knight can move to
     * @return an ArrayList of valid locations
     */
    public ArrayList<Location> destinations()
    {
        ArrayList<Location> validDests = new ArrayList<Location>();
        int[] y = {2, 2, -1, 1, -2, -2, -1, 1};
        int[] x = {-1, 1, 2, 2, -1, 1, -2, -2};
        for(int i=0; i<8; i++)
        {
            Location check = new Location(getLocation().getRow()+y[i], getLocation().getCol()+x[i]);
            if(isValidDestination(check))
                validDests.add(check);
        }
        return validDests;
    }
}
