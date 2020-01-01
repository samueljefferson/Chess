package chess;

import java.util.ArrayList;
import java.util.List;

import chess.Piece.Coords;

/**
 * Implementation of Piece class representing the knight.
 * 
 * @author James Beetham
 * @author Samuel Jefferson
 */
public class Knight extends Piece {
	/**
	 * Constructs a new knight (name = "N").
	 * 
	 * @param color what color the piece is (usually "Black" or "White")
	 */
	public Knight(String color) {
		super(color, "N");
	}

	@Override
	public List<Coords> getPossibleMoves(Board b, int r, int c) {
		ArrayList<Piece.Coords> arr = new ArrayList<>();
		for (int ir = -1; ir <= 1; ir += 2) {
			for (int ic = -1; ic <= 1; ic += 2) {
				Piece p;
				if (this.validLoc(r + 2 * ir,  c + ic) && ((p = b.getTile(r + 2 * ir, c + ic)) == null || 
						p.getColor() != this.getColor())) {
					arr.add(new Piece.Coords(r + 2 * ir, c + ic));
				}
				if (this.validLoc(r + ir,  c + 2 * ic) && ((p = b.getTile(r + ir, c + 2 * ic)) == null || 
						p.getColor() != this.getColor())) {
					arr.add(new Piece.Coords(r + ir, c + 2 * ic));
				}

			}
		}
		
		return arr;
	}

	@Override
	public Coords isValidMove(Board b, int r, int c, int endR, int endC) {
		if ((Math.abs(r - endR) == 2 && Math.abs(c - endC) == 1)
				|| (Math.abs(r - endR) == 1 && Math.abs(c - endC) == 2)) {
			Piece p;
			if (this.validLoc(endR, endC) && ((p = b.getTile(endR, endC)) == null || !p.getColor().equals(this.getColor()))) {
				return new Piece.Coords(endR, endC);
			}
		}
		return null;
	}
}
