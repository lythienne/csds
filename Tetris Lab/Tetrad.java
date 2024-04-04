import java.awt.Color;
import java.util.concurrent.Semaphore;
import java.util.Map;
import java.util.HashMap;

/**
 * Class tetrad is a tetris piece of 4 blocks, it has an array of its blocks, and the grid its in
 * when created, it is given a type and a color scheme to use, the type determines which piece to make
 * and the color scheme decides whether to use the correct colors or the AP CS DS wrong colors.
 * 
 * Tetrad class can put and remove itself from the grid, check if its locations in the grid are empty, 
 * translate, and rotate itself in the grid, make and remove a ghost block of itself, and check if it
 * can move down any further
 * 
 * @author Harrison Chen
 * @version 3/8/23
 */
public class Tetrad 
{
    private Block[] blocks;
    private Location[] locs;
    private MyBoundedGrid<Block> grid;
    private Tetrad ghost;
    private int type;
    private int state; //only used for rotating I pieces bc they're special 
    private Semaphore lock;


    /**
     * Constructs a new Tetrad (tetris piece) with the grid its in, the type of piece, and its array
     * of blocks initialized
     * @param gr the grid the piece is in
     * @param typeInt the type of piece to create and store in the array of blocks
     * @param originalColor the color scheme of this piece
     */
    public Tetrad(MyBoundedGrid<Block> gr, int typeInt, boolean originalColor)
    {
        type = typeInt;
        grid = gr;

        lock = new Semaphore(1, true);

        int mid = grid.getNumCols()/2;
        blocks = new Block[4];
        for(int i=0; i<blocks.length; i++)
            blocks[i] = new Block();

        Color color;
        Location[] locs;

        switch(type)
        {
            case 0: //I
                if(originalColor) color = Color.CYAN;
                else color = Color.RED;
                locs = new Location[]{new Location(1, mid-2), new Location(1, mid-1), 
                                        new Location(1, mid), new Location(1, mid+1)};
                state = 0;
                break;
            case 1: //O
                if(originalColor) color = Color.YELLOW;
                else color = Color.CYAN;
                locs = new Location[]{new Location(0, mid), new Location(1, mid-1), 
                                        new Location(1, mid), new Location(0, mid-1)};
                break;
            case 2: //T
                if(originalColor) color = Color.MAGENTA;
                else color = Color.GRAY;
                locs = new Location[]{new Location(1, mid-1), new Location(1, mid-2), 
                                        new Location(1, mid), new Location(0, mid-1)};
                break;
            case 3: //L
                if(originalColor) color = Color.ORANGE;
                else color = Color.YELLOW;
                locs = new Location[]{new Location(1, mid-1), new Location(1, mid-2), 
                                        new Location(1, mid), new Location(0, mid)};
                break;
            case 4: //J
                if(originalColor) color = Color.BLUE;
                else color = Color.MAGENTA;
                locs = new Location[]{new Location(1, mid-1), new Location(1, mid-2), 
                                        new Location(1, mid), new Location(0, mid-2)};
                break;
            case 5: //S
                if(originalColor) color = Color.GREEN;
                else color = Color.BLUE;
                locs = new Location[]{new Location(1, mid-1), new Location(1, mid-2), 
                                        new Location(0, mid-1), new Location(0, mid)};
                break;
            default: //Z
                if(originalColor) color = Color.RED;
                else color = Color.GREEN;
                locs = new Location[]{new Location(1, mid-1), new Location(0, mid-2), 
                                        new Location(0, mid-1), new Location(1, mid)};
                break;
        }
        for(Block b : blocks)
            b.setColor(color);
        addToLocations(locs);
    }

    /**
     * Adds itself to the locations given in an array
     * @param locs the array of locations where the new blocks will go
     */
    private void addToLocations(Location[] locs)
    {
        this.locs = locs;
        for(int i=0; i<4; i++)
            blocks[i].setLocation(locs[i]);
    }

    /**
     * Returns true if all locations in a list of locations are valid and empty, false otherwise
     * @param locs the list of locations to check
     * @return true if all locations in locs are valid and empty, false otherwise
     */
    private boolean areEmpty(Location[] locs)
    {
        for(Location l : locs)
            if(!grid.isValid(l) || grid.get(l)!=null)
                return false;
        return true;
    }

