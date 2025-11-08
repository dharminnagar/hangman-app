package markus.wieland.hangman;

import android.os.Bundle;
import android.view.WindowManager;

import java.util.List;

import markus.wieland.games.GameActivity;
import markus.wieland.games.game.GameConfiguration;
import markus.wieland.games.game.GameEventListener;
import markus.wieland.games.game.Highscore;
import markus.wieland.games.persistence.GameGenerator;
import markus.wieland.games.persistence.GameSaver;
import markus.wieland.games.screen.view.EndScreenView;
import markus.wieland.games.screen.view.StartScreenView;
import markus.wieland.hangman.database.HangmanRepository;
import markus.wieland.hangman.database.models.GameHistory;
import markus.wieland.hangman.database.models.LetterTry;

public class HangmanActivity extends GameActivity<HangmanConfiguration, Highscore, HangmanGameState, HangmanGameResult, Hangman> implements GameEventListener<HangmanGameResult> {

    private HangmanRepository repository;
    private boolean isCustomWord;
    public HangmanActivity() {
        super(R.layout.activity_hangman);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        
        // Initialize database repository
        repository = HangmanRepository.getInstance(this);
    }

    @Override
    public void onGameStart() {
        game.setEnableKeyboard(true);
    }

    @Override
    public void onGameFinish(HangmanGameResult gameResult) {
        super.onGameFinish(gameResult);
        gameSaver.delete();
        game.setEnableKeyboard(false);
        
        // Save game history to database
        saveGameToDatabase(gameResult);
    }

    @Override
    protected StartScreenView initializeStartScreen() {
        return findViewById(R.id.activity_hangman_start_screen);
    }

    @Override
    protected EndScreenView initializeEndScreen() {
        return findViewById(R.id.activity_hangman_end_screen);
    }

    @Override
    protected GameGenerator<HangmanGameState> initializeGenerator(GameConfiguration configuration) {
        if (!(configuration instanceof HangmanConfiguration))
            throw new IllegalArgumentException("Wrong configuration loaded. Was: "
                    + configuration.getClass().getName() + " but expected: " + HangmanConfiguration.class.getName());
        HangmanConfiguration hangmanConfiguration = (HangmanConfiguration) configuration;
        if (hangmanConfiguration.getHangmanWord() == null) {
            isCustomWord = false;
            return new HangmanGenerator(hangmanConfiguration, this);
        }
        isCustomWord = true;
        initializeGame(new HangmanGameState(hangmanConfiguration.getHangmanWord()));
        return null;
    }

    @Override
    protected GameSaver<HangmanGameState, Highscore> initializeGameSaver() {
        return new GameSaver<>(HangmanGameState.class, Highscore.class, this);
    }

    @Override
    protected void onStop() {
        if (game != null && gameSaver != null && game.isRunning())
            gameSaver.save(game.getGameState());
        super.onStop();
    }

    @Override
    protected void initializeGame(HangmanGameState hangmanGameState) {
        game = new Hangman(findViewById(R.id.activity_hangman_game_board), hangmanGameState, this);
        game.start();
    }

    /**
     * Save the completed game to the database
     */
    private void saveGameToDatabase(HangmanGameResult gameResult) {
        if (game == null || repository == null) return;
        
        try {
            // Get letter tries from the game
            List<LetterTry> letterTries = game.getLetterTries();
            
            // Count wrong attempts
            int wrongAttempts = 0;
            for (LetterTry letterTry : letterTries) {
                if (!letterTry.isCorrect()) {
                    wrongAttempts++;
                }
            }
            
            // Create game history record
            GameHistory gameHistory = new GameHistory(
                gameResult.getOriginalWord(),
                gameResult.isWin(),
                letterTries.size(),
                wrongAttempts,
                isCustomWord
            );
            
            // Save to database
            repository.saveGame(gameHistory, letterTries);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}