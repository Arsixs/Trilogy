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

        // Get intent data
        Intent intent = getIntent();
        int score = intent.getIntExtra("SCORE", 0);
        boolean gameOver = intent.getBooleanExtra("GAME_OVER", false);
        int remainingLives = intent.getIntExtra("LIVES", 0);

        // Bind views
        TextView resultText = findViewById(R.id.resultScore);
        TextView livesText = findViewById(R.id.remainingLives);
        Button playAgainBtn = findViewById(R.id.playAgainButton);

        // Set result message
        if (gameOver) {
            resultText.setText("Game Over 😢\nScore: " + score);
        } else {
            resultText.setText("You Win! 🎉\nScore: " + score);
        }

        // Optional: show remaining lives
        if (livesText != null) {
            livesText.setText("Lives Left: " + remainingLives);
        }

        // 🔁 Play again (restart game cleanly)
        playAgainBtn.setOnClickListener(v -> {
            Intent i = new Intent(ResultActivity.this, GameActivity.class);
            startActivity(i);
            finish();
        });
    }
}

