package chess;

import java.util.ArrayList;
import java.util.List;

import chess.Piece.Coords;

/**
 * Piece is an abstract class that holds all the data types shared by all chess pieces.
 * 
 * @author James Beetham
 * @author Samuel Jefferson
 */
public abstract class Piece {
	private String color;
	private String name;
	
	/**
	 * This method constructs the piece and sets its color.
	 * 
	 * @param color The color of the player who controls the piece
	 * @param name The name of the piece
	 */
	public Piece(String color, String name) {
		if (color.compareTo("Black") != 0 && color.compareTo("White") != 0) {
			throw new IllegalArgumentException("error: " + color + " is not a valid color");
		}
		this.color = color;
		this.name = name;
	}
	
	/**
	 * Sets the toString() to be the first letter of the color and the name of the piece.
	 */
	public String toString() {
		return Character.toLowerCase(color.charAt(0)) + name;
	}
	
	/**
	 * Whether or not a loc is within an 8x8 board (matrix).
	 * 
	 * @param r row to check
	 * @param c column to check
	 * @return true if the location is within the board
	 */
	public static boolean validLoc(int r, int c) {
		return !(r < 0 || r >= 8 || c < 0 || c >= 8);
	}
	
	/**
	 * Whether this piece is able to capture the location specified
	 * 
	 * @param b board
	 * @param r target row
	 * @param c target column
	 * @param targetR target row
	 * @param targetC target column
	 * @return true if this piece is able to capture
	 */
	public boolean canCapture(Board b, int r, int c, int targetR, int targetC) {
		return this.isValidMove(b, r, c, targetR, targetC) != null;
	}
	
	/**
	 * Find which direction to go forwards (based on row).
	 * 
	 * @return -1 if going up the board is forward (white), 1 if going down the board is forward (black)
	 */
	public int getDirection() {
		return this.color == "White" ? -1 : 1;
	}
	
	/**
	 * Get the color of this piece ("White" or "Black")
	 * 
	 * @return the color of this piece
	 */
	public String getColor() {
		return this.color;
	}
	
	/**
	 * Get the name of this piece (eg "P" for pawn, "Q" for queen, etc)
	 * 
	 * @return string representing the name of this piece
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Used for returning pairs of locations (r, c).
	 * 
	 * @author James Beetham
	 * @author Samuel Jefferson
	 */
	public static class Coords {
		private int r;
		private int c;
		
		/**
		 * Constructs a new row-column pair
		 * 
		 * @param r row
		 * @param c column
		 */
		public Coords(int r, int c) {
			this.r = r;
			this.c = c;
		}
		
		/**
		 * Get row
		 * 
		 * @return the int of the row
		 */
		public int getR() {
			return this.r;
		}
		
		/**
		 * Get column
		 * 
		 * @return the int of the column
		 */
		public int getC() {
			return this.c;
		}
		
		@Override
		public boolean equals(Object o) {
			if (o == null || !(o instanceof Piece.Coords)) return false;
			Piece.Coords obj = (Piece.Coords)o;
			return obj.getR() == this.getR() && obj.getC() == this.getC();
		}
	}
	
	/**
	 * Gets the possible valid straight moves (horizontally or vertically) a piece can make. Note: takes into account
	 * piece colors (capturable). 
	 * 
	 * @param b board to use
	 * @param piece piece type
	 * @param r row of the piece
	 * @param c column of the piece
	 * @return list of piece coordinates of possible places this piece could move to from r and c specified
	 */
	public static List<Piece.Coords> getValidStraightMoves(Board b, Piece piece, int r, int c) {
		ArrayList<Piece.Coords> arr = new ArrayList<>();
		Piece p;
		for (int i = r + 1; i < 8; i++) {
			if ((p = b.getTile(i, c)) == null) {
				arr.add(new Piece.Coords(i, c));
			} else {
				if (!p.getColor().equals(piece.getColor())) arr.add(new Piece.Coords(i, c));
				break;
			}
		}
		for (int i = c + 1; i < 8; i++) {
			if ((p = b.getTile(r, i)) == null) {
				arr.add(new Piece.Coords(r, i));
			} else {
				if (!p.getColor().equals(piece.getColor())) arr.add(new Piece.Coords(r, i));
				break;
			}
		}
		for (int i = r - 1; i >= 0; i--) {
			if ((p = b.getTile(i, c)) == null) {
				arr.add(new Piece.Coords(i, c));
			} else {
				if (!p.getColor().equals(piece.getColor())) arr.add(new Piece.Coords(i, c));
				break;
			}
		}
		for (int i = c - 1; i >= 0; i--) {
			if ((p = b.getTile(r, i)) == null) {
				arr.add(new Piece.Coords(r, i));
			} else {
				if (!p.getColor().equals(piece.getColor())) arr.add(new Piece.Coords(r, i));
				break;
			}
		}
		return arr;
	}
	
