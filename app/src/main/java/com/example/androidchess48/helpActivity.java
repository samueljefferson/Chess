package com.example.androidchess48;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

/**
 * Displays information about the chess game
 *
 * @author James Beetham
 * @author Samuel Jefferson
 */
public class helpActivity extends AppCompatActivity {

    /**
     * Run on start of activity
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
    }
}
