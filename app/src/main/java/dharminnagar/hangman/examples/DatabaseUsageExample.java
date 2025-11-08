package dharminnagar.hangman.examples;

import android.content.Context;
import android.util.Log;

import java.util.List;

import dharminnagar.hangman.database.HangmanRepository;
import dharminnagar.hangman.database.models.GameHistory;
import dharminnagar.hangman.database.models.LetterTry;
import dharminnagar.hangman.utils.GameStatisticsUtil;

/**
 * Example class demonstrating how to use the Hangman database
 * This is for demonstration purposes only and is not used in the main app
 * (the app automatically saves games in HangmanActivity)
 */
public class DatabaseUsageExample {

    private static final String TAG = "DatabaseExample";
    private final HangmanRepository repository;
    private final GameStatisticsUtil statsUtil;

    public DatabaseUsageExample(Context context) {
        this.repository = HangmanRepository.getInstance(context);
        this.statsUtil = new GameStatisticsUtil(context);
    }

    /**
     * Example 1: View overall statistics
     */
    public void exampleViewStatistics() {
        Log.d(TAG, "=== EXAMPLE 1: View Statistics ===");
        
        int totalGames = repository.getTotalGamesPlayed();
        int wins = repository.getTotalWins();
        int losses = repository.getTotalLosses();
        double winRate = repository.getWinRate();
        
        Log.d(TAG, "Total Games: " + totalGames);
        Log.d(TAG, "Wins: " + wins);
        Log.d(TAG, "Losses: " + losses);
        Log.d(TAG, "Win Rate: " + String.format("%.1f%%", winRate));
        
        // Or use the utility method
        statsUtil.printStatistics();
    }

    /**
     * Example 2: View recent game history
     */
    public void exampleViewRecentGames() {
        Log.d(TAG, "=== EXAMPLE 2: Recent Games ===");
        
        List<GameHistory> recentGames = repository.getRecentGameHistory(5);
        
        for (GameHistory game : recentGames) {
            Log.d(TAG, String.format("Game #%d: %s - %s (Attempts: %d, Wrong: %d)",
                    game.getGameId(),
                    game.getWord(),
                    game.isWin() ? "WIN" : "LOSS",
                    game.getTotalAttempts(),
                    game.getWrongAttempts()));
        }
        
        // Or use the utility method
        statsUtil.printRecentGameHistory(5);
    }

    /**
     * Example 3: View complete game details with letter tries
     */
    public void exampleViewCompleteGame(long gameId) {
        Log.d(TAG, "=== EXAMPLE 3: Complete Game Details ===");
        
        GameHistory game = repository.getCompleteGameHistory(gameId);
        
        if (game != null) {
            Log.d(TAG, "Word: " + game.getWord());
            Log.d(TAG, "Result: " + (game.isWin() ? "WIN" : "LOSS"));
            Log.d(TAG, "Date: " + game.getDate().toString());
            
            Log.d(TAG, "Letter tries in order:");
            for (LetterTry letterTry : game.getLetterTries()) {
                Log.d(TAG, String.format("  %d. %c - %s",
                        letterTry.getTryOrder(),
                        letterTry.getLetter(),
                        letterTry.isCorrect() ? "CORRECT ✓" : "WRONG ✗"));
            }
        }
        
        // Or use the utility method
        statsUtil.printCompleteGameHistory(gameId);
    }

    /**
     * Example 4: Search games by word
     */
    public void exampleSearchGamesByWord(String word) {
        Log.d(TAG, "=== EXAMPLE 4: Search by Word ===");
        
        List<GameHistory> games = repository.getGamesByWord(word);
        
        Log.d(TAG, "Found " + games.size() + " games with word: " + word);
        
        for (GameHistory game : games) {
            Log.d(TAG, String.format("Game #%d on %s - %s",
                    game.getGameId(),
                    game.getDate().toString(),
                    game.isWin() ? "WIN" : "LOSS"));
        }
    }

    /**
     * Example 5: View all games with their tries
     */
    public void exampleViewAllGamesWithTries() {
        Log.d(TAG, "=== EXAMPLE 5: All Games with Tries ===");
        
        List<GameHistory> allGames = repository.getAllGamesWithTries();
        
        for (GameHistory game : allGames) {
            Log.d(TAG, "---");
            Log.d(TAG, "Game #" + game.getGameId() + ": " + game.getWord());
            Log.d(TAG, "Letters tried: " + game.getLetterTries().size());
            
            // Show the sequence of letters
            StringBuilder letterSequence = new StringBuilder("Sequence: ");
            for (LetterTry letterTry : game.getLetterTries()) {
                letterSequence.append(letterTry.getLetter());
                letterSequence.append(letterTry.isCorrect() ? "✓ " : "✗ ");
            }
            Log.d(TAG, letterSequence.toString());
        }
    }