    /**
     * Makes a ghost of this piece with the same type and slightly darker color
     */
    public void makeGhost()
    {
        try 
        {
            lock.acquire();
            removeGhost();
            Location[] pieceLocs = locs;
            Location[] ghostLocs = new Location[4];
            ghost = new Tetrad(grid, type, false);
            for(int i=0; i<4; i++)
            {
                int row = blocks[i].getLocation().getRow();
                int col = blocks[i].getLocation().getCol();
                ghostLocs[i] = new Location(row, col);
                ghost.blocks[i].setColor(blocks[i].getColor().darker().darker());
            }
            for(Block b : blocks)
                b.removeSelfFromGrid();
            ghost.addToLocations(ghostLocs);
            ghost.translate(0, 21);
            if(areEmpty(ghost.locs))
                ghost.putSelf(grid);
            addToLocations(pieceLocs);
            putSelf(grid);
        } 
        catch (InterruptedException e) {}
        finally
        {
            lock.release();
        }
    }

    /**
     * Removes ghost from existence
     */
    public void removeGhost()
    {
        if(ghost!=null)
        {
            for(Block b : ghost.blocks)
                if(b.getGrid() != null)
                    b.removeSelfFromGrid();
            ghost = null;
        }
    }

    /**
     * Moves the object vX units to the right and vY units down, stopping when the location moving to is
     * not valid
     * @param vX the number of units to move to the right
     * @param vY the number of units to move down
     */
    public void translate(int vX, int vY)
    {
        try
        {
            lock.acquire();
            removeGhost();
            Location[] tempLocs = locs;
            int x = 0;
            for(Block b : blocks)
                if(b.getGrid()!=null)
                    removeSelf();
            while(x<=Math.abs(vX) && areEmpty(tempLocs))
            {
                increment(tempLocs, vX>0, true);
                x++;
            }
            increment(tempLocs, vX<=0, true);
            int y = 0;
            while(y<=Math.abs(vY) && areEmpty(tempLocs))
            {
                increment(tempLocs, vY>0, false);
                y++;
            }
            increment(tempLocs, vY<=0, false);
            addToLocations(tempLocs);
        }
        catch (InterruptedException e) {}
        finally
        {
            lock.release();
        }
    }

    /**
     * Increments or decrements all row/column in an array of locations
     * @param locs the array of locations
     * @param positive true if increment, false if decrement
     * @param cols true if changing columns, false if changing rows
     */
    public void increment(Location[] locs, boolean positive, boolean cols)
    {
        if(cols)
            for(Location l : locs)
            {
                if(positive)
                    l.setCol(l.getCol()+1);
                else
                    l.setCol(l.getCol()-1);
            }
        else
            for(Location l : locs)
            {
                if(positive)
                    l.setRow(l.getRow()+1);
                else
                    l.setRow(l.getRow()-1);
            }
    }

