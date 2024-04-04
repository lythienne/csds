import java.awt.*;
import java.util.*;

/**
 * Piece class represents a chess piece on a board, at a location, with a color, a file name for the
 * icon that represents it, and a piece value.
 * @author Harrison Chen
 * @version 3/27/23
 */
public abstract class Piece
{
	//the board this piece is on
	private Board board;

	//the location of this piece on the board
	private Location location;

	//the color of the piece
	private Color color;

	//the file used to display this piece
	private String imageFileName;

	//the approximate value of this piece in a game of chess
	private int value;

	/**
     * Constructs a new piece with a color, filename, and value
     * @param col the color
     * @param fileName the filename
     * @param val the value
     */
	public Piece(Color col, String fileName, int val)
	{
		color = col;
		imageFileName = fileName;
		value = val;
	}

	//returns the board this piece is on
	public Board getBoard()
	{
		return board;
	}

	//returns the location of this piece on the board
	public Location getLocation()
	{
		return location;
	}

	//returns the color of this piece
	public Color getColor()
	{
		return color;
	}

	//returns the name of the file used to display this piece
	public String getImageFileName()
	{
		return imageFileName;
	}

	//returns a number representing the relative value of this piece
	public int getValue()
	{
		return value;
	}

    /**
     * Puts this piece into a board. If there is another piece at the given
     * location, it is removed. <br />
     * Precondition: (1) This piece is not contained in a grid (2)
     * <code>loc</code> is valid in <code>gr</code>
     * @param brd the board into which this piece should be placed
     * @param loc the location into which the piece should be placed
     */
    public void putSelfInGrid(Board brd, Location loc)
    {
        if (board != null)
            throw new IllegalStateException(
                    "This piece is already contained in a board.");

        Piece piece = brd.get(loc);
        if (piece != null)
            piece.removeSelfFromGrid();
        brd.put(loc, this);
        board = brd;
        location = loc;
    }

    /**
     * Removes this piece from its board. <br />
     * Precondition: This piece is contained in a board
     */
    public void removeSelfFromGrid()
    {
        if (board == null)
            throw new IllegalStateException(
                    "This piece is not contained in a board.");
        if (board.get(location) != this)
            throw new IllegalStateException(
                    "The board contains a different piece at location "
                            + location + ".");

        board.remove(location);
        board = null;
        location = null;
    }

    /**
     * Moves this piece to a new location. If there is another piece at the
     * given location, it is removed. <br />
     * Precondition: (1) This piece is contained in a grid (2)
     * <code>newLocation</code> is valid in the grid of this piece
     * @param newLocation the new location
     */
    public void moveTo(Location newLocation)
    {
        if (board == null)
            throw new IllegalStateException("This piece is not on a board.");
        if (board.get(location) != this)
            throw new IllegalStateException(
                    "The board contains a different piece at location "
                            + location + ".");
        if (!board.isValid(newLocation))
            throw new IllegalArgumentException("Location " + newLocation
                    + " is not valid.");

        if (newLocation.equals(location))
            return;
        board.remove(location);
        Piece other = board.get(newLocation);
        if (other != null)
            other.removeSelfFromGrid();
        location = newLocation;
        board.put(location, this);
    }

    /**
     * Returns whether a destination is valid
     * @return true if location is empty or the piece there is of opposite color, false otherwise
     */
    public boolean isValidDestination(Location dest)
    {
        return board.isValid(dest) && (board.get(dest)==null || board.get(dest).getColor()!=color);
    }

    /**
     * Lists the locations this piece can move to
     * @return an ArrayList<Location> of valid locations this piece can move to
     */
    public abstract ArrayList<Location> destinations();

    /**
     * Checks all locations in a direction until it hits a piece or a wall and adds them to a list
     * @param dests the list to add to
     * @param direction the direction to check in (0 is North, 180 is South, etc)
     */
    public void sweep(ArrayList<Location> dests, int direction)
    {
        int[] directions =  {0, 45, 90, 135, 180, 225, 270, 315};
        int[] xArr = {0, -1, -1, -1, 0, 1, 1, 1};
        int[] yArr = {-1, -1, 0, 1, 1, 1, 0, -1};
        int x = 0;
        int y = 0;
        for(int i=0; i<directions.length;i++)
            if(directions[i] == direction)
            {
                x=xArr[i];
                y=yArr[i];
            }  
        Location check = new Location(location.getRow()+y, location.getCol()+x);
        while(isValidDestination(check))
        {
            dests.add(check);
            if(board.get(check)!=null && board.get(check).getColor()!=color)
                check = new Location(1000, 1000); //ends loop on captured piece
            else    
                check = new Location(check.getRow()+y, check.getCol()+x);
        }
    }
}