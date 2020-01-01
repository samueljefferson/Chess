package chess;

import java.util.Scanner;

/**
 * InputParser parses the input and interacts with Board as needed
 * 
 * @author James Beetham
 * @author Samuel Jefferson
 */
public class InputParser {
	boolean testingMode = false;
	Board board = new Board();
	boolean game = true;			// is there a game being played
	boolean offerDraw = false;		// has a draw been offered
	String drawOfferedBy = "";		// who offered the draw
	String winner;					// who won the game
	
	/**
	 * Constructor enables testingMode if needed.
	 * 
	 * @param testingMode true for testing
	 */
	public InputParser(boolean testingMode) {
		this.testingMode = testingMode;
	}
	
	/**
	 * Converts the input into a 2d array of integers that contains the move the user made.
	 * 
	 * @param input The string entered by the user
	 * @return move The 2d array of integers that represents the move
	 */
	public int[][] convertInput(String input) {
		String[] split = input.split(" ");
		int[][] move = new int[2][2];
		for (int i = 0; i < 2; i++) {
			if (split[i].length() != 2) {
				throw new IllegalArgumentException("Illegal move, try again");
			}
			// make sure first char is a letter
			if (Character.isLetter(split[i].charAt(0)) == false) {
				throw new IllegalArgumentException("Illegal move, try again");
			}
			// bounds check
			int x = (int)split[i].charAt(0) - (int)'a';
			if (x < 0 || x > 7) {
				throw new IllegalArgumentException("Illegal move, try again");
			}
			
			// make sure second char is a number
			if (Character.isDigit(split[i].charAt(1)) == false) {
				throw new IllegalArgumentException("Illegal move, try again");
			}
			// bounds check
			int y = (int)split[i].charAt(1)-48;
			y = 8-y;
			if (y < 0 || y > 7) {
				throw new IllegalArgumentException("Illegal move, try again");
			}
			
			// save x, y
			move[i][0] = x;
			move[i][1] = y;
		}
		
		return move;
	}
	
	/**
	 * Receives the input from gameLoop() and handles it.  Passes input to convertInput() in order to process moves.
	 * 
	 * @param input The string entered by the user
	 */
	public void validateInput(String input) {
		int[][] move = new int[2][2];		
		String[] split = input.split(" ");
		
		switch (split.length) {
			case 1:
				if (split[0].compareTo("resign") == 0) {
					resign();
				} else if (split[0].compareTo("draw") == 0) {
					confirmDraw();
				} else {
					throw new IllegalArgumentException("Illegal move, try again");
				}
				break;
				
			case 2:
				move = convertInput(input);
				runMove(move[0][1], move[0][0], move[1][1], move[1][0], null);
				break;
				
			case 3:
				move = convertInput(input);
				if (split[2].compareTo("draw?") == 0) {
					offerDraw();		// need to offer draw while still in players turn
					runMove(move[0][1], move[0][0], move[1][1], move[1][0], null);
				} else  {
					runMove(move[0][1], move[0][0], move[1][1], move[1][0], split[2]);
				}
				break;
				
			default:
				throw new IllegalArgumentException("Illegal move, try again");
		}
	}
	
	/**
	 * Used by validateInput() to pass values to Board.takeTurn()
	 * 
	 * @param r row of piece being moved
	 * @param c column of piece being moved
	 * @param destR row piece is being moved to
	 * @param destC column piece is being moved to
	 * @param promoteTo what pawn is being promoted to (optional)
	 */
	void runMove(int r, int c, int destR, int destC, String promoteTo) {
		String result;
		if (promoteTo == null) {
			result = board.takeTurn(r, c, destR, destC);
		} else {
			result = board.takeTurn(r, c, destR, destC, promoteTo);
		}
		
		if (testingMode) {
			System.out.println(r + "," + c + " " + destR + "," + destC);
		}
		
		if (result.compareTo("Success") != 0) {
			switch (result) {
				case "Checkmate":
					// get winner
					
					// reversed because board.takeTurn() ends the previous players turn
					if (board.getTurn().compareTo("Black") == 0) { 
						winner = "Black";
					} else {
						winner = "White";
					}
					System.out.println("Checkmate " + winner + " wins");
					// end the game
					game = false;
					break;

				case "Stalemate":
					System.out.println("Stalemate");
					game = false;
					break;
					
				default:
					// show real message if testing, otherwise print default error
					if (testingMode == true) {
						System.out.println(result);
					} else {
//						System.out.println("Illegal move, try again");
						throw new IllegalArgumentException("Illegal move, try again");
					}
					break;
			}
		}
	}
	
	/**
	 * Used to offer draw to the other player.
	 */
	void offerDraw() {
		offerDraw = true;
		drawOfferedBy = board.getTurn();
	}
	
	/**
	 * Used to confirm a draw offered by the other player.
	 */
	void confirmDraw() {
		if (offerDraw && drawOfferedBy.compareTo(board.getTurn()) != 0) {
			game = false;
			System.out.println("draw");
		} else {
			throw new IllegalArgumentException("Illegal move, try again");
		}
	}
	
	/**
	 * This methods is used when a player resigns.  The game ends and the other player is the winner.
	 **/
	void resign() {
		game = false;
		if (board.getTurn().compareTo("White") == 0) {
			winner = "Black";
		} else {
			winner = "White";
		}
		System.out.println(winner + " wins");
	}
	
	/**
	 * Main loop of program.  Gets input and sends to to validateInput(), displays results.
	 */
	public void gameLoop() {
		Scanner scanner = new Scanner(System.in);
		String input;
		int turn = 1;
		String subTurn = "a";
		System.out.println(board);

		while(game) {
			try {
				// if in testingMode, print turn info
				if (testingMode) {
					System.out.println("\n" + turn + subTurn);
					if (subTurn.compareTo("a") == 0) {
						subTurn = "b";
					} else {
						subTurn = "a";
						turn++;
					}
				}
				
				if (board.isInCheck()) {
					System.out.println("\nCheck");
					System.out.print(board.getTurn() + "'s move: ");
				} else {
					// get input
					System.out.print("\n" + board.getTurn() + "'s move: ");
				}
				input = scanner.nextLine();
				// process input
				validateInput(input);
				
				if (game) {	// only do this if game isn't over
					System.out.println();
					System.out.println(board);
				}
			} catch(Exception e) {
				System.out.print(e.getMessage());
			}	
		}
		scanner.close();		
	}
}