    /**
     * Example 6: Analyze wrong guesses
     */
    public void exampleAnalyzeWrongGuesses(long gameId) {
        Log.d(TAG, "=== EXAMPLE 6: Wrong Guesses Analysis ===");
        
        GameHistory game = repository.getGameHistoryById(gameId);
        List<LetterTry> wrongTries = repository.getWrongLetterTriesByGameId(gameId);
        
        if (game != null) {
            Log.d(TAG, "Game #" + gameId + ": " + game.getWord());
            Log.d(TAG, "Wrong letters guessed: " + wrongTries.size());
            
            StringBuilder wrongLetters = new StringBuilder("Wrong letters: ");
            for (LetterTry letterTry : wrongTries) {
                wrongLetters.append(letterTry.getLetter()).append(" ");
            }
            Log.d(TAG, wrongLetters.toString());
        }
    }

    /**
     * Example 7: Compare performance on custom vs random words
     */
    public void exampleCompareCustomVsRandom() {
        Log.d(TAG, "=== EXAMPLE 7: Custom vs Random Words ===");
        
        List<GameHistory> allGames = repository.getAllGameHistory();
        
        int customGames = 0, customWins = 0;
        int randomGames = 0, randomWins = 0;
        
        for (GameHistory game : allGames) {
            if (game.isCustomWord()) {
                customGames++;
                if (game.isWin()) customWins++;
            } else {
                randomGames++;
                if (game.isWin()) randomWins++;
            }
        }
        
        double customWinRate = customGames > 0 ? (customWins * 100.0 / customGames) : 0;
        double randomWinRate = randomGames > 0 ? (randomWins * 100.0 / randomGames) : 0;
        
        Log.d(TAG, "Custom words: " + customGames + " games, " + 
              String.format("%.1f%% win rate", customWinRate));
        Log.d(TAG, "Random words: " + randomGames + " games, " + 
              String.format("%.1f%% win rate", randomWinRate));
    }

    /**
     * Example 8: Find most difficult words (most attempts)
     */
    public void exampleFindMostDifficultWords() {
        Log.d(TAG, "=== EXAMPLE 8: Most Difficult Words ===");
        
        List<GameHistory> allGames = repository.getAllGameHistory();
        
        // Sort by total attempts (descending)
        allGames.sort((g1, g2) -> Integer.compare(g2.getTotalAttempts(), g1.getTotalAttempts()));
        
        Log.d(TAG, "Top 5 most difficult words:");
        for (int i = 0; i < Math.min(5, allGames.size()); i++) {
            GameHistory game = allGames.get(i);
            Log.d(TAG, String.format("%d. %s - %d attempts, %d wrong (%s)",
                    i + 1,
                    game.getWord(),
                    game.getTotalAttempts(),
                    game.getWrongAttempts(),
                    game.isWin() ? "Won" : "Lost"));
        }
    }

    /**
     * Example 9: Calculate average attempts per game
     */
    public void exampleCalculateAverages() {
        Log.d(TAG, "=== EXAMPLE 9: Average Statistics ===");
        
        List<GameHistory> allGames = repository.getAllGameHistory();
        
        if (allGames.isEmpty()) {
            Log.d(TAG, "No games played yet");
            return;
        }
        
        int totalAttempts = 0;
        int totalWrongAttempts = 0;
        
        for (GameHistory game : allGames) {
            totalAttempts += game.getTotalAttempts();
            totalWrongAttempts += game.getWrongAttempts();
        }
        
        double avgAttempts = (double) totalAttempts / allGames.size();
        double avgWrongAttempts = (double) totalWrongAttempts / allGames.size();
        
        Log.d(TAG, String.format("Average attempts per game: %.1f", avgAttempts));
        Log.d(TAG, String.format("Average wrong attempts per game: %.1f", avgWrongAttempts));
    }

    /**
     * Example 10: Delete specific game
     */
    public void exampleDeleteGame(long gameId) {
        Log.d(TAG, "=== EXAMPLE 10: Delete Game ===");
        
        GameHistory game = repository.getGameHistoryById(gameId);
        if (game != null) {
            Log.d(TAG, "Deleting game #" + gameId + ": " + game.getWord());
            int deleted = repository.deleteGameHistory(gameId);
            Log.d(TAG, "Deleted " + deleted + " record(s)");
        }
    }

    /**
     * Run all examples (for testing)
     */
    public void runAllExamples() {
        exampleViewStatistics();
        exampleViewRecentGames();
        
        // Only run these if there are games in the database
        if (repository.getTotalGamesPlayed() > 0) {
            List<GameHistory> games = repository.getRecentGameHistory(1);
            if (!games.isEmpty()) {
                long gameId = games.get(0).getGameId();
                exampleViewCompleteGame(gameId);
                exampleAnalyzeWrongGuesses(gameId);
                exampleSearchGamesByWord(games.get(0).getWord());
            }
            
            exampleViewAllGamesWithTries();
            exampleCompareCustomVsRandom();
            exampleFindMostDifficultWords();
            exampleCalculateAverages();
        }
    }
}
