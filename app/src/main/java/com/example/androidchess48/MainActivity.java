package com.example.androidchess48;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import java.io.File;

/**
 * Initial activity of the program.  Used to start a new chess game or view completed games.
 *
 * @author James Beetham
 * @author Samuel Jefferson
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Creates a directory to store finished games if none currently exists.
     *
     * @param savedInstanceState the savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // make sure a dat directory exists for storage
        String storeDir = getFilesDir().toString() + "/" + "dat";
        File dir = new File(storeDir);
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdir();
        }
    }

    /**
     * Start a new chess game
     *
     * @param view the view
     */
    public void startGame(View view) {
        Intent intent = new Intent(this, Chess.class);
        intent.putExtra("replay", "false");
        startActivity(intent);
    }

    /**
     * View completed chess games
     *
     * @param view the view
     */
    public void viewGames(View view) {
        Intent intent = new Intent(this, GameList.class);
        startActivity(intent);
    }

    /**
     * View instructions
     * @param view the view
     */
    public void help(View view) {
        Intent intent = new Intent(this, helpActivity.class);
        startActivity(intent);
    }
}
