package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import chess.Piece.Coords;

/**
 * Board holds the pieces and their state.
 *
 * @author James Beetham
 * @author Samuel Jefferson
 */
public class Board {
	/**
	 * An 8x8 grid of pieces, tiles are null if no piece is present at that r,c.
	 */
	private Piece[][] board = new Piece[8][8];

	/**
	 * Whose turn it is; "White" is default first turn, "Black" is other turn.
	 */
	private String turn = "White";

	/**
	 * A list of pieces which have a delayed change (eg. pawn and FirstTurnAfterMove).
	 */
	private ArrayList<Piece> unsetFirstTurnAfterMoveList;

	/**
	 * Array of states the board was previously in.
	 */
	private ArrayList<Piece[][]> boardHistory;

	/**
	 * Array of moves taken to get to current board state.
	 * In form of "e2 e4\ne7 e5\n..." with 3rd column being promotion piece choice.
	 */
	private ArrayList<String> history;

	/** used to determine if a player can use their undo **/
	private boolean canUndo = true;

	/**
	 * Constructs a new board from given string.
	 * Use format "bR bN bB bQ bK bB bN bR\nbp\n## ## bp" where black pieces start with "b",
	 * white pieces start with "w", and rows are filled from left to right and ended at '\n'.
	 *
	 * @param s base string to construct the board from - empty for empty board
	 */
	public Board(String s) {
		String sRemainder = s;
		for (int r = 0; r < 8; r++) {
			for (int c = 0; c < 8; c++) {
				int newLineChar = sRemainder.indexOf("\n");
				if (newLineChar == 0) break;
				if (newLineChar == -1 && sRemainder.length() == 0) break;
				int spaceChar = sRemainder.indexOf(" ");
				int endBreak = Math.min(spaceChar == -1 ? sRemainder.length() : spaceChar,
						newLineChar == -1 ? sRemainder.length() : newLineChar);
				String pieceStr = sRemainder.substring(0, endBreak);
				sRemainder = sRemainder.substring(endBreak +
						((spaceChar < newLineChar || newLineChar == -1) && spaceChar != -1 ? 1 : 0));
				if (pieceStr.equals("##") || pieceStr.length() < 2) continue;
				Piece p;
				String color = pieceStr.charAt(0) == 'w' ? "White" : "Black";
				char pieceChar = pieceStr.charAt(1);
				if (pieceChar == 'R') p = new Rook(color);
				else if (pieceChar == 'N') p = new Knight(color);
				else if (pieceChar == 'B') p = new Bishop(color);
				else if (pieceChar == 'Q') p = new Queen(color);
				else if (pieceChar == 'K') p = new King(color);
				else if (pieceChar == 'p') p = new Pawn(color);
				else throw new Error("Invalid piece: " + pieceStr + " at (" + r + ", " + c + ")");
				this.board[r][c] = p;
			}
			if (sRemainder.indexOf("\n") == -1 && sRemainder.length() == 0) break;
			sRemainder = sRemainder.substring(sRemainder.indexOf("\n") + 1);
		}
		unsetFirstTurnAfterMoveList = new ArrayList<>();
		boardHistory = new ArrayList<>();
		history = new ArrayList<>();
		/*
		 * bR bN bB bQ bK bB bN bR 8
		   bp bp bp bp bp bp bp bp 7
   			  ##    ##    ##    ## 6
		   ##    ##    ##    ##    5
   			  ##    ##    ##    ## 4
		   ##    ##    ##    ##    3
		   wp wp wp wp wp wp wp wp 2
		   wR wN wB wQ wK wB wN wR 1
 			a  b  c  d  e  f  g  h
		 */
	}

	/**
	 * Board constructor creates all the pieces and places them on the board.
	 */
	public Board() {
		this("bR bN bB bQ bK bB bN bR\nbp bp bp bp bp bp bp bp\n\n\n\n\nwp wp wp wp wp wp wp wp\nwR wN wB wQ wK wB wN wR");
	}

	/**
	 * Constructs board from predefined board matrix.
	 *
	 * @param b 2d array of pieces
	 */
	public Board(Piece[][] b) {
		this.board = b;
		unsetFirstTurnAfterMoveList = new ArrayList<>();
		boardHistory = new ArrayList<>();
		history = new ArrayList<>();
	}

	/**
	 * Make the current player take their turn with default promoteTo as queen.
	 *
	 * @param r row of the piece
	 * @param c column of the piece
	 * @param destR row of where the piece is going
	 * @param destC column of where the piece is going
	 * @return different messages for success or failed etc
	 */
	public String takeTurn(int r, int c, int destR, int destC) {
		return this.takeTurn(r, c, destR, destC, "Q");
	}

