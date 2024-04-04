import java.util.*;
import java.awt.Color;
import java.lang.Math;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.sound.sampled.*;

/**
 * Tetris class runs a tetris game with a grid world style grid and blocks. The current tetrad, next 6 (in a queue), and held tetrad are
 * kept track of using the tetrad class (of 4 blocks). Otherwise, various game states and variables (like time and lines cleared) are stored
 * as booleans and ints. A block display class is used to display the game, including the main tetris board, the held piece, the next 6 pieces,
 * the lines left, the timer, and the win/lose screen.
 * 
 * Standard tetris game rules: https://harddrop.com/wiki/Tetris_Guideline
 * This lab is heavily based on: https://jstris.jezevec10.com/?play=1&mode=1 (you should try playing this its way cooler, controls are a bit differnt)
 * 
 * Tetris game is a standard (almost) tetris 40 line sprint (complete 40 lines as fast as possible) with the following features:
 * - correct rotating: I and O pieces rotate around the vertex in the center, not the block (O doesn't rotate); if piece would bump into
 *                     a wall after rotation completion, "kicks" the piece back out, if piece still overlaps, tries to move it down, if
 *                     still can't be placed, rotate doesn't happen (gives support for tspins + esp tspin triples):
 *                     rotation system: https://harddrop.com/wiki/SRS, tspin gif: https://tenor.com/view/tetris-gif-25530338
 * - bagged random next pieces: pieces are randomly chosen from a "bag", when bag is empty, resets the bag with one piece of each type
 * - original colors (can be turned off for ugly CS lab colors)
 * - default controls, see below (harrison's controls can be optionally turned on)
 * - holds: swap the current piece with the piece in hold, if hold is empty, then just places current piece in hold, getting a new block
 *          only 1 hold is available per new block (you'll see)
 * - 6 piece preview: shows the next 6 pieces that will be spawned in (if you're asking why you would need so many, I use that many :D)
 * - regular soft drop lock period: this irked me in the original tetris lab, players should have a regular amount of time to move piece around
 *                                  before it locks into place
 * - high DAS: explained more in control explanation below but just means that after holding left/right, block teleports to the wall (good for
 *             playing tetris really fast)
 * - ghost piece: shows you where the piece is going to land before you place it
 * - 100fps: game runs at exactly 100fps so that it can check for blocks that can't move, update ghost pieces, update timer, add new blocks,
 *           make softdrop/left+right DAS quicker, and check for win/lose at basically any time, block naturally falls at frame 0 and 50 (twice/second)
 * - win/lose screen: standard, win screen shows your time (incorrect bc my timer is broken datar help), # of pieces placed, and pieces per second
 *                    lose screen shows how many lines you had left before you won 
 * - music: soundtrack of Tetris 99 for the Nintendo Switch
 * 
 * For reference, my best time (currently) of the 40 line sprint is 53.508 seconds, where I placed 1.98 pieces per second
 * 
 * Standard Controls Are:                                   Harrison's Controls Are:
 *                          left: left arrow                                        left: J (my arrow keys are broken so i use IJKL)
 *                          right: right arrow                                      right: L
 *                          softDrop: down arrow                                    softDrop: K
 *                          hardDrop: space                                         hardDrop: I
 *                          turn CW: up arrow                                       turn CW: X
 *                          turn CCW: X                                             turn CCW: Z
 *                          turn 180: Z                                             turn 180: A
 *                          hold: C                                                 hold: space
 *                          restart: R                                              restart: R
 * 
 * Left/Right: moves one space left/right, DAS completely to the wall when held for 0.1 seconds
 * Softdrop: slowly moves piece down, gives 0.5 seconds for player to move and rotate piece before locking into place (for overhangs + tspins)
 * Harddrop: instantly moves piece to the bottom, instantly locks piece, and instantly gives a new block
 * Turn CW/CCW/180: turns the block 90/270/180 degrees clockwise with the tetris SRS method explained above
 * Hold: swaps current piece and piece in hold as described above
 * Restart: restarts the game with a new grid, block, timer, and 40 more lines to clear :D
 * 
 * @author Harrison Chen
 * @version 03/13/23
 */
public class Tetris implements TetrisListener
{
    private MyBoundedGrid<Block> grid;
    private MyBoundedGrid<Block> holdGrid;
    private MyBoundedGrid<Block> nextGrid;
    private BlockDisplay display;

