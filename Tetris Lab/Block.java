import java.awt.Color;
/**
 * Class Block encapsulates a Block abstraction which can be placed into a Gridworld style grid
 * Block has a color, grid, and location in the grid and can get and change all 3, block can
 * also be added and removed from the grid and moved to a new location in the grid
 * @author Harrison Chen
 */
public class Block
{
    private MyBoundedGrid<Block> grid;
    private Location location;
    private Color color;
    /**
     * Constructs a block with a color
     */
    public Block()
    {
        color = Color.PINK;
        grid = null;
        location = null;
    }
	/**
	 * Return the color of this block
     * @return the color of this block
     */
    public Color getColor()
    {
        return color;
    }
	/**
	 * Sets the color of this block
     * @param newColor the new color to set this block
     */
    public void setColor(Color newColor)
    {
        color = newColor;
    }
    
	/**
	 * Return the grid containing this block
     * @return the grid of blocks containing this block
     */
    public MyBoundedGrid<Block> getGrid()
    {
        return grid;
    }
    
	/**
	 * Return the location of this block in the grid
     * @return the location of this block in the grid
     */
    public Location getLocation()
    {
        return location;
    }

    /**
     * Sets the location to a new location
     * @param loc the new location
     */
    public void setLocation(Location loc)
    {
        location = loc;
    }
    
	/**
	 * Removes the block from the grid by setting its location and grid to null
     */
    public void removeSelfFromGrid()
    {
        grid.remove(location);
        location = null;
        grid = null;
    }
    
	/**
	 * Puts itself in the grid at a location
     * @param grid the grid to be put in
     * @param loc the location to put itself in
	 */
    public void putSelfInGrid(MyBoundedGrid<Block> gr, Location loc)
    {
        if(gr.get(loc)!=null)
            gr.get(loc).removeSelfFromGrid();
        location = loc;
        grid = gr;
        gr.put(loc, this);
    }

    /**
	 * Moves itself to a new location
     * @param newLocation the new location to move to
	 */
    public void moveTo(Location newLocation)
    {
        grid.remove(location);
        putSelfInGrid(grid, newLocation);
    }

    /**
	* returns a string with the location and color of this block
	*/
    public String toString()
    {
        return "Block[location=" + location + ",color=" + color + "]";
    }
}