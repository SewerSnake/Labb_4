package com.example.hangman;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * @author Eric Groseclos
 * @version 1.0
 */
public class Hangman {

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
    public Hangman(ArrayList<String> allWords) {

        this.allWords = allWords;

        tries = 10;

        word = " ";

        newWord();

    }

    /**
     * Choose a new word. Changes the current word, by randomly
     * choosing a word from all available words.
     */
    public void newWord() {
        Random random = new Random();
        int randomWord = random.nextInt(allWords.size());

        word = allWords.get(randomWord);

        visible = new boolean[word.length()];

        for (int i = 0; i < word.length(); i++) {
            visible[i] = false;
        }
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
        }
        usedLetters.add(guess);
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
        return badLetters;
    }

    /**
     * Checks to see if the user has guessed all letters correctly.
     * @return  true if user has won, i.e. all letters have been guessed
     * correctly. Returns false if the user has not done this yet
     */
    public boolean hasWon() {
        if (getHiddenWord().equals(getRealWord())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks to see if the user has used up all his or her guesses.
     * @return  true if user has lost (has no guesses left),
     * false if he or she can still play
     */
    public boolean hasLost() {
        if (getTriesLeft() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public static char getUserInput() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter your guess here: ");
        String input = sc.nextLine();
        char letter = input.charAt(0);

        if (input.length() > 1) {
            System.out.println("You can only enter one letter!");
            letter = getUserInput();
            //Remove this else if-statement if Å, Ä and Ö are among the letters in a word!
        } else if (!(letter >= 'A' && letter <= 'Z')) {
            System.out.println("You must enter capital letters! In other words, from A to Z!");
            letter = getUserInput();
        }

        return letter;
    }

}
