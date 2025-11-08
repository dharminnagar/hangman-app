package markus.wieland.hangman.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import markus.wieland.hangman.database.HangmanDatabaseHelper;
import markus.wieland.hangman.database.models.GameHistory;
import markus.wieland.hangman.database.models.LetterTry;

/**
 * Data Access Object for Game History operations
 */
public class GameHistoryDao {

    private final HangmanDatabaseHelper dbHelper;

    public GameHistoryDao(Context context) {
        this.dbHelper = HangmanDatabaseHelper.getInstance(context);
    }

    /**
     * Insert a new game history record
     * @param gameHistory The game history to insert
     * @return The ID of the inserted game, or -1 if failed
     */
    public long insertGameHistory(GameHistory gameHistory) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(HangmanDatabaseHelper.COLUMN_WORD, gameHistory.getWord());
        values.put(HangmanDatabaseHelper.COLUMN_IS_WIN, gameHistory.isWin() ? 1 : 0);
        values.put(HangmanDatabaseHelper.COLUMN_TOTAL_ATTEMPTS, gameHistory.getTotalAttempts());
        values.put(HangmanDatabaseHelper.COLUMN_WRONG_ATTEMPTS, gameHistory.getWrongAttempts());
        values.put(HangmanDatabaseHelper.COLUMN_TIMESTAMP, gameHistory.getTimestamp());
        values.put(HangmanDatabaseHelper.COLUMN_IS_CUSTOM_WORD, gameHistory.isCustomWord() ? 1 : 0);

        long gameId = db.insert(HangmanDatabaseHelper.TABLE_GAME_HISTORY, null, values);
        gameHistory.setGameId(gameId);
        
