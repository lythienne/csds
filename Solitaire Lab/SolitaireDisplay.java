import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

/**
 * SolitaireDisplay class displays a game of solitaire using javax and mouse listener
 * @author Harrison Chen
 * @version 11/9/22
 */
public class SolitaireDisplay extends JComponent implements MouseListener
{
    private static final int CARD_WIDTH = 73;
    private static final int CARD_HEIGHT = 97;
    private static final int SPACING = 5;  //distance between cards
    private static final int FACE_UP_OFFSET = 15;  //distance for cascading face-up cards
    private static final int FACE_DOWN_OFFSET = 5;  //distance for cascading face-down cards

    private JFrame frame;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private Solitaire game;

    private String background;
    private boolean error;
    private String theme;

    /**
     * Creates a new Solitaire display, sets the background to random and not showing an error
     * Creates a frame and sets it to be visible
     * @param game the game of Solitaire to display
     */
    public SolitaireDisplay(Solitaire game)
    {
        background = "";
        error = false;
        theme = "sussy";

    	this.game = game;   
		
        frame = new JFrame("Sussy Solitaire");
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.getContentPane().add(this);

    	this.setPreferredSize(new Dimension(CARD_WIDTH * 7 + SPACING * 8, CARD_HEIGHT 
                * 3 + SPACING * 0 + FACE_DOWN_OFFSET * 7 + 13 * FACE_UP_OFFSET));
    	this.addMouseListener(this);

    	frame.pack();
    	frame.setVisible(true);
    }

    /**
     * Paints the screen of Solitaire including its background image, stock, waste, 
     * foundations, piles, undo button, error messages, and a winscreen if the player has won
     * @param g the Graphics object used to draw the screen
     */
    public void paintComponent(Graphics g)
    {
        if(background.equals("undo"))
            drawBackground(g, "impostor");
        else
            drawBackground(g, String.valueOf((int)(Math.random()*5)+1));
        background = "";

		//stock
    	drawCard(g, game.getStockCard(), SPACING, SPACING); 

		//waste
        drawCard(g, game.getWasteCard(), SPACING * 2 + CARD_WIDTH, SPACING);
        if (selectedRow == 0 && selectedCol == 1)
			drawBorder(g, SPACING * 2 + CARD_WIDTH, SPACING);

		//foundations
        for (int i = 0; i < 4; i++)
        {
            drawCard(g, game.getFoundationCard(i), SPACING 
                    * (4 + i) + CARD_WIDTH * (3 + i), SPACING);
            if (selectedRow == 0 && selectedCol == 3+i)
                drawBorder(g, SPACING + (CARD_WIDTH + SPACING) 
                        * (3+i), SPACING);
        }

		//piles
        for (int i = 0; i < 7; i++)
        {
            Stack<Card> pile = game.getPile(i);
            int offset = 0;
            for (int j = 0; j < pile.size(); j++)
            {
            	drawCard(g, pile.get(j), SPACING + (CARD_WIDTH + SPACING) 
                        * i, CARD_HEIGHT + 2 * SPACING + offset);
            	if (selectedRow == 1 && selectedCol == i && j == pile.size() - 1)
            		drawBorder(g, SPACING + (CARD_WIDTH + SPACING) 
                            * i, CARD_HEIGHT + 2 * SPACING + offset);

                if (pile.get(j).isFaceUp())
					offset += FACE_UP_OFFSET;
                else
					offset += FACE_DOWN_OFFSET;
            }
        }

        //undo button (shaped like a card)
        Image undo = new ImageIcon("cards/undo.png").getImage();
        g.drawImage(undo, SPACING, CARD_HEIGHT*4 + SPACING*4, 
                CARD_WIDTH, CARD_HEIGHT, null);
        
        //newgame button (also shaped like a card)
        Image newgame = new ImageIcon(theme+"/newgame.png").getImage();
        g.drawImage(newgame, SPACING*1+CARD_WIDTH*1, CARD_HEIGHT*4 + SPACING*4, 
                CARD_WIDTH+2*SPACING, CARD_HEIGHT, null);

        //theme swap button (also shaped like a card)
        Image themeSwap = new ImageIcon(theme+"/themeSwap.png").getImage();
        g.drawImage(themeSwap, SPACING*6+CARD_WIDTH*6, CARD_HEIGHT*4 + SPACING*4, 
                CARD_WIDTH+SPACING, CARD_HEIGHT, null);

        //displays error message
        if(error)
        {
            displayError(g);
            error = false;
        }

        //displays win screen
        if(game.checkWin())
        {
            Image winScreen = new ImageIcon(theme+"/win.png").getImage();
            g.drawImage(winScreen, 0, 105, 550, 300, null);
        }
    }

    /**
     * Draws a card based on its filename at x, y
     * @param g the Graphics object used to draw the screen
     * @param card the card to be drawn
     * @param x the x coordinate of the top left corner of the card
     * @param y the y coordinate of the top left corner of the card
     */
    private void drawCard(Graphics g, Card card, int x, int y)
    {
    	if (card == null)
    	{
            g.setColor(Color.BLACK);
            g.drawRect(x, y, CARD_WIDTH, CARD_HEIGHT);
            g.setColor(new Color(0, 128, 0, 100));
    	    g.fillRect(x, y, CARD_WIDTH, CARD_HEIGHT);
    	}
        else
        {   
            String fileName;
            if(!card.isFaceUp())
                fileName=theme+"/back.png";
            else
                fileName = card.getFileName();
            if (!new File(fileName).exists())
            	throw new IllegalArgumentException("bad file name:  " + fileName);
            Image image = new ImageIcon(fileName).getImage();
            g.drawImage(image, x, y, CARD_WIDTH, CARD_HEIGHT, null);
    	}
    }

