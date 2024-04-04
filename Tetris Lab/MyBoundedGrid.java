import java.util.*;

/**
 * MyBoundedGrid class is a bounded grid with a number of rows and cols and stores objects
 * of type E in a 2D array. It can return its number of rows and cols, return whether a 
 * location in the grid is valid, get, put, and remove items in the grid, and return a list
 * of all occupied locations in the grid.
 * @param E the type of object stored in this MyBoundedGrid
 */
public class MyBoundedGrid<E> 
{
    private int rows;
    private int cols;
    private Object[][] grid;

    /**
     * Constructs a new MyBoundedGrid with a number of rows and columns
     * initializes the array of locations to have a number of spaces equal
     * to the number of spaces available in the grid
     * @param rows the number of rows
     * @param cols the number of columns
     */
    public MyBoundedGrid(int rows, int cols)
    {
        this.rows = rows;
        this.cols = cols;
        grid = new Object[rows][cols];
    }

    /**
     * Returns the number of rows
     * @return rows
     */
    public int getNumRows()
    {
        return rows;
    }
    
    /**
     * Returns the number of cols
     * @return cols
     */
    public int getNumCols()
    {
        return cols;
    }

    /**
     * Returns whether a location is within the grid
     * @param loc the location to check
     * @return true if the location's row is within 0 and rows
     *         and if the location's column is within 0 and cols
     */
    public boolean isValid(Location loc)
    {
        return loc.getCol()>=0 && loc.getCol()<cols
            && loc.getRow()>=0 && loc.getRow()<rows;
    }

    /**
     * Returns the object at a location
     * @param loc the location
     * @return the object of type E at that location's row and column
     */
    public E get(Location loc)
    {
        if (!isValid(loc))
            throw new IllegalArgumentException("Can't get from  " + loc);
        return (E) grid[loc.getRow()][loc.getCol()];
    }

    /**
     * Puts an object at a location, returns the object previously there
     * @param loc the location
     * @param obj the object to put in the location
     * @return the object previously at the location's row and column
     */
    public E put(Location loc, E obj)
    {
        E old = get(loc);
        grid[loc.getRow()][loc.getCol()] = obj;
        return old;
    }

    /**
     * Removes an object at a location, returns the object previously there
     * @param loc the location
     * @return the object previously at the location's row and column
     * @throws IllegalArgumentException when location is not valid
     */
    public E remove(Location loc)
    {
        if (!isValid(loc))
            throw new IllegalArgumentException("Can't remove from  " + loc);
        return put(loc, null);
    }

    /**
     * Returns all locations in this grid that are occupied by an object
     * @return an ArrayList of location objects that are all the locations
     *         in this grid that are occupied
     */
    public ArrayList<Location> getOccupiedLocations()
    {
        ArrayList<Location> locations = new ArrayList<Location>();
        for(int i=0; i<rows; i++)
            for(int j=0; j<cols; j++)
                if(grid[i][j]!=null)
                    locations.add(new Location(i, j));
        return locations;
    }
}
