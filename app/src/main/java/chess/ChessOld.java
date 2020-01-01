package chess;

/**
 * Main entry function for our program.
 * 
 * @author James Beetham
 * @author Samuel Jefferson
 */
public class ChessOld {
	/**
	 * Entry point for our program
	 * @param args not used
	 */
	public static void main(String[] args) {
		
		boolean testingMode = false;
		if (args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				if (args[i].compareTo("testing") == 0) {
					testingMode = true;
				}
			}
		}
		
		InputParser inputParser = new InputParser(testingMode);
		inputParser.gameLoop();
	}
}