package com.example.androidchess48;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import chess.Board;

/**
 * Creates the board image and moves pieces as the game plays.
 *
 * @author James Beetham
 * @author Samuel Jefferson
 */
public class ChessView extends View implements View.OnTouchListener {
    // chess stuff
    Board chessBoard = null;
    boolean game = true;			// is there a game being played
    boolean offerDraw = false;		// has a draw been offered
    String drawOfferedBy = "";		// who offered the draw
    String winner;					// who won the game
    boolean replay = false;
    public Button prevTurn = null;
    public Button nextTurn = null;


//    TextView br1;
    ConstraintLayout constraintLayout = null;
    TextView[][] pieces = null;
    TextView turn = null;

    Paint paint;
    int height;
    int width;
    int size;
    public Rect[][] board = board = new Rect[8][8];

    int srcX = -1;
    int srcY = -1;
    int destX = -1;
    int destY = -1;

    // TODO remove
    TextView temp;

    Chess chess;

    public ChessView(Context context, TextView[][] pieces, Board chessBoard, TextView turn, boolean replay, Chess chess) {
        super(context);
        this.chess = chess;

        this.pieces = pieces;
        this.chessBoard = chessBoard;
        this.turn = turn;
        this.replay = replay;

        if (replay) {
            prevTurn = findViewById(R.id.draw);
            nextTurn = findViewById(R.id.resign);
        }

        this.setOnTouchListener(this);
        constraintLayout = findViewById(R.id.chessLayout);
        Log.d("msg", "created view");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        paint = new Paint();
        size = width / 8;
        Log.d("msg", "size = " + size);

        // create chess board
        int x = 0;
        int y = 0;
        for (int i = 0; i < 8; i++) {
            x = 0;
            for (int j = 0; j < 8; j++) {
                board[i][j] = new Rect();
                board[i][j].left = x;
                board[i][j].top = y;
                board[i][j].right = x + size;
                board[i][j].bottom = y + size;
                x += size;
            }
            y += size;
        }

        // redraw pieces
        drawPieces();
    }