    private Tetrad curr;
    private Tetrad hold;
    private LinkedList<Tetrad> bag; //bagged random goes randomly through all 7 blocks before starting a new bag
    private LinkedList<Tetrad> nextQueue;

    private boolean originalColor;
    private boolean defaultControls;

    private boolean blockNeedsToStopMoving;
    private boolean holdAvailable;
    private boolean softDropping;
    private boolean goLeft;
    private int leftFrame; //i really dont know how to do these things without making like 20 variables
    private boolean goRight;
    private int rightFrame; //these are for DAS (delayed auto shift)
    private boolean hardDropped;
    
    private final int SPRINTLENGTH;
    private int piecesPlaced;
    private int time;
    private int clearedRows;
    private int end; //0=restart, 1=lose, 2=win, -1 continue

    final private int[][] DTCANNON =  //ignore this, this is just to have a dtcannon set up from the start for demonstration purposes
    {                                 //dtcannon https://youtu.be/PiohsFMXyGQ, a tetris opener to start with a double tspin into a triple tspin
        {0, 0, 1, 1, 0, 0, 0, 0, 1, 1},
        {0, 0, 0, 1, 1, 1, 0, 1, 1, 1},
        {1, 1, 0, 1, 1, 1, 1, 1, 1, 1},
        {1, 0, 0, 1, 1, 1, 1, 1, 1, 1},
        {1, 0, 0, 0, 1, 1, 1, 1, 1, 1},
        {1, 1, 0, 1, 1, 1, 1, 1, 1, 1},
        {1, 1, 0, 1, 1, 1, 1, 1, 1, 1},
    };

    /**
     * Creates a new Tetris game with a grid, an initialized empty bag, original colors off, nothing in hold,
     * default controls on, a current falling tetrad, a display
     * @param color true if default color scheme false if ugly AP CS DS lab color scheme
     * @param controls true if default controls false if Harrison's controls
     */
    public Tetris(boolean color, boolean controls)
    {
        originalColor = color;
        defaultControls = controls;

        grid = new MyBoundedGrid<Block>(22, 10);
        holdGrid = new MyBoundedGrid<Block>(2, 4);
        nextGrid = new MyBoundedGrid<Block>(17, 4);

        if(true) //if true makes a dtcannon on startup
        {
            for(int r=0; r<DTCANNON.length; r++)
            {
                for(int c=0; c<DTCANNON[0].length; c++)
                {
                    Block b = new Block();
                    b.setColor(Color.GRAY);
                    if(DTCANNON[r][c]==1)
                        b.putSelfInGrid(grid, new Location(r+15, c));
                }
            }
        }

        bag = new LinkedList<Tetrad>();
        nextQueue = new LinkedList<Tetrad>();
        initializeNext();

        curr = getNextPiece();
        curr.putSelf(grid);

        hold = null;

        SPRINTLENGTH = 40;
        time = 0;
        clearedRows = 0;
        piecesPlaced = 0;
        end = -1;

        blockNeedsToStopMoving = true;
        holdAvailable = true;
        hardDropped = false;
        softDropping = false;
        goLeft = false;
        goRight = false;

        display = new BlockDisplay(grid, holdGrid, nextGrid, defaultControls, originalColor);
        display.setTitle("Hstris");
        display.setListener(this);
        display.updateDisplay(time, SPRINTLENGTH-clearedRows, piecesPlaced, end);
    }
    /**
     * Initializes the next queue and places all pieces in the queue in the next grid
     */
    private void initializeNext()
    {
        int row = 0;
        for(int i=0; i<(nextGrid.getNumRows()+1)/3; i++)
        {
            Tetrad temp = getBagPiece();
            nextQueue.add(temp);
            temp.translate(0, row);
            row+=3;
        }
        for(Tetrad t : nextQueue)
            t.putSelf(nextGrid);
    }

    /**
     * Returns the next piece in the queue, updates queue and nextGrid
     * @return the next piece in the queue
     */
    private Tetrad getNextPiece()
    {
        int nextType = nextQueue.remove().removeSelf();
        for(Tetrad t : nextQueue)
            t.translate(0, -3);
        Tetrad temp = getBagPiece();
        nextQueue.add(temp);
        temp.translate(0, nextGrid.getNumRows()-2);
        for(Tetrad t : nextQueue)
            t.putSelf(nextGrid);
        holdAvailable = true;
        piecesPlaced++;
        return new Tetrad(grid, nextType, originalColor);
    }

