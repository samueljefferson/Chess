package com.example.androidchess48;

import java.util.Comparator;

/**
 * Comparator used for sorting chess games
 *
 * @author James Beetham
 * @author Samuel Jefferson
 */
public class SortGame implements Comparator<Game> {
    GameList gameList;

    /**
     * Constructor
     *
     * @param gameList the gameList class
     */
    public SortGame(GameList gameList) {
        this.gameList = gameList;
    }

    /**
     * Compares two game based on specified attribute
     *
     * @param g1 game 1
     * @param g2 game 2
     * @return the order
     */
    @Override
    public int compare(Game g1, Game g2) {
        if (this.gameList.sortBy.equals("title")) {
            return g1.getTitle().compareTo(g2.getTitle());
        } else {
            return g1.getDate().compareTo(g2.getDate());
        }
//        return 0;
    }
}