	/**
	 * Make the current player take their turn.
	 *
	 * @param r row of the piece
	 * @param c column of the piece
	 * @param destR row of where the piece is going
	 * @param destC column of where the piece is going
	 * @param promoteTo what piece a pawn should promote to ("Q", "R", "B", "N")
	 * @return different messages for success or failed etc
	 */
	public String takeTurn(int r, int c, int destR, int destC, String promoteTo) {
		Piece p;
		Piece[][] prevBoard = this.copyBoard();
		if (!Piece.validLoc(r, c)) return "Invalid location";
		if (!Piece.validLoc(destR, destC)) return "Invalid destination";
		if ((p = this.getTile(r, c)) == null) return "No piece specified";
		if (p.getColor() != this.turn) return "Wrong piece color";
		if (promoteTo == null || promoteTo.length() != 1 || ("QRBN").indexOf(promoteTo) == -1) return "Invalid promoteTo";
		Piece.Coords captureCoords;
		if ((captureCoords = p.isValidMove(this, r, c, destR, destC)) == null) return "Invalid move";
		if (Piece.validLoc(captureCoords.getR(), captureCoords.getC())) {
			// captured a piece?
		} else if (captureCoords.getR() == -1 && captureCoords.getC() >= 0) { // castle
			// check if in check if moved one in direction
			int midC = (c + destC) / 2;
			this.board[r][midC] = this.board[r][c];
			this.board[r][c] = null;
			if (this.isInCheck(this.turn)) {
				this.board[r][c] = this.board[r][midC];
				this.board[r][midC] = null;
				return "Invalid move";
			}
			this.board[r][destC] = this.board[r][midC];
			this.board[r][midC] = this.board[r][captureCoords.getC()]; // put rook in midC
			this.board[r][captureCoords.getC()] = null;
		}
		Piece backupPiece = this.board[destR][destC];
		this.board[r][c] = null;
		this.board[destR][destC] = p;

		ArrayList<Piece> backupUnsetFirstTurnAfterMoveList = new ArrayList<>();
		while (unsetFirstTurnAfterMoveList.size() > 0) {
			Piece ftamP = unsetFirstTurnAfterMoveList.remove(0);
			backupUnsetFirstTurnAfterMoveList.add(ftamP);
			((FirstTurnAfterMove)ftamP).unsetFirstTurnAfterMove();
		}

		if (p instanceof HasMoved) {
			boolean prev = ((HasMoved) p).hasMoved();
			((HasMoved) p).setMoved();
			if (p instanceof FirstTurnAfterMove) {
				if (((FirstTurnAfterMove) p).setIsFirstTurnAfterMove(prev)) {
					unsetFirstTurnAfterMoveList.add(p);
				}
			}
		}

		if (this.isInCheck(this.getTurn())) {
			// undo current move
//			this.board[destR][destC] = this.board[r][c];
			this.board[r][c] = this.board[destR][destC];
			this.board[destR][destC] = backupPiece;
			this.unsetFirstTurnAfterMoveList = backupUnsetFirstTurnAfterMoveList;
			return "Moved into check";
		}

		boolean promoted = false;
		if (p.getName().equals("p") && (destR == 7 || destR == 0)) { // promote
			if (promoteTo.equals("Q")) {
				p = new Queen(p.getColor());
			} else if (promoteTo.equals("R")) {
				p = new Rook(p.getColor());
			} else if (promoteTo.equals("N")) {
				p = new Knight(p.getColor());
			} else if (promoteTo.equals("B")) {
				p = new Bishop(p.getColor());
			}

			this.board[destR][destC] = p;
			promoted = true;
		}

		// record move to history
		this.boardHistory.add(prevBoard);
		String colLabel = "abcdefgh";
		this.history.add("" + colLabel.charAt(c) + (8 - r) + " " + colLabel.charAt(destC) + (8 - destR) + (promoted ? " " + promoteTo : ""));

		// reset canUndo
		canUndo = true;

		String retStr = "Success";
		if (this.isInCheck(this.getNextTurn())) { // checkmate
			boolean notCheckMate = false;
			for (int tmpR = 0; tmpR < 8; tmpR++) {
				for (int tmpC = 0; tmpC < 8; tmpC++) {
					Piece currentPiece;
					if ((currentPiece = this.getTile(tmpR, tmpC)) != null && currentPiece.getColor().equals(this.getNextTurn())) {
						// for all enemy pieces, if they are unable to move in a way that gets the king out of check, checkmate
						Piece.Coords prevCoords = new Piece.Coords(tmpR, tmpC);
						for (Piece.Coords coords : currentPiece.getPossibleMoves(this, tmpR, tmpC)) {
							Piece[][] copyBoard = this.copyBoard();
							copyBoard[coords.getR()][coords.getC()] = currentPiece;
							copyBoard[prevCoords.getR()][prevCoords.getC()] = null;
							if (!this.isInCheck(this.getNextTurn(), copyBoard)) notCheckMate = true;
							if (notCheckMate) break;
						}
					}
					if (notCheckMate) break;
				}
			}
			if (!notCheckMate) retStr = "Checkmate";
		} else { // stalemate
			boolean canMove = false;
			for (int tmpR = 0; tmpR < 8; tmpR++) {
				for (int tmpC = 0; tmpC < 8; tmpC++) {
					Piece currentPiece;
					if ((currentPiece = this.getTile(tmpR, tmpC)) != null && currentPiece.getColor().equals(this.getNextTurn())) {
						// for all enemy pieces, if they are unable to move in a way such that the king is not in check, stalemate
						Piece.Coords prevCoords = new Piece.Coords(tmpR, tmpC);
						for (Piece.Coords coords : currentPiece.getPossibleMoves(this, tmpR, tmpC)) {
							Piece[][] copyBoard = this.copyBoard();
							copyBoard[coords.getR()][coords.getC()] = currentPiece;
							copyBoard[prevCoords.getR()][prevCoords.getC()] = null;
							if (!this.isInCheck(this.getNextTurn(), copyBoard)) canMove = true;
							if (canMove) break;
						}
					}
					if (canMove) break;
				}
				if (canMove) break;
			}
			if (!canMove) retStr = "Stalemate";
		}

		this.nextTurn();
		return retStr;
	}

