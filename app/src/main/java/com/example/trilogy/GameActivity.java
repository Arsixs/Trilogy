package com.example.trilogy;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.trilogy.db.DatabaseHelper;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class GameActivity extends AppCompatActivity {

    private int score = 0;
    private int lives = 3;
    private int questionsAnswered = 0;
    private static final int MAX_QUESTIONS = 5;

    private ProgressBar progressBar;
    private TextView progressText, lifeText;
    private ImageView img1, img2;
    private EditText answer;

    private DatabaseHelper db;
    private Cursor cursor;

    private String correctAnswer;
    private String currentQuestionId;

    private void syncFromFirestore() {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();

        fs.collection("questions")
                .get()
                .addOnSuccessListener(snapshot -> {
                    for (DocumentSnapshot doc : snapshot) {

                        // Convert Firestore string names to drawable resource IDs
                        int img1Id = getResources().getIdentifier(
                                doc.getString("i1").toLowerCase().replaceAll("\\s+", "_"),
                                "drawable",
                                getPackageName()
                        );
                        int img2Id = getResources().getIdentifier(
                                doc.getString("i2").toLowerCase().replaceAll("\\s+", "_"),
                                "drawable",
                                getPackageName()
                        );

                        // Only insert if drawable IDs are valid
                        db.insertQuestion(
                                doc.getId(),
                                doc.getString("i1"), // string name from Firestore
                                doc.getString("i2"), // string name from Firestore
                                doc.getString("a")
                        );
                    }

                    // After syncing, load the first question
                    cursor = db.getQuestion();
                    loadQuestion();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load questions", Toast.LENGTH_SHORT).show()
                );
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        answer = findViewById(R.id.answerInput);
        lifeText = findViewById(R.id.lifeText);
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);

        progressBar.setMax(MAX_QUESTIONS);
        updateLivesUI();
        updateProgressUI();

        db = new DatabaseHelper(this);

        if (db.isEmpty()) {
            syncFromFirestore();
        } else {
            cursor = db.getQuestion();
            loadQuestion();
        }
    }

    void loadQuestion() {
        if (cursor.moveToNext()) {
            currentQuestionId = cursor.getString(0); // ID column
            correctAnswer = cursor.getString(3);

            // Convert string name to drawable ID
            String img1Name = cursor.getString(1).toLowerCase().replaceAll("\\s+", "_");
            String img2Name = cursor.getString(2).toLowerCase().replaceAll("\\s+", "_");

            int imgRes1 = getResources().getIdentifier(img1Name, "drawable", getPackageName());
            int imgRes2 = getResources().getIdentifier(img2Name, "drawable", getPackageName());

            if (imgRes1 != 0) img1.setImageResource(imgRes1);
            if (imgRes2 != 0) img2.setImageResource(imgRes2);

        } else {
            finishGame(); // no more questions
        }
    }


    public void submit(View v) {
        String userAnswer = answer.getText().toString().trim();

        if (userAnswer.isEmpty()) {
            Toast.makeText(this, "Please enter an answer!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userAnswer.equalsIgnoreCase(correctAnswer)) {
            // Safe MediaPlayer
            MediaPlayer mp = MediaPlayer.create(this, R.raw.correct);
            mp.setOnCompletionListener(MediaPlayer::release);
            mp.start();
            score += 10;
            questionsAnswered++;
            db.markUsed(currentQuestionId);
            updateProgressUI();

            if (questionsAnswered >= MAX_QUESTIONS) {
                finishGame();
                return;
            }

        } else {
            // Safe MediaPlayer
            MediaPlayer mp = MediaPlayer.create(this, R.raw.wrong);
            mp.setOnCompletionListener(MediaPlayer::release);
            mp.start();
            lives--;
            updateLivesUI();

            if (lives <= 0) {
                gameOver();
                return;
            }
        }

        answer.setText("");
        cursor.close();
        cursor = db.getQuestion();
        loadQuestion();
    }

    private void updateLivesUI() {
        StringBuilder hearts = new StringBuilder();
        for (int i = 0; i < lives; i++) hearts.append("❤");
        lifeText.setText("Lives: " + hearts);
    }

    private void updateProgressUI() {
        progressBar.setProgress(questionsAnswered);
        progressText.setText("Correct: " + questionsAnswered + " / " + MAX_QUESTIONS);
    }

    private void gameOver() {
        launchResult(true);
    }

    private void finishGame() {
        launchResult(false);
    }

    private void launchResult(boolean gameOver) {
        Intent i = new Intent(this, ResultActivity.class);
        i.putExtra("SCORE", score);
        i.putExtra("GAME_OVER", gameOver);
        i.putExtra("LIVES", lives);
        startActivity(i);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null && !cursor.isClosed()) cursor.close();
    }
}
