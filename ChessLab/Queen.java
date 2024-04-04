import java.awt.Color;
import java.util.*;

/**
 * Queen piece represents a Queen in a chess game, it has a location, color, and a value of 9, 
 * it can move horizontally, vertically, and diagonally.
 * @author Harrison Chen
 * @version 3/27/23
 */
public class Queen extends Piece
{
    /**
     * Constructs a Queen piece with a color and file name of a picture of it
     * @param col the color of the queen (black/white)
     * @param fileName the file name of the queen icon
     */
    public Queen(Color col, String fileName)
    {
        super(col, fileName, 9);
    }

    /**
     * Returns all the valid locations this queen can move to
     * @return an ArrayList of valid locations
     */
    public ArrayList<Location> destinations()
    {
        ArrayList<Location> validDests = new ArrayList<Location>();
        int[] directions = {Location.NORTH, Location.SOUTH, Location.EAST, Location.WEST,
                            Location.NORTHEAST, Location.NORTHWEST, Location.SOUTHEAST, Location.SOUTHWEST};
        for(int d : directions)
            sweep(validDests, d);
        return validDests;
    }
}