    /**
     * Rotates a block 90*rotation degrees clockwise around a vertex (if I/O piece) or around 
     * a central block (otherwise)
     * @param rotations the number of times to rotate piece 90 degrees
     */
    public void rotate(int rotations)
    {
        try
        {
            lock.acquire();
            Location[] tempLocs = locs;
            Location[] finalLocs = new Location[4];
            for(int i=0; i<4; i++)
                finalLocs[i] = new Location(tempLocs[i].getRow(), tempLocs[i].getCol());
            for(Block b : blocks)
                if(b.getGrid()!=null)
                    removeSelf();
            if(type>1) //not I/O which rotate on a vertex not a block (O doesn't rotate)
            {
                int col = tempLocs[0].getCol();
                int row = tempLocs[0].getRow();
                for(int i=0;i<rotations;i++)
                    for(Location l : tempLocs)
                    {
                        int blockRow = l.getRow();
                        l.setRow(row-col+l.getCol());
                        l.setCol(row+col-blockRow);
                    }
            }
            if(type==0) //bc I is :sparkle: special :sparkle:
            {
                for(int i=0;i<rotations;i++)
                {
                    int col = tempLocs[0].getCol();
                    int row = tempLocs[0].getRow();
                    for(Location l : tempLocs)
                    {
                        int rowAdj;
                        int colAdj;
                        int blockCol = l.getCol();
                        int blockRow = l.getRow();
                        switch (state) 
                        {
                            case 0: rowAdj = 1; colAdj = 0; break;
                            case 1: rowAdj = 0; colAdj = 2; break;
                            case 2: rowAdj = 2; colAdj = 3; break;
                            default: rowAdj = 3; colAdj = 1; break;
                        }
                        l.setRow(blockCol-col+colAdj+row-rowAdj);
                        l.setCol(3-(blockRow-row+rowAdj)+col-colAdj);
                    }
                    state++;
                    if(state == 4) state = 0;
                }
            }
            if(areEmpty(tempLocs))
                finalLocs = tempLocs;
            else
            {
                boolean leftOverlap = false;
                boolean rightOverlap = false;
                boolean bothOverlapAndIPiece = false; //why are I pieces so special
                int midCol = getMidCol(finalLocs); //needs middle of *original* position
                for(Location l : tempLocs)
                    if(!grid.isValid(l) || grid.get(l)!=null)
                    {
                        if(l.getCol()<=midCol)
                            leftOverlap = true;
                        else if(l.getCol()>midCol)
                            rightOverlap = true;
                    }
                if(leftOverlap && rightOverlap)
                {
                    //do nothing bc piece cant rotate
                    //ig i could change this if i wanted to implement tspins but that sounds hard
                }   
                if(!bothOverlapAndIPiece)
                {
                    if(leftOverlap)
                    {
                        increment(tempLocs, true, true);
                        if(type == 0 && state == 2) //I pieces are special
                        {
                            increment(tempLocs, true, true);
                        }
                        if(areEmpty(tempLocs))
                            finalLocs = tempLocs;
                        else
                        {
                            increment(tempLocs, true, false);
                            if(areEmpty(tempLocs))
                                finalLocs = tempLocs;
                            else if(type == 2) //i want tspin triples in my game shush
                            {
                                increment(tempLocs, true, false);
                                if(areEmpty(tempLocs))
                                    finalLocs = tempLocs;
                            }
                        }
                    }
                    else if(rightOverlap)
                    {
                        increment(tempLocs, false, true);
                        if(type == 0 && state == 0) //bc I pieces, again, are special
                        {
                            increment(tempLocs, false, true);
                        }
                        if(areEmpty(tempLocs))
                            finalLocs = tempLocs;
                        else
                        {
                            increment(tempLocs, true, false);
                            if(areEmpty(tempLocs))
                                finalLocs = tempLocs;
                            else if(type == 2) //i want tspin triples in my game shush
                            {
                                increment(tempLocs, true, false);
                                if(areEmpty(tempLocs))
                                    finalLocs = tempLocs;
                            }
                        }
                    }
                }
                
            }
            addToLocations(finalLocs);
        }
        catch (InterruptedException e) {}
        finally
        {
            lock.release();
        }
    }

    /**
     * Returns the middle column of this block, need to input locations array because is called 
     * in rotate after block is removed from grid
     * @param locs array of locations of rotated block
     * @return the middle column of this block
     */
    private int getMidCol(Location[] locs)
    {
        int left = 10;
        int right = -1;
        for(Location l : locs)
        {
            int col = l.getCol();
            if(col<left)
                left = col;
            if(col>right)
                right = col;
        }
        return (left+right)/2;
    }

    /**
     * Puts itself in a grid
     * @param grid the grid to be put in
     */
    public void putSelf(MyBoundedGrid<Block> grid)
    {
        for(Block b : blocks)
            b.putSelfInGrid(grid, b.getLocation());
    }

    /**
     * Removes this piece from its grid, returning its type
     * @return the type of this piece
     */
    public int removeSelf()
    {
        removeGhost();
        for(Block b : blocks)
            b.removeSelfFromGrid();
        return type;
    }

    /**
     * Returns true if piece has space underneath to fall, false otherwise
     * @return true if spaces under block are empty, false otherwise
     */
    public boolean canMove()
    {
        try 
        {
            lock.acquire();
            removeGhost();
            Map<Integer, Location> locs = new HashMap<Integer, Location>();
            for(Block b : blocks)
            {
                Location bLoc = b.getLocation();
                if(bLoc != null && (locs.get(bLoc.getCol()) == null || locs.get(bLoc.getCol()).getRow()<bLoc.getRow()))
                    locs.put(b.getLocation().getCol(), b.getLocation());
            }
            Location[] arrLocs = new Location[locs.size()];
            int i = 0;
            for(Integer col : locs.keySet()) 
            {
                Location l = locs.get(col);
                arrLocs[i] = new Location(l.getRow()+1, l.getCol());
                i++;
            }
            return areEmpty(arrLocs);
        } 
        catch (InterruptedException e) {}
        finally
        {
            lock.release();
        }
        return false;
    }
}
