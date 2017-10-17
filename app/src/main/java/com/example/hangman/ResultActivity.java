package com.example.hangman;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**
 * @author Eric Groseclos
 * @version 1.0
 */
public class ResultActivity extends AppCompatActivity {

    private final String WIN = "You won!";

    private final String LOSE = "You lost!";

    private String theWord;

    private String triesAtEnd;

    private String[] values = new String[3];

    private TextView resultOfGame;

    private TextView word;

    private TextView triesRemaining;


    /**
     * Handles information regarding the result of the game.
     * The word is shown, as well as the number of tries.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        theWord = getResources().getString(R.string.theWord);
        triesAtEnd = getResources().getString(R.string.triesAtEnd);

        resultOfGame = (TextView) findViewById(R.id.result);
        word = (TextView) findViewById(R.id.word);
        triesRemaining = (TextView) findViewById(R.id.triesRemaining);

        Intent intent = getIntent();

        String receivedMessage = intent.getStringExtra("Game result");

        values = receivedMessage.split(" ");

        if (values[0].equals("W")) {
            resultOfGame.setText(WIN);
            String output = theWord + " " + values[1];
            word.setText(output);
            output = triesAtEnd + " " + values[2];
            triesRemaining.setText(output);
        } else if (values[0].equals("L")) {
            resultOfGame.setText(LOSE);
            String output = theWord + " " + values[1];
            word.setText(output);
            output = triesAtEnd + " " + values[2];
            triesRemaining.setText(output);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu_buttons, menu);
        return true;
    }

    /**
     * When the "Info" button is tapped, information
     * about the developer is shown. When the "Play"
     * button is tapped, a new game is started.
     * @param menuItem  The MenuItem that was pressed
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.action_play:
                startActivity(new Intent(this, GameActivity.class));
                return true;
            default:
        }
        return super.onOptionsItemSelected(menuItem);
    }

    /**
     * Called when the user taps the "BACK TO MAIN MENU" button. Starts a new activity,
     * which returns the player to the main menu (MainActivity).
     */
    public void mainMenu(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
