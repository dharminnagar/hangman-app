# Hangman Database Implementation

## Overview

This implementation adds SQL database functionality to the Hangman Android app for storing game history and individual letter tries. The database automatically tracks all games played, including:

- Words guessed (both custom and random)
- Win/loss results
- Number of attempts and errors
- Individual letter tries for each game
- Timestamps for each game

## Architecture

The database implementation follows a clean architecture pattern with the following components:

### 1. Database Layer (`database/`)

#### `HangmanDatabaseHelper.java`
- SQLite database helper using `SQLiteOpenHelper`
- Manages database creation and versioning
- Defines two tables:
  - **game_history**: Stores overall game information
  - **letter_tries**: Stores individual letter attempts with foreign key to game_history

#### Tables Schema

**game_history**
```sql
CREATE TABLE game_history (
    game_id INTEGER PRIMARY KEY AUTOINCREMENT,
    word TEXT NOT NULL,
    is_win INTEGER NOT NULL,
    total_attempts INTEGER NOT NULL,
    wrong_attempts INTEGER NOT NULL,
    timestamp INTEGER NOT NULL,
    is_custom_word INTEGER NOT NULL DEFAULT 0
);
```

**letter_tries**
```sql
CREATE TABLE letter_tries (
    try_id INTEGER PRIMARY KEY AUTOINCREMENT,
    game_id INTEGER NOT NULL,
    letter TEXT NOT NULL,
    is_correct INTEGER NOT NULL,
    try_order INTEGER NOT NULL,
    FOREIGN KEY(game_id) REFERENCES game_history(game_id) ON DELETE CASCADE
);
```

### 2. Data Models (`database/models/`)

#### `GameHistory.java`
- Model class representing a complete game record
- Includes fields for game ID, word, win/loss status, attempts, timestamp
- Can hold a list of associated `LetterTry` objects

#### `LetterTry.java`
- Model class representing a single letter attempt
- Includes fields for try ID, game ID, letter, correctness, and order

### 3. Data Access Objects (`database/dao/`)

#### `GameHistoryDao.java`
Provides CRUD operations for game history:
- `insertGameHistory()` - Save a new game
- `getAllGameHistory()` - Get all games
- `getRecentGameHistory(limit)` - Get recent games
- `getGameHistoryById(id)` - Get specific game
- `getGamesByWord(word)` - Find games by word
- `getTotalGamesPlayed()` - Get total count
- `getTotalWins()` - Get wins count
- `getTotalLosses()` - Get losses count
- `deleteGameHistory(id)` - Delete specific game
- `deleteAllGameHistory()` - Clear all history

#### `LetterTryDao.java`
Provides CRUD operations for letter tries:
- `insertLetterTry()` - Save a single try
- `insertLetterTries()` - Batch insert tries
- `getLetterTriesByGameId()` - Get all tries for a game
- `getCorrectLetterTriesByGameId()` - Get only correct tries
- `getWrongLetterTriesByGameId()` - Get only wrong tries
- `getTriesCountByGameId()` - Get count of tries
- `deleteLetterTriesByGameId()` - Delete tries for a game

### 4. Repository Layer (`database/`)

#### `HangmanRepository.java`
- Singleton repository pattern
- Combines GameHistoryDao and LetterTryDao
- Provides high-level operations:
  - `saveGame(gameHistory, letterTries)` - Save complete game with tries
  - `getCompleteGameHistory(id)` - Get game with all tries loaded
  - `getAllGamesWithTries()` - Get all games with tries
  - `getRecentGamesWithTries(limit)` - Get recent games with tries
  - `getWinRate()` - Calculate win percentage
  - `getStatisticsString()` - Get formatted statistics

### 5. Utilities (`utils/`)

#### `GameStatisticsUtil.java`
Helper class for viewing and debugging game statistics:
- `printStatistics()` - Print overall stats to logcat
- `printGameHistory()` - Print all games to logcat
- `printRecentGameHistory(limit)` - Print recent games
- `printGameDetails(game)` - Print specific game details
- `printCompleteGameHistory(id)` - Print game with all tries
- `clearAllHistory()` - Delete all records

## Integration

The database is integrated into the existing game flow:

### Modified Files

1. **`Hangman.java`**
   - Added `letterTries` list to track each letter attempt
   - Added `tryOrder` counter
   - Modified `onClick()` to record each letter try
   - Added `getLetterTries()` method

