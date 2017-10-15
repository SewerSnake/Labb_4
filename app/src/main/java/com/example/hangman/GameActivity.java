package com.example.hangman;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * @author Eric Groseclos
 * @version 1.0
 */
public class GameActivity extends AppCompatActivity {

    // Allows information to be sent from one activity to another
    public static final String EXTRA_MESSAGE = "com.example.hangman.message";

    private Hangman hangman;

    private TextView letters;

    private TextView triesRemaining;

    private TextView usedLetters;

    private ImageView image;


    /**
     * Creates everything necessary for a round of "Hangman".
     * The basic gallow is loaded and shown on the screen.
     * The number of tries the player has to escape the gallows
     * is also shown.
     */
    @Override
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

    /**
     * Called when the user taps the "GUESS!" button. Handles the user's
     * guess. Updates the application accordingly.
     */
    public void makeGuess(View view) {
        int tries = hangman.getTriesLeft();

        processUserInput();

        checkIfWon();

        checkIfLost(tries);
    }

    /**
     * Creates a list containing all words used in the game.
     * @return  A list with all words
     */
    private ArrayList<String> createWordList() {
        ArrayList<String> words = new ArrayList<>();
        words.add("ALKEMI");
        words.add("FORNTIDA");
        words.add("HÅLA");
        words.add("KARANTÄN");
        words.add("LEGENDARISK");
        words.add("PREMIÄR");
        words.add("TUPP");
        words.add("ZOO");
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

    /**
     * Gets a letter from the player and makes a guess.
     */
    private void processUserInput() {
        boolean guess = true;
        EditText editText = (EditText) findViewById(R.id.userGuess);
        String guessedLetterRaw = editText.getText().toString();

        if (guessedLetterRaw.length() > 1) {
            guess = false;
            createToast(getResources().getString(R.string.error_1));
        }

        char guessedLetter = '0';

        if (!guessedLetterRaw.equals("")) {
            guessedLetter = guessedLetterRaw.charAt(0);
        }

        if (!isLetter(guessedLetter)) {
            guess = false;
        }

        guessedLetter = letterConversion(guessedLetter);

        if (guess) {
            if (!hangman.hasUsedLetter(guessedLetter) || hangman.getTriesLeft() == 10) {
                hangman.guess(guessedLetter);
            } else {
                createToast(getResources().getString(R.string.error_2));
            }
        }
    }

    /**
     * Determines if the provided character is a letter or not.
     * The Swedish letters 'å', 'ä' and 'ö' are supported.
     * @param character the character to examine
     */
    private boolean isLetter(char character) {
        if ((character >= 'a' && character <= 'z') || character == 'å'
                || character == 'ä' || character == 'ö' ||
                (character >= 'A' && character <= 'Z') ||
                character == 'Å' || character == 'Ä'
                || character == 'Ö') {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Allows the application to accept lowercase letters.
     * If the provided letter is a lowercase letter, its
     * uppercase counterpart is returned.
     * @param guessedLetter A letter, which might be converted
     */
    private char letterConversion(char guessedLetter) {
        if ((guessedLetter >= 'a' && guessedLetter <= 'z') || guessedLetter == 'å'
                || guessedLetter == 'ä' || guessedLetter == 'ö') {
            String letter = String.valueOf(guessedLetter);
            letter = letter.toUpperCase();
            return letter.charAt(0);
        } else {
            return guessedLetter;
        }
    }

    /**
     * Determines if the player has won the game. Sends
     * the identity of the word, as well as the number of
     * tries left to a new ResultActivity.
     */
    private void checkIfWon() {
        if (hangman.hasWon()) {
            Intent intent = new Intent(this, ResultActivity.class);
            String message = "W " + hangman.getRealWord() + " " + hangman.getTriesLeft();
            intent.putExtra(EXTRA_MESSAGE, message);
            startActivity(intent);
        }
    }

    /**
     * Determines if the player has lost the game. Updates
     * the number of tries left. Informs the player which
     * letters he/she has used. If the player loses,
     * the identity of the word, as well as the number of
     * tries left, are sent to a new ResultActivity.
     * @param tries A number used for checking if the number of tries
     *              has been reduced or not
     */
    private void checkIfLost(int tries) {
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
                outputString = hangman.getTriesLeft() + " " + getResources().getString(R.string.currentTries);
            }
            triesRemaining.setText(outputString);
        } else {
            letters.setText(hangman.getHiddenWord());
        }
    }

    /**
     * A toast is created to inform the player that he/she has
     * done something undesirable, i.e giving more than one letter
     * or entering a used letter.
     * @param messageToUser What shall be displayed to the player
     */
    private void createToast(String messageToUser) {
        Context context = getApplicationContext();
        CharSequence text = messageToUser;
        int durationOfToast = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, durationOfToast);
        toast.setGravity(Gravity.BOTTOM|Gravity.END, 150, 200);
        toast.show();
    }

}
