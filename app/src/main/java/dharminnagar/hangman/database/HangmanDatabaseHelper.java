package dharminnagar.hangman.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database helper for managing the Hangman game database.
 * Stores game history and individual letter tries.
 */
public class HangmanDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "hangman.db";
    private static final int DATABASE_VERSION = 1;

    // Game History Table
    public static final String TABLE_GAME_HISTORY = "game_history";
    public static final String COLUMN_GAME_ID = "game_id";
    public static final String COLUMN_WORD = "word";
    public static final String COLUMN_IS_WIN = "is_win";
    public static final String COLUMN_TOTAL_ATTEMPTS = "total_attempts";
    public static final String COLUMN_WRONG_ATTEMPTS = "wrong_attempts";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_IS_CUSTOM_WORD = "is_custom_word";

    // Letter Tries Table
    public static final String TABLE_LETTER_TRIES = "letter_tries";
    public static final String COLUMN_TRY_ID = "try_id";
    public static final String COLUMN_FK_GAME_ID = "game_id";
    public static final String COLUMN_LETTER = "letter";
    public static final String COLUMN_IS_CORRECT = "is_correct";
    public static final String COLUMN_TRY_ORDER = "try_order";

    // Create Game History Table
    private static final String CREATE_GAME_HISTORY_TABLE = 
        "CREATE TABLE " + TABLE_GAME_HISTORY + " (" +
        COLUMN_GAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        COLUMN_WORD + " TEXT NOT NULL, " +
        COLUMN_IS_WIN + " INTEGER NOT NULL, " +
        COLUMN_TOTAL_ATTEMPTS + " INTEGER NOT NULL, " +
        COLUMN_WRONG_ATTEMPTS + " INTEGER NOT NULL, " +
        COLUMN_TIMESTAMP + " INTEGER NOT NULL, " +
        COLUMN_IS_CUSTOM_WORD + " INTEGER NOT NULL DEFAULT 0" +
        ");";

    // Create Letter Tries Table
    private static final String CREATE_LETTER_TRIES_TABLE = 
        "CREATE TABLE " + TABLE_LETTER_TRIES + " (" +
        COLUMN_TRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        COLUMN_FK_GAME_ID + " INTEGER NOT NULL, " +
        COLUMN_LETTER + " TEXT NOT NULL, " +
        COLUMN_IS_CORRECT + " INTEGER NOT NULL, " +
        COLUMN_TRY_ORDER + " INTEGER NOT NULL, " +
        "FOREIGN KEY(" + COLUMN_FK_GAME_ID + ") REFERENCES " + 
        TABLE_GAME_HISTORY + "(" + COLUMN_GAME_ID + ") ON DELETE CASCADE" +
        ");";

    // Create index for faster queries
    private static final String CREATE_TIMESTAMP_INDEX = 
        "CREATE INDEX idx_timestamp ON " + TABLE_GAME_HISTORY + "(" + COLUMN_TIMESTAMP + ");";

    private static final String CREATE_GAME_ID_INDEX = 
        "CREATE INDEX idx_game_id ON " + TABLE_LETTER_TRIES + "(" + COLUMN_FK_GAME_ID + ");";

    private static HangmanDatabaseHelper instance;

    private HangmanDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Get singleton instance of the database helper
     */
    public static synchronized HangmanDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new HangmanDatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_GAME_HISTORY_TABLE);
        db.execSQL(CREATE_LETTER_TRIES_TABLE);
        db.execSQL(CREATE_TIMESTAMP_INDEX);
        db.execSQL(CREATE_GAME_ID_INDEX);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // For future versions, add migration logic here
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LETTER_TRIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAME_HISTORY);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
}
