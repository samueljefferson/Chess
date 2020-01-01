package chess;

/**
 * Used for pieces which care about whether they have moved or not (like king, pawn, and rook).
 * 
 * @author James Beetham
 * @author Samuel Jefferson
 */
public interface HasMoved {
	/**
	 * Sets hasmoved to true.
	 */
	public void setMoved();
	
	/**
	 * Whether this piece has moved or not.
	 * 
	 * @return false if they have not yet moved
	 */
	public boolean hasMoved();
}
