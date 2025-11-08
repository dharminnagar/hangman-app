package dharminnagar.hangman.database.models;

import androidx.annotation.NonNull;

/**
 * Model class representing a letter try in a game
 */
public class LetterTry {
    
    private long tryId;
    private long gameId;
    private char letter;
    private boolean isCorrect;
    private int tryOrder;

    public LetterTry() {
    }

    public LetterTry(long gameId, char letter, boolean isCorrect, int tryOrder) {
        this.gameId = gameId;
        this.letter = letter;
        this.isCorrect = isCorrect;
        this.tryOrder = tryOrder;
    }

    // Getters
    public long getTryId() {
        return tryId;
    }

    public long getGameId() {
        return gameId;
    }

    public char getLetter() {
        return letter;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public int getTryOrder() {
        return tryOrder;
    }

    // Setters
    public void setTryId(long tryId) {
        this.tryId = tryId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public void setLetter(char letter) {
        this.letter = letter;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public void setTryOrder(int tryOrder) {
        this.tryOrder = tryOrder;
    }

    @NonNull
    @Override
    public String toString() {
        return "LetterTry{" +
                "tryId=" + tryId +
                ", gameId=" + gameId +
                ", letter=" + letter +
                ", isCorrect=" + isCorrect +
                ", tryOrder=" + tryOrder +
                '}';
    }
}
