package com.example.hangman;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

/**
 * @author Eric Groseclos
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** Called when the user taps the "Spela spel" button. Starts a new activity,
     * which allows the user to play the "Hangman" game. */
    public void game(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the "Om" button. Starts a new activity,
     * which shows the name of the application creator. */
    public void about(View view) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }
}
