package chess;

import java.util.List;

/**
 * Implementation of Piece class representing the queen.
 * 
 * @author James Beetham
 * @author Samuel Jefferson
 */
public class Queen extends Piece {
	/**
	 * Constructs a new queen (name = "Q").
	 * 
	 * @param color what color the piece is (usually "Black" or "White")
	 */
	Queen(String color) {
		super(color, "Q");
	}

	@Override
	public List<Coords> getPossibleMoves(Board b, int r, int c) {
		List<Coords> arr = Piece.getValidStraightMoves(b, this, r, c);
		arr.addAll(Piece.getValidDiagonalMoves(b, this, r, c));
		return arr;
	}

	@Override
	public Coords isValidMove(Board b, int r, int c, int endR, int endC) {
		Piece.Coords success = Piece.isValidStraightMove(b, this, r, c, endR, endC);
		if (success == null) success = Piece.isValidDiagonalMove(b, this, r, c, endR, endC);
		return success;
	}
}
