package com.example.androidchess48;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Calendar;


/**
 * Used for saving a chess game to a .dat file
 *
 * @author James Beetham
 * @author Samuel Jefferson
 */
public class Game implements Serializable {
    private String title;
    private Calendar date = Calendar.getInstance();
    private String moves;
    private String[] convertedMoves;


    /**
     * Constructor for creating a game
     *
     * @param title name of the game
     * @param moves the moves in the game as a String
     */
    public Game(String title, String moves) {
        this.title = title;
        this.moves = moves;
        // TODO check
        this.date.setTimeInMillis(System.currentTimeMillis());
        // TODO create comparator for title and date
        if (this.moves != null) {
            this.convertedMoves = convertedMoves = moves.split("\n");
        }
    }

    /**
     * Getter function for the title
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Getter function for converted moves
     *
     * @return converted moves
     */
    public String[] getConvertedMoves() {
        return convertedMoves;
    }

    /**
     * Getter function for moves
     *
     * @return the moves
     */
    public String getMoves() {
        return moves;
    }

    /**
     * Writes the game to a .dat file
     *
     * @param storeDir directory to write the game to
     * @param game the game to be written
     */
    public static void writeGame(String storeDir, Game game) {
        String storeFile = game.title + ".dat";
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                    new FileOutputStream(storeDir + File.separator + storeFile)
            );
            objectOutputStream.writeObject(game);
        } catch (IOException e) {
            // TODO error message
//            e.printStackTrace();
        }
    }

    /**
     * Reads the game from a .dat file
     *
     * @param storeDir directory the game was stored in
     * @param filename filename of the game
     * @return the game
     */
    public static Game readGame(String storeDir, String filename) {
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(
                    new FileInputStream(storeDir + File.separator + filename)
            );
            return (Game)objectInputStream.readObject();
        } catch (IOException e) {
//            e.printStackTrace();
        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
        }
        return null;
    }

    /**
     * Used for displaying completed games
     *
     * @return game title follwed by date
     */
    public String toString() {
        return this.getTitle() + ", " + getDate();
    }

    /**
     * Returns the calendar as a date
     *
     * @return the date the game was created
     */
    public String getDate() {
        return date.getTime().toString();
    }
}
