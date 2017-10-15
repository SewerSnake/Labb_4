package com.example.hangman;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * @author Eric Groseclos
 * @version 1.0
 */
public class ResultActivity extends AppCompatActivity {

    private final String WIN = "You won!";

    private final String LOSE = "You lost!";

    private String[] values = new String[3];

    private TextView resultOfGame;

    private TextView word;

    private TextView triesRemaining;

    @Override
    /**
     * Handles information regarding the result of the game.
     * The word is shown, as well as the number of tries.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        resultOfGame = (TextView) findViewById(R.id.result);
        word = (TextView) findViewById(R.id.word);
        triesRemaining = (TextView) findViewById(R.id.triesRemaining);


        Intent intent = getIntent();

        String receivedMessage = intent.getStringExtra(GameActivity.EXTRA_MESSAGE);

        values = receivedMessage.split(" ");

        if (values[0].equals("W")) {
            resultOfGame.setText(WIN);
            String output = "Ordet var: " + values[1];
            word.setText(output);
            output = "Antal försök kvar: " + values[2];
            triesRemaining.setText(output);
        } else if (values[0].equals("L")) {
            resultOfGame.setText(LOSE);
            String output = "Ordet var: " + values[1];
            word.setText(output);
            output = "Antal försök kvar: " + values[2];
            triesRemaining.setText(output);
        }

    }

    /** Called when the user taps the "Tillbaka till huvudmenyn" button. Starts a new activity,
     * which shows the name of the creator of the application. */
    public void mainMenu(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
