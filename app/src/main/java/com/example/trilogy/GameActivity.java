package com.example.trilogy;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
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

    private static final int MAX_QUESTIONS = 5;

    private int score = 0;
    private int lives = 3;
    private int questionsAnswered = 0;
    private int rightAnswers = 0;
    private int wrongAnswers = 0;

    private ProgressBar progressBar;
    private TextView progressText, lifeText, sscore, time;
    private ImageView img1, img2, timerGif;
    private EditText answer;

    private DatabaseHelper db;
    private Cursor cursor;

    private String correctAnswer;
    private String currentQuestionId;

    private CountDownTimer timer;
    private long timeLeftMillis = 60000; // 1 minute
    private long startTime;

    private MediaPlayer beepPlayer;
    private boolean beepPlayed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        timerGif = findViewById(R.id.timerGif);
        answer = findViewById(R.id.answerInput);

        lifeText = findViewById(R.id.lifeText);
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);
        sscore = findViewById(R.id.sscore);
        time = findViewById(R.id.time);

        progressBar.setMax(MAX_QUESTIONS);

        updateLivesUI();
        updateProgressUI();
        updateScoreUI();
        updateTimeUI(timeLeftMillis);

        db = new DatabaseHelper(this);
        startTime = System.currentTimeMillis();

        if (db.isEmpty()) {
            syncFromFirestore();
        } else {
            cursor = db.getQuestion();
            loadQuestion();
            startTimer();
        }
    }

    private void syncFromFirestore() {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();

        fs.collection("questions")
                .get()
                .addOnSuccessListener(snapshot -> {
                    for (DocumentSnapshot doc : snapshot) {
                        db.insertQuestion(
                                doc.getId(),
                                doc.getString("i1"),
                                doc.getString("i2"),
                                doc.getString("a")
                        );
                    }
                    cursor = db.getQuestion();
                    loadQuestion();
                    startTimer();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load questions", Toast.LENGTH_SHORT).show()
                );
    }

    private void loadQuestion() {
        if (cursor != null && cursor.moveToNext()) {
            currentQuestionId = cursor.getString(0);
            correctAnswer = cursor.getString(3);

            int imgRes1 = getResources().getIdentifier(
                    cursor.getString(1).toLowerCase().replaceAll("\\s+", "_"),
                    "drawable", getPackageName()
            );
            int imgRes2 = getResources().getIdentifier(
                    cursor.getString(2).toLowerCase().replaceAll("\\s+", "_"),
                    "drawable", getPackageName()
            );

            if (imgRes1 != 0) img1.setImageResource(imgRes1);
            if (imgRes2 != 0) img2.setImageResource(imgRes2);
        } else {
            finishGame();
        }
    }

    public void submit(View v) {
        String userAnswer = answer.getText().toString().trim();

        if (userAnswer.isEmpty()) {
            Toast.makeText(this, "Please enter an answer!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userAnswer.equalsIgnoreCase(correctAnswer)) {
            MediaPlayer mp = MediaPlayer.create(this, R.raw.correct);
            mp.setOnCompletionListener(MediaPlayer::release);
            mp.start();

            score += 10;
            rightAnswers++;
            updateScoreUI();
        } else {
            MediaPlayer mp = MediaPlayer.create(this, R.raw.wrong);
            mp.setOnCompletionListener(MediaPlayer::release);
            mp.start();

            lives--;
            wrongAnswers++;
            updateLivesUI();

            timeLeftMillis -= 10000;
            if (timeLeftMillis <= 0) {
                finishGame();
                return;
            }

            if (timeLeftMillis <= 10000) {
                beepPlayed = true;
            }

            updateTimeUI(timeLeftMillis);
            restartTimer();
        }

        questionsAnswered++;
        db.markUsed(currentQuestionId);
        updateProgressUI();

        answer.setText("");

        if (cursor != null) cursor.close();
        cursor = db.getQuestion();
        loadQuestion();

        if (questionsAnswered >= MAX_QUESTIONS) {
            finishGame();
        }
    }

    private void startTimer() {
        if (timer != null) timer.cancel();

        timer = new CountDownTimer(timeLeftMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftMillis = millisUntilFinished;
                updateTimeUI(timeLeftMillis);

                if (timeLeftMillis <= 9000 && !beepPlayed) {
                    beepPlayed = true;

                    timerGif.setVisibility(View.VISIBLE);

                    beepPlayer = MediaPlayer.create(GameActivity.this, R.raw.beep);
                    beepPlayer.start();

                    timerGif.postDelayed(() ->
                            timerGif.setVisibility(View.GONE), 10000);
                }
            }

            @Override
            public void onFinish() {
                stopBeep();
                updateTimeUI(0);
                finishGame();
            }
        }.start();
    }

    private void restartTimer() {
        if (timer != null) {
            timer.cancel();
        }
        startTimer();
    }

    private void stopBeep() {
        if (beepPlayer != null) {
            if (beepPlayer.isPlaying()) {
                beepPlayer.stop();
            }
            beepPlayer.release();
            beepPlayer = null;
        }
    }

    private void updateLivesUI() {
        StringBuilder hearts = new StringBuilder();
        for (int i = 0; i < lives; i++) hearts.append("❤");
        lifeText.setText(hearts.toString());
    }

    private void updateProgressUI() {
        progressBar.setProgress(questionsAnswered);
        progressText.setText(questionsAnswered + " / " + MAX_QUESTIONS);
    }

    private void updateScoreUI() {
        sscore.setText(String.valueOf(score));
    }

    private void updateTimeUI(long millis) {
        int totalSeconds = (int) (millis / 1000);
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        time.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void finishGame() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        stopBeep();

        long timeTaken = System.currentTimeMillis() - startTime;

        Intent i = new Intent(this, ResultActivity.class);
        i.putExtra("SCORE", score);
        i.putExtra("RIGHT_ANSWERS", rightAnswers);
        i.putExtra("WRONG_ANSWERS", wrongAnswers);
        i.putExtra("TIME_TAKEN", timeTaken);
        startActivity(i);
        finish();
    }

    @Override
    protected void onDestroy() {
        stopBeep();
        if (timer != null) timer.cancel();
        if (cursor != null && !cursor.isClosed()) cursor.close();
        super.onDestroy();
    }
}
