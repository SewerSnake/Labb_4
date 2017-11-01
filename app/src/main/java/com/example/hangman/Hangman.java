package com.example.hangman;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * @author Eric Groseclos
 * @version 1.0
 */
public class Hangman {

    private SharedPreferences sharedPreferences;

    private static Random random = new Random();

    //A list that contains all guessable words
    private ArrayList<String> allWords;
    //A list that contains all letters the user has guessed
    private ArrayList<Character> usedLetters = new ArrayList<>();
    //Number of tries the player has before the game ends
    private int tries;
    //The word the user must guess
    private String word;
    //An array that controls the visibility of each character in the hidden word
    private boolean[] visible;

    /**
     * Creates a new Hangman game, and chooses a word.
     * @param allWords  a list of all words the game can choose from
     */
    public Hangman(ArrayList<String> allWords, Context appContext) {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext);

        Log.d("firstTime Hangman:", String.valueOf(sharedPreferences.getBoolean("firstTime", true)));

        if (sharedPreferences.getBoolean("firstTime", true) || getHiddenWord().equals(getRealWord())) {
            this.allWords = allWords;
            tries = 10;
            word = " ";

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("firstTime", false);
            editor.apply();

            newWord();
        } else {
            Log.d("Hangman", "Fetching values from DefaultSharedPreferences");
            Set<String> usedLetterSet = sharedPreferences.getStringSet("usedLetters", null);

            for (String usedLetter : usedLetterSet) {
                usedLetters.add(usedLetter.charAt(0));
                Log.d("Used letters:", usedLetter);
            }

            tries = sharedPreferences.getInt("tries", 10);

            word = sharedPreferences.getString("word", "AWOL");

            Set<String> visibleSet = sharedPreferences.getStringSet("visible", null);

            visible = new boolean[visibleSet.size()];
            int i = 0;
            for (String boolString : visibleSet) {
                if (boolString.equals("true")) {
                    visible[i] = true;
                } else if(boolString.equals("false")) {
                    visible[i] = false;
                }
                i++;
            }
        }

    }

    public Hangman(Bundle savedInstanceState, Context appContext) {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext);

        ArrayList<String> usedLetterList;

        usedLetterList = savedInstanceState.getStringArrayList("usedLetters");

        for (String usedLetter : usedLetterList) {
            usedLetters.add(usedLetter.charAt(0));
        }

        tries = savedInstanceState.getInt("tries", 10);

        word = savedInstanceState.getString("word", "AWOL");

        visible = savedInstanceState.getBooleanArray("visible");

    }

    public ArrayList<Character> getUsedLetters() {
        return usedLetters;
    }

    public boolean[] getVisible() {
        return visible;
    }

    /**
     * Choose a new word. Changes the current word, by randomly
     * choosing a word from all available words.
     */
    public void newWord() {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        
        int randomWordIndex = random.nextInt(allWords.size());

        word = allWords.get(randomWordIndex);

        visible = new boolean[word.length()];

        for (int i = 0; i < word.length(); i++) {
            visible[i] = false;
        }

        editor.putString("word", word);
        Log.d("The word is", word);
        editor.apply();
    }

    /**
     * Returns the current word, hiding all the letters the user has not guessed yet.
     * Example: Word is "DOG". User has guessed 'D' previously. This then returns the string "D--"
     */
    public String getHiddenWord() {
        StringBuilder sb = new StringBuilder();
        String hiddenWord = "";
        for (int i = 0; i < word.length(); i++) {

            if (visible[i]) {

                sb.append(word.charAt(i));

            } else {
                sb.append('-');
            }

        }
        hiddenWord = sb.toString();
        return hiddenWord;
    }

    /**
     * Returns the current word, without any hidden letters.
     * Example: "CAT"
     */
    public String getRealWord() {
        return word;
    }

    /**
     * Makes a guess for a letter. If the letter is in the word,
     * we mark that letter as "complete" and remember the letter.
     * If the letter is not in word, we decrease the number of
     * guesses left and remember the letter.
     * @param guess the letter the user has entered
     */
    public void guess(char guess) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        boolean reduceTries = false;
        boolean changedVisibility = false;

        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == guess) {
                visible[i] = true;
                changedVisibility = true;
            } else {
                reduceTries = true;
            }
        }

        if (reduceTries && !changedVisibility) {
            tries--;
            editor.putInt("tries", tries);
        }

        if (changedVisibility) {
            Set<String> visibleSet = new HashSet<>();
            for (int i = 0; i < visible.length; i++) {
                if (visible[i]) {
                    visibleSet.add("true");
                } else {
                    visibleSet.add("false");
                }
            }
            editor.putStringSet("visible", visibleSet);
        }

        usedLetters.add(guess);
        Set<String> usedLettersSet = new HashSet<>();
        for (Character usedLetter : usedLetters) {
            usedLettersSet.add(String.valueOf(usedLetter));
        }
        editor.putStringSet("usedLetters", usedLettersSet);
        editor.apply();
    }

    /**
     * Returns the number of tries left.
     * If this value is zero, the user has lost!
     */
    public int getTriesLeft() {
        return tries;
    }

    /**
     * Checks to see if the supplied char has been guessed for
     * already, either erroneously or correctly.
     * @param c the letter to check
     * @return  true if letter has been used already,
     * false if letter is free
     */
    public boolean hasUsedLetter(char c) {
        for (char usedLetter : usedLetters) {
            if (c == usedLetter) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a String with all wrong guesses the user has made.
     * Example: "J, I, U"
     */
    public String getBadLettersUsed() {

        StringBuilder sb = new StringBuilder();

        boolean append;

        for (int i = 0; i < usedLetters.size(); i++) {

            append = true;

            char usedLetter = usedLetters.get(i);

            for (int j = 0; j < word.length(); j++) {
                if (usedLetter == word.charAt(j)) {
                    append = false;
                }
            }

            if (append) {
                sb.append(usedLetter);
                if (usedLetters.get(i) != (usedLetters.size() - 1)) {
                    sb.append(", ");
                }
            }
        }

        String badLetters = sb.toString();

        if (badLetters.endsWith(", ")) {
            return badLetters.substring(0, badLetters.length() - 2);
        }

        if (usedLetters.size() > 0) {
            return badLetters;
        } else {
            return "";
        }
    }

    /**
     * Checks to see if the user has guessed all letters correctly.
     * @return  true if user has won, i.e. all letters have been guessed
     * correctly. Returns false if the user has not done this yet
     */
    public boolean hasWon() {
        return getHiddenWord().equals(getRealWord());
    }

    /**
     * Checks to see if the user has used up all his or her guesses.
     * @return  true if user has lost (has no guesses left),
     * false if he or she can still play
     */
    public boolean hasLost() {
        return getTriesLeft() == 0;
    }

}
