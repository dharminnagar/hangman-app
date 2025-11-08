package markus.wieland.hangman.database;

import android.content.Context;

import java.util.List;

import markus.wieland.hangman.database.dao.GameHistoryDao;
import markus.wieland.hangman.database.dao.LetterTryDao;
import markus.wieland.hangman.database.models.GameHistory;
import markus.wieland.hangman.database.models.LetterTry;

/**
 * Repository class that provides a unified interface for database operations.
 * This class combines the functionality of GameHistoryDao and LetterTryDao.
 */
public class HangmanRepository {

    private static HangmanRepository instance;
    private final GameHistoryDao gameHistoryDao;
    private final LetterTryDao letterTryDao;

    private HangmanRepository(Context context) {
        this.gameHistoryDao = new GameHistoryDao(context);
        this.letterTryDao = new LetterTryDao(context);
    }

    /**
     * Get singleton instance of the repository
     */
    public static synchronized HangmanRepository getInstance(Context context) {
        if (instance == null) {
            instance = new HangmanRepository(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Save a complete game with all its letter tries
     * @param gameHistory The game history to save
     * @param letterTries List of letter tries for the game
     * @return The game ID if successful, -1 otherwise
     */
    public long saveGame(GameHistory gameHistory, List<LetterTry> letterTries) {
        long gameId = gameHistoryDao.insertGameHistory(gameHistory);
        
        if (gameId != -1 && letterTries != null && !letterTries.isEmpty()) {
            // Set the game ID for all letter tries
            for (LetterTry letterTry : letterTries) {
                letterTry.setGameId(gameId);
            }
            letterTryDao.insertLetterTries(letterTries);
        }
        
        return gameId;
    }

    /**
     * Get complete game history with letter tries
     * @param gameId The game ID
     * @return GameHistory with letter tries loaded
     */
    public GameHistory getCompleteGameHistory(long gameId) {
        GameHistory gameHistory = gameHistoryDao.getGameHistoryById(gameId);
        if (gameHistory != null) {
            List<LetterTry> letterTries = letterTryDao.getLetterTriesByGameId(gameId);
            gameHistory.setLetterTries(letterTries);
        }
        return gameHistory;
    }

    /**
     * Get all games with their letter tries
     */
    public List<GameHistory> getAllGamesWithTries() {
        List<GameHistory> games = gameHistoryDao.getAllGameHistory();
        for (GameHistory game : games) {
            List<LetterTry> tries = letterTryDao.getLetterTriesByGameId(game.getGameId());
            game.setLetterTries(tries);
        }
        return games;
    }

    /**
     * Get recent games with their letter tries
     */
    public List<GameHistory> getRecentGamesWithTries(int limit) {
        List<GameHistory> games = gameHistoryDao.getRecentGameHistory(limit);
        for (GameHistory game : games) {
            List<LetterTry> tries = letterTryDao.getLetterTriesByGameId(game.getGameId());
            game.setLetterTries(tries);
        }
        return games;
    }

    // Game History methods
    public List<GameHistory> getAllGameHistory() {
        return gameHistoryDao.getAllGameHistory();
    }

    public List<GameHistory> getRecentGameHistory(int limit) {
        return gameHistoryDao.getRecentGameHistory(limit);
    }

    public GameHistory getGameHistoryById(long gameId) {
        return gameHistoryDao.getGameHistoryById(gameId);
    }

    public List<GameHistory> getGamesByWord(String word) {
        return gameHistoryDao.getGamesByWord(word);
    }

    public int getTotalGamesPlayed() {
        return gameHistoryDao.getTotalGamesPlayed();
    }

    public int getTotalWins() {
        return gameHistoryDao.getTotalWins();
    }

    public int getTotalLosses() {
        return gameHistoryDao.getTotalLosses();
    }

    public int deleteGameHistory(long gameId) {
        return gameHistoryDao.deleteGameHistory(gameId);
    }

    public int deleteAllGameHistory() {
        return gameHistoryDao.deleteAllGameHistory();
    }

    // Letter Try methods
    public List<LetterTry> getLetterTriesByGameId(long gameId) {
        return letterTryDao.getLetterTriesByGameId(gameId);
    }

    public List<LetterTry> getCorrectLetterTriesByGameId(long gameId) {
        return letterTryDao.getCorrectLetterTriesByGameId(gameId);
    }

    public List<LetterTry> getWrongLetterTriesByGameId(long gameId) {
        return letterTryDao.getWrongLetterTriesByGameId(gameId);
    }

    public int getTriesCountByGameId(long gameId) {
        return letterTryDao.getTriesCountByGameId(gameId);
    }

    /**
     * Get win rate as a percentage
     */
    public double getWinRate() {
        int total = getTotalGamesPlayed();
        if (total == 0) return 0.0;
        int wins = getTotalWins();
        return (wins * 100.0) / total;
    }

    /**
     * Get statistics as a formatted string
     */
    public String getStatisticsString() {
        int total = getTotalGamesPlayed();
        int wins = getTotalWins();
        int losses = getTotalLosses();
        double winRate = getWinRate();
        
        return String.format("Total Games: %d\nWins: %d\nLosses: %d\nWin Rate: %.1f%%", 
                            total, wins, losses, winRate);
    }
}
