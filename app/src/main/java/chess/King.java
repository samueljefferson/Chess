package chess;

import java.util.ArrayList;
import java.util.List;

import chess.Piece.Coords;

/**
 * Implementation of Piece class representing the king.
 * 
 * @author James Beetham
 * @author Samuel Jefferson
 */
public class King extends Piece implements HasMoved {
	/**
	 * Whether this piece has moved or not.
	 */
	private boolean moved = false;
	
	/**
	 * Constructs a new king (name = "K").
	 * 
	 * @param color what color the piece is (usually "Black" or "White")
	 */
	public King(String color) {
		super(color, "K");
	}

	@Override
	public void setMoved() {
		this.moved = true;
	}

	@Override
	public boolean hasMoved() {
		return this.moved;
	}

	@Override
	public List<Coords> getPossibleMoves(Board b, int r, int c) {
		List<Coords> ret = new ArrayList<>();
		for (int tmpR = r - 1; tmpR <= r + 1; tmpR++) {
			for (int tmpC = c - 1; tmpC <= c + 1; tmpC++) {
				Piece p;
				if (this.validLoc(tmpR, tmpC) && 
						((p = b.getTile(tmpR, tmpC)) == null || !p.getColor().equals(this.getColor()))) {
					ret.add(new Piece.Coords(tmpR, tmpC));
				}
			}
		}

		return ret;
	}

	@Override
	public Coords isValidMove(Board b, int r, int c, int endR, int endC) {
		if (r == endR && Math.abs(c - endC) == 2) { // castle
			int direction = c < endC ? 1 : -1;
			if (this.hasMoved()) return null;
			int i;
			int rookCol = -1;
			for (i = c + direction; i < 7 && i > 0; i += direction) { // check empty between king and rook
				rookCol = i;
				if (b.getTile(r, i) != null) return null;
			}
			rookCol += direction;
			Piece rook;
			if ((rook = b.getTile(r, rookCol)) != null && rook.getColor().equals(this.getColor()) 
					&& !((HasMoved)rook).hasMoved()) {
				return new Piece.Coords(-1, rookCol);
			}
		} else if (Math.abs(c - endC) <= 1 && Math.abs(r - endR) <= 1) {
			Piece p;
			if ((p = b.getTile(endR, endC)) != null) {
				if (p.getColor().equals(this.getColor())) return null;
				return new Piece.Coords(endR, endC);
			}
			return new Piece.Coords(-1, -1);
		}
		return null;
	}
}
