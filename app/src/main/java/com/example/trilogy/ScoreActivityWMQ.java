package com.example.trilogy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;



import androidx.appcompat.app.AppCompatActivity;

public class ScoreActivityWMQ extends AppCompatActivity {

    TextView tvScore2, tvLevel2, tvMessage;
    ImageButton btnPlayAgain, btnExit;

    String gameType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_wmq);

        tvScore2 = findViewById(R.id.tvScore2);
        tvLevel2 = findViewById(R.id.tvLevel2);
        tvMessage = findViewById(R.id.tvMessage);
        btnPlayAgain = findViewById(R.id.btnPlayAgain);
        btnExit = findViewById(R.id.btnExit);

        Intent intent = getIntent();
        int score = intent.getIntExtra("score", 0);
        int level = intent.getIntExtra("level", 1);
        int lives = intent.getIntExtra("lives", 0);

        gameType = intent.getStringExtra("game");

        if (gameType == null) {
            gameType = "WMQ";
        }


        tvScore2.setText("Score: " + score);
        tvLevel2.setText("Level Reached: " + level);

        if (score >= 50) {
            tvMessage.setText("Good job!");
        } else {
            tvMessage.setText("Try again!");
        }

        btnPlayAgain.setOnClickListener(v -> {
            Intent restart;

            if ("NSG".equals(gameType)) {
                restart = new Intent(ScoreActivityWMQ.this, NumberSequenceGame.class);
            } else {
                restart = new Intent(ScoreActivityWMQ.this, WordMeaningQuiz.class);
            }

            restart.putExtra("level", 1);
            restart.putExtra("score", 0);
            restart.putExtra("lives", 3);
            startActivity(restart);
            finish();
        });

        btnExit.setOnClickListener(v -> {
            Intent exit = new Intent(ScoreActivityWMQ.this, Playscreen.class); // closes app
            startActivity(exit);
        });
    }
}

