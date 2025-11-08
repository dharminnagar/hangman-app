package markus.wieland.hangman;

import java.util.ArrayList;
import java.util.List;

import markus.wieland.games.game.Game;
import markus.wieland.games.game.GameEventListener;
import markus.wieland.hangman.database.models.LetterTry;
import markus.wieland.hangman.models.HangmanWord;

public class Hangman extends Game<HangmanGameState, HangmanGameResult> implements HangmanGameBoardInteractionListener {

    private final HangmanWord word;
    private final HangmanGameBoardView hangmanGameBoard;
    private final List<LetterTry> letterTries;
    private int tryOrder;

    public Hangman(HangmanGameBoardView hangmanGameBoard, HangmanGameState hangmanGameState, GameEventListener<HangmanGameResult> gameEventListener) {
        super(gameEventListener);
        this.word = hangmanGameState.getWord();
        this.hangmanGameBoard = hangmanGameBoard;
        this.letterTries = new ArrayList<>();
        this.tryOrder = 0;
        this.hangmanGameBoard.setGameBoardInteractionListener(this);
        this.hangmanGameBoard.loadGameState(hangmanGameState);
        this.hangmanGameBoard.updateHangmanWord(word);
        this.hangmanGameBoard.updateHangmanImage();
        this.hangmanGameBoard.update();
    }

    @Override
    public HangmanGameState getGameState() {
        return new HangmanGameState(hangmanGameBoard.getGameState(), word);
    }

    @Override
    public HangmanGameResult getResult() {
        return checkForFinish();
    }

    public HangmanGameResult checkForFinish() {
        if (hangmanGameBoard.getAmountOfErrors() >= 11)
            return new HangmanGameResult(false, word.getOriginalWord());
        else if (word.isCompleted())
            return new HangmanGameResult(true, word.getOriginalWord());
        return null;
    }

    public void setEnableKeyboard(boolean enable) {
        hangmanGameBoard.enableKeyboard(enable);
    }

    public List<LetterTry> getLetterTries() {
        return letterTries;
    }

    @Override
    public void onClick(HangmanGameBoardFieldView hangmanGameBoardField) {
        char letter = hangmanGameBoardField.getCharacter();
        HangmanGameBoardFieldState state = word.checkLetter(letter);
        
        // Record this letter try
        boolean isCorrect = (state == HangmanGameBoardFieldState.USED_CORRECT);
        LetterTry letterTry = new LetterTry(0, letter, isCorrect, tryOrder++);
        letterTries.add(letterTry);
        
        hangmanGameBoardField.use(state);
        hangmanGameBoardField.update();
        hangmanGameBoard.updateHangmanWord(word);
        hangmanGameBoard.updateHangmanImage();

        HangmanGameResult result = getResult();
        if (result != null)
            finish(result);
    }
}
