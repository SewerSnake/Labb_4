package com.example.hangman;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
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

    private Hangman hangman;

    private TextView letters;

    private TextView triesRemaining;

    private TextView usedLetters;

    private ImageView image;

    private SharedPreferences sharedPreferences;


    /**
     * Creates everything necessary for a round of "Hangman".
     * The basic gallows is loaded and shown on the screen.
     * The number of tries the player has to escape the gallows
     * is also shown.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        image = (ImageView) findViewById(R.id.gallow);

        ArrayList<String> words = createWordList();

        Context contextOfApp = getApplicationContext();

        if (savedInstanceState != null) {
            hangman = new Hangman(savedInstanceState, contextOfApp);
        } else {
            hangman = new Hangman(words, contextOfApp);
        }

        loadImage("hang" + hangman.getTriesLeft() + ".gif");

        letters = (TextView) findViewById(R.id.hiddenWord);

        letters.setText(hangman.getHiddenWord());

        triesRemaining = (TextView) findViewById(R.id.triesLeft);

        usedLetters = (TextView) findViewById(R.id.usedLetters);

        if (hangman.getTriesLeft() == 1) {
            triesRemaining.setText(hangman.getTriesLeft() + " " + getResources().getString(R.string.oneTry));
        } else {
            triesRemaining.setText(hangman.getTriesLeft() + " " + getResources().getString(R.string.currentTries));
        }

        usedLetters.setText(hangman.getBadLettersUsed());
    }

    /**
     *  Inflates the menu. The necessary buttons are
     *  added to the Actionbar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.game_activity_buttons, menu);
        return true;
    }

    /**
     * When the "Info" button is tapped, information
     * about the developer is shown.
     * @param menuItem  The MenuItem that was pressed
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            default:
        }
        return super.onOptionsItemSelected(menuItem);
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
     * Ensures that a word cannot be chosen again,
     * until the player has guessed all words correctly.
     * @return  A list with all words
     */
    private ArrayList<String> createWordList() {
        ArrayList<String> words = new ArrayList<>();

        String[] wordArray = fetchWords();

        SharedPreferences firstTimePrefs = getSharedPreferences("GameActivity", 0);

        boolean firstTimePlaying = firstTimePrefs.getBoolean("firstTime", true);

        if (firstTimePlaying || sharedPreferences.getInt("size", 0) == 0) {

            for (String word : wordArray) {
                words.add(word);
            }

        } else {
            for (int i = 0; i < wordArray.length; i++) {
                boolean add = true;
                for (int j = 0; j < sharedPreferences.getInt("size", 1); j++) {
                    if (wordArray[i].equals(sharedPreferences.getString("usedWord_" + j, "AWOL"))) {
                        add = false;
                    }
                }
                if (add) {
                    words.add(wordArray[i]);
                }
            }
        }
        return words;
    }

    /**
     * Gets the batch of words that the player chose in ChooseActivity.
     * @return  An array containing the words to be used in the game
     */
    private String[] fetchWords() {
        Intent intent = getIntent();
        int index = intent.getIntExtra("category", 0);
        switch (index) {
            case 0:
                return getResources().getStringArray(R.array.animals);
            case 1:
                return getResources().getStringArray(R.array.medieval);
            case 2:
                return getResources().getStringArray(R.array.zodiac);
            default:
                return getResources().getStringArray(R.array.animals);
        }
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
        EditText userGuess = (EditText) findViewById(R.id.userGuess);
        String guessedLetterRaw = userGuess.getText().toString();
        userGuess.setText("");

        if (guessedLetterRaw.length() > 1) {
            guess = false;
            createToast(getResources().getString(R.string.error_1));
        }

        char guessedLetter = '0';

        if (!guessedLetterRaw.equals("")) {
            guessedLetter = guessedLetterRaw.charAt(0);
        }

        if (!isLetter(guessedLetter) && guess) {
            guess = false;
            createToast(getResources().getString(R.string.error_3));
        }

        guessedLetter = letterConversion(guessedLetter);

        if (guess) {
            if (!hangman.hasUsedLetter(guessedLetter) || hangman.getTriesLeft() == 10) {
                if (!hangman.hasLost()) {
                    hangman.guess(guessedLetter);
                }
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
            SharedPreferences.Editor editor = sharedPreferences.edit();
            int index = sharedPreferences.getInt("size", 0);
            editor.putString("usedWord_" + index, hangman.getRealWord());
            editor.putInt("size", index + 1 == numberOfWords() ? 0 : index + 1);
            editor.apply();
            Intent resultIntent = new Intent(this, ResultActivity.class);
            String message = "W " + hangman.getRealWord() + " " + hangman.getTriesLeft();
            resultIntent.putExtra("Game result", message);
            startActivity(resultIntent);
        }
    }

    /**
     *  Returns the number of words the array has.
     */
    private int numberOfWords() {
        Intent chooseIntent = getIntent();
        int index = chooseIntent.getIntExtra("category", 0);
        switch (index) {
            case 0:
                return getResources().getStringArray(R.array.animals).length;
            case 1:
                return getResources().getStringArray(R.array.medieval).length;
            case 2:
                return getResources().getStringArray(R.array.zodiac).length;
            default:
                return getResources().getStringArray(R.array.animals).length;
        }
    }

    /**
     * Determines if the player has lost the game. Updates
     * the number of tries left and the gallows.
     * Informs the player which letters he/she has used.
     * If the player loses, the identity of the word, as
     * well as the number of tries left, are sent to a new
     * ResultActivity.
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
                intent.putExtra("Game result", message);
                startActivity(intent);
            } else {
                if (hangman.getTriesLeft() == 1) {
                    outputString = hangman.getTriesLeft() + " " + getResources().getString(R.string.oneTry);
                } else {
                    outputString = hangman.getTriesLeft() + " " + getResources().getString(R.string.currentTries);
                }
            }

            triesRemaining.setText(outputString);
        } else {
            letters.setText(hangman.getHiddenWord());
        }
    }

    /**
     * A toast is created to inform the player that he/she has
     * done something undesirable, i.e entering a non-letter,
     * entering more than one letter, or entering a used letter.
     * @param messageToUser What shall be displayed to the player
     */
    private void createToast(String messageToUser) {
        Context context = getApplicationContext();
        int durationOfToast = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, messageToUser, durationOfToast);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            toast.setGravity(Gravity.BOTTOM | Gravity.END, 150, 200);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            toast.setGravity(Gravity.BOTTOM | Gravity.END, 0, 140);
        }
        toast.show();
    }

    /**
     * Saves the state of the current game.
     * The bundle will be passed on to the onCreate method if the
     * process is killed and restarted.
     * @param savedInstanceState    A bundle containing crucial info
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        ArrayList<String> usedLetterList = new ArrayList<>();

        for (Character usedLetter : hangman.getUsedLetters()) {
            usedLetterList.add(String.valueOf(usedLetter));
        }
        savedInstanceState.putStringArrayList("usedLetters", usedLetterList);

        savedInstanceState.putInt("tries", hangman.getTriesLeft());

        savedInstanceState.putString("word", hangman.getRealWord());

        savedInstanceState.putBooleanArray("visible", hangman.getVisible());
    }

}
