package markus.wieland.hangman.database.models;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Model class representing a game history record
 */
public class GameHistory {
    
    private long gameId;
    private String word;
    private boolean isWin;
    private int totalAttempts;
    private int wrongAttempts;
    private long timestamp;
    private boolean isCustomWord;
    private List<LetterTry> letterTries;

    public GameHistory() {
        this.letterTries = new ArrayList<>();
    }

    public GameHistory(String word, boolean isWin, int totalAttempts, int wrongAttempts, boolean isCustomWord) {
        this.word = word;
        this.isWin = isWin;
        this.totalAttempts = totalAttempts;
        this.wrongAttempts = wrongAttempts;
        this.timestamp = System.currentTimeMillis();
        this.isCustomWord = isCustomWord;
        this.letterTries = new ArrayList<>();
    }

    // Getters
    public long getGameId() {
        return gameId;
    }

    public String getWord() {
        return word;
    }

    public boolean isWin() {
        return isWin;
    }

    public int getTotalAttempts() {
        return totalAttempts;
    }

    public int getWrongAttempts() {
        return wrongAttempts;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Date getDate() {
        return new Date(timestamp);
    }

    public boolean isCustomWord() {
        return isCustomWord;
    }

    public List<LetterTry> getLetterTries() {
        return letterTries;
    }

    // Setters
    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setWin(boolean win) {
        isWin = win;
    }

    public void setTotalAttempts(int totalAttempts) {
        this.totalAttempts = totalAttempts;
    }

    public void setWrongAttempts(int wrongAttempts) {
        this.wrongAttempts = wrongAttempts;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setCustomWord(boolean customWord) {
        isCustomWord = customWord;
    }

    public void setLetterTries(List<LetterTry> letterTries) {
        this.letterTries = letterTries;
    }

    public void addLetterTry(LetterTry letterTry) {
        this.letterTries.add(letterTry);
    }

    @NonNull
    @Override
    public String toString() {
        return "GameHistory{" +
                "gameId=" + gameId +
                ", word='" + word + '\'' +
                ", isWin=" + isWin +
                ", totalAttempts=" + totalAttempts +
                ", wrongAttempts=" + wrongAttempts +
                ", timestamp=" + timestamp +
                ", isCustomWord=" + isCustomWord +
                '}';
    }
}