2. **`HangmanActivity.java`**
   - Added `repository` field
   - Added `isCustomWord` flag
   - Initialize repository in `onCreate()`
   - Track whether word is custom in `initializeGenerator()`
   - Save game to database in `onGameFinish()`
   - Added `saveGameToDatabase()` method

## Usage

### Automatic Saving
Games are automatically saved to the database when they finish. No additional code is required.

### Viewing Statistics

#### Using GameStatisticsUtil
```java
GameStatisticsUtil statsUtil = new GameStatisticsUtil(context);

// Print overall statistics
statsUtil.printStatistics();

// Print all game history
statsUtil.printGameHistory();

// Print recent 10 games
statsUtil.printRecentGameHistory(10);

// Print specific game with all tries
statsUtil.printCompleteGameHistory(gameId);
```

#### Using Repository Directly
```java
HangmanRepository repository = HangmanRepository.getInstance(context);

// Get statistics
int totalGames = repository.getTotalGamesPlayed();
int wins = repository.getTotalWins();
int losses = repository.getTotalLosses();
double winRate = repository.getWinRate();

// Get recent games
List<GameHistory> recentGames = repository.getRecentGameHistory(10);

// Get complete game with tries
GameHistory game = repository.getCompleteGameHistory(gameId);
for (LetterTry letterTry : game.getLetterTries()) {
    Log.d("Letter", letterTry.getLetter() + " - " + 
          (letterTry.isCorrect() ? "Correct" : "Wrong"));
}

// Search games by word
List<GameHistory> gamesWithWord = repository.getGamesByWord("EXAMPLE");
```

### Clearing History
```java
// Delete specific game
repository.deleteGameHistory(gameId);

// Delete all history (careful!)
repository.deleteAllGameHistory();
```

## Testing the Implementation

### 1. Build the Project
```bash
./gradlew clean build
```

### 2. Run the App
Install and run the app on an emulator or device.

### 3. Play Some Games
- Play with custom words
- Play with random words
- Win some games
- Lose some games

### 4. Check the Database (via Logcat)
Add this code temporarily to `HangmanActivity.onGameFinish()`:
```java
GameStatisticsUtil statsUtil = new GameStatisticsUtil(this);
statsUtil.printStatistics();
statsUtil.printRecentGameHistory(5);
```

### 5. Inspect Database File
Use Android Studio's Database Inspector:
1. Run app on device/emulator
2. View → Tool Windows → App Inspection
3. Select "Database Inspector" tab
4. View tables: `game_history` and `letter_tries`

Or use ADB:
```bash
adb shell
cd /data/data/dharminnagar.hangman/databases
sqlite3 hangman.db
.tables
SELECT * FROM game_history;
SELECT * FROM letter_tries;
```

## Features

### Current Features
✅ Automatic game history tracking
✅ Letter-by-letter attempt recording
✅ Win/loss statistics
✅ Custom vs random word tracking
✅ Timestamp tracking
✅ Foreign key constraints with cascade delete
✅ Indexed queries for performance
✅ Singleton pattern for thread safety
✅ Comprehensive query methods

### Potential Future Enhancements
- Add UI screen to view game history
- Add statistics screen with charts
- Add ability to replay a game
- Export/import game history
- Add achievements system
- Add daily/weekly statistics
- Add word difficulty tracking
- Add player profiles

## Database Location

The database file is stored at:
```
/data/data/dharminnagar.hangman/databases/hangman.db
```

## Performance Considerations

- **Indexes**: Created on `timestamp` and `game_id` columns for faster queries
- **Batch Insert**: `insertLetterTries()` uses transactions for efficiency
- **Singleton Pattern**: Database helper and repository use singleton pattern
- **Foreign Keys**: Enabled for data integrity with cascade delete

## Error Handling

All database operations include try-catch blocks to prevent crashes. Errors are logged to logcat for debugging.

## Migration Notes

Current database version: 1

For future schema changes:
1. Increment `DATABASE_VERSION` in `HangmanDatabaseHelper`
2. Implement migration logic in `onUpgrade()`
3. Test migration on existing installations

## Dependencies

No additional dependencies required. Uses Android's built-in SQLite support:
- `android.database.sqlite.SQLiteDatabase`
- `android.database.sqlite.SQLiteOpenHelper`
- `android.content.ContentValues`
- `android.database.Cursor`

## Contributing

When adding new database features:
1. Update schema in `HangmanDatabaseHelper`
2. Add corresponding model classes in `database/models/`
3. Add DAO methods in `database/dao/`
4. Update repository methods in `HangmanRepository`
5. Update this README with new features