	/**
	 * Gets the possible valid diagonal moves (all 4 directions) a piece can make. Note: takes into account
	 * piece colors (capturable). 
	 * 
	 * @param b board to use
	 * @param piece piece type
	 * @param r row of the piece
	 * @param c column of the piece
	 * @return list of piece coordinates of possible places this piece could move to from r and c specified
	 */
	public static List<Piece.Coords> getValidDiagonalMoves(Board b, Piece piece, int r, int c) {
		ArrayList<Piece.Coords> arr = new ArrayList<>();
		Piece p;
		for (int ir = r + 1, ic = c + 1; ir < 8 && ic < 8; ir++, ic++) {
			if ((p = b.getTile(ir, ic)) == null) {
				arr.add(new Piece.Coords(ir, ic));
			} else {
				if (!p.getColor().equals(piece.getColor())) arr.add(new Piece.Coords(ir, ic));
				break;
			}
		}
		for (int ir = r + 1, ic = c - 1; ir < 8 && ic >= 0; ir++, ic--) {
			if ((p = b.getTile(ir, ic)) == null) {
				arr.add(new Piece.Coords(ir, ic));
			} else {
				if (!p.getColor().equals(piece.getColor())) arr.add(new Piece.Coords(ir, ic));
				break;
			}
		}
		for (int ir = r - 1, ic = c + 1; ir >= 0 && ic < 8; ir--, ic++) {
			if ((p = b.getTile(ir, ic)) == null) {
				arr.add(new Piece.Coords(ir, ic));
			} else {
				if (!p.getColor().equals(piece.getColor())) arr.add(new Piece.Coords(ir, ic));
				break;
			}
		}
		for (int ir = r - 1, ic = c - 1; ir >= 0 && ic >= 0; ir--, ic--) {
			if ((p = b.getTile(ir, ic)) == null) {
				arr.add(new Piece.Coords(ir, ic));
			} else {
				if (!p.getColor().equals(piece.getColor())) arr.add(new Piece.Coords(ir, ic));
				break;
			}
		}
		
		return arr;
	}
	
	/**
	 * Whether this move from start to dest is a valid straight move.
	 * 
	 * @param b board to check on
	 * @param piece piece to move
	 * @param r starting row
	 * @param c starting column
	 * @param endR ending row
	 * @param endC ending column
	 * @return null if invalid, coordinates of the piece's position otherwise
	 */
	public static Piece.Coords isValidStraightMove(Board b, Piece piece, int r, int c, int endR, int endC) {
		if (!Piece.validLoc(r, c) || !Piece.validLoc(endR, endC)) return null;
		Piece.Coords successCoords = new Piece.Coords(endR, endC);
		Piece p;
		if (r == endR) { // horizontal
			int direction = endC > c ? 1 : -1;
			for (int i = c + direction; i != endC; i += direction) { // check all cells between c and endC, skipping current cell
				if (b.getTile(r, i) != null) return null; // piece infront of destination
			}
			// capture
			if ((p = b.getTile(r, endC)) != null && p.getColor().equals(piece.getColor())) return null;
		} else if (c == endC) { // vertical
			int direction = endR > r ? 1 : -1;
			for (int i = r + direction; i != endR; i += direction) { // check all cells between c and endC, skipping current cell
				if (b.getTile(i, c) != null) return null; // piece infront of destination
			}
			// capture
			if ((p = b.getTile(endR, c)) != null && p.getColor().equals(piece.getColor())) return null;
		} else { // sorts out diagonal here
			return null;
		}
		
		return successCoords;
	}
	
	/**
	 * Whether this move from start to dest is a valid diagonal move.
	 * 
	 * @param b board to check on
	 * @param piece piece to move
	 * @param r starting row
	 * @param c starting column
	 * @param endR ending row
	 * @param endC ending column
	 * @return null if invalid, coordinates of the piece's position otherwise
	 */
	public static Piece.Coords isValidDiagonalMove(Board b, Piece piece, int r, int c, int endR, int endC) {
		if (!Piece.validLoc(r, c) || !Piece.validLoc(endR, endC)) return null;	
		if (Math.abs(r - endR) != Math.abs(c - endC)) return null;
		int directionC = endC > c ? 1 : -1;
		int directionR = endR > r ? 1 : -1;
		Piece p;
		for (int iC = c + directionC, iR = r + directionR; iC != endC; iC += directionC, iR += directionR) { // check all cells between c and endC, skipping current cell
			if (b.getTile(iR, iC) != null) return null; // piece infront of destination
		}
		
		if ((p = b.getTile(endR, endC)) != null && p.getColor().equals(piece.getColor())) return null;
		return new Piece.Coords(endR, endC);
	}
		
	/**
	 * Collects the possible coords this piece can move to.
	 * 
	 * @param b board to check on
	 * @param r current row this piece is in
	 * @param c current column this piece is in
	 * @return list of coordinates of the r and c's
	 */
	public abstract List<Coords> getPossibleMoves(Board b, int r, int c);

	/**
	 * Finds whether or not the piece can move from one location to another location for a given board. Note castling returns (0, -1).
	 * 
	 * @param b board to check on
	 * @param r starting row location
	 * @param c starting column location
	 * @param endR ending row location
	 * @param endC ending column location
	 * @return null if invalid, otherwise the coordinates of the captured piece (or r, c = -1 if no piece was captured)
	 */
	public abstract Coords isValidMove(Board b, int r, int c, int endR, int endC);
}
