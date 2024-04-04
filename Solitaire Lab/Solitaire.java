import java.util.*;

/**
 * Solitaire class runs a game of Solitaire it implements stacks for its stock, waste,
 * foundations, piles, and undo list and uses a Solitaire Display to display its boardstate
 * @author Harrison Chen
 * @version 11/9/22
 */
public class Solitaire
{
    /**
     * Runs the solitaire game
     * @param args .
     */
    public static void main(String[] args)
    {
        new Solitaire();
    }

    private Stack<Card> stock;
    private Stack<Card> waste;
    private Stack<Card>[] foundations;
    private Stack<Card>[] piles;
    private SolitaireDisplay display;
    private Stack<Move> undo;

    /**
     * Creates a new Solitaire game with a stacks for stock, waste, foundations, and piles.
     * Deals cards out in the form of a normal Solitaire game.
     * Creates a stack to hold moves to undo
     * Creates a SolitaireDisplay to display the game
     */
    public Solitaire()
    {
        stock = new Stack<Card>();
        createStock();

        waste = new Stack<Card>();
        
        foundations = new Stack[4];
        for (int i=0; i<foundations.length; i++)
            foundations[i] = new Stack<Card>();

        piles = new Stack[7];
        for (int i=0; i<piles.length; i++)
            piles[i] = new Stack<Card>();

        deal();
        undo = new Stack<Move>();

    	display = new SolitaireDisplay(this);
        
        /* to check win
        for (Stack<Card> s : foundations)
        {
            s.push(new Card(13, "s"));
            s.peek().turnUp();
        }*/
    }   
    
    /**
     * Returns the card on top of the stock 
     * or null if the stock is empty
     * @return if stock is empty return null, otherwise stock.peek()
     */
    public Card getStockCard()
    {
        if (stock.isEmpty())
            return null;
        return stock.peek();
    }

	/**
     * Returns the card on top of the waste 
     * or null if the waste is empty
     * @return if waste is empty return null, otherwise waste.peek()
     */
    public Card getWasteCard()
    {
        if (waste.isEmpty())
            return null;
        return waste.peek();
    }

    /**
     * Returns the card on top of a specified foundation 
     * or null if that foundation is empty
     * @precondition  0 <= index < 4
     * @param index the index of the foundation
     * @return if foundation is empty return null, otherwise foundation[index].peek()
     */
    public Card getFoundationCard(int index)
    {
        if (foundations[index].isEmpty())
            return null;
        return foundations[index].peek();
    }

    /**
     * Returns the pile at an index
     * @precondition  0 <= index < 7
     * @param index the index of the pile
     * @return piles[index]
     */
    public Stack<Card> getPile(int index)
    {
    	return piles[index];
    }

	/**
     * Deals three cards when the stock is clicked
     * if the stock is null, flip the waste into the stock instead
     */
    public void stockClicked()
    {
        if(!display.isWasteSelected())
        {
            if(stock.isEmpty())
                resetStock();
            else
                dealThreeCards();
            undo.push(null);
        }
        display.unselect();
    }

	/**
     * If the waste is selected, unselect the waste
     * otherwise, selects the waste
     */
    public void wasteClicked()
    {
    	if (!display.isPileSelected())
        {
            if(display.isWasteSelected())
                display.unselect();
            else
                display.selectWaste();
        }
    }

    /**
     * Moves a card from the selected stack to the clicked foundation if legal
     * unselects the stack
     * @precondition  0 <= index < 4
     * @param index the index of the foundation clicked
     */
    public void foundationClicked(int index)
    {
        Stack<Card> selectedStack = null;
        if(display.isWasteSelected())
            selectedStack = waste;
        else if(display.isPileSelected())
            selectedStack = piles[display.selectedPile()];
    	if(selectedStack != null && canAddToFoundation(selectedStack.peek(), index))
        {
            moveCard(foundations[index], selectedStack);
            display.unselect();
            undo.push(null);
        }
        else if(!foundations[index].isEmpty())
            display.selectFoundation(index);
        else
            display.unselect();
    }

    /**
     * Moves a card or a set of face up cards from the waste or a pile 
     * to the clicked pile if legal, unselects the stack moved from
     * Turns the top card of the clicked pile face up if it is face down
     * Selects the pile otherwise
     * @precondition 0 <= index < 7
     * @param index the index of the pile clicked
     */
    public void pileClicked(int index)
    {
    	if(display.isWasteSelected() && canAddToPile(getWasteCard(), index))
    	{
            moveCard(piles[index], waste);
            display.unselect();
            undo.push(null);
        }
        else if(display.isPileSelected())
        {
            int selectedPileIndex = display.selectedPile();

            if(!(selectedPileIndex == index))
            {
                Stack<Card> removedCards = removeFaceUpCards(selectedPileIndex);
                //System.out.println(removedCards);
                while(!removedCards.isEmpty())
                {
                    if(canAddToPile(removedCards.peek(), index))
                        moveCard(piles[index], removedCards);
                    else
                        moveCard(piles[selectedPileIndex], removedCards);
                }
                display.unselect();    
                undo.push(null);
            }
            else
                display.unselect();
        }
        else if(display.isFoundationSelected())
        {
            int selectedIndex = display.selectedFoundation();

            if(canAddToPile(foundations[selectedIndex].peek(), index))
                moveCard(piles[index], foundations[selectedIndex]);
            display.unselect();    
            undo.push(null);
        }
        else if(!piles[index].isEmpty())
        {
            if(!piles[index].peek().isFaceUp())
                piles[index].peek().turnUp();
            else
                display.selectPile(index);
        }
        
    }

    /**
     * Undoes a move when the undo (uno reverse) card is clicked
     * If there are no moves to undo, displays an error.
     */
    public void undoClicked()
    {
        if(undo.isEmpty())
            display.showError(true);
        else if (undo.peek()==null)
        {
            undo.pop();
            while(!undo.isEmpty() && undo.peek()!=null)
            {
                undo.pop().doMove();
                display.setBackground("undo");
            }
        }
        display.unselect();
    }

    /**
     * Restarts the game
     */
    public void newGameClicked()
    {
        display.unselect();
        new Solitaire();
    }

    /**
     * Swaps the theme
     */
    public void themeSwapClicked()
    {
        if(display.getTheme().equals("sussy"))
            display.setTheme("lucina");
        else
            display.setTheme("sussy");
    }

    /**
     * Checks to see if the player has won if all four foundations
     * have kings on top
     * @return true if player has won, false otherwise
     */
    public boolean checkWin()
    {
        try
        {
            for (Stack<Card> s : foundations)
            {
                if(s.peek().getRank()!=13)
                    return false;
            }
            return true;
        }
        catch(Exception e)
        {
            return false;
        }
    }

    /**
     * Creates the stock by making a standard deck of 52 cards and randomly adding
     * them to the stock
     */
    public void createStock()
    {
        ArrayList<Card> cards = new ArrayList<Card>();
        for (int i=1; i<=13; i++)
        {
            cards.add(new Card(i, "s"));
            cards.add(new Card(i, "h"));
            cards.add(new Card(i, "c"));
            cards.add(new Card(i, "d"));
        }
        while(cards.size()>0)
            stock.push(cards.remove((int) (Math.random() * cards.size()-1)));
    }

    /**
     * Deals cards from the stock into the piles with the standard Solitaire method
     * turns all top cards face up
     */
    public void deal()
    {
        for(int i=6; i>=0; i--)
        {
            for(int j=0; j<=i; j++)
            {
                moveCard(piles[j], stock);
                piles[j].peek().turnDown();
            }
            piles[i].peek().turnUp();
        }
    }

    /**
     * Deals 3 cards from the stock to the waste
     */
    public void dealThreeCards()
    {
        for(int i=0; i<3; i++)
        {
            if(!stock.isEmpty())
            {
                moveCard(waste, stock);
            }
        }
        undo.push(null);
    }

    /**
     * Moves the top card from a stack to the top of another stack
     * @param to the stack moved to
     * @param from the stack moved from
     */
    public void moveCard(Stack<Card> to, Stack<Card> from)
    {
        Card movedCard = from.pop();
        if(to.equals(stock))
            movedCard.turnDown();
        else
            movedCard.turnUp();
        to.push(movedCard);

        if(undo!=null)
            undo.push(new Move(from, to));
    }

