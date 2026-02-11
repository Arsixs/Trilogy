package com.example.trilogy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WordleActivity extends AppCompatActivity {

    private static final String PREF_NAME = "wordle_stats";
    private static final String KEY_SCORE = "score";
    private static final String KEY_CORRECT = "correct_guess_count";
    private static final String KEY_GUESSED_WORDS = "guessed_words";

    private static final String GREEN = "#538D4E";
    private static final String YELLOW = "#B59F3B";
    private static final String GRAY = "#3A3A3C";
    private static final String RED = "#B00020";

    private SharedPreferences prefs;
    private GridLayout gridLayout;
    private EditText guessInput;
    private Button submitBtn;
    private TextView cgCounter, points;
    private ImageButton dictionaryBtn;

    private FirebaseFirestore db;
    private final List<String> wordsList = new ArrayList<>();
    private String correctWord;

    private int currentRow = 0;
    private int correctGuessCount = 0;
    private int score = 0;

    private static final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_wordle);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI
        gridLayout = findViewById(R.id.gridLayout);
        guessInput = findViewById(R.id.guessInput);
        submitBtn = findViewById(R.id.submitBtn);
        cgCounter = findViewById(R.id.cgCounter);
        points = findViewById(R.id.points);
        dictionaryBtn = findViewById(R.id.dict);

        // SharedPreferences
        prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        score = prefs.getInt(KEY_SCORE, 0);
        correctGuessCount = prefs.getInt(KEY_CORRECT, 0);

        cgCounter.setText("GUESSED WORDS: " + correctGuessCount);
        points.setText(score + " pts");

        // Firestore
        db = FirebaseFirestore.getInstance();
        loadWordsFromFirestore();

        // Grid setup
        createGrid();

        // Button listeners
        submitBtn.setOnClickListener(v -> checkGuess());
        dictionaryBtn.setOnClickListener(v -> {
            startActivity(new Intent(WordleActivity.this, Dictionary.class));
        });
    }

    // ------------------ GRID METHODS ------------------

    private void createGrid() {
        for (int i = 0; i < 30; i++) {
            TextView tile = new TextView(this);
            int size = (int) (56 * getResources().getDisplayMetrics().density);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = size;
            params.height = size;
            params.setMargins(8, 8, 8, 8);

            tile.setLayoutParams(params);
            tile.setGravity(Gravity.CENTER);
            tile.setTextSize(24);
            tile.setTypeface(null, Typeface.BOLD);
            tile.setTextColor(Color.WHITE);
            tile.setBackgroundColor(Color.parseColor("#B33A3A3C"));

            gridLayout.addView(tile);
        }
    }

    private void flipTile(TextView tile, String letter, int bgColor) {
        tile.animate()
                .rotationY(90)
                .setDuration(150)
                .withEndAction(() -> {
                    tile.setText(letter);
                    tile.setBackgroundColor(bgColor);
                    tile.setRotationY(-90);
                    tile.animate().rotationY(0).setDuration(150).start();
                })
                .start();
    }

    // ------------------ FIRESTORE ------------------

    private void loadWordsFromFirestore() {
        db.collection("wordle_words")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    wordsList.clear();
                    if (querySnapshot.isEmpty()) {
                        Toast.makeText(this, "No words found in Firestore", Toast.LENGTH_LONG).show();
                        return;
                    }
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        if (doc.contains("word")) {
                            String word = doc.getString("word");
                            if (word != null) {
                                word = word.trim().toUpperCase();
                                if (word.matches("[A-Z]{5}")) {
                                    wordsList.add(word);
                                }
                            }
                        }
                    }
           //         temporaryResetStats();

                    if (!wordsList.isEmpty()) pickRandomWord();
                    else Toast.makeText(this, "No valid 5-letter words found", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load words", Toast.LENGTH_SHORT).show());
    }

    private void pickRandomWord() {
        Random rand = new Random();
        correctWord = wordsList.get(rand.nextInt(wordsList.size()));
        System.out.println("NEW WORD: " + correctWord);
    }

    // ------------------ GAME LOGIC ------------------

    private void checkGuess() {
        if (correctWord == null) return;

        String guess = guessInput.getText().toString().toUpperCase().trim();
        if (guess.length() != 5) {
            Toast.makeText(this, "Enter a 5-letter word", Toast.LENGTH_SHORT).show();
            return;
        }

        int[] letterCount = new int[26];
        for (char c : correctWord.toCharArray()) letterCount[c - 'A']++;

        TextView[] rowTiles = new TextView[5];
        for (int i = 0; i < 5; i++) {
            rowTiles[i] = (TextView) gridLayout.getChildAt(currentRow * 5 + i);
            rowTiles[i].setText(String.valueOf(guess.charAt(i)));
            rowTiles[i].setBackgroundColor(Color.parseColor(GRAY));
        }

        // PASS 1: GREEN
        for (int i = 0; i < 5; i++) {
            char g = guess.charAt(i);
            if (g == correctWord.charAt(i)) {
                rowTiles[i].setBackgroundColor(Color.parseColor(GREEN));
                letterCount[g - 'A']--;
            }
        }

        // PASS 2: YELLOW / RED / GRAY
        for (int i = 0; i < 5; i++) {
            char g = guess.charAt(i);
            if (((ColorDrawable) rowTiles[i].getBackground()).getColor() == Color.parseColor(GREEN)) continue;

            int index = g - 'A';
            if (correctWord.indexOf(g) == -1) rowTiles[i].setBackgroundColor(Color.parseColor(GRAY));
            else if (letterCount[index] > 0) {
                rowTiles[i].setBackgroundColor(Color.parseColor(YELLOW));
                letterCount[index]--;
            } else rowTiles[i].setBackgroundColor(Color.parseColor(RED));
        }

        // WIN
        if (guess.equals(correctWord)) {
            playSound(R.raw.correct);
            correctGuessCount++;
            int earnedPoints = addScore(currentRow);
            Toast.makeText(this, "Correct! +" + earnedPoints + " points", Toast.LENGTH_LONG).show();

            cgCounter.setText("GUESSED WORDS: " + correctGuessCount);
            points.setText(score + " pts");

            saveGuessedWord(correctWord);
            saveStats();

            submitBtn.setEnabled(false);
            guessInput.setEnabled(false);

            handler.postDelayed(() -> {
                resetGame();
                submitBtn.setEnabled(true);
                guessInput.setEnabled(true);
            }, 3000);
            return;
        }

        currentRow++;
        guessInput.setText("");

        // LOSE
        if (currentRow == 6) {
            playSound(R.raw.wrong);
            submitBtn.setEnabled(false);
            guessInput.setEnabled(false);

            for (int i = 0; i < 5; i++) {
                final int idx = i;
                TextView lastTile = (TextView) gridLayout.getChildAt(5 * 5 + i);
                handler.postDelayed(() -> flipTile(lastTile, String.valueOf(correctWord.charAt(idx)), Color.parseColor(GREEN)), i * 500);
            }

            Toast.makeText(this, "Word was: " + correctWord, Toast.LENGTH_LONG).show();
            handler.postDelayed(() -> {
                resetGame();
                submitBtn.setEnabled(true);
                guessInput.setEnabled(true);
            }, 5000);
        }
    }

    private void resetGame() {
        currentRow = 0;
        guessInput.setText("");
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            TextView tile = (TextView) gridLayout.getChildAt(i);
            tile.setText("");
            tile.setBackgroundColor(Color.parseColor("#B33A3A3C"));
        }
        if (!wordsList.isEmpty()) pickRandomWord();
    }

    // ------------------ SCORE & STATS ------------------

    private int addScore(int attempt) {
        int earned = 0;
        switch (attempt) {
            case 0: earned = 30;break;
            case 1: earned = 25;break;
            case 2: earned = 20;break;
            case 3: earned = 15;break;
            case 4: earned = 10;break;
            case 5: earned = 5; break;
        }
        score += earned;
        return earned;
    }

    private void saveStats() {
        prefs.edit().putInt(KEY_SCORE, score).putInt(KEY_CORRECT, correctGuessCount).apply();
    }

    private void saveGuessedWord(String word) {
        String existing = prefs.getString(KEY_GUESSED_WORDS, "");
        if (!existing.contains(word)) {
            String updated = existing.isEmpty() ? word : existing + "," + word;
            prefs.edit().putString(KEY_GUESSED_WORDS, updated).apply();
        }
    }

    // ------------------ UTILS ------------------

    private void playSound(int resId) {
        MediaPlayer mp = MediaPlayer.create(this, resId);
        mp.setOnCompletionListener(MediaPlayer::release);
        mp.start();
    }

    private void temporaryResetStats() {
        // Clear all saved stats in SharedPreferences
        prefs.edit().clear().apply();

        // Reset in-memory variables
        score = 0;
        correctGuessCount = 0;

        // Reset UI
        cgCounter.setText("GUESSED WORDS: 0");
        points.setText("0 pts");
        guessInput.setText("");

        // Reset the grid
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            TextView tile = (TextView) gridLayout.getChildAt(i);
            tile.setText("");
            tile.setBackgroundColor(Color.parseColor("#B33A3A3C")); // or GRAY
        }

        // Pick a new word for the session
        if (!wordsList.isEmpty()) pickRandomWord();

        Toast.makeText(this, "Game stats reset for this session!", Toast.LENGTH_SHORT).show();
    }

}