        return gameId;
    }

    /**
     * Get all game history records
     * @return List of all game history records
     */
    public List<GameHistory> getAllGameHistory() {
        List<GameHistory> gameHistoryList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] columns = {
            HangmanDatabaseHelper.COLUMN_GAME_ID,
            HangmanDatabaseHelper.COLUMN_WORD,
            HangmanDatabaseHelper.COLUMN_IS_WIN,
            HangmanDatabaseHelper.COLUMN_TOTAL_ATTEMPTS,
            HangmanDatabaseHelper.COLUMN_WRONG_ATTEMPTS,
            HangmanDatabaseHelper.COLUMN_TIMESTAMP,
            HangmanDatabaseHelper.COLUMN_IS_CUSTOM_WORD
        };

        Cursor cursor = db.query(
            HangmanDatabaseHelper.TABLE_GAME_HISTORY,
            columns,
            null,
            null,
            null,
            null,
            HangmanDatabaseHelper.COLUMN_TIMESTAMP + " DESC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                GameHistory gameHistory = cursorToGameHistory(cursor);
                gameHistoryList.add(gameHistory);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return gameHistoryList;
    }

    /**
     * Get game history by ID
     * @param gameId The game ID
     * @return GameHistory object or null if not found
     */
    public GameHistory getGameHistoryById(long gameId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] columns = {
            HangmanDatabaseHelper.COLUMN_GAME_ID,
            HangmanDatabaseHelper.COLUMN_WORD,
            HangmanDatabaseHelper.COLUMN_IS_WIN,
            HangmanDatabaseHelper.COLUMN_TOTAL_ATTEMPTS,
            HangmanDatabaseHelper.COLUMN_WRONG_ATTEMPTS,
            HangmanDatabaseHelper.COLUMN_TIMESTAMP,
            HangmanDatabaseHelper.COLUMN_IS_CUSTOM_WORD
        };

        Cursor cursor = db.query(
            HangmanDatabaseHelper.TABLE_GAME_HISTORY,
            columns,
            HangmanDatabaseHelper.COLUMN_GAME_ID + " = ?",
            new String[]{String.valueOf(gameId)},
            null,
            null,
            null
        );

        GameHistory gameHistory = null;
        if (cursor != null && cursor.moveToFirst()) {
            gameHistory = cursorToGameHistory(cursor);
            cursor.close();
        }

        return gameHistory;
    }

    /**
     * Get recent game history with limit
     * @param limit Maximum number of records to return
     * @return List of recent game history records
     */
    public List<GameHistory> getRecentGameHistory(int limit) {
        List<GameHistory> gameHistoryList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] columns = {
            HangmanDatabaseHelper.COLUMN_GAME_ID,
            HangmanDatabaseHelper.COLUMN_WORD,
            HangmanDatabaseHelper.COLUMN_IS_WIN,
            HangmanDatabaseHelper.COLUMN_TOTAL_ATTEMPTS,
            HangmanDatabaseHelper.COLUMN_WRONG_ATTEMPTS,
            HangmanDatabaseHelper.COLUMN_TIMESTAMP,
            HangmanDatabaseHelper.COLUMN_IS_CUSTOM_WORD
        };

        Cursor cursor = db.query(
            HangmanDatabaseHelper.TABLE_GAME_HISTORY,
            columns,
            null,
            null,
            null,
            null,
            HangmanDatabaseHelper.COLUMN_TIMESTAMP + " DESC",
            String.valueOf(limit)
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                GameHistory gameHistory = cursorToGameHistory(cursor);
                gameHistoryList.add(gameHistory);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return gameHistoryList;
    }

    /**
     * Get total number of games played
     */
    public int getTotalGamesPlayed() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + HangmanDatabaseHelper.TABLE_GAME_HISTORY, null);
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }

    /**
     * Get total number of wins
     */
    public int getTotalWins() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
            "SELECT COUNT(*) FROM " + HangmanDatabaseHelper.TABLE_GAME_HISTORY + 
            " WHERE " + HangmanDatabaseHelper.COLUMN_IS_WIN + " = 1", 
            null
        );
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }

    /**
     * Get total number of losses
     */
    public int getTotalLosses() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
            "SELECT COUNT(*) FROM " + HangmanDatabaseHelper.TABLE_GAME_HISTORY + 
            " WHERE " + HangmanDatabaseHelper.COLUMN_IS_WIN + " = 0", 
            null
        );
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }

    /**
     * Get all games with a specific word
     * @param word The word to search for
     * @return List of game history records with the specified word
     */
    public List<GameHistory> getGamesByWord(String word) {
        List<GameHistory> gameHistoryList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] columns = {
            HangmanDatabaseHelper.COLUMN_GAME_ID,
            HangmanDatabaseHelper.COLUMN_WORD,
            HangmanDatabaseHelper.COLUMN_IS_WIN,
            HangmanDatabaseHelper.COLUMN_TOTAL_ATTEMPTS,
            HangmanDatabaseHelper.COLUMN_WRONG_ATTEMPTS,
            HangmanDatabaseHelper.COLUMN_TIMESTAMP,
            HangmanDatabaseHelper.COLUMN_IS_CUSTOM_WORD
        };

        Cursor cursor = db.query(
            HangmanDatabaseHelper.TABLE_GAME_HISTORY,
            columns,
            HangmanDatabaseHelper.COLUMN_WORD + " = ?",
            new String[]{word},
            null,
            null,
            HangmanDatabaseHelper.COLUMN_TIMESTAMP + " DESC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                GameHistory gameHistory = cursorToGameHistory(cursor);
                gameHistoryList.add(gameHistory);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return gameHistoryList;
    }

    /**
     * Delete a game history record by ID
     * @param gameId The game ID to delete
     * @return Number of rows deleted
     */
    public int deleteGameHistory(long gameId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(
            HangmanDatabaseHelper.TABLE_GAME_HISTORY,
            HangmanDatabaseHelper.COLUMN_GAME_ID + " = ?",
            new String[]{String.valueOf(gameId)}
        );
    }

    /**
     * Delete all game history records
     * @return Number of rows deleted
     */
    public int deleteAllGameHistory() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(HangmanDatabaseHelper.TABLE_GAME_HISTORY, null, null);
    }

    /**
     * Helper method to convert cursor to GameHistory object
     */
    private GameHistory cursorToGameHistory(Cursor cursor) {
        GameHistory gameHistory = new GameHistory();
        gameHistory.setGameId(cursor.getLong(cursor.getColumnIndexOrThrow(HangmanDatabaseHelper.COLUMN_GAME_ID)));
        gameHistory.setWord(cursor.getString(cursor.getColumnIndexOrThrow(HangmanDatabaseHelper.COLUMN_WORD)));
        gameHistory.setWin(cursor.getInt(cursor.getColumnIndexOrThrow(HangmanDatabaseHelper.COLUMN_IS_WIN)) == 1);
        gameHistory.setTotalAttempts(cursor.getInt(cursor.getColumnIndexOrThrow(HangmanDatabaseHelper.COLUMN_TOTAL_ATTEMPTS)));
        gameHistory.setWrongAttempts(cursor.getInt(cursor.getColumnIndexOrThrow(HangmanDatabaseHelper.COLUMN_WRONG_ATTEMPTS)));
        gameHistory.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(HangmanDatabaseHelper.COLUMN_TIMESTAMP)));
        gameHistory.setCustomWord(cursor.getInt(cursor.getColumnIndexOrThrow(HangmanDatabaseHelper.COLUMN_IS_CUSTOM_WORD)) == 1);
        return gameHistory;
    }
}