    /**
     * For bagged random, picks a random piece from non selected pieces, if bag is empty, resets bag
     * with all pieces
     */
    private Tetrad getBagPiece()
    {
        if(bag.isEmpty())
        {
            List<Integer> pieces = new ArrayList<Integer>(7);
            for(int i=0; i<7; i++)
                pieces.add(i);
            for(int i=0; i<7; i++)
                bag.add(new Tetrad(nextGrid, pieces.remove((int) (Math.random()*pieces.size())), 
                    originalColor));
        }
        return bag.remove();
    }

    /**
     * Executes one frame of tetris (100fps), waiting a 10 milliseconds, then executing
     * what happens on each frame based on the frame number, then checks to see if the current piece
     * has reached a resting position
     * @param frame the number of the frame in this second
     */
    public void frame(int frame)
    {
        boolean newBlock = false;
        try
        {
            Thread.sleep((long)8.3);
        }
        catch (InterruptedException e){}

        switch (frame) 
        {
            case 0:
                if(blockNeedsToStopMoving && !curr.canMove())
                    newBlock = true;
                else
                {
                    curr.translate(0, 1); 
                    curr.putSelf(grid);
                }    
                break;
            case 50:
                curr.translate(0, 1);
                curr.putSelf(grid);
                break;
        }
        if(softDropping)
        {
            curr.translate(0, 1); 
            curr.putSelf(grid);
        }
        if(goLeft) //DAR (block teleports to left wall after left held down for 10 milliseconds)
        {
            if(leftFrame == -1)
                leftFrame = frame;
            if(leftFrame+10 == frame)
            {
                curr.translate(-10, 0); 
                curr.putSelf(grid);
            }
        }
        if(goRight)
        {
            if(rightFrame == -1)
                rightFrame = frame;
            if(rightFrame+10 == frame)
            {
                curr.translate(10, 0); 
                curr.putSelf(grid);
            }
        }

        if(!curr.canMove())
        {
            if(hardDropped)
            {
                newBlock=true;
                hardDropped = false;
            }
            else if(!blockNeedsToStopMoving)
            {
                blockNeedsToStopMoving = true;
                frame = 0; //gives half a second for player to move piece around before piece locks
            }
        }
        if(newBlock)
        {
            clearCompletedRows();
            for(int i=0; i<grid.getNumCols(); i++)
                if(grid.get(new Location(0, i)) != null)
                    end=1;
            blockNeedsToStopMoving = false;
            curr = getNextPiece();
            curr.putSelf(grid);
        }
        if(clearedRows>=SPRINTLENGTH)
            end=2;
        if(curr.canMove())
            curr.makeGhost();
        time++;
        display.updateDisplay(time, SPRINTLENGTH-clearedRows, piecesPlaced, end);
    }

    /**
     * Plays a game of tetris, executing each frame then advancing it, resetting the frame count to 0
     * when it reaches 100
     */
    private void play()
    {
        int frame = 0;
        while(end==-1)
        {
            frame(frame); 
            frame++; 
            if(frame == 100) 
                frame = 0;
        }
    }

