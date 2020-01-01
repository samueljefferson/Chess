package chess;

import java.util.ArrayList;
import java.util.List;

import chess.Piece.Coords;

/**
 * Implementation of Piece class representing the pawn.
 * 
 * @author James Beetham
 * @author Samuel Jefferson
 */
public class Pawn extends Piece implements FirstTurnAfterMove {
	/**
	 * Whether this piece has moved or not.
	 */
	private boolean moved = false;

	/**
	 * Only true the first turn after this piece has moved forward double.
	 */
	private boolean enPassantable = false;
	
	/**
	 * Constructs a new pawn (name = "p").
	 * 
	 * @param color what color the piece is (usually "Black" or "White")
	 */
	public Pawn(String color) {
		super(color, "p");
	}

	@Override
	public List<Coords> getPossibleMoves(Board b, int r, int c) {
		ArrayList<Piece.Coords> arr = new ArrayList<>();
		int direction = this.getDirection();
		// one move ahead
		if (Piece.validLoc(r + direction, c) && b.getTile(r + direction, c) == null) {
			arr.add(new Piece.Coords(r + direction, c));
			// two moves ahead
			if (!this.moved && b.getTile(r + 2 * direction, c) == null) arr.add(new Piece.Coords(r + 2 * direction, c));
		}
		// capture and en passant
		for (int i = -1; i <= 1; i += 2) {
			Piece p;
			if (this.validLoc(r + direction, c + i) && (p = b.getTile(r + direction, c + i)) != null && !p.getColor().equals(this.getColor())) {
				arr.add(new Piece.Coords(r + direction, c + i));				
			} else if (this.validLoc(r, c + i) && (p = b.getTile(r, c + i)) != null && p.getName().equals(this.getName()) 
						&& !p.getColor().equals(this.getColor()) && ((FirstTurnAfterMove)p).isFirstTurnAfterMove()) {
				arr.add(new Piece.Coords(r + direction, c + i));				
			}
		}
		return arr;
	}

	@Override
	public Coords isValidMove(Board b, int r, int c, int endR, int endC) {
		// TODO do something in input parser about start and end being the same?
		if (!Piece.validLoc(r, c) || !Piece.validLoc(endR, endC)) return null;
		int direction = this.getDirection();
		Piece.Coords successCoords = new Piece.Coords(-1, -1);
		if (c == endC) { // move forward
			if (b.getTile(r + direction, c) == null) {
				if (r + direction == endR) return successCoords;
				if (!this.moved && b.getTile(r + 2 * direction, c) == null && r + 2 * direction == endR) return successCoords;
			}
		} else if (Math.abs(endC - c) == 1) { // capture
			if (endR - r == direction) {
				int cDirection = endC - c;
				Piece p;
				if ((p = b.getTile(r + direction, c + cDirection)) != null && !p.getColor().equals(this.getColor())) {
					// capture diagonally
					return new Piece.Coords(r + direction, c + cDirection);
				} else if ((p = b.getTile(r, c + cDirection)) != null && p.getName().equals(this.getName()) 
						&& !p.getColor().equals(this.getColor()) && ((FirstTurnAfterMove)p).isFirstTurnAfterMove()) {
					// capture via en passant
					return new Piece.Coords(direction, c + cDirection);
				}
			}
		}
		return null;
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
	public boolean isFirstTurnAfterMove() {
		return this.enPassantable;
	}

	@Override
	public boolean setIsFirstTurnAfterMove(boolean previous) {
		if (!this.moved) return false;
		if (previous) return false;
		this.enPassantable = true;
		return true;
	}
	
	@Override
	public void unsetFirstTurnAfterMove() {
		this.enPassantable = false;
	}
}
