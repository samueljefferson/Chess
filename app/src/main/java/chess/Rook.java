package chess;

import java.util.List;

/**
 * Implementation of Piece class representing the rook.
 * 
 * @author James Beetham
 * @author Samuel Jefferson
 */
public class Rook extends Piece implements HasMoved {
	/**
	 * Whether this piece has moved or not.
	 */
	private boolean moved = false;
	
	/**
	 * Constructs a new rook (name = "R").
	 * 
	 * @param color what color the piece is (usually "Black" or "White")
	 */
	public Rook(String color) {
		super(color, "R");
	}

	@Override
	public List<Coords> getPossibleMoves(Board b, int r, int c) {
		return Piece.getValidStraightMoves(b, this, r, c);
	}

	@Override
	public Coords isValidMove(Board b, int r, int c, int endR, int endC) {
		return Piece.isValidStraightMove(b, this, r, c, endR, endC);
	}

	@Override
	public void setMoved() {
		this.moved = true;
	}

	@Override
	public boolean hasMoved() {
		return this.moved;
	}
}