    /**
     * Moves current piece left
     */
    public void left()
    {
        curr.translate(-1, 0);
        curr.putSelf(grid);
        goLeft = true;
    }
    /**
     * Stops moving piece left
     */
    public void leftReleased()
    {
        goLeft = false;
        leftFrame = -1;
    }
    /**
     * Moves current piece right
     */
    public void right()
    {
        curr.translate(1, 0);
        curr.putSelf(grid);
        goRight = true;
    }
    /**
     * Stops moving piece right
     */
    public void rightReleased()
    {
        goRight = false;
        rightFrame = -1;
    }
    /**
     * Moves current piece down
     */
    public void softDrop()
    {
        curr.translate(0, 1);
        softDropping = true;
    }
    /**
     * Stops moving current piece down
     */
    public void SDReleased()
    {
        softDropping = false;
    }
    /**
     * Drops piece to the bottom
     */
    public void hardDrop()
    {
        curr.translate(0, 21);
        curr.putSelf(grid);
        hardDropped = true;
    }
    /**
     * Swaps current piece with piece in hold, if hold is empty puts next piece 
     */
    public void hold()
    {
        if(holdAvailable)
        {
            Tetrad temp = null;
            if(hold!=null)
                temp = new Tetrad(grid, hold.removeSelf(), originalColor);
            hold = new Tetrad(holdGrid, curr.removeSelf(), originalColor);
            curr = temp;

            if(curr==null)
                curr = getNextPiece();
            
            curr.putSelf(grid);
            hold.putSelf(holdGrid);
            holdAvailable = false;
        }
    }
    /**
     * Rotates current piece 90 degrees clockwise
     */
    public void turnClockwise()
    {
        curr.rotate(1);
        curr.putSelf(grid);
    }
    /**
     * Rotates current piece 270 degrees clockwise
     */
    public void turnCounterCW()
    {
        curr.rotate(3);
        curr.putSelf(grid);
    }
    /**
     * Rotates current piece 180 degrees clockwise
     */
    public void turn180()
    {
        curr.rotate(2);
        curr.putSelf(grid);
    }
    /**
     * Ends the current game and restarts it
     */
    public void restart()
    {
        end = 0;
    }

    /**
     * Swaps the color scheme from the original colors or the lab colors to the other
     */
    public void swapColor()
    {
        originalColor = !originalColor;
        restart();
    }
    /**
     * Swaps the control scheme from the original controls or harrison's controls to the other
     */
    public void swapControls()
    {
        defaultControls = !defaultControls;
        restart();
    }

    /**
     * Returns the color scheme of this tetris game
     * @return true if original colors, false if lab colors
     */
    public boolean getColor()
    {
        return originalColor;
    }
    /**
     * Returns the control scheme of this tetris game
     * @return true if default controls, false if harrison's controls
     */
    public boolean getControls()
    {
        return defaultControls;
    }

    /**
     * Returns true if row is completed, false otherwise
     * @param row the row to be checked
     * @precondition row is in bounds (1-20)
     * @return true if every cell in row is occupied, false otherwise
     */
    private boolean isCompletedRow(int row)
    {
        for(int i=0; i<grid.getNumCols(); i++)
        {
            if(grid.get(new Location(row, i))==null)
                return false;
        }
        return true;
    }

    /**
     * Clears all blocks in row, moves all other blocks down by one
     * @param row the row to clear
     * @precondition row is in bounds (1-20), row is full of blocks
     */
    private void clearRow(int row)
    {
        clearedRows++;
        for(int i=row; i>0; i--)
        {
            for(int j=0; j<grid.getNumCols(); j++)
                grid.put(new Location(i, j), grid.remove(new Location(i-1, j)));
        }
    }

    /**
     * Clears all rows that are completed, moves all other blocks down to fill their places
     */
    private void clearCompletedRows()
    {
        for(int i=1; i<grid.getNumRows(); i++)
            if(isCompletedRow(i))
                clearRow(i);
    }

    /**
     * Returns 0 if game should end and restart, 1 if game should end and player lost
     * 2 if game should end and player won, -1 if game should continue
     * @return end
     */
    private int endGame()
    {
        return end;
    }

    /**
     * Runs a game of tetris, restarting while end = 0 (end game + restart) and ending the session
     * when player wins or loses
     * @param args :(
     */
    public static void main(String[] args) 
    {
        try
        {
            AudioInputStream ais;
            Clip clip;
            Tetris t = new Tetris(true, true);
            do 
            {
                File song = new File("theme.wav");
                if(song.exists())
                {
                    ais = AudioSystem.getAudioInputStream(song);
                    
                    clip = AudioSystem.getClip();
                    clip.close();
                    clip.setFramePosition(0);
                    clip.open(ais);
                }
                else
                    throw new FileNotFoundException();
                
                t = new Tetris(t.getColor(), t.getControls()); 
                clip.setFramePosition(0);
                clip.loop(clip.LOOP_CONTINUOUSLY);
                clip.start();
                while(t.endGame()==-1)
                    t.play();       
                clip.stop();   
            }
            while (t.endGame()==0);
            if(t.endGame()==1)
                System.out.println("You lose");
            else if(t.endGame()==2)
                System.out.println("You win");
        }
        catch (LineUnavailableException e) 
        {
            e.printStackTrace();
        } 
        catch (UnsupportedAudioFileException e) 
        {
            e.printStackTrace();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
}
