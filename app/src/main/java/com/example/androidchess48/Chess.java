package com.example.androidchess48;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import chess.Board;

/**
 * Main activity for chess, contains a chessView for the board
 *
 * @author James Beetham
 * @author Samuel Jefferson
 */
public class Chess extends AppCompatActivity {
    boolean replay = false;
    Game replayGame = null;
    int turnCount = -1;
    String gameTitle = null;
    Board chessBoard = new Board();
    ChessView chessView = null;

    boolean drawOffered = false;
    String drawOfferedBy = "";
    String winner = null;
    boolean resignGame = false;
    boolean drawnGame = false;
    String resignedBy = "";

    String promoteTo = null;

    ConstraintLayout constraintLayout = null;
    public Button draw = null;
    public Button resign = null;
    public Button AI = null;
    public Button undo = null;
    public TextView turn = null;

    TextView[][] pieces = new TextView[8][8];

    /**
     * Things that are run when Chess starts
     *
     * @param savedInstanceState the savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess);

        draw = findViewById(R.id.draw);
        resign = findViewById(R.id.resign);
        AI = findViewById(R.id.AI);
        undo = findViewById(R.id.undo);
        turn = findViewById(R.id.turn);
        constraintLayout = findViewById(R.id.chessLayout);

        if (getIntent().getStringExtra("replay").equals("true")) {
            replay = true;
            gameTitle = getIntent().getStringExtra("gameTitle");
            Log.d("msg", "replaying: " + gameTitle);

            draw.setText(R.string.button_prev);
            resign.setText(R.string.button_next);

            AI.setVisibility(View.GONE);
            undo.setVisibility(View.GONE);

            // read game
            String storeDir = getFilesDir().toString() + "/" + "dat";
            String filename = gameTitle + ".dat";

            replayGame = Game.readGame(storeDir, filename);
            Log.d("replay", "onCreate: \n" + replayGame.getMoves());

        } else {

            replay = false;
        }
        Log.d("msg", "replay: " + replay);

        // make all the chess pieces
        // row, col
        pieces[0][0] = new TextView(this);
        pieces[0][0].setText("\u265C");
        pieces[0][1] = new TextView(this);
        pieces[0][1].setText("\u265E");
        pieces[0][2] = new TextView(this);
        pieces[0][2].setText("\u265D");
        pieces[0][3] = new TextView(this);
        pieces[0][3].setText("\u265B");
        pieces[0][4] = new TextView(this);
        pieces[0][4].setText("\u265A");
        pieces[0][5] = new TextView(this);
        pieces[0][5].setText("\u265D");
        pieces[0][6] = new TextView(this);
        pieces[0][6].setText("\u265E");
        pieces[0][7] = new TextView(this);
        pieces[0][7].setText("\u265C");

        pieces[1][0] = new TextView(this);
        pieces[1][0].setText("\u265F");
        pieces[1][1] = new TextView(this);
        pieces[1][1].setText("\u265F");
        pieces[1][2] = new TextView(this);
        pieces[1][2].setText("\u265F");
        pieces[1][3] = new TextView(this);
        pieces[1][3].setText("\u265F");
        pieces[1][4] = new TextView(this);
        pieces[1][4].setText("\u265F");
        pieces[1][5] = new TextView(this);
        pieces[1][5].setText("\u265F");
        pieces[1][6] = new TextView(this);
        pieces[1][6].setText("\u265F");
        pieces[1][7] = new TextView(this);
        pieces[1][7].setText("\u265F");

        pieces[6][0] = new TextView(this);
        pieces[6][0].setText("\u2659");
        pieces[6][1] = new TextView(this);
        pieces[6][1].setText("\u2659");
        pieces[6][2] = new TextView(this);
        pieces[6][2].setText("\u2659");
        pieces[6][3] = new TextView(this);
        pieces[6][3].setText("\u2659");
        pieces[6][4] = new TextView(this);
        pieces[6][4].setText("\u2659");
        pieces[6][5] = new TextView(this);
        pieces[6][5].setText("\u2659");
        pieces[6][6] = new TextView(this);
        pieces[6][6].setText("\u2659");
        pieces[6][7] = new TextView(this);
        pieces[6][7].setText("\u2659");

        pieces[7][0] = new TextView(this);
        pieces[7][0].setText("\u2656");
        pieces[7][1] = new TextView(this);
        pieces[7][1].setText("\u2658");
        pieces[7][2] = new TextView(this);
        pieces[7][2].setText("\u2657");
        pieces[7][3] = new TextView(this);
        pieces[7][3].setText("\u2655");
        pieces[7][4] = new TextView(this);
        pieces[7][4].setText("\u2654");
        pieces[7][5] = new TextView(this);
        pieces[7][5].setText("\u2657");
        pieces[7][6] = new TextView(this);
        pieces[7][6].setText("\u2658");
        pieces[7][7] = new TextView(this);
        pieces[7][7].setText("\u2656");

        // empty TextView for everything else
        for (int i = 2; i < 6; i++) {
            for (int j = 0; j < 8; j++) {
                pieces[i][j] = new TextView(this);
                pieces[i][j].setText("");
            }
        }

        chessView = new ChessView(this, pieces, chessBoard, turn, replay, this);
        constraintLayout.addView(chessView);

        // set the pieces
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (pieces[i][j] != null) {
                    pieces[i][j].setTextSize((float)36);
                    pieces[i][j].setTextColor(Color.BLACK);
                    constraintLayout.addView(pieces[i][j]);

                }
            }
        }

    }

    /**
     * Draw game button.  If replay mode is on then this goes back a turn.
     *
     * @param view the view
     */
    public void draw(View view) {

        // draw functionality
        if (!replay) {
            Log.d("msg", "offerDraw: ");

            if (!drawOffered) {
                drawOffered = true;
                drawOfferedBy = chessBoard.getTurn();
                draw.setText(R.string.buton_draw_accept);
                Toast toast = Toast.makeText(getApplicationContext(), "offering draw", Toast.LENGTH_SHORT);
                toast.show();

            } else {
                if (!drawOfferedBy.equals(chessBoard.getTurn())) {
                    winner = "draw";
                    drawnGame = true;
                    endGame();

                } else {
                    // toast can't accept draw that you requested
                    Toast toast = Toast.makeText(getApplicationContext(), "player cannot accept draw for opponent", Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        } else {
            // previous turn functionality
            Log.d("msg", "prev turn");
            if (turnCount > -1) {
                turnCount--;
            }
            Log.d("msg", "turn " + turnCount);
            displayTurn();
            // TODO draw stuff
            chessView.replay(replayGame, turnCount);
        }

    }

    /**
     * Resign from the game.  If replay mode is enabled this plays the next turn.
     * @param view the view
     */
    public void resign(View view) {

        // resign functionality
        if (!replay) {
            Log.d("msg", "resign: " + chessBoard.getTurn());
            resignGame = true;
            resignedBy = chessBoard.getTurn();
            if (chessBoard.getTurn().equals("White")) {
                winner = "Black";
            } else {
                winner = "White";
            }
            endGame();
        } else {
            // TODO check for max turn
            if (turnCount < replayGame.getConvertedMoves().length -1) {
                turnCount++;
            }
            Log.d("msg", "turn " + turnCount);
            displayTurn();
            // TODO draw stuff
            chessView.replay(replayGame, turnCount);
        }

    }

    /**
     * Displays the which players turn it is
     */
    public void displayTurn() {
        if (!replay) {
            turn.setText("turn: " + chessBoard.getTurn());
        } else {
            if (turnCount % 2 == 0) {
                turn.setText(R.string.turn_white);
            } else {
                turn.setText(R.string.turn_black);
            }
        }

    }

    /**
     * Undoes the previous turn
     *
     * @param view the view
     */
    public void undo(View view) {
//        boolean result = chessBoard.undoMove();
        boolean result = chessBoard.undoLimited();
        if (result) {
            turnCount--;
            displayTurn();
        }
        chessView.drawBoard();
    }

    /**
     * Does a random move
     *
     * @param view the view
     */
    public void ai(View view) {
        chessBoard.makeMove();
        chessView.drawBoard();
    }

    /**
     * Ends the game and sends the required information to GameComplete
     */
    public void endGame() {
        Intent intent = new Intent(this, GameComplete.class);
        intent.putExtra("winner", winner);
        // TODO put moves in intent so they can be saved
        String moves = chessBoard.printGameAsMoves();
        if (resignGame) {
            moves += "\n" + resignedBy + " resigns";
        } else if (drawnGame) {
            moves += "\n" + "draw";
        }
        if (winner != null) {
            moves += "\n" + winner;
        }

        intent.putExtra("moves", moves);
        startActivity(intent);
    }

    /**
     * Displays a message prompting the user to pick what their pawn should be promoted to.
     *
     * @return the piece that the pawn will be promoted to
     */
    public String promote() {
        Log.d("msg", "promote: ");

        String[] choices = {"Rook", "Knight", "Bishop", "Queen"};
        String[] result = {"R", "N", "B", "Q"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

//        builder.setMessage("What should the pawn promote to?");
        builder.setTitle("What should the pawn promote to?");
        builder.setItems(choices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("msg", "choice");
                promoteTo = result[which];
                chessView.finishPromotion();
            }
        });

        builder.show();
        Log.d("msg", "TRACE");
        return promoteTo;
    }
}