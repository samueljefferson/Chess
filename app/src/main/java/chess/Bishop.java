package chess;

import java.util.List;

/**
 * Implementation of Piece class representing the bishop.
 * 
 * @author James Beetham
 * @author Samuel Jefferson
 */
public class Bishop extends Piece {
	/**
	 * Constructs a new bishop (name = "B").
	 * 
	 * @param color what color the piece is (usually "Black" or "White")
	 */
	public Bishop(String color) {
		super(color, "B");
	}

	@Override
	public List<Coords> getPossibleMoves(Board b, int r, int c) {
		return Piece.getValidDiagonalMoves(b, this, r, c);
	}

	@Override
	public Coords isValidMove(Board b, int r, int c, int endR, int endC) {
		return Piece.isValidDiagonalMove(b, this, r, c, endR, endC);
	}
}
