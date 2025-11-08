# Hangman Database Implementation - Quick Start Guide

## ✅ What Was Added

A complete SQL database system has been successfully integrated into your Hangman Android app to track game history and letter attempts.

## 📁 New Files Created

### Database Core (`app/src/main/java/markus/wieland/hangman/database/`)
1. **HangmanDatabaseHelper.java** - SQLite database management
2. **HangmanRepository.java** - Unified interface for database operations

### Data Models (`database/models/`)
3. **GameHistory.java** - Game record model
4. **LetterTry.java** - Letter attempt model

### Data Access Objects (`database/dao/`)
5. **GameHistoryDao.java** - Game history CRUD operations
6. **LetterTryDao.java** - Letter tries CRUD operations

### Utilities (`utils/`)
7. **GameStatisticsUtil.java** - Helper for viewing statistics

### Examples (`examples/`)
8. **DatabaseUsageExample.java** - 10 examples showing how to use the database

### Documentation
9. **DATABASE_README.md** - Comprehensive documentation
10. **DATABASE_QUICKSTART.md** - This file

## 🔄 Modified Files

### app/src/main/java/markus/wieland/hangman/
1. **Hangman.java** 
   - Added `letterTries` list to track attempts
   - Modified `onClick()` to record each letter try
   - Added `getLetterTries()` method

2. **HangmanActivity.java**
   - Added `repository` initialization
   - Added `isCustomWord` tracking
   - Added `saveGameToDatabase()` method
   - Auto-saves games when they finish

## 🗄️ Database Schema

### Table: game_history
| Column | Type | Description |
|--------|------|-------------|
| game_id | INTEGER | Primary key (auto-increment) |
| word | TEXT | The word to guess |
| is_win | INTEGER | 1 if won, 0 if lost |
| total_attempts | INTEGER | Total letters tried |
| wrong_attempts | INTEGER | Number of wrong guesses |
| timestamp | INTEGER | Unix timestamp |
| is_custom_word | INTEGER | 1 if custom, 0 if random |

### Table: letter_tries
| Column | Type | Description |
|--------|------|-------------|
| try_id | INTEGER | Primary key (auto-increment) |
| game_id | INTEGER | Foreign key to game_history |
| letter | TEXT | The letter guessed |
| is_correct | INTEGER | 1 if correct, 0 if wrong |
| try_order | INTEGER | Order of this try (0, 1, 2...) |

## 🚀 How It Works

### Automatic Saving
**Games are automatically saved** when they finish. No additional code needed!

The flow is:
1. Player plays the game
2. Each letter clicked is tracked in memory
3. When game ends → `HangmanActivity.onGameFinish()` is called
4. Game data is saved to database automatically

## 📊 Viewing Statistics

### Option 1: Using GameStatisticsUtil (Recommended)

Add this to `HangmanActivity.onGameFinish()` after the save call:

```java
// View stats after each game
GameStatisticsUtil statsUtil = new GameStatisticsUtil(this);
statsUtil.printStatistics();
statsUtil.printRecentGameHistory(5);
```

### Option 2: Using Repository Directly

```java
HangmanRepository repository = HangmanRepository.getInstance(this);

// Get totals
int total = repository.getTotalGamesPlayed();
int wins = repository.getTotalWins();
double winRate = repository.getWinRate();

// Get recent games
List<GameHistory> recent = repository.getRecentGameHistory(10);

// Get specific game with all letter tries
GameHistory game = repository.getCompleteGameHistory(gameId);
```

## 🧪 Testing the Database

### Step 1: Build and Run
```bash
cd /Users/dharmin/Documents/testing/hangman-android-app
./gradlew installDebug
```

### Step 2: Play Some Games
- Play a few games (both wins and losses)
- Try custom words and random words

### Step 3: View Database via Android Studio
1. **Open Database Inspector**
   - View → Tool Windows → App Inspection
   - Select "Database Inspector" tab
   - Select your app from dropdown
   - You'll see `hangman.db` with tables:
     - `game_history`
     - `letter_tries`

2. **Run Queries**
   ```sql
   SELECT * FROM game_history ORDER BY timestamp DESC;
   SELECT * FROM letter_tries WHERE game_id = 1;
   ```

### Step 4: View Stats in Logcat

Add temporary code to view stats:

```java
// In HangmanActivity.onGameFinish(), add:
GameStatisticsUtil statsUtil = new GameStatisticsUtil(this);
statsUtil.printStatistics();
```

Then filter Logcat by "GameStatisticsUtil" to see output.

## 📱 What Gets Stored

### For Each Game:
- ✅ The word that was guessed
- ✅ Whether you won or lost
- ✅ Total number of letter attempts
- ✅ Number of wrong guesses
- ✅ Date and time played
- ✅ Whether it was a custom or random word

### For Each Letter Attempt:
- ✅ Which letter was guessed
- ✅ Whether it was correct or wrong
- ✅ The order it was guessed in (1st, 2nd, 3rd...)

## 🎯 Use Cases

### 1. View Overall Performance
```java
HangmanRepository repo = HangmanRepository.getInstance(context);
Log.d("Stats", repo.getStatisticsString());
// Output: Total Games: 25, Wins: 18, Losses: 7, Win Rate: 72.0%
```

### 2. Replay a Game (View Letter Sequence)
```java
GameHistory game = repo.getCompleteGameHistory(gameId);
for (LetterTry letterTry : game.getLetterTries()) {
    Log.d("Try", letterTry.getLetter() + " - " + 
          (letterTry.isCorrect() ? "✓" : "✗"));
}
```

### 3. Find Hardest Words
```java
List<GameHistory> all = repo.getAllGameHistory();
all.sort((g1, g2) -> Integer.compare(g2.getWrongAttempts(), g1.getWrongAttempts()));
GameHistory hardest = all.get(0);
Log.d("Hardest", hardest.getWord() + " with " + hardest.getWrongAttempts() + " errors");
```

### 4. Search Previous Games with Same Word
```java
List<GameHistory> games = repo.getGamesByWord("HANGMAN");
Log.d("History", "You've played HANGMAN " + games.size() + " times");
```

## 🔍 Examples

See **DatabaseUsageExample.java** for 10 detailed examples:
1. View statistics
2. View recent games
3. View complete game with letter sequence
4. Search games by word
5. View all games with tries
6. Analyze wrong guesses
7. Compare custom vs random word performance
8. Find most difficult words
9. Calculate averages
10. Delete specific games

## ⚠️ Important Notes

### Thread Safety
- Repository uses singleton pattern ✓
- Database operations are thread-safe ✓

### Data Persistence
- Database is stored in app's private storage
- Survives app restarts ✓
- Cleared only when app is uninstalled

### Performance
- Indexed queries for fast retrieval ✓
- Batch inserts for letter tries ✓
- Foreign key cascade delete ✓

## 🛠️ Customization

### Add New Statistics
Extend `HangmanRepository` with new query methods:

```java
public int getLongestWinStreak() {
    // Your implementation
}
```

### Add UI for History
Create a new Activity to display game history with RecyclerView.

### Export Data
Add export functionality to share stats:

```java
public String exportToCsv() {
    List<GameHistory> games = repository.getAllGameHistory();
    // Convert to CSV format
}
```

## 📚 Documentation

For complete documentation, see:
- **DATABASE_README.md** - Full technical documentation
- **DatabaseUsageExample.java** - Working code examples
- **HangmanRepository.java** - API reference

## ✨ Features

✅ Automatic game tracking
✅ Letter-by-letter history
✅ Win/loss statistics
✅ Custom vs random word tracking
✅ Timestamp tracking
✅ Fast indexed queries
✅ Thread-safe operations
✅ Comprehensive query methods
✅ Easy-to-use repository pattern

## 🎉 You're Done!

The database is fully integrated and working. Every game you play is automatically saved with complete details. Use the examples and utilities to explore your game history!

---

**Questions?** Check the full documentation in `DATABASE_README.md`