	/**
	 * Undoes previous move
	 * @return true if successful
	 */
	public boolean undoMove() {
		if (this.boardHistory.size() == 0) return false;
		this.board = this.boardHistory.get(this.boardHistory.size() - 1);
		this.boardHistory.remove(this.boardHistory.size() - 1);
		this.history.remove(this.history.size() - 1);
		this.nextTurn();
		return true;
	}

	/**
	 * Limits undo to once per turn
	 *
	 * @return whether the undo was successful
	 */
	public boolean undoLimited() {
		if (canUndo) {
			undoMove();
			canUndo = false;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Prints the current game's moves
	 * @return String in form of "e2 e4\ne7 e5\ne4 e7 Q\n..." where 3rd column is promotion choice.
	 */
	public String printGameAsMoves() {
		String ret = "";
		for (String s : this.history) {
			ret += s + "\n";
		}
		return ret;
	}

	public boolean makeMove() {
		ArrayList<int[]> coords = new ArrayList<>();
		for (int c = 0; c < 7; c++) {
			for (int r = 0; r < 7; r++) {
				Piece p = this.board[r][c];
				if (p == null || p.getColor() != this.getTurn()) continue;
				List<Piece.Coords> possible = p.getPossibleMoves(this, r, c);
				for (Piece.Coords co : possible) {
					coords.add(new int[]{r, c, co.getR(), co.getC()});
				}
			}
		}

		while (coords.size() > 0) {
			int rand = (int)Math.floor(Math.random() * coords.size());
			int[] test = coords.get(rand);
			coords.remove(rand);
			String res = this.takeTurn(test[0], test[1], test[2], test[3]);
			if (res.equals("Success")) return true;
			if (res.equals("Checkmate") || res.equals("Stalemate")) return true;
		}
		return false; // checkmate / stalemate?
	}

	/**
	 * Checks whether specified side is in check on specified board matrix.
	 *
	 * @param side string name (usually "Black" or "White")
	 * @param board 2d matrix of pieces
	 * @return true if in check, false if no king found or not
	 */
	public boolean isInCheck(String side, Piece[][] board) {
		int kingR = -1;
		int kingC = -1;
		for (int tmpR = 0; tmpR < 8; tmpR++) {
			for (int tmpC = 0; tmpC < 8; tmpC++) {
				Piece currentKing;
				if ((currentKing = board[tmpR][tmpC]) != null && currentKing.getName() == "K" && currentKing.getColor().equals(side)) {
					kingR = tmpR;
					kingC = tmpC;
					break;
				}
			}
			if (kingR != -1 && kingC != -1) break;
		}
		if (kingR == -1 || kingC == -1) return false; // king not found
		Piece selectedPiece;
		for (int tmpR = 0; tmpR < 8; tmpR++) {
			for (int tmpC = 0; tmpC < 8; tmpC++) {
				if ((selectedPiece = board[tmpR][tmpC]) == null || selectedPiece.getColor().equals(side)) {
					continue;
				}
				if (selectedPiece.canCapture(new Board(board), tmpR, tmpC, kingR, kingC)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks whether specified side is in check on this board.
	 *
	 * @param side string name (usually "Black" or "White")
	 * @return true if in check, false if no king found or not
	 */
	public boolean isInCheck(String side) {
		return this.isInCheck(side, this.board);
	}

	/**
	 * Checks whether current turn's side is in check on this board.
	 *
	 * @return true if in check, false if no king found or not
	 */
	public boolean isInCheck() {
		return this.isInCheck(this.getTurn());
	}

	/**
	 * Get the piece located at the row and column specified.
	 *
	 * @param r row
	 * @param c column
	 * @return piece at tile specified, or null if none there
	 */
	public Piece getTile(int r, int c) {
		if (r < 0) throw new IllegalArgumentException("r must be greater than 0 but was: " + r);
		if (r >= 8) throw new IllegalArgumentException("r must be less than 8 but was: " + r);
		if (c < 0) throw new IllegalArgumentException("c must be greater than 0 but was: " + c);
		if (c >= 8) throw new IllegalArgumentException("c must be less than 8 but was: " + c);
		return this.board[r][c];
	}

	/**
	 * Set tile to a particular piece (or null).
	 *
	 * @param r row of piece to set
	 * @param c column of piece to set
	 * @param p piece to replace that row and column with
	 */
	public void setTile(int r, int c, Piece p) {
		this.board[r][c] = p;
	}

	/**
	 * The current player's turn.
	 *
	 * @return "White" if white's turn, same for black
	 */
	public String getTurn() {
		return this.turn;
	}

	/**
	 * This method changes the turn to the other player.
	 */
	private void nextTurn() {
		this.turn = this.getNextTurn();
	}

	/**
	 * Gets the next players turn.
	 *
	 * @return either "White" or "Black" - the opposite of the current player's turn
	 */
	private String getNextTurn() {
		return this.turn.equals("White") ? "Black" : "White";
	}

	/**
	 * Copies this board and pieces (new pieces).
	 *
	 * @return 2d matrix of new pieces
	 */
	private Piece[][] copyBoard() {
		Piece[][] ret = new Piece[8][8];
		for (int r = 0; r < 8; r++) {
			for (int c = 0; c < 8; c++) {
				Piece p = null;
				Piece thisPiece;
				if ((thisPiece = this.board[r][c]) != null) {
					if (thisPiece.getName() == "p") {
						p = new Pawn(thisPiece.getColor());
					} else if (thisPiece.getName() == "R") {
						p = new Rook(thisPiece.getColor());
					} else if (thisPiece.getName() == "N") {
						p = new Knight(thisPiece.getColor());
					} else if (thisPiece.getName() == "B") {
						p = new Bishop(thisPiece.getColor());
					} else if (thisPiece.getName() == "Q") {
						p = new Queen(thisPiece.getColor());
					} else if (thisPiece.getName() == "K") {
						p = new King(thisPiece.getColor());
					} else {
						throw new IllegalStateException("Do not recognize pieceName: " + thisPiece.getName());
					}
				}
				ret[r][c] = p;
			}
		}
		return ret;
	}

	/**
	 * Copies this board and creates a new Board.
	 *
	 * @return new board with the same board matrix as this one
	 */
	public Board copy() {
		return new Board(this.copyBoard());
	}

	/**
	 * Returns a string representation of the board with ASCII art.
	 */
	@Override
	public String toString() {
		String ret = "";
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				if (board[i][j] == null) {
					if ((i % 2 == 0 && j % 2 != 0) || (i % 2 != 0 && j % 2 == 0)) {
						ret += "## ";
					} else {
						ret += "   ";
					}
				} else {
					ret += board[i][j] + " ";
				}
			}
			ret += (8 - i) + "\n";
		}
		ret += " a  b  c  d  e  f  g  h";
		return ret;
	}

	/**
	 * Returns a 2d array with char representations of pieces
	 * @return 2d array with char representations of pieces
	 */
	public String[][] androidToString() {
		String[][] temp = new String[8][8];
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				if (board[i][j] == null) {
					temp[i][j] = "";
				} else {
					temp[i][j] = board[i][j].toString();
				}
			}
		}
		return temp;
	}
}