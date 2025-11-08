package markus.wieland.hangman.ui.history;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import markus.wieland.hangman.R;
import markus.wieland.hangman.database.HangmanRepository;
import markus.wieland.hangman.database.models.GameHistory;
import markus.wieland.hangman.database.models.LetterTry;

/**
 * Dialog fragment to show detailed game information including letter tries
 */
public class GameDetailDialogFragment extends DialogFragment {

    private static final String ARG_GAME_ID = "game_id";
    private HangmanRepository repository;
    private long gameId;

    public static GameDetailDialogFragment newInstance(long gameId) {
        GameDetailDialogFragment fragment = new GameDetailDialogFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_GAME_ID, gameId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            gameId = getArguments().getLong(ARG_GAME_ID);
        }
        repository = HangmanRepository.getInstance(requireContext());
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_game_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        TextView tvWord = view.findViewById(R.id.tv_detail_word);
        TextView tvResult = view.findViewById(R.id.tv_detail_result);
        TextView tvAttempts = view.findViewById(R.id.tv_detail_attempts);
        TextView tvWrongAttempts = view.findViewById(R.id.tv_detail_wrong_attempts);
        TextView tvDate = view.findViewById(R.id.tv_detail_date);
        TextView tvWordType = view.findViewById(R.id.tv_detail_word_type);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_letter_tries);
        View btnClose = view.findViewById(R.id.btn_close);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        LetterTryAdapter adapter = new LetterTryAdapter();
        recyclerView.setAdapter(adapter);

        // Load game data
        GameHistory game = repository.getCompleteGameHistory(gameId);
        
        if (game != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy 'at' HH:mm", Locale.getDefault());
            
            tvWord.setText(game.getWord());
            tvResult.setText(game.isWin() ? "WIN" : "LOSS");
            tvAttempts.setText(String.valueOf(game.getTotalAttempts()));
            tvWrongAttempts.setText(String.valueOf(game.getWrongAttempts()));
            tvDate.setText(dateFormat.format(game.getDate()));
            tvWordType.setText(game.isCustomWord() ? "Custom Word" : "Random Word");

            // Set result color
            int color = game.isWin() 
                ? requireContext().getColor(R.color.hangman_used_correct)
                : requireContext().getColor(R.color.hangman_used_wrong);
            tvResult.setTextColor(color);

            // Load letter tries
            adapter.setLetterTries(game.getLetterTries());
        }

        // Close button
        btnClose.setOnClickListener(v -> dismiss());

        // Set dialog title
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setTitle("Game Details");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }

    /**
     * Adapter for displaying letter tries
     */
    private static class LetterTryAdapter extends RecyclerView.Adapter<LetterTryAdapter.LetterTryViewHolder> {

        private List<LetterTry> letterTries;

        public void setLetterTries(List<LetterTry> letterTries) {
            this.letterTries = letterTries;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public LetterTryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_letter_try, parent, false);
            return new LetterTryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LetterTryViewHolder holder, int position) {
            if (letterTries != null) {
                holder.bind(letterTries.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return letterTries != null ? letterTries.size() : 0;
        }

        static class LetterTryViewHolder extends RecyclerView.ViewHolder {
            private final TextView tvTryNumber;
            private final TextView tvLetter;
            private final TextView tvResult;
            private final View resultIndicator;

            public LetterTryViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTryNumber = itemView.findViewById(R.id.tv_try_number);
                tvLetter = itemView.findViewById(R.id.tv_letter);
                tvResult = itemView.findViewById(R.id.tv_try_result);
                resultIndicator = itemView.findViewById(R.id.view_try_indicator);
            }

            public void bind(LetterTry letterTry) {
                tvTryNumber.setText(String.format(Locale.getDefault(), "#%d", letterTry.getTryOrder() + 1));
                tvLetter.setText(String.valueOf(letterTry.getLetter()));
                tvResult.setText(letterTry.isCorrect() ? "✓" : "✗");

                int color = letterTry.isCorrect()
                    ? itemView.getContext().getColor(R.color.hangman_used_correct)
                    : itemView.getContext().getColor(R.color.hangman_used_wrong);

                resultIndicator.setBackgroundColor(color);
                tvResult.setTextColor(color);
                tvLetter.setTextColor(color);
            }
        }
    }
}
