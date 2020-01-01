package com.example.androidchess48;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Lists all the saved games
 *
 * @author James Beetham
 * @author Samuel Jefferson
 */
public class GameList extends AppCompatActivity {
    ListView gameListView = null;
    ArrayList<String> titleList = null;
    ArrayList<Game> gameList = null;
    String sortBy = "title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_list);

        gameListView = findViewById(R.id.gameListView);

        String storeDir = getFilesDir().toString() + "/" + "dat";

        // check to see if sample game exists
        boolean found = false;
        File dir = new File(storeDir);
        for (File file : dir.listFiles()) {
            if(file.getName().equals("sample_game.dat")) {
                found = true;
                break;
            }
        }

        // if sample games doesn't exist then create it
        if (!found) {
            createSampleGame();
        }

        // create linked list of all the .dat files in directory
        titleList = new ArrayList<String>();
        for (File file : dir.listFiles()) {
            titleList.add(file.getName().toString().substring(0, file.getName().toString().length()-4));
        }

        gameList = new ArrayList<Game>();
        for (File file : dir.listFiles()) {
            gameList.add(Game.readGame(storeDir, file.getName()));
        }
        Log.d("msg", "length " + gameList.size());


        for (Game game : gameList) {
            Log.d("msg", "game " + game.getTitle());
        }

        gameListView.setAdapter(new ArrayAdapter<Game>(this, R.layout.game, gameList));
        gameListView.setOnItemClickListener(
                (p,v,pos,id) -> showGame(pos)
        );

    }

    /**
     * Run to replay the selected game
     *
     * @param pos index of the selected game
     */
    public void showGame(int pos) {
        Log.d("msg", "showGame: " + titleList.get(pos));

        Intent intent = new Intent(this, Chess.class);
        intent.putExtra("replay", "true");
        intent.putExtra("gameTitle", titleList.get(pos));
        startActivity(intent);
    }

    /**
     * Sort the games by their title
     *
     * @param view the view
     */
    public void sortByTitle(View view) {
        sortBy = "title";
        Collections.sort(gameList, new SortGame(this));
        gameListView.setAdapter(new ArrayAdapter<Game>(this, R.layout.game, gameList));


    }

    /**
     * Sort the games by their date
     *
     * @param view the view
     */
    public void sortByDate(View view) {
        sortBy = "date";
        Collections.sort(gameList, new SortGame(this));
        gameListView.setAdapter(new ArrayAdapter<Game>(this, R.layout.game, gameList));
    }


    /**
     * Create a sample game for the game list.
     */
    public void createSampleGame() {
        String moves = "e2 e4\n" +
                "e7 e5\n" +
                "g1 f3\n" +
                "b8 c6\n" +
                "f1 b5\n" +
                "a7 a6\n" +
                "b5 c6\n" +
                "d7 c6\n" +
                "e1 g1\n" +
                "g8 e7\n" +
                "d2 d4\n" +
                "e5 d4\n" +
                "f3 d4\n" +
                "f7 f6\n" +
                "a2 a4\n" +
                "e7 g6\n" +
                "c2 c4\n" +
                "f8 e7\n" +
                "b2 b3\n" +
                "e8 g8\n" +
                "d1 d3\n" +
                "c6 c5\n" +
                "d4 f5\n" +
                "d8 d3\n" +
                "b1 d2\n" +
                "c8 f5\n" +
                "e4 f5\n" +
                "g6 f4\n" +
                "d2 f3\n" +
                "f4 e2\n" +
                "g1 h1\n" +
                "e2 g3\n" +
                "h2 g3\n" +
                "d3 f1\n" +
                "h1 h2\n" +
                "e7 d6\n" +
                "c1 b2\n" +
                "f1 f2\n" +
                "a1 d1\n" +
                "d6 g3\n" +
                "h2 h1\n" +
                "f2 b2\n" +
                "d1 d8\n" +
                "f8 d8\n" +
                "f3 e5\n" +
                "b2 c1\n" +
                "Black wins";
        Game game = new Game("sample_game", moves);

        // write sample game
        String storeDir = getFilesDir().toString() + "/" + "dat";
        Game.writeGame(storeDir, game);

        Log.d("msg", "createSampleGame");
    }
}
