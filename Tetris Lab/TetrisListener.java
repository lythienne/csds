/**
 * DO NOT MODIFY THIS CODE
 * There are no user serviceable components.  Any changes will void your warranty.
 * 
 * I modified the code
 * Made better method names + included all actions a tetris game should have, not just moving up/down,
 * soft dropping, and rotating one direction
 * @author Harrison Chen
 * @version 3/9/23
 */
public interface TetrisListener
{
    void hardDrop();
	void softDrop();
	void left();
	void right();
    void turnClockwise();
    void turnCounterCW();
    void turn180();
    void hold();
    void restart();

    void SDReleased();
    void leftReleased();
    void rightReleased();

    void swapColor();
    void swapControls();
}