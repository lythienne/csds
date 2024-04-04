import java.awt.Color;
import java.util.*;

/**
 * Bishop piece represents a Bishop in a chess game, it has a location, color, and a value of 3, 
 * it can move diagonally.
 * @author Harrison Chen
 * @version 3/27/23
 */
public class Bishop extends Piece
{
    /**
     * Constructs a Bishop piece with a color and file name of a picture of it
     * @param col the color of the bishop (black/white)
     * @param fileName the file name of the bishop icon
     */
    public Bishop(Color col, String fileName)
    {
        super(col, fileName, 5);
    }

    /**
     * Returns all the valid locations this bishop can move to
     * @return an ArrayList of valid locations
     */
    public ArrayList<Location> destinations()
    {
        ArrayList<Location> validDests = new ArrayList<Location>();
        int[] directions = {Location.NORTHEAST, Location.NORTHWEST, Location.SOUTHEAST, Location.SOUTHWEST};
        for(int d : directions)
            sweep(validDests, d);
        return validDests;
    }
}
