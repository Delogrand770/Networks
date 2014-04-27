
import java.util.Random;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author C14Gavin.Delphia
 */
public class Mastermind {

    private String alphabet = "abcdef";
    private String scrambled;
    private int tries = 1;
    public final int MAXGUESSES = 10;
    public final int PUZZLESIZE = 4;
    private String[] history = new String[MAXGUESSES];
    private int historyIndex = 0;
    private String gameState;

    public Mastermind() {
        System.out.println("\nStarting new Mastermind Game");
        tries = 1;
        gameState = "playing";
        shuffleCharacters();
        System.out.println(toString());
    }

    public Mastermind(String pattern) {
        System.out.println("\nStarting new Mastermind Game with custom pattern: " + pattern);
        tries = 1;
        gameState = "playing";
        scrambled = pattern;
        System.out.println(toString());
    }

    private void shuffleCharacters() {
        Random rand = new Random();
        int len = alphabet.length();
        boolean[] used = new boolean[len];
        String scrambledChars = "";
        while (scrambledChars.length() < PUZZLESIZE) {
            int index = rand.nextInt(len);
            if (used[index] == false) {
                scrambledChars += alphabet.charAt(index);
                used[index] = true;
            }
        }
        scrambled = scrambledChars;
    }

    public void addHistory(String data) {
        history[historyIndex] = data;
        historyIndex++;
    }

    public void resetHistory() {
        historyIndex = 0;
        history = new String[MAXGUESSES];
    }

    public void showHistory() {
        System.out.println("History");
        for (int i = 0; i < historyIndex; i++) {
            System.out.println("\t" + i + ". " + history[i]);
        }
    }

    public String getSendableHistory() {
        String data = "history";
        for (int i = 0; i < historyIndex; i++) {
            data += "," + history[i];
        }
        return data;
    }

    public String processGuess(String guess) {
        tries++;

        String data = "<";
        for (int i = 0; i < PUZZLESIZE; i++) {
            if (guess.charAt(i) == scrambled.charAt(i)) {
                data += " CORRECT";
            } else {
                data += " INCORRECT";
            }
        }
        data += " > (" + (MAXGUESSES - tries + 1) + " guesses remain)";
        if (tries > MAXGUESSES) {
            gameState = "outOfGuesses";
        }
        if (guess.equalsIgnoreCase(scrambled)) {
            gameState = "patternMatched";
        }
        addHistory(guess + " " + data);
        return data;
    }

    public String getSolution() {
        return scrambled;
    }

    public int getNumTries() {
        return tries;
    }

    public String getGameState() {
        return gameState;
    }

    @Override
    public final String toString() {
        return "\tSolution: " + getSolution() + "\n\tRemaining Guesses: " + (MAXGUESSES - tries + 1) + "\n";
    }
}
