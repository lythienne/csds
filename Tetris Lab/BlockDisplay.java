import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.text.DecimalFormat;
/**
 * @author Anu Datar
 * 
 * Changed block size and added a split panel display for next block and Score
 * 
 * @author Ryan Adolf
 * @version 1.0
 * 
 * Fixed the lag issue with block rendering 
 * Removed the JPanel
 * 
 * Added different control settings because my controls are better :D
 * Added mouselistener + buttons for changing color scheme and control scheme
 * @author Harrison Chen
 * @version 3/11/23
 */
// Used to display the contents of a game board
public class BlockDisplay extends JComponent implements KeyListener, MouseListener
{
    private static final Color BACKGROUND = Color.BLACK;
    private static final Color BORDER = Color.DARK_GRAY;

    private static final int OUTLINE = 1;
    private static final int BLOCKSIZE = 20;
    private static final int PADDING = 10;
    private static final int HORIGAP = PADDING*2+BLOCKSIZE*4;
    private static final int VERTGAP = PADDING;
    
    private static final int BUTTONSIZE = 40;

    private MyBoundedGrid<Block> board;
    private MyBoundedGrid<Block> hold;
    private MyBoundedGrid<Block> next;
    private JFrame frame;
    private TetrisListener listener;
    private boolean defaultControls;
    private boolean originalColor;
    private int time;
    private int linesLeft;
    private int pieces;
    private int status;

    /**
     * Constructs a new display for displaying a given board
     * @param board the board of blocks
     * @param defaultControls the control settings for tetris game
     */
    public BlockDisplay(MyBoundedGrid<Block> board, MyBoundedGrid<Block> hold, 
                            MyBoundedGrid<Block> next, boolean defaultControls, boolean originalColor)
    {
        this.board = board;
        this.hold = hold;
        this.next = next;
        this.defaultControls = defaultControls;
        this.originalColor = originalColor;

        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    createAndShowGUI();
                }
            });

        //Wait until display has been drawn
        try
        {
            while (frame == null || !frame.isVisible())
                Thread.sleep(1);
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private void createAndShowGUI()
    {
        //Create and set up the window.
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(this);
        frame.addKeyListener(this);
        this.addMouseListener(this);

        //Display the window.
        this.setPreferredSize(new Dimension(
                2*HORIGAP + BLOCKSIZE * (board.getNumCols()),
                2*VERTGAP + BLOCKSIZE * (board.getNumRows()-1)
            ));

        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Paints the board to include the pieces and border colors
     */
    public void paintComponent(Graphics g)
    {
        g.setColor(BACKGROUND);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(BORDER);
        g.fillRect(HORIGAP-OUTLINE, VERTGAP-OUTLINE, 
                BLOCKSIZE * board.getNumCols()+OUTLINE, BLOCKSIZE * (board.getNumRows()-1)+OUTLINE);
        for (int row = 0; row < board.getNumRows()-1; row++) //board
            for (int col = 0; col < board.getNumCols(); col++)
            {
                Location loc = new Location(row+1, col);

                Block square = board.get(loc);

                int offset = OUTLINE;

                if (square == null)
                    g.setColor(BACKGROUND);
                else
                {
                    g.setColor(square.getColor());
                    offset = 0;
                }    
                g.fillRect(HORIGAP + col * BLOCKSIZE + offset/2, VERTGAP + row * BLOCKSIZE + offset/2,
                            BLOCKSIZE - offset, BLOCKSIZE - offset);
            }
        for (int row = 0; row < hold.getNumRows(); row++) //hold
            for (int col = 0; col < hold.getNumCols(); col++)
            {
                Location loc = new Location(row, col);

                Block square = hold.get(loc);

                if (square == null)
                    g.setColor(BACKGROUND);
                else
                    g.setColor(square.getColor());  
                g.fillRect(PADDING + col * BLOCKSIZE, (row+1) * BLOCKSIZE, BLOCKSIZE, BLOCKSIZE);
            }
        for (int row = 0; row < next.getNumRows(); row++) //next
            for (int col = 0; col < next.getNumCols(); col++)
            {
                Location loc = new Location(row, col);

                Block square = next.get(loc);

                if (square == null)
                    g.setColor(BACKGROUND);
                else
                    g.setColor(square.getColor());  
                g.fillRect(PADDING + HORIGAP + BLOCKSIZE*board.getNumCols()+ col * BLOCKSIZE, 
                    (row+1) * BLOCKSIZE, BLOCKSIZE, BLOCKSIZE);
            }
        g.setColor(Color.LIGHT_GRAY);
        g.setFont(new Font("COMIC_SANS_MS", Font.PLAIN, 20)); 
        g.drawString(timeToString(time), 5, 300);
        g.setFont(new Font("COMIC_SANS_MS", Font.PLAIN, 60)); 
        g.drawString(""+linesLeft, PADDING, 380);

        //buttons
        String off = "";
        if(!originalColor)
            off = "Off";
        Image color = new ImageIcon("icons/color"+off+".png").getImage();
        g.drawImage(color, 307, 390, BUTTONSIZE, BUTTONSIZE, null);
        off = "";
        if(!defaultControls)
            off = "Off";
        Image controls = new ImageIcon("icons/controls"+off+".png").getImage();
        g.drawImage(controls, 353, 390, BUTTONSIZE, BUTTONSIZE, null);

        if(status == 1) //lose
        {
            g.setColor(new Color(255, 0, 0, 85));
            g.fillRect(0, 0, getPreferredSize().width, getPreferredSize().height);
            g.setColor(Color.WHITE);
            g.setFont(new Font("COMIC_SANS_MS", Font.BOLD, 150)); 
            g.drawString("YOU", 22, 150);
            g.drawString("LOSE", 7, 300);
            g.setFont(new Font("COMIC_SANS_MS", Font.PLAIN, 40)); 
            g.drawString("Lines Left: "+linesLeft, 75, 380);
        }
        if(status == 2) //win
        {
            g.setColor(new Color(0, 0, 0, 90));
            g.fillRect(0, 0, getPreferredSize().width, getPreferredSize().height);
            g.setColor(Color.WHITE);
            g.setFont(new Font("COMIC_SANS_MS", Font.BOLD, 90)); 
            g.drawString(timeToString(time), 0,250);
            g.setFont(new Font("COMIC_SANS_MS", Font.PLAIN, 40)); 
            g.drawString("# "+pieces+" : "+new DecimalFormat("#.##").format(((double) pieces)/time*100)
                            +" pps", 50, 330);
        }
    }

    /**
     * Redraws the board to include the pieces and border colors.
     * @param time the time spent playing this game
     * @param linesLeft the number of lines left in the sprint
     */
    public void updateDisplay(int time, int linesLeft, int pieces, int gameStatus)
    {
        this.time = time;
        this.linesLeft = linesLeft;
        this.pieces = pieces;
        this.status = gameStatus;
        repaint();
    }

    /**
     * Converts a time in 10s of milliseconds into a string time in min:sec:milliseconds
     * @param time time in 10s of milliseconds
     * @return a string of "min:sec:millisec"
     */
    public String timeToString(int time)
    {
        int min = time/6000;
        int sec = (time%6000)/100;
        String min0 = "";
        String sec0 = "";
        String mil0 = "";
        if(min<10) min0 = "0";
        if(sec<10) sec0 = "0";
        if(time%100<10) mil0 = "0";
        return min0+min+":"+sec0+sec+":"+mil0+time%100;
    }
    /**
     * Setst the title of the window to a new title
     * @param title the new title
     */
    public void setTitle(String title)
    {
        frame.setTitle(title);
    }

    /**
     * Checks to see if a key is released, executes a method based on the key released based on the
     * keybinds
     * @param e the key event recorded
     */
    public void keyReleased(KeyEvent e)
    {
        if (listener == null)
            return;
        int code = e.getKeyCode();
        if(defaultControls)
        {
            if (code == KeyEvent.VK_LEFT)
                listener.leftReleased();
            else if (code == KeyEvent.VK_RIGHT)
                listener.rightReleased();
            else if (code == KeyEvent.VK_DOWN)
                listener.SDReleased();
        }
        else
        {
            if (code == KeyEvent.VK_J)
                listener.leftReleased();
            else if (code == KeyEvent.VK_L)
                listener.rightReleased();
            else if (code == KeyEvent.VK_K)
                listener.SDReleased();
        }
    }

    /**
     * Checks to see if a key is pressed, executes a method based on the key pressed and the
     * keybinds
     * @param e the key event recorded
     */
    public void keyPressed(KeyEvent e)
    {
        if (listener == null)
            return;
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_R)
            listener.restart();
        if(defaultControls)
        {
            if (code == KeyEvent.VK_LEFT)
                listener.left();
            else if (code == KeyEvent.VK_RIGHT)
                listener.right();
            else if (code == KeyEvent.VK_DOWN)
                listener.softDrop();
            else if (code == KeyEvent.VK_SPACE)
                listener.hardDrop();
            else if (code == KeyEvent.VK_UP)
                listener.turnClockwise();
            else if (code == KeyEvent.VK_X)
                listener.turnCounterCW();
            else if (code == KeyEvent.VK_Z)
                listener.turn180();
            else if (code == KeyEvent.VK_C)
                listener.hold();
        }
        else
        {
            if (code == KeyEvent.VK_J)
                listener.left();
            else if (code == KeyEvent.VK_L)
                listener.right();
            else if (code == KeyEvent.VK_K)
                listener.softDrop();
            else if (code == KeyEvent.VK_I)
                listener.hardDrop();
            else if (code == KeyEvent.VK_X)
                listener.turnClockwise();
            else if (code == KeyEvent.VK_Z)
                listener.turnCounterCW();
            else if (code == KeyEvent.VK_A)
                listener.turn180();
            else if (code == KeyEvent.VK_SPACE)
                listener.hold();
        }
    }

    /**
     * Detects a mouse click and determines what it clicked based 
     * on the location of the mouse click
     * @param e the mouse click
     */
    public void mouseClicked(MouseEvent e)
    {
    	int x = e.getX();
    	int y = e.getY();

    	if(x>=307 && x<=307+BUTTONSIZE && y>=390 && y<=390+BUTTONSIZE)
        {
            originalColor = !originalColor;
            listener.swapColor();
        }
        else if (x>=353 && x<=353+BUTTONSIZE && y>=390 && y<=390+BUTTONSIZE)
        {
            defaultControls = !defaultControls;
            listener.swapControls();
        }
    }

    /**
     * Sets the keylistener of this display to a new listener
     * @param listener the new listener
     */
    public void setListener(TetrisListener listener)
    {
        this.listener = listener;
    }

    //Unused
    /** @param e */
    public void keyTyped(KeyEvent e)
    {
    }
    /** @param e */
    public void mouseExited(MouseEvent e)
    {
    }
    /** @param e */
    public void mouseEntered(MouseEvent e)
    {
    }
    /** @param e */
    public void mouseReleased(MouseEvent e)
    {
    }
    /** @param e */
    public void mousePressed(MouseEvent e)
    {
    }
}
