package markus.wieland.hangman.ui.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import markus.wieland.hangman.R;
import markus.wieland.hangman.database.models.GameHistory;

/**
 * RecyclerView adapter for displaying game history
 */
public class GameHistoryAdapter extends RecyclerView.Adapter<GameHistoryAdapter.GameHistoryViewHolder> {

    private List<GameHistory> games;
    private final OnGameClickListener listener;
    private final SimpleDateFormat dateFormat;

    public interface OnGameClickListener {
        void onGameClick(GameHistory game);
    }

    public GameHistoryAdapter(OnGameClickListener listener) {
        this.games = new ArrayList<>();
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
    }

    public void setGames(List<GameHistory> games) {
        this.games = games;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GameHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_game_history, parent, false);
        return new GameHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameHistoryViewHolder holder, int position) {
        GameHistory game = games.get(position);
        holder.bind(game, dateFormat, listener);
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    static class GameHistoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvWord;
        private final TextView tvResult;
        private final TextView tvAttempts;
        private final TextView tvDate;
        private final View resultIndicator;

        public GameHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWord = itemView.findViewById(R.id.tv_game_word);
            tvResult = itemView.findViewById(R.id.tv_game_result);
            tvAttempts = itemView.findViewById(R.id.tv_game_attempts);
            tvDate = itemView.findViewById(R.id.tv_game_date);
            resultIndicator = itemView.findViewById(R.id.view_result_indicator);
        }

        public void bind(GameHistory game, SimpleDateFormat dateFormat, OnGameClickListener listener) {
            tvWord.setText(game.getWord());
            tvResult.setText(game.isWin() ? "WIN" : "LOSS");
            tvAttempts.setText(String.format(Locale.getDefault(), 
                "%d attempts (%d wrong)", 
                game.getTotalAttempts(), 
                game.getWrongAttempts()));
            tvDate.setText(dateFormat.format(game.getDate()));

            // Set result color
            int color = game.isWin() 
                ? itemView.getContext().getColor(R.color.hangman_used_correct)
                : itemView.getContext().getColor(R.color.hangman_used_wrong);
            
            resultIndicator.setBackgroundColor(color);
            tvResult.setTextColor(color);

            // Set click listener
            itemView.setOnClickListener(v -> listener.onGameClick(game));
        }
    }
}