    /**
     * Used to draw the canvas
     * @param canvas the canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoardSquares(canvas);
    }

    /**
     * Draws the chess board squares
     * @param canvas the canvas
     */
    void drawBoardSquares(Canvas canvas) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i % 2 == 0 && j % 2 != 0) || (i % 2 != 0 && j % 2 == 0)) {
//                    paint.setColor(Color.rgb(80,80,80));
                    paint.setColor(Color.rgb(150,75,0));
                } else {
                    paint.setColor(Color.WHITE);
                }
                canvas.drawRect(board[i][j], paint);
            }
        }
    }


    /**
     * Resets all the pieces to be null
     *
     * @param canvas the canvas
     */
    void resetBoard(Canvas canvas) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                pieces[i][j] = null;
            }
        }
    }

    /**
     * Moves a piece
     *
     * @param x the x value of the piece
     * @param y the y value of the piece
     */
    void move(int x, int y) {
        boolean dontReset = false;
        // set source
        if (srcX == -1) {
            // row, col
            if (pieces[x][y] != null) {
                srcX = x;
                srcY = y;
                pieces[srcX][srcY].setTextColor(Color.GREEN);
            }


        } else {
            // set destination
            destX = x;
            destY = y;

            // white pawn and destination is y=0
            // black pawn and destination is y=7
            if (destX == 0 && srcX == 1 && pieces[srcX][srcY].getText().equals("\u2659")) {
                Log.d("msg", "white pawn promotion");
                // display promotion options
                chess.promote();
                dontReset = true;
            } else if (destX == 7 && srcX == 6 && pieces[srcX][srcY].getText().equals("\u265F")) {
                Log.d("msg", "black pawn promotion");
                chess.promote();
                dontReset = true;
            } else if (destX != srcX || destY != srcY) {

//                Log.d("msg",  srcY + ", " + srcX + " to " + destY + ", " + destX);

                String result = chessBoard.takeTurn(srcX, srcY, destX, destY);
                Log.d("board", "result: " + result);


                if (!result.equals("Success")) {
                    switch (result) {
                        case "Checkmate":
//                            chess.winner = chessBoard.getTurn() + " wins";
                            if (chessBoard.getTurn().equals("White")) {
                                chess.winner = "Black wins";
                            } else {
                                chess.winner = "White wins";
                            }
                            chess.endGame();

                            break;
                        case "Stalemate":
                            chess.winner = "Stalemate";
                            chess.endGame();
                            break;
                        default:
                            Log.d("msg", "error: " + result);
                            Toast toast = Toast.makeText(getContext(), result, Toast.LENGTH_SHORT);
                            toast.show();
                            break;
                    }
                } else {
                    Log.d("board", "successful move");

                    // move
                    drawBoard();
                    if (chessBoard.getTurn().equals("White")) {
                        turn.setText(R.string.turn_white);
                    } else {
                        turn.setText(R.string.turn_black);
                    }
                }

                if (chessBoard.isInCheck()) {
                    Log.d("board", "check");
                    Toast toast = Toast.makeText(getContext(), "check", Toast.LENGTH_SHORT);
                    toast.show();
                }

                Log.d("board", "board" + "\n" + chessBoard.toString());

            }
            // reset source
            if (!dontReset) {
                srcX = -1;
                srcY = -1;
            }

            // reset piece highlight
            drawPieces();
        }
        drawBoard();
    }

    /**
     * Used when a pawn is promoted.
     */
    public void finishPromotion() {
        if (destX != srcX || destY != srcY) {
            Log.d("msg",  srcY + ", " + srcX + " to " + destY + ", " + destX + " to " + chess.promoteTo);

            String result = chessBoard.takeTurn(srcX, srcY, destX, destY, chess.promoteTo);
            Log.d("board", "result: " + result);

            if (!result.equals("Success")) {
                switch (result) {
                    case "Checkmate":
                        chess.winner = chessBoard.getTurn() + " wins";
                        chess.endGame();

                        break;
                    case "Stalemate":
                        chess.winner = "Stalemate";
                        chess.endGame();
                        break;
                    default:
                        Toast toast = Toast.makeText(getContext(), result, Toast.LENGTH_SHORT);
                        toast.show();
                        break;
                }
            } else {
                Log.d("board", "successful move");
                // move
                drawBoard();
                if (chessBoard.getTurn().equals("White")) {
                    turn.setText(R.string.turn_white);
                } else {
                    turn.setText(R.string.turn_black);
                }

            }

            if (chessBoard.isInCheck()) {
                Log.d("board", "check");
                Toast toast = Toast.makeText(getContext(), "check", Toast.LENGTH_SHORT);
                toast.show();
            }
            Log.d("board", "board" + "\n" + chessBoard.toString());

        }
        // reset source
        srcX = -1;
        srcY = -1;

        // reset piece highlight
        drawPieces();
    }

    /**
     * Used to move pieces.
     *
     * @param v the view
     * @param event the motion event
     * @return success of the motion event
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!replay) {
            switch (event.getAction()) {
                case (MotionEvent.ACTION_DOWN):

                    // figure out what was touched
                    for (int i = 0; i < 8; i++) {
                        for (int j = 0; j < 8; j++) {
                            if (board[i][j].contains((int)event.getX(), (int)event.getY())) {

                                try {
                                    move(i, j);
                                } catch (Exception e) {
                                    // TODO fix far right row thing here
                                    Toast toast = Toast.makeText(getContext(), e.getMessage() + "||||", Toast.LENGTH_SHORT);
//                                    toast.show();
                                    drawBoard();
                                }
                            }
                        }
                    }
                    break;

                case (MotionEvent.ACTION_UP):
//                Log.d("msg", "ACTION_UP");
                    break;

                default:
                    break;
            }
        }
        return true;
    }

    /**
     * Draws the pieces on the board
     */
    void drawPieces() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (pieces[i][j] != null) {
                    // TODO no magic numbers
                    pieces[i][j].setX(j * size + 20);
                    pieces[i][j].setY(i * size);
                    pieces[i][j].setTextColor(Color.BLACK);
                }
            }
        }
    }

    /**
     * Use Board.toString() to redraw the pieces
     */
    void drawBoard() {
        String display = chessBoard.toString();

        String[][] tempBoard = chessBoard.androidToString();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                switch (tempBoard[row][col]) {
                    case("bR"):
                        pieces[row][col].setText("\u265C");
                        break;
                    case("bN"):
                        pieces[row][col].setText("\u265E");
                        break;
                    case("bB"):
                        pieces[row][col].setText("\u265D");
                        break;
                    case("bQ"):
                        pieces[row][col].setText("\u265B");
                        break;
                    case("bK"):
                        pieces[row][col].setText("\u265A");
                        break;
                    case("bp"):
                        pieces[row][col].setText("\u265F");
                        break;
                    case("wR"):
                        pieces[row][col].setText("\u2656");
                        break;
                    case("wN"):
                        pieces[row][col].setText("\u2658");
                        break;
                    case("wB"):
                        pieces[row][col].setText("\u2657");
                        break;
                    case("wQ"):
                        pieces[row][col].setText("\u2655");
                        break;
                    case("wK"):
                        pieces[row][col].setText("\u2654");
                        break;
                    case("wp"):
                        pieces[row][col].setText("\u2659");
                        break;
                    default:
                        pieces[row][col].setText("");
                        break;
                }
            }
        }
    }

    /**
     * Used to display replays of games.
     *
     * @param replayGame the game being replayed
     * @param turnCount the current turn
     */
    public void replay(Game replayGame, int turnCount) {
        String[] moves = replayGame.getConvertedMoves();

        // reset board
        chessBoard = new Board();
        if (turnCount >= 0) {
            Log.d("msg", "replay turn " + turnCount + ": " + moves[turnCount]);
            for (int i = 0; i <= turnCount; i++) {
                String moveString = moves[i];
                if (isMove(moveString)) {
                    int[][] move = null;
                    try {
                        move = convertInput(moveString);
                        // is move a pawn promotion?
                        if (moveString.split(" ").length == 3) {
                            String promote = moveString.split(" ")[2];
                            chessBoard.takeTurn(move[0][1], move[0][0], move[1][1], move[1][0], promote);
                        } else {
                            chessBoard.takeTurn(move[0][1], move[0][0], move[1][1], move[1][0]);
                        }
                    } catch (Exception e) {
                        Log.d("msg", e.getMessage());
                    }
                } else {
                    Log.d("msg", "NOT A MOVE");
                    Toast toast = Toast.makeText(getContext(), moveString, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }
        drawBoard();

    }

    /**
     * Checks if the input is a move or not.
     *
     * @param input the input being checked
     * @return if the input is a move or not
     */
    public boolean isMove(String input) {
        if (input.equals("resign") || input.equals("draw") || input.equals("Black wins") ||
                input.equals("White wins")) {
            return false;
        } else {
            return true;
        }
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
}