    /**
     * Returns the current theme
     * @return theme
     */
    public String getTheme()
    {
        return theme;
    }

    /**
     * Sets the theme to a new theme
     * @param newTheme the new theme
     */
    public void setTheme(String newTheme)
    {
        theme = newTheme;
    }

    /**
     * Sets the background to a new background
     * @param newBackground the new background
     */
    public void setBackground(String newBackground)
    {background = newBackground;}

    /**
     * Sets the error status to a new error status
     * @param yes true if you want to display the error, false otherwise
     */
    public void showError(boolean yes)
    {error = yes;}

    /**
     * Draws the background based on a string
     * @param g the Graphics object used to draw the screen
     * @param name the name of the background to be drawn
     */
    public void drawBackground(Graphics g, String name)
    {
        String fileName = theme+"/bg-"+name+".jpg";
        if (!new File(fileName).exists())
            throw new IllegalArgumentException("bad file name:  " + fileName);
        Image logo = new ImageIcon(fileName).getImage();
        g.drawImage(logo, 0, 0, getWidth(), getHeight(), null);
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
    
    /**
     * Detects a mouse click and determines what it clicked based 
     * on the location of the mouse click
     * @param e the mouse click
     */
    public void mouseClicked(MouseEvent e)
    {
    	//none selected previously
    	int col = e.getX() / (SPACING + CARD_WIDTH);
    	int row = e.getY() / (SPACING + CARD_HEIGHT);
    	if (row > 1 && row!=4)
    		row = 1;
    	if (col > 6)
    		col = 6;

    	if (row == 0 && col == 0)
    		game.stockClicked();
    	else if (row == 0 && col == 1)
			game.wasteClicked();
    	else if (row == 0 && col >= 3)
    		game.foundationClicked(col - 3);
    	else if (row == 1)
    		game.pileClicked(col);
        else if (row==4 && col==0)
            game.undoClicked();
        else if (row==4 && col==1)
            game.newGameClicked();
        else if (row==4 && col==6)
            game.themeSwapClicked();
    	repaint();
    }

    /**
     * Draws a border around a card when the card is selected
     * @param g the Graphics object used to draw the screen
     * @param x the x coordinate of the top left corner of the border
     * @param y the y coordinate of the top left corner of the border
     */
    private void drawBorder(Graphics g, int x, int y)
    {
    	g.setColor(Color.YELLOW);
    	g.drawRect(x, y, CARD_WIDTH, CARD_HEIGHT);
    	g.drawRect(x + 1, y + 1, CARD_WIDTH - 2, CARD_HEIGHT - 2);
    	g.drawRect(x + 2, y + 2, CARD_WIDTH - 4, CARD_HEIGHT - 4);
    }

    /**
     * Unselects a card
     */
    public void unselect()
    {
    	selectedRow = -1;
    	selectedCol = -1;
    }

    /**
     * Returns whether the waste is selected or not
     * @return true if waste is selected, false otherwise
     */
    public boolean isWasteSelected()
    {
    	return selectedRow == 0 && selectedCol == 1;
    }

    /**
     * Selects the waste
     */
    public void selectWaste()
    {
    	selectedRow = 0;
    	selectedCol = 1;
    }

    /**
     * Returns whether the pile is selected or not
     * @return true if pile is selected, false otherwise
     */
    public boolean isPileSelected()
    {
    	return selectedRow == 1;
    }

    /**
     * Returns the index of the selected pile
     * @return index of the selected pile
     */
    public int selectedPile()
    {
    	if (selectedRow == 1)
			return selectedCol;
    	else
    		return -1;
    }

    /**
     * Selects a pile based on its index
     * @param index the index of the pile to be selected
     */
    public void selectPile(int index)
    {
    	selectedRow = 1;
    	selectedCol = index;
    }

    /**
     * Returns whether the foundation is selected or not
     * @return true if foundation is selected, false otherwise
     */
    public boolean isFoundationSelected()
    {
    	return selectedRow == 0 && selectedCol>=3;
    }

    /**
     * Returns the index of the selected pile
     * @return index of the selected pile
     */
    public int selectedFoundation()
    {
    	if (selectedRow == 0)
			return selectedCol-3;
    	else
    		return -1;
    }

    /**
     * Selects a foundation based on its index
     * @param index the index of the foundation to be selected
     */
    public void selectFoundation(int index)
    {
    	selectedRow = 0;
    	selectedCol = 3+index;
    }

    /**
     * Displays the error message on screen
     * @param g the Graphics object used to draw the screen
     */
    public void displayError(Graphics g)
    {
        String fileName = theme+"/error.jpg";
        if (!new File(fileName).exists())
            throw new IllegalArgumentException("bad file name:  " + fileName);
        Image error = new ImageIcon(fileName).getImage();
        g.drawImage(error, 100, 75, 220, 170, null);
    }
}