package dharminnagar.hangman.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import dharminnagar.hangman.database.HangmanDatabaseHelper;
import dharminnagar.hangman.database.models.LetterTry;

/**
 * Data Access Object for Letter Tries operations
 */
public class LetterTryDao {

    private final HangmanDatabaseHelper dbHelper;

    public LetterTryDao(Context context) {
        this.dbHelper = HangmanDatabaseHelper.getInstance(context);
    }

    /**
     * Insert a new letter try record
     * @param letterTry The letter try to insert
     * @return The ID of the inserted try, or -1 if failed
     */
    public long insertLetterTry(LetterTry letterTry) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(HangmanDatabaseHelper.COLUMN_FK_GAME_ID, letterTry.getGameId());
        values.put(HangmanDatabaseHelper.COLUMN_LETTER, String.valueOf(letterTry.getLetter()));
        values.put(HangmanDatabaseHelper.COLUMN_IS_CORRECT, letterTry.isCorrect() ? 1 : 0);
        values.put(HangmanDatabaseHelper.COLUMN_TRY_ORDER, letterTry.getTryOrder());

        long tryId = db.insert(HangmanDatabaseHelper.TABLE_LETTER_TRIES, null, values);
        letterTry.setTryId(tryId);
        
        return tryId;
    }

    /**
     * Insert multiple letter tries in a batch
     * @param letterTries List of letter tries to insert
     * @return Number of successfully inserted tries
     */
    public int insertLetterTries(List<LetterTry> letterTries) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int inserted = 0;
        
        db.beginTransaction();
        try {
            for (LetterTry letterTry : letterTries) {
                ContentValues values = new ContentValues();
                values.put(HangmanDatabaseHelper.COLUMN_FK_GAME_ID, letterTry.getGameId());
                values.put(HangmanDatabaseHelper.COLUMN_LETTER, String.valueOf(letterTry.getLetter()));
                values.put(HangmanDatabaseHelper.COLUMN_IS_CORRECT, letterTry.isCorrect() ? 1 : 0);
                values.put(HangmanDatabaseHelper.COLUMN_TRY_ORDER, letterTry.getTryOrder());

                long tryId = db.insert(HangmanDatabaseHelper.TABLE_LETTER_TRIES, null, values);
                if (tryId != -1) {
                    letterTry.setTryId(tryId);
                    inserted++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        
        return inserted;
    }

    /**
     * Get all letter tries for a specific game
     * @param gameId The game ID
     * @return List of letter tries for the game
     */
    public List<LetterTry> getLetterTriesByGameId(long gameId) {
        List<LetterTry> letterTries = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] columns = {
            HangmanDatabaseHelper.COLUMN_TRY_ID,
            HangmanDatabaseHelper.COLUMN_FK_GAME_ID,
            HangmanDatabaseHelper.COLUMN_LETTER,
            HangmanDatabaseHelper.COLUMN_IS_CORRECT,
            HangmanDatabaseHelper.COLUMN_TRY_ORDER
        };

        Cursor cursor = db.query(
            HangmanDatabaseHelper.TABLE_LETTER_TRIES,
            columns,
            HangmanDatabaseHelper.COLUMN_FK_GAME_ID + " = ?",
            new String[]{String.valueOf(gameId)},
            null,
            null,
            HangmanDatabaseHelper.COLUMN_TRY_ORDER + " ASC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                LetterTry letterTry = cursorToLetterTry(cursor);
                letterTries.add(letterTry);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return letterTries;
    }

    /**
     * Get the count of tries for a specific game
     * @param gameId The game ID
     * @return Number of tries for the game
     */
    public int getTriesCountByGameId(long gameId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
            "SELECT COUNT(*) FROM " + HangmanDatabaseHelper.TABLE_LETTER_TRIES + 
            " WHERE " + HangmanDatabaseHelper.COLUMN_FK_GAME_ID + " = ?",
            new String[]{String.valueOf(gameId)}
        );
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }

    /**
     * Get correct letter tries for a specific game
     * @param gameId The game ID
     * @return List of correct letter tries
     */
    public List<LetterTry> getCorrectLetterTriesByGameId(long gameId) {
        List<LetterTry> letterTries = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] columns = {
            HangmanDatabaseHelper.COLUMN_TRY_ID,
            HangmanDatabaseHelper.COLUMN_FK_GAME_ID,
            HangmanDatabaseHelper.COLUMN_LETTER,
            HangmanDatabaseHelper.COLUMN_IS_CORRECT,
            HangmanDatabaseHelper.COLUMN_TRY_ORDER
        };

        Cursor cursor = db.query(
            HangmanDatabaseHelper.TABLE_LETTER_TRIES,
            columns,
            HangmanDatabaseHelper.COLUMN_FK_GAME_ID + " = ? AND " + 
            HangmanDatabaseHelper.COLUMN_IS_CORRECT + " = 1",
            new String[]{String.valueOf(gameId)},
            null,
            null,
            HangmanDatabaseHelper.COLUMN_TRY_ORDER + " ASC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                LetterTry letterTry = cursorToLetterTry(cursor);
                letterTries.add(letterTry);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return letterTries;
    }

    /**
     * Get wrong letter tries for a specific game
     * @param gameId The game ID
     * @return List of wrong letter tries
     */
    public List<LetterTry> getWrongLetterTriesByGameId(long gameId) {
        List<LetterTry> letterTries = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] columns = {
            HangmanDatabaseHelper.COLUMN_TRY_ID,
            HangmanDatabaseHelper.COLUMN_FK_GAME_ID,
            HangmanDatabaseHelper.COLUMN_LETTER,
            HangmanDatabaseHelper.COLUMN_IS_CORRECT,
            HangmanDatabaseHelper.COLUMN_TRY_ORDER
        };

        Cursor cursor = db.query(
            HangmanDatabaseHelper.TABLE_LETTER_TRIES,
            columns,
            HangmanDatabaseHelper.COLUMN_FK_GAME_ID + " = ? AND " + 
            HangmanDatabaseHelper.COLUMN_IS_CORRECT + " = 0",
            new String[]{String.valueOf(gameId)},
            null,
            null,
            HangmanDatabaseHelper.COLUMN_TRY_ORDER + " ASC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                LetterTry letterTry = cursorToLetterTry(cursor);
                letterTries.add(letterTry);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return letterTries;
    }

    /**
     * Delete all letter tries for a specific game
     * @param gameId The game ID
     * @return Number of rows deleted
     */
    public int deleteLetterTriesByGameId(long gameId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(
            HangmanDatabaseHelper.TABLE_LETTER_TRIES,
            HangmanDatabaseHelper.COLUMN_FK_GAME_ID + " = ?",
            new String[]{String.valueOf(gameId)}
        );
    }

    /**
     * Delete all letter tries
     * @return Number of rows deleted
     */
    public int deleteAllLetterTries() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(HangmanDatabaseHelper.TABLE_LETTER_TRIES, null, null);
    }

    /**
     * Helper method to convert cursor to LetterTry object
     */
    private LetterTry cursorToLetterTry(Cursor cursor) {
        LetterTry letterTry = new LetterTry();
        letterTry.setTryId(cursor.getLong(cursor.getColumnIndexOrThrow(HangmanDatabaseHelper.COLUMN_TRY_ID)));
        letterTry.setGameId(cursor.getLong(cursor.getColumnIndexOrThrow(HangmanDatabaseHelper.COLUMN_FK_GAME_ID)));
        String letterStr = cursor.getString(cursor.getColumnIndexOrThrow(HangmanDatabaseHelper.COLUMN_LETTER));
        letterTry.setLetter(letterStr.charAt(0));
        letterTry.setCorrect(cursor.getInt(cursor.getColumnIndexOrThrow(HangmanDatabaseHelper.COLUMN_IS_CORRECT)) == 1);
        letterTry.setTryOrder(cursor.getInt(cursor.getColumnIndexOrThrow(HangmanDatabaseHelper.COLUMN_TRY_ORDER)));
        return letterTry;
    }
}
