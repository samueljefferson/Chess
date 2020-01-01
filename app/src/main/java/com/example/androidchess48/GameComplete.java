package com.example.androidchess48;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

/**
 * Activity shown after the game is completed.  Allows the player to save the game.
 *
 * @author James Beetham
 * @author Samuel Jefferson
 */
public class GameComplete extends AppCompatActivity {
    TextView winner = null;
    EditText title = null;

    String gameTitle;
    String moves;

    /**
     * Gets the information from the previous game
     * @param savedInstanceState the savedInstanceBundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_complete);

        winner = findViewById(R.id.winner);
        title = findViewById(R.id.title);

        if (getIntent().getStringExtra("winner").equals("White") ||
                getIntent().getStringExtra("winner").equals("Black")) {
            winner.setText(getIntent().getStringExtra("winner") + " wins");
        } else {
            winner.setText(getIntent().getStringExtra("winner"));
        }
        moves = getIntent().getStringExtra("moves");
    }

    /**
     * Returns the player to the main menu
     *
     * @param view the view
     */
    public void backToMenu(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Save the game
     *
     * @param view the current view
     */
    public void saveGame(View view) {

        try {
            gameTitle = title.getText().toString();

            if (gameTitle.equals("")) {
                throw new IllegalArgumentException("error: filename is empty");
            }
            String storeDir = getFilesDir().toString() + "/" + "dat";
            String storeFile = gameTitle + ".dat";

            for (File file : getFilesDir().listFiles()) {
                if (file.getName().equals(storeFile)) {
                    throw new IllegalArgumentException("error: game title " + gameTitle + " already exists");
                }
            }

            // TODO remove
//            Log.d("msg", "finished game moves:");
//            Log.d("msg", "moves\n" + moves);

            Game game = new Game(gameTitle, moves);

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                    new FileOutputStream(storeDir + File.separator + storeFile)
            );

            objectOutputStream.writeObject(game);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        } catch (Exception e) {
            Log.d("msg", "errror: " + e.getMessage());
            Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
            title.setText("");
        }
    }
}