    /**
     * Resets the stock by flipping all cards from the waste into the stock
     */
    public void resetStock()
    {
        while(!waste.isEmpty())
        {
            moveCard(stock, waste);
        }
    }

    /**
     * Returns true if the given card can be legally moved to the top of the given pile
     * @precondition: 0 <= index < 7
     * @param card the card to be moved
     * @param index the index of the pile to be moved to
     * @return true if the card is a king and the pile is empty or
     *         if card's color is opposite the pile's card's color and the rank is one less
     *         false otherwise
     */
    private boolean canAddToPile(Card card, int index)
    {
        /*System.out.println("card:"+card+" which is red: "+card.isRed()
                +" index:"+index+" adding to: "+piles[index].peek()
                +" which is red: "+piles[index].peek().isRed());*/
        if (piles[index].isEmpty() && card.getRank()!=13)
            return false;
        return (piles[index].isEmpty() && card.getRank()==13)
            || piles[index].peek().isFaceUp()
            && piles[index].peek().getRank()-1 == card.getRank()
            && piles[index].peek().isRed() != card.isRed();
    }

    /**
     * Returns true if the given card can be legally moved to the top of the given foundation
     * @precondition: 0 <= index < 4
     * @param card the card to be moved
     * @param index the index of the foundation to be moved to
     * @return true if the card is an ace and the foundation is empty or
     *         if card's suit is the same as the foundation's card's suit and the rank is one more
     *         false otherwise
     */
    private boolean canAddToFoundation(Card card, int index)
    {
        if (foundations[index].isEmpty() && card.getRank()!=1)
            return false;
        return (foundations[index].isEmpty() && card.getRank()==1)
            || foundations[index].peek().getRank()+1 == card.getRank()
            && foundations[index].peek().getSuit() == card.getSuit();
    }

    /**
     * Removes all face-up cards on the top of the given pile; 
     * returns a stack containing these cards
     * @precondition:  0 <= index < 7
     * @param index the index of the pile removed from
     * @return a Stack<Card> containing all the removed cards in reverse order
     */
    private Stack<Card> removeFaceUpCards(int index)
    {
        Stack<Card> faceUpCards = new Stack<Card>();
        while (!piles[index].isEmpty() && piles[index].peek().isFaceUp())
        {
            moveCard(faceUpCards, piles[index]);
        }
        return faceUpCards;
    }

    /** not using this method because I implemented moving part pile moving
     * @precondition: 0 <= index < 7
     * postcondition: Removes elements from cards, and adds // them to the given pile.
     */
    /*private void addToPile(Stack<Card> cards, int index)
    {
        while(!cards.isEmpty())
        {
            piles[index].push(cards.pop());
        }
    }
    */

    /**
     * Move class is one card move from a pile to a pile
     * used to implement undo
     * @author Harrison Chen
     * @version 11/9/22
     */
    public class Move 
    {
    
        private Stack<Card> to;
        private Stack<Card> from;
        private boolean wasFromFaceUp;
        
        /**
         * Creates a new move from a stack moved from and to
         * @param to the stack to move to
         * @param from the stack to move from
         */
        public Move(Stack<Card> to, Stack<Card> from)
        {
            this.to = to;
            this.from = from;
            //checks to because when undoing from and to are swapped
            if(!to.isEmpty())
                wasFromFaceUp = to.peek().isFaceUp();
            else
                wasFromFaceUp = false;
        }
        
        /**
         * Executes this move and removes it from the undo list
         */
        public void doMove()
        {
            //turns top card of the pile its moving to face down IF 
            //1. it is the last move in the undo (for multi-move undos like pile->pile)
            //2. the pile its moving to is not empty
            //3. the card on top of the pile is face up
            //if ((undo.isEmpty() || undo.peek()==null)
            //        && !to.isEmpty() && to.peek().isFaceUp())
            if(!to.isEmpty() && !wasFromFaceUp)
            {
                for(Stack<Card> p : piles)
                {
                    if(to.equals(p))
                        to.peek().turnDown();
                }
            }
            moveCard(to, from);
            //does this because moveCard automatically adds to undo
            undo.pop();
        }
    }
}