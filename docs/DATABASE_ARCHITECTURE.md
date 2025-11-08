# Hangman Database Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          HANGMAN DATABASE SYSTEM                            │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                            APPLICATION LAYER                                │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────┐        ┌──────────────────────────────────────┐  │
│  │  HangmanActivity    │        │       Hangman (Game)                 │  │
│  │                     │        │                                      │  │
│  │  • Manages UI       │───────▶│  • Tracks letter tries in memory    │  │
│  │  • Saves to DB      │        │  • letterTries: List<LetterTry>     │  │
│  │    on game finish   │        │  • tryOrder: int                    │  │
│  └─────────────────────┘        └──────────────────────────────────────┘  │
│           │                                                                 │
│           │ saveGameToDatabase()                                            │
│           ▼                                                                 │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                          REPOSITORY LAYER                                   │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌────────────────────────────────────────────────────────────────────┐   │
│  │                    HangmanRepository (Singleton)                    │   │
│  ├────────────────────────────────────────────────────────────────────┤   │
│  │  HIGH-LEVEL OPERATIONS:                                            │   │
│  │  • saveGame(gameHistory, letterTries)                              │   │
│  │  • getCompleteGameHistory(gameId)                                  │   │
│  │  • getAllGamesWithTries()                                          │   │
│  │  • getRecentGamesWithTries(limit)                                  │   │
│  │  • getWinRate()                                                    │   │
│  │  • getStatisticsString()                                           │   │
│  └────────────────────────────────────────────────────────────────────┘   │
│           │                                    │                            │
│           │                                    │                            │
│           ▼                                    ▼                            │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                            DAO LAYER                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌────────────────────────────┐      ┌────────────────────────────────┐   │
│  │    GameHistoryDao          │      │      LetterTryDao              │   │
│  ├────────────────────────────┤      ├────────────────────────────────┤   │
│  │ CRUD Operations:           │      │ CRUD Operations:               │   │
│  │ • insertGameHistory()      │      │ • insertLetterTry()            │   │
│  │ • getAllGameHistory()      │      │ • insertLetterTries() (batch)  │   │
│  │ • getRecentGameHistory()   │      │ • getLetterTriesByGameId()     │   │
│  │ • getGameHistoryById()     │      │ • getCorrectLetterTries()      │   │
│  │ • getGamesByWord()         │      │ • getWrongLetterTries()        │   │
│  │ • getTotalGamesPlayed()    │      │ • getTriesCountByGameId()      │   │
│  │ • getTotalWins()           │      │ • deleteLetterTriesByGameId()  │   │
│  │ • getTotalLosses()         │      │ • deleteAllLetterTries()       │   │
│  │ • deleteGameHistory()      │      │                                │   │
│  │ • deleteAllGameHistory()   │      │                                │   │
│  └────────────────────────────┘      └────────────────────────────────┘   │
│           │                                    │                            │
│           │                                    │                            │
│           ▼                                    ▼                            │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                         DATABASE HELPER LAYER                               │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌────────────────────────────────────────────────────────────────────┐   │
│  │            HangmanDatabaseHelper (SQLiteOpenHelper)                │   │
│  ├────────────────────────────────────────────────────────────────────┤   │
│  │  • Database Name: hangman.db                                       │   │
│  │  • Version: 1                                                      │   │
│  │  • onCreate() - Create tables and indexes                          │   │
│  │  • onUpgrade() - Handle version migrations                         │   │
│  │  • onConfigure() - Enable foreign keys                             │   │
│  └────────────────────────────────────────────────────────────────────┘   │
│           │                                                                 │
│           ▼                                                                 │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                           SQLITE DATABASE                                   │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌──────────────────────────────────┐    ┌──────────────────────────────┐ │
│  │      TABLE: game_history         │    │    TABLE: letter_tries       │ │
│  ├──────────────────────────────────┤    ├──────────────────────────────┤ │
│  │ game_id (PK, AUTOINCREMENT)      │◀───│ try_id (PK, AUTOINCREMENT)   │ │
│  │ word (TEXT)                      │    │ game_id (FK) ───────────────▶│ │
│  │ is_win (INTEGER)                 │    │ letter (TEXT)                │ │
│  │ total_attempts (INTEGER)         │    │ is_correct (INTEGER)         │ │
│  │ wrong_attempts (INTEGER)         │    │ try_order (INTEGER)          │ │
│  │ timestamp (INTEGER) [INDEXED]    │    │                              │ │
│  │ is_custom_word (INTEGER)         │    │ [INDEX: game_id]             │ │
│  └──────────────────────────────────┘    └──────────────────────────────┘ │
│                                                                             │
│  Foreign Key Constraint: ON DELETE CASCADE                                  │
│  (Deleting a game automatically deletes all its letter tries)               │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                          DATA MODEL LAYER                                   │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌────────────────────────────┐      ┌────────────────────────────────┐   │
│  │      GameHistory           │      │        LetterTry               │   │
│  ├────────────────────────────┤      ├────────────────────────────────┤   │
│  │ - gameId: long             │      │ - tryId: long                  │   │
│  │ - word: String             │      │ - gameId: long                 │   │
│  │ - isWin: boolean           │      │ - letter: char                 │   │
│  │ - totalAttempts: int       │      │ - isCorrect: boolean           │   │
│  │ - wrongAttempts: int       │      │ - tryOrder: int                │   │
│  │ - timestamp: long          │      │                                │   │
│  │ - isCustomWord: boolean    │      │ + getters/setters              │   │
│  │ - letterTries: List        │◀─────│ + toString()                   │   │
│  │                            │  has │                                │   │
│  │ + getters/setters          │  many│                                │   │
│  │ + getDate(): Date          │──────│                                │   │
│  │ + toString()               │      │                                │   │
│  └────────────────────────────┘      └────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                          UTILITY LAYER                                      │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌────────────────────────────────────────────────────────────────────┐   │
│  │                    GameStatisticsUtil                              │   │
│  ├────────────────────────────────────────────────────────────────────┤   │
│  │  DEBUGGING & VIEWING:                                              │   │
│  │  • printStatistics() - Log overall stats                           │   │
│  │  • printGameHistory() - Log all games                              │   │
│  │  • printRecentGameHistory(limit) - Log recent games                │   │
│  │  • printGameDetails(game) - Log specific game                      │   │
│  │  • printCompleteGameHistory(gameId) - Log game with tries          │   │
│  │  • clearAllHistory() - Delete all records                          │   │
│  └────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘

════════════════════════════════════════════════════════════════════════════════

                              DATA FLOW EXAMPLE

   User plays game and clicks letter 'E'
            │
            ▼
   Hangman.onClick('E')
            │
            ├─► Check if correct/wrong
            │
            ├─► Create LetterTry object
            │   LetterTry(gameId=0, letter='E', isCorrect=true, tryOrder=0)
            │
            └─► Add to letterTries list
            
   ... user continues playing ...
   
   Game ends (win or loss)
            │
            ▼
   HangmanActivity.onGameFinish(result)
            │
            ├─► Create GameHistory object
            │   GameHistory(word="EXAMPLE", isWin=true, totalAttempts=7,
            │                wrongAttempts=2, isCustomWord=false)
            │
            ├─► Get letterTries from game
            │   List<LetterTry> tries = game.getLetterTries()
            │
            └─► Save to database
                    │
                    ▼
            repository.saveGame(gameHistory, letterTries)
                    │
                    ├─► gameHistoryDao.insertGameHistory()
                    │   Returns gameId = 42
                    │
                    └─► letterTryDao.insertLetterTries()
                        Sets gameId=42 for all tries
                        Batch inserts all letter tries
                        
   ✓ Game saved to database!

════════════════════════════════════════════════════════════════════════════════

                           QUERY EXAMPLES

   1. Get recent games:
      repository.getRecentGameHistory(10)
      ▼
      SELECT * FROM game_history ORDER BY timestamp DESC LIMIT 10
      
   2. Get game with tries:
      repository.getCompleteGameHistory(42)
      ▼
      SELECT * FROM game_history WHERE game_id = 42
      SELECT * FROM letter_tries WHERE game_id = 42 ORDER BY try_order ASC
      
   3. Get statistics:
      repository.getTotalWins()
      ▼
      SELECT COUNT(*) FROM game_history WHERE is_win = 1
      
   4. Search by word:
      repository.getGamesByWord("EXAMPLE")
      ▼
      SELECT * FROM game_history WHERE word = 'EXAMPLE' 
      ORDER BY timestamp DESC

════════════════════════════════════════════════════════════════════════════════
