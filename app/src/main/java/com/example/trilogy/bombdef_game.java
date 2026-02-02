package com.example.trilogy;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class bombdef_game extends AppCompatActivity {

    TextView tvTimer, tvQuestion, tvScore;
    EditText etAnswer;
    ImageButton btndefuse;

    int correctAnswer;
    int solved = 0;

    Random random = new Random();
    CountDownTimer timer;

    long timeLeft = 60000; // 60 seconds
    final long PENALTY_TIME = 5000; // 5 seconds penalty

    final long WARNING_TIME = 10000; // 10 seconds


    MediaPlayer defuseSound, boomSound,beepSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bombdef_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
            );
            return insets;
        });

        // ✅ REST OF YOUR CODE
        tvTimer = findViewById(R.id.tvTimer);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvScore = findViewById(R.id.tvScore);
        etAnswer = findViewById(R.id.etAnswer);
        btndefuse = findViewById(R.id.btndefuse);

        defuseSound = MediaPlayer.create(this, R.raw.defuse);
        boomSound = MediaPlayer.create(this, R.raw.boom);
        beepSound = MediaPlayer.create(this, R.raw.beep);

        startTimer();
        generateQuestion();

        btndefuse.setOnClickListener(v -> checkAnswer());
    }

    private void startTimer() {
        timer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                long seconds = millisUntilFinished / 1000;
                long minutes = seconds / 60;
                long remainingSeconds = seconds % 60;

                tvTimer.setText(
                        String.format("%02d:%02d", minutes, remainingSeconds)
                );


                // 🔊 Beep every second from 60s to 0
                if (beepSound != null) {
                    beepSound.seekTo(0);
                    beepSound.start();
                }

                // Optional color change
                if (millisUntilFinished <= 10000) {
                    tvTimer.setTextColor(getResources().getColor(R.color.red));
                } else {
                    tvTimer.setTextColor(getResources().getColor(R.color.green));
                }
            }

            @Override
            public void onFinish() {
                if (beepSound.isPlaying()) beepSound.stop();
                boomSound.start();
                Toast.makeText(
                        bombdef_game.this,
                        "💣 BOOM! Time's up!",
                        Toast.LENGTH_LONG
                ).show();
                btndefuse.setEnabled(false);
            }
        }.start();
    }

    private void restartTimer() {
        if (timer != null) timer.cancel();
        startTimer();
    }

    private void generateQuestion() {
        int a = random.nextInt(20) + 1;
        int b = random.nextInt(20) + 1;
        int operator = random.nextInt(4);

        switch (operator) {
            case 0:
                tvQuestion.setText(a + " + " + b);
                correctAnswer = a + b;
                break;

            case 1:
                tvQuestion.setText(a + " - " + b);
                correctAnswer = a - b;
                break;

            case 2:
                tvQuestion.setText(a + " × " + b);
                correctAnswer = a * b;
                break;

            case 3:
                tvQuestion.setText((a * b) + " ÷ " + b);
                correctAnswer = a;
                break;
        }
    }

    private void checkAnswer() {
        if (etAnswer.getText().toString().isEmpty()) return;

        int userAnswer = Integer.parseInt(etAnswer.getText().toString());

        if (userAnswer == correctAnswer) {
            solved++;
            tvScore.setText("Solved: " + solved + " / 5");
            etAnswer.setText("");

            if (solved == 5) {
                if (beepSound.isPlaying()) beepSound.stop();
                timer.cancel();
                defuseSound.start();
                Toast.makeText(
                        this,
                        "🎉 Bomb Defused!",
                        Toast.LENGTH_LONG
                ).show();
                btndefuse.setEnabled(false);
            } else {
                generateQuestion();
            }

        } else {
            etAnswer.setText("");
            timeLeft -= PENALTY_TIME;

            Toast.makeText(
                    this,
                    "❌ Wrong! -5 seconds",
                    Toast.LENGTH_SHORT
            ).show();

            if (timeLeft <= 0) {
                timer.cancel();
                boomSound.start();
                Toast.makeText(
                        this,
                        "💥 BOOM!",
                        Toast.LENGTH_LONG
                ).show();
                btndefuse.setEnabled(false);
            } else {
                restartTimer();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
        if (defuseSound != null) defuseSound.release();
        if (boomSound != null) boomSound.release();
        if (beepSound != null) beepSound.release();
    }
}
