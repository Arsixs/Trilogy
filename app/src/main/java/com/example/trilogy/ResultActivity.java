package com.example.trilogy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_result);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Bind views
        TextView resultText = findViewById(R.id.resultScore);
        TextView correctnwrong = findViewById(R.id.resultScore2);
        TextView timetaken = findViewById(R.id.timetaken);
        TextView livesText = findViewById(R.id.remainingLives);
        Button playAgainBtn = findViewById(R.id.playAgainButton);

        // Get intent data (✔ CORRECT TYPES)
        Intent intent = getIntent();
        int score = intent.getIntExtra("SCORE", 0);
        int rightAnswers = intent.getIntExtra("RIGHT_ANSWERS", 0);
        int wrongAnswers = intent.getIntExtra("WRONG_ANSWERS", 0);
        int remainingLives = intent.getIntExtra("LIVES", 0);
        boolean gameOver = intent.getBooleanExtra("GAME_OVER", false);

        // ✅ READ TIME AS LONG
        long timeTaken = intent.getLongExtra("TIME_TAKEN", 0);

        // ✅ FORMAT TIME (MM:SS)
        int seconds = (int) (timeTaken / 1000);
        String formattedTime = String.format(
                "%02d:%02d",
                seconds / 60,
                seconds % 60
        );

        // Set texts
        if (gameOver) {
            resultText.setText("Game Over 😢\nScore: " + score);
        } else {
            resultText.setText("You Win! 🎉\nScore: " + score);
        }

        correctnwrong.setText(
                "Correct: " + rightAnswers + "\nWrong: " + wrongAnswers
        );

        timetaken.setText("Time Taken: " + formattedTime);
        livesText.setText("Lives Left: " + remainingLives);

        // Play again
        playAgainBtn.setOnClickListener(v -> {
            Intent i = new Intent(ResultActivity.this, GameActivity.class);
            startActivity(i);
            finish();
        });
    }
}
