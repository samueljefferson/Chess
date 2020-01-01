package chess;

/**
 * Used in pawns to unset them after their opponent has made a move.
 * Specifically for En pessant
 * 
 * @author James Beetham
 * @author Samuel Jefferson
 */
public interface FirstTurnAfterMove extends HasMoved {
	/**
	 * Get whether this piece is in the state of having been moved last turn.
	 * 
	 * @return true if this piece has just been moved the prior turn
	 */
	public boolean isFirstTurnAfterMove();

	/**
	 * Set first turn after move to be true.
	 * 
	 * @param previous if this is true, it won't set due to being unable to set first turn after move twice in a row
	 * @return true if successfully set
	 */
	public boolean setIsFirstTurnAfterMove(boolean previous);

	/**
	 * Unsets the variable (to false)
	 */
	public void unsetFirstTurnAfterMove();
}
