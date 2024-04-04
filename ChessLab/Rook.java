import java.awt.Color;
import java.util.*;

/**
 * Rook piece represents a rook in a chess game, it has a location, color, and a value of 5, 
 * it can move horizontally and vertically.
 * @author Harrison Chen
 * @version 3/27/23
 */
public class Rook extends Piece
{
    /**
     * Constructs a Rook piece with a color and file name of a picture of it
     * @param col the color of the rook (black/white)
     * @param fileName the file name of the rook icon
     */
    public Rook(Color col, String fileName)
    {
        super(col, fileName, 5);
    }

    /**
     * Returns all the valid locations this rook can move to
     * @return an ArrayList of valid locations
     */
    public ArrayList<Location> destinations()
    {
        ArrayList<Location> validDests = new ArrayList<Location>();
        int[] directions = {Location.NORTH, Location.SOUTH, Location.EAST, Location.WEST};
        for(int d : directions)
            sweep(validDests, d);
        return validDests;
    }
}
