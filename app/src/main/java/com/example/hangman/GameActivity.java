package com.example.hangman;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * @author Eric Groseclos
 * @version 1.0
 */
public class GameActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.hangman.message";

    private Hangman hangman;

    private TextView letters;

    private TextView triesRemaining;

    private TextView usedLetters;

    private ImageView image;

    @Override
    /**
     * Creates everything necessary for a round of "Hangman".
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        image = (ImageView) findViewById(R.id.gallow);

        loadImage("hang10.gif");

        ArrayList<String> words = createWordList();

        hangman = new Hangman(words);

        letters = (TextView) findViewById(R.id.hiddenWord);

        letters.setText(hangman.getHiddenWord());

        triesRemaining = (TextView) findViewById(R.id.triesLeft);

        usedLetters = (TextView) findViewById(R.id.usedLetters);
    }

    /** Called when the user taps the "Gissa!" button. Handles the user's
     * guess. Updates the application accordingly. */
    public void makeGuess(View view) {
        int tries = hangman.getTriesLeft();

        EditText editText = (EditText) findViewById(R.id.userGuess);
        String guessedLetter = editText.getText().toString();

        if (!hangman.hasUsedLetter(guessedLetter.charAt(0)) || hangman.getTriesLeft() == 10) {
            hangman.guess(guessedLetter.charAt(0));
        }

        if (hangman.hasWon()) {
            Intent intent = new Intent(this, ResultActivity.class);
            String message = "W " + hangman.getRealWord() + " " + hangman.getTriesLeft();
            intent.putExtra(EXTRA_MESSAGE, message);
            startActivity(intent);
        }

        if (hangman.getTriesLeft() < tries) {
            loadImage("hang" + hangman.getTriesLeft() + ".gif");
            usedLetters.setText(hangman.getBadLettersUsed());
            String outputString = "";
            if (hangman.hasLost()) {
                Intent intent = new Intent(this, ResultActivity.class);
                String message = "L " + hangman.getRealWord() + " " + hangman.getTriesLeft();
                intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
            } else {
                outputString = hangman.getTriesLeft() + " försök kvar";
            }
            triesRemaining.setText(outputString);
        } else {
            letters.setText(hangman.getHiddenWord());
        }
    }

    /**
     * Creates a list containing all words used in the game.
     * @return  A list with all words
     */
    private ArrayList<String> createWordList() {
        ArrayList<String> words = new ArrayList<>();
        words.add("ANCIENT");
        words.add("BURROW");
        words.add("FRONT");
        words.add("LEGENDARY");
        words.add("PREMATURE");
        words.add("QUARANTINE");
        words.add("ROOSTER");
        words.add("ZODIAC");
        return words;
    }

    /**
     * Loads the necessary image needed for the application.
     * The ImageView is updated if the user makes a wrong
     * guess.
     * @param fileName  The name of the image to be loaded
     */
    private void loadImage(String fileName) {
        try {

            InputStream ims = getAssets().open(fileName);

            Drawable d = Drawable.createFromStream(ims, null);

            image.setImageDrawable(d);

            ims.close();

        } catch(IOException e) {
            loadImage("hang10.gif");
        }
    }

}
