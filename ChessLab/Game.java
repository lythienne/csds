import java.awt.Color;
/**
 * Game class runs a game of chess, initializes the board of pieces and has 2 players that each make
 * moves in a turn
 * @author Harrison Chen
 * @version 3/29/23
 */
public class Game 
{
    private static boolean endGame = false;
    /**
     * Runs a game of tetris, initializes board and players and then starts the game
     * @param args
     */
    public static void main(String[] args) 
    {
        Board board = new Board();
        int[][] init = //1=pawn, 2=bishop, 3=knight, 4=rook, 5=queen, 6=king; - is black, + is white
        {
            {-4,-3,-2,-5,-6,-2,-3,-4},
            {-1,-1,-1,-1,-1,-1,-1,-1},
            { 0, 0, 0, 0, 0, 0, 0, 0},
            { 0, 0, 0, 0, 0, 0, 0, 0},
            { 0, 0, 0, 0, 0, 0, 0, 0},
            { 0, 0, 0, 0, 0, 0, 0, 0},
            { 1, 1, 1, 1, 1, 1, 1, 1},
            { 4, 3, 2, 5, 6, 2, 3, 4},
        };
        for(int r=0; r<init.length; r++)
            for(int c=0; c<init[r].length; c++)
            {
                Piece piece;
                Color color = Color.BLACK;
                String colorStr = "black";
                if(init[r][c]>0) 
                {
                    color = Color.WHITE;
                    colorStr = "white";
                }
                switch(Math.abs(init[r][c]))
                {
                    case 1: 
                        piece = new Pawn(color, colorStr+"_pawn.gif");
                        break;
                    case 2: 
                        piece = new Bishop(color, colorStr+"_bishop.gif");
                        break;
                    case 3:
                        piece = new Knight(color, colorStr+"_knight.gif");
                        break;
                    case 4: 
                        piece = new Rook(color, colorStr+"_rook.gif");
                        break;
                    case 5:
                        piece = new Queen(color, colorStr+"_queen.gif");
                        break;
                    case 6: 
                        piece = new King(color, colorStr+"_king.gif");
                        break;
                    default: piece = null;
                }
                if(piece!=null)
                    piece.putSelfInGrid(board, new Location(r, c));
                    
            }

        BoardDisplay display = new BoardDisplay(board);

        Player w = new HumanPlayer(board, Color.WHITE, "white", display);
        Player b = new SmartPlayer(board, Color.BLACK, "black");

        play(board, display, w, b);
    }

    /**
     * Plays a turn of this game, asking a player for a move and executing that move and displaying it
     * on the board
     * @param board the chess game board the turn is played on
     * @param display to display the board and the move played
     * @param player the player to make the move
     */
    private static void nextTurn(Board board, BoardDisplay display, Player player)
    {
        display.setTitle(player.getName()+" to move");
        Move move = player.nextMove();
        if(move!=null)
        {
            board.executeMove(move);
            int row = 0;
            String colorStr = "white";
            if(player.getColor() == Color.BLACK) 
            {
                row = 7;
                colorStr = "black";
            }
            if(move.getPiece() instanceof Pawn && move.getDestination().getRow() == row)
            {
                new Queen(player.getColor(), colorStr+"_queen.gif").putSelfInGrid(board, move.getDestination());
            }
            display.clearColors();
            display.setColor(move.getSource(), Color.PINK);
            display.setColor(move.getDestination(), Color.PINK);
        }
        else
        {
            display.setTitle(player.getName()+" loses");
            endGame = true;
        }
    }

    /**
     * Plays a game of chess, repeatedly asking each player for a move, playing those moves, and
     * displaying them on the board. Ends when one player is checkmated
     * @param board the chess game board the turn is played on
     * @param display to display the board and the move played
     * @param white the white player
     * @param black the black player
     */
    public static void play(Board board, BoardDisplay display, Player white, Player black)
    {
        while(!endGame)
        {
            nextTurn(board, display, white);
            if(!endGame)
                nextTurn(board, display, black);
        }
    }
}
