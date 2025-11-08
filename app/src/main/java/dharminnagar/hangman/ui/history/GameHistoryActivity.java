package dharminnagar.hangman.ui.history;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dharminnagar.hangman.R;
import dharminnagar.hangman.database.HangmanRepository;
import dharminnagar.hangman.database.models.GameHistory;

/**
 * Activity to display game history
 */
public class GameHistoryActivity extends AppCompatActivity implements GameHistoryAdapter.OnGameClickListener {

    private RecyclerView recyclerView;
    private GameHistoryAdapter adapter;
    private HangmanRepository repository;
    private TextView tvTotalGames;
    private TextView tvWins;
    private TextView tvLosses;
    private TextView tvWinRate;
    private TextView tvEmptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Apply the app theme
        setTheme(R.style.Theme_Hangman);
        
        setContentView(R.layout.activity_game_history);

        // Enable back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Game History");
        }

        // Initialize repository
        repository = HangmanRepository.getInstance(this);

        // Initialize views
        tvTotalGames = findViewById(R.id.tv_total_games);
        tvWins = findViewById(R.id.tv_wins);
        tvLosses = findViewById(R.id.tv_losses);
        tvWinRate = findViewById(R.id.tv_win_rate);
        tvEmptyState = findViewById(R.id.tv_empty_state);
        recyclerView = findViewById(R.id.recycler_view_history);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GameHistoryAdapter(this);
        recyclerView.setAdapter(adapter);

        // Load data
        loadGameHistory();
        updateStatistics();
    }

    private void loadGameHistory() {
        List<GameHistory> games = repository.getAllGameHistory();
        
        if (games.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.setGames(games);
        }
    }

    private void updateStatistics() {
        int total = repository.getTotalGamesPlayed();
        int wins = repository.getTotalWins();
        int losses = repository.getTotalLosses();
        double winRate = repository.getWinRate();

        tvTotalGames.setText(String.valueOf(total));
        tvWins.setText(String.valueOf(wins));
        tvLosses.setText(String.valueOf(losses));
        tvWinRate.setText(String.format("%.1f%%", winRate));
    }

    @Override
    public void onGameClick(GameHistory game) {
        // Open detail dialog
        GameDetailDialogFragment dialog = GameDetailDialogFragment.newInstance(game.getGameId());
        dialog.show(getSupportFragmentManager(), "GameDetail");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
