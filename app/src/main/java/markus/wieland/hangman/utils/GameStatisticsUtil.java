package markus.wieland.hangman.utils;

import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import markus.wieland.hangman.database.HangmanRepository;
import markus.wieland.hangman.database.models.GameHistory;
import markus.wieland.hangman.database.models.LetterTry;

/**
 * Utility class for viewing and managing game statistics
 */
public class GameStatisticsUtil {

    private static final String TAG = "GameStatisticsUtil";
    private final HangmanRepository repository;
    private final SimpleDateFormat dateFormat;

    public GameStatisticsUtil(Context context) {
        this.repository = HangmanRepository.getInstance(context);
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
    }

    /**
     * Print all game statistics to logcat
     */
    public void printStatistics() {
        Log.d(TAG, "========== GAME STATISTICS ==========");
        Log.d(TAG, repository.getStatisticsString());
        Log.d(TAG, "=====================================");
    }

    /**
     * Print all game history to logcat
     */
    public void printGameHistory() {
        List<GameHistory> games = repository.getAllGameHistory();
        
        Log.d(TAG, "========== GAME HISTORY ==========");
        Log.d(TAG, "Total games: " + games.size());
        
        for (GameHistory game : games) {
            printGameDetails(game);
        }
        
        Log.d(TAG, "==================================");
    }

    /**
     * Print recent game history
     */
    public void printRecentGameHistory(int limit) {
        List<GameHistory> games = repository.getRecentGameHistory(limit);
        
        Log.d(TAG, "========== RECENT GAME HISTORY (Last " + limit + ") ==========");
        
        for (GameHistory game : games) {
            printGameDetails(game);
        }
        
        Log.d(TAG, "==================================================");
    }

    /**
     * Print details of a specific game
     */
    public void printGameDetails(GameHistory game) {
        Log.d(TAG, "-----------------------------------");
        Log.d(TAG, "Game ID: " + game.getGameId());
        Log.d(TAG, "Word: " + game.getWord());
        Log.d(TAG, "Result: " + (game.isWin() ? "WIN" : "LOSS"));
        Log.d(TAG, "Total Attempts: " + game.getTotalAttempts());
        Log.d(TAG, "Wrong Attempts: " + game.getWrongAttempts());
        Log.d(TAG, "Custom Word: " + (game.isCustomWord() ? "Yes" : "No"));
        Log.d(TAG, "Date: " + dateFormat.format(game.getDate()));
        
        // Print letter tries if loaded
        if (!game.getLetterTries().isEmpty()) {
            Log.d(TAG, "Letter Tries:");
            for (LetterTry letterTry : game.getLetterTries()) {
                Log.d(TAG, "  " + letterTry.getTryOrder() + ". " + 
                      letterTry.getLetter() + " - " + 
                      (letterTry.isCorrect() ? "CORRECT" : "WRONG"));
            }
        }
    }

    /**
     * Print game history with letter tries
     */
    public void printCompleteGameHistory(long gameId) {
        GameHistory game = repository.getCompleteGameHistory(gameId);
        
        if (game != null) {
            Log.d(TAG, "========== COMPLETE GAME DETAILS ==========");
            printGameDetails(game);
            Log.d(TAG, "===========================================");
        } else {
            Log.d(TAG, "Game with ID " + gameId + " not found");
        }
    }

    /**
     * Get statistics as formatted string
     */
    public String getStatisticsString() {
        return repository.getStatisticsString();
    }

    /**
     * Clear all game history (use with caution!)
     */
    public void clearAllHistory() {
        int deleted = repository.deleteAllGameHistory();
        Log.d(TAG, "Deleted " + deleted + " game records");
    }

    /**
     * Get repository instance for direct access
     */
    public HangmanRepository getRepository() {
        return repository;
    }
}
