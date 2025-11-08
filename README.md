# Hangman Android App

A classic word-guessing game built for Android with a modern Material Design interface and game history tracking.

## Features

### Game Modes
- **Custom Word Mode**: Enter a custom word for a friend to guess
- **Random Word Mode**: Get a random word when playing alone

### Visual Feedback
- Color-coded letter buttons indicate correct (green) and incorrect (red) guesses
- Progressive hangman drawing that updates with each wrong attempt
- Clear win/loss indicators

### Game History
- Track all your played games with comprehensive statistics
- View total games, wins, losses, and win rate
- Detailed game history showing:
  - The word that was played
  - Number of attempts and wrong guesses
  - Date and time of each game
  - Letter-by-letter playback of each game

### Theme Support
- Automatic light and dark mode support
- Follows system theme preferences
- Consistent dark theme throughout the app

## Screenshots

### Home Screen
Start a new game by entering a custom word or choosing a random word.

### Game Screen
Guess letters to complete the word before the hangman is fully drawn.

### History Screen
View your complete game history with statistics and detailed letter-by-letter playback.

## Technical Details

### Built With
- **Language**: Java
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 34 (Android 14)
- **Database**: SQLite for game history storage
- **UI**: Material Design Components

### Architecture
- Repository pattern for data access
- DAO (Data Access Object) pattern for database operations
- RecyclerView with ViewHolder pattern for efficient list rendering
- DialogFragment for game detail views

### Database Schema
- `game_history`: Stores game records (word, result, attempts, timestamps)
- `letter_tries`: Stores individual letter attempts for each game

## Installation

1. Clone the repository:
```bash
git clone https://github.com/dharminnagar/hangman-app.git
```

2. Open the project in Android Studio

3. Build and run the app on an emulator or physical device

## How to Play

1. **Start a Game**:
   - Enter a custom word for someone else to guess, OR
   - Click "Random Word" to play with a computer-generated word

2. **Make Guesses**:
   - Tap letter buttons to guess
   - Green indicates correct letters
   - Red indicates wrong letters
   - You have limited attempts before the hangman is completed

3. **Win or Lose**:
   - Win by guessing all letters correctly
   - Lose if the hangman drawing is completed

4. **View History**:
   - Click "Game History" from the home screen
   - See your statistics and past games
   - Tap any game to see detailed letter-by-letter playback

## Game Rules

- Guess the word before the hangman is fully drawn
- Each wrong guess adds a part to the hangman
- Maximum of 11 wrong attempts allowed
- All games are automatically saved to history

## License

This project is open source and available under the MIT License.

## Contributing

Contributions are welcome! Feel free to submit issues and pull requests.

## Credits

Original concept: Classic Hangman word game
Developed by: Dharmin Nagar