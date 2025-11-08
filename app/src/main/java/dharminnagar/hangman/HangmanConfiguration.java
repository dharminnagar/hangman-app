package dharminnagar.hangman;

import markus.wieland.games.game.GameConfiguration;
import dharminnagar.hangman.models.HangmanWord;

public class HangmanConfiguration implements GameConfiguration {

    private final HangmanWord hangmanWord;

    public HangmanConfiguration(HangmanWord hangmanWord) {
        this.hangmanWord = hangmanWord;
    }

    public HangmanWord getHangmanWord() {
        return hangmanWord;
    }
}
