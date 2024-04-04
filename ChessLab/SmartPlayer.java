import java.awt.Color;
import java.util.*;
/**
 * SmartPlayer class represents a chess player, it has a chess board, color, and name that it can return
 * Its next move will be decided by a 3 level deep simplified minimax algorithm
 * @author Harrison Chen
 * @version 3/29/23
 */
public class SmartPlayer extends Player
{
    /**
     * Constructs a new player with a chess board, color, and name
     * @param b the chess board
     * @param c the color of the player
     * @param n the name of the player
     */
    public SmartPlayer(Board b, Color c, String n)
    {
        super(b, c, n);
    }

    /**
     * Returns the score of this board state from the perspective of this smart player
     * @return the total value of the pieces of this player - total value of the pieces of opponent
     */
    public int score()
    {
        int score = 0;
        Board board = getBoard();
        for(Location l: board.getOccupiedLocations())
        {
            Piece piece = board.get(l);
            if(piece != null)
            {
                if(piece.getColor() == getColor())
                    score += piece.getValue();
                else
                    score -= piece.getValue();
            }
        }
        return score;
    }

    /**
     * Returns the value of the worst repsonse an opponent could make at the current boardstate
     * @return the minimum scored move that the opponent can make in this boardstate
     */
    private int valueOfWorstResponse(int deep)
    {
        if(deep == 0)
            return score();
        Color c = Color.BLACK;
        if(getColor()==Color.BLACK) c = Color.WHITE;
        ArrayList<Move> moves = getBoard().allMoves(c);
        int minScore = Integer.MAX_VALUE;
        for(Move m : moves)
        {
            getBoard().executeMove(m);
            //display.showBoard();
            int score = valueOfBestMove(deep-1);
            if(score<minScore)
            {
                minScore = score;
            }
            getBoard().undoMove(m);
        }
        return minScore;
    }

    /**
     * Returns the value of the best move the player could make at the current boardstate
     * @return the maximum scored move that the player can make in this boardstate
     */
    private int valueOfBestMove(int deep)
    {
        if(deep == 0)
            return score();
        ArrayList<Move> moves = getBoard().allMoves(getColor());
        int maxScore = Integer.MIN_VALUE;
        for(Move m : moves)
        {
            getBoard().executeMove(m);
            int score = valueOfWorstResponse(deep-1);
            if(score>maxScore)
            {
                maxScore = score;
            }
            getBoard().undoMove(m);
        }
        return maxScore;
    }

    /**
     * Returns the best next move for this player based on calculated score of the next turn
     * null if no legal moves (checkmated)
     */
    public Move nextMove()
    {
        ArrayList<Move> moves = getBoard().allMoves(getColor());
        if(moves.size()==0)
            return null;
        Move bestMove = moves.get(0);
        int bestWorst = Integer.MIN_VALUE;
        for(Move m : moves)
        {
            getBoard().executeMove(m);
            int worst = valueOfWorstResponse(3);
            if(worst>bestWorst)
            {
                bestWorst = worst;
                bestMove = m;
            }
            getBoard().undoMove(m);
        }
        return bestMove;
    }
}

