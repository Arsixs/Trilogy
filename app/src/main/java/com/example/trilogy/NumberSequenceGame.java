package com.example.trilogy;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class NumberSequenceGame extends AppCompatActivity {

    // UI
    TextView tvScore, tvLevel;
    TextView tvA, tvB, tvC, tvD;
    ImageView heart1, heart2, heart3;

    Button btnA2, btnB2, btnC2, btnD2;

    // Game Data
    ArrayList<int[]> sequences = new ArrayList<>();
    int level = 0;
    int score = 0;
    int lives = 3;

    int correctAnswer;
    int missingIndex;

    MediaPlayer mpCorrect, mpWrong;


    Random random = new Random();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_sequence_game);


        tvScore = findViewById(R.id.tvScore3);
        tvLevel = findViewById(R.id.tvLevel3);

        tvA = findViewById(R.id.tvA);
        tvB = findViewById(R.id.tvB);
        tvC = findViewById(R.id.tvC);
        tvD = findViewById(R.id.tvD);

        heart1 = findViewById(R.id.heart1);
        heart2 = findViewById(R.id.heart2);
        heart3 = findViewById(R.id.heart3);

        btnA2 = findViewById(R.id.btnA2);
        btnB2 = findViewById(R.id.btnB2);
        btnC2 = findViewById(R.id.btnC2);
        btnD2 = findViewById(R.id.btnD2);

        setupSequences();
        loadLevel();

        btnA2.setOnClickListener(v -> checkAnswer(btnA2));
        btnB2.setOnClickListener(v -> checkAnswer(btnB2));
        btnC2.setOnClickListener(v -> checkAnswer(btnC2));
        btnD2.setOnClickListener(v -> checkAnswer(btnD2));

        mpCorrect = MediaPlayer.create(this, R.raw.checksoundeffect);
        mpWrong = MediaPlayer.create(this, R.raw.wrongsoundeffect);

    }

    void setupSequences() {
        sequences.add(new int[]{2, 4, 6, 8});
        sequences.add(new int[]{1, 2, 3, 4});
        sequences.add(new int[]{5, 10, 15, 20});
        sequences.add(new int[]{3, 6, 9, 12});
        sequences.add(new int[]{10, 20, 30, 40});
        sequences.add(new int[]{4, 8, 12, 16});
        sequences.add(new int[]{7, 14, 21, 28});
        sequences.add(new int[]{2, 4, 8, 16});
        sequences.add(new int[]{3, 6, 12, 24});
        sequences.add(new int[]{5, 10, 20, 40});

        sequences.add(new int[]{1, 3, 5, 7});
        sequences.add(new int[]{2, 6, 10, 14});
        sequences.add(new int[]{10, 15, 20, 25});
        sequences.add(new int[]{4, 6, 8, 10});
        sequences.add(new int[]{9, 18, 27, 36});
        sequences.add(new int[]{6, 12, 18, 24});
        sequences.add(new int[]{8, 16, 24, 32});
        sequences.add(new int[]{11, 22, 33, 44});
        sequences.add(new int[]{12, 14, 16, 18});
        sequences.add(new int[]{20, 25, 30, 35});

        sequences.add(new int[]{1, 4, 7, 10});
        sequences.add(new int[]{2, 5, 8, 11});
        sequences.add(new int[]{3, 7, 11, 15});
        sequences.add(new int[]{5, 8, 11, 14});
        sequences.add(new int[]{6, 9, 12, 15});
        sequences.add(new int[]{10, 12, 14, 16});
        sequences.add(new int[]{15, 20, 25, 30});
        sequences.add(new int[]{18, 21, 24, 27});
        sequences.add(new int[]{2, 3, 4, 5});
        sequences.add(new int[]{30, 35, 40, 45});

        sequences.add(new int[]{1, 4, 9, 16});
        sequences.add(new int[]{4, 9, 16, 25});
        sequences.add(new int[]{2, 6, 18, 54});
        sequences.add(new int[]{3, 9, 27, 81});
        sequences.add(new int[]{5, 15, 45, 135});
        sequences.add(new int[]{10, 30, 90, 270});
        sequences.add(new int[]{1, 2, 4, 8});
        sequences.add(new int[]{2, 4, 8, 16});
        sequences.add(new int[]{3, 6, 12, 24});
        sequences.add(new int[]{4, 12, 36, 108});

        sequences.add(new int[]{20, 18, 16, 14});
        sequences.add(new int[]{50, 45, 40, 35});
        sequences.add(new int[]{100, 90, 80, 70});
        sequences.add(new int[]{64, 32, 16, 8});
        sequences.add(new int[]{81, 27, 9, 3});
        sequences.add(new int[]{16, 8, 4, 2});
        sequences.add(new int[]{40, 35, 30, 25});
        sequences.add(new int[]{60, 50, 40, 30});
        sequences.add(new int[]{28, 21, 14, 7});
        sequences.add(new int[]{45, 40, 35, 30});

        sequences.add(new int[]{2, 5, 10, 17});
        sequences.add(new int[]{3, 8, 15, 24});
        sequences.add(new int[]{4, 11, 20, 31});
        sequences.add(new int[]{5, 14, 25, 38});
        sequences.add(new int[]{6, 17, 30, 45});
        sequences.add(new int[]{10, 13, 18, 25});
        sequences.add(new int[]{7, 14, 28, 56});
        sequences.add(new int[]{9, 18, 36, 72});
        sequences.add(new int[]{11, 22, 44, 88});
        sequences.add(new int[]{12, 24, 48, 96});

        sequences.add(new int[]{2, 3, 5, 8});
        sequences.add(new int[]{3, 5, 8, 13});
        sequences.add(new int[]{5, 8, 13, 21});
        sequences.add(new int[]{8, 13, 21, 34});
        sequences.add(new int[]{1, 1, 2, 3});
        sequences.add(new int[]{1, 2, 3, 5});
        sequences.add(new int[]{2, 3, 5, 7});
        sequences.add(new int[]{4, 6, 9, 13});
        sequences.add(new int[]{6, 10, 15, 21});
        sequences.add(new int[]{10, 15, 21, 28});

        sequences.add(new int[]{2, 6, 12, 20});
        sequences.add(new int[]{3, 8, 15, 24});
        sequences.add(new int[]{5, 12, 21, 32});
        sequences.add(new int[]{7, 16, 27, 40});
        sequences.add(new int[]{4, 10, 18, 28});
        sequences.add(new int[]{6, 14, 24, 36});
        sequences.add(new int[]{8, 18, 30, 44});
        sequences.add(new int[]{9, 20, 33, 48});
        sequences.add(new int[]{10, 22, 36, 52});
        sequences.add(new int[]{12, 26, 42, 60});

        sequences.add(new int[]{1, 4, 10, 20});
        sequences.add(new int[]{2, 6, 14, 30});
        sequences.add(new int[]{3, 8, 18, 36});
        sequences.add(new int[]{4, 10, 22, 46});
        sequences.add(new int[]{5, 12, 26, 54});
        sequences.add(new int[]{6, 14, 30, 62});
        sequences.add(new int[]{7, 16, 34, 70});
        sequences.add(new int[]{8, 18, 38, 78});
        sequences.add(new int[]{9, 20, 42, 86});
        sequences.add(new int[]{10, 22, 46, 94});

        sequences.add(new int[]{100, 95, 85, 70});
        sequences.add(new int[]{90, 81, 64, 49});
        sequences.add(new int[]{81, 72, 63, 54});
        sequences.add(new int[]{64, 56, 49, 42});
        sequences.add(new int[]{49, 42, 36, 30});
        sequences.add(new int[]{36, 30, 25, 20});
        sequences.add(new int[]{25, 20, 16, 12});
        sequences.add(new int[]{16, 12, 9, 6});
        sequences.add(new int[]{20, 18, 15, 11});
        sequences.add(new int[]{30, 27, 23, 18});

    }

    void loadLevel() {
        if (level >= sequences.size()) {
            openScoreScreen();
            return;
        }

        tvScore.setText("Score: " + score);
        tvLevel.setText("Level: " + (level + 1));

        int[] seq = sequences.get(level);

        missingIndex = random.nextInt(4);
        correctAnswer = seq[missingIndex];

        TextView[] slots = {tvA, tvB, tvC, tvD};

        for (int i = 0; i < 4; i++) {
            if (i == missingIndex) {
                slots[i].setText("?");
            } else {
                slots[i].setText(String.valueOf(seq[i]));
            }
        }

        setupAnswers();
    }

    void setupAnswers() {
        ArrayList<Integer> choices = new ArrayList<>();
        choices.add(correctAnswer);
        choices.add(correctAnswer + 2);
        choices.add(correctAnswer - 2);
        choices.add(correctAnswer + 4);

        Collections.shuffle(choices);

        btnA2.setText(String.valueOf(choices.get(0)));
        btnB2.setText(String.valueOf(choices.get(1)));
        btnC2.setText(String.valueOf(choices.get(2)));
        btnD2.setText(String.valueOf(choices.get(3)));
    }

    void checkAnswer(Button btn) {
        int selected = Integer.parseInt(btn.getText().toString());

        if (selected == correctAnswer) {
            score += 1;
            level++;

            if (mpCorrect != null) mpCorrect.start();

        } else {
            lives--;
            updateHearts();

            if (mpWrong != null) mpWrong.start();

            if (lives == 0) {
                openScoreScreen();
                return;
            }
        }

        loadLevel();
    }

    void updateHearts() {
        if (lives < 3) heart3.setVisibility(View.INVISIBLE);
        if (lives < 2) heart2.setVisibility(View.INVISIBLE);
        if (lives < 1) heart1.setVisibility(View.INVISIBLE);
    }

    void openScoreScreen() {
        Intent intent = new Intent(this, ScoreActivityWMQ.class);
        intent.putExtra("score", score);
        intent.putExtra("level", level);
        intent.putExtra("lives", lives);
        intent.putExtra("game", "NSG");
        startActivity(intent);
        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mpCorrect != null) {
            mpCorrect.release();
            mpCorrect = null;
        }
        if (mpWrong != null) {
            mpWrong.release();
            mpWrong = null;
        }
    }


}


