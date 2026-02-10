package com.example.trilogy;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class bombdef_game extends AppCompatActivity {

    TextView tvTimer, tvQuestion, tvDefuse, starpoints;
    EditText etAnswer;
    ImageButton btndefuse;
    ImageView bomb;


    int correctAnswer;
    int solved = 0;
    int score = 0;
    int fastCorrectStreak = 0;

    int mathLevel = 1;
    int mathXP = 0;
    int mathXPToLevel = 100;


    String difficulty = "EASY";

    final int POINTS_PER_CORRECT = 10;
    final long PENALTY_TIME = 5000; // 5 seconds
    final long WARNING_TIME = 10000; // 10 seconds
    final long SPEED_WINDOW = 5000; // 5 seconds
    final int SPEED_BONUS = 20;     // extra points

    // Speed bonus tracking
    long timeLeft = 60000; // 60 seconds
    long questionStartTime;
    Random random = new Random();
    CountDownTimer timer;

    MediaPlayer defuseSound, boomSound, beepSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bombdef_game);
        SharedPreferences prefs = getSharedPreferences("levels", MODE_PRIVATE);

        mathLevel = prefs.getInt("math_level", 1);
        mathXP = prefs.getInt("math_xp", 0);
        mathXPToLevel = prefs.getInt("math_xpToLevel", 100);

        if (getIntent() != null && getIntent().hasExtra("DIFFICULTY")) {
            difficulty = getIntent().getStringExtra("DIFFICULTY");
        }

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

        // UI
        tvTimer = findViewById(R.id.tvTimer);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvDefuse = findViewById(R.id.tvDefuse);
        starpoints = findViewById(R.id.starpoints);
        etAnswer = findViewById(R.id.etAnswer);
        btndefuse = findViewById(R.id.btndefuse);
        bomb = findViewById(R.id.bomb);

        // Sounds
        defuseSound = MediaPlayer.create(this, R.raw.defuse);
        boomSound = MediaPlayer.create(this, R.raw.boom);
        beepSound = MediaPlayer.create(this, R.raw.beep);

        // Initial UI
        tvDefuse.setText("Defuse Progress: 0 / 10");
        starpoints.setText("0");

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

                tvTimer.setText(String.format("%02d:%02d", minutes, remainingSeconds));

                // Beep every second
                if (beepSound != null) {
                    beepSound.seekTo(0);
                    beepSound.start();
                }

                // Panic mode
                if (millisUntilFinished <= WARNING_TIME) {
                    tvTimer.setTextColor(getResources().getColor(R.color.red));

                    Animation shake = AnimationUtils.loadAnimation(
                            bombdef_game.this, R.anim.shake);
                    tvTimer.startAnimation(shake);
                    bomb.startAnimation(shake);
                } else {
                    tvTimer.setTextColor(getResources().getColor(R.color.green));
                    tvTimer.clearAnimation();
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
        questionStartTime = System.currentTimeMillis();

        int a = random.nextInt(20) + 1;
        int b = random.nextInt(20) + 1;

        int operator;

        switch (difficulty) {
            case "EASY":
                // Only + and -
                operator = random.nextInt(2); // 0,1
                break;

            case "NORMAL":
                // Only × and ÷
                operator = random.nextInt(2) + 2; // 2,3
                break;

            case "HARD":
            default:
                // All operators
                operator = random.nextInt(4); // 0,1,2,3
                break;
        }

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
    private void addMathXP(int xpGained) {
        SharedPreferences prefs = getSharedPreferences("levels", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        mathXP += xpGained;

        while (mathXP >= mathXPToLevel) {
            mathXP -= mathXPToLevel;
            mathLevel++;
        }

        editor.putInt("math_xp", mathXP);
        editor.putInt("math_level", mathLevel);
        editor.apply();
    }

    private void checkAnswer() {
        if (etAnswer.getText().toString().isEmpty()) return;

        int userAnswer = Integer.parseInt(etAnswer.getText().toString());
        etAnswer.setText("");

        if (userAnswer == correctAnswer) {
            solved++;

            long answerTime = System.currentTimeMillis() - questionStartTime;

            // Speed logic
            if (answerTime <= SPEED_WINDOW) {
                fastCorrectStreak++;
            } else {
                fastCorrectStreak = 0;
            }

            score += POINTS_PER_CORRECT;

            // Bonus after 3 fast correct answers
            if (fastCorrectStreak == 3) {
                score += SPEED_BONUS;
                fastCorrectStreak = 0;

                Toast.makeText(
                        this,
                        "⚡ SPEED BONUS! +" + SPEED_BONUS,
                        Toast.LENGTH_SHORT
                ).show();
            }

            tvDefuse.setText("Defuse Progress: " + solved + " / 10");
            starpoints.setText(String.valueOf(score));


            if (solved == 10) {
                if (beepSound.isPlaying()) beepSound.stop();
                timer.cancel();
                defuseSound.start();

                int xpEarned = score; // XP based on performance
                addMathXP(xpEarned);

                Toast.makeText(
                        this,
                        "🎉 Bomb Defused!\n+" + xpEarned + " Math XP",
                        Toast.LENGTH_LONG
                ).show();

                btndefuse.setEnabled(false);

                // Return to PlayScreen after short delay
                bomb.postDelayed(() -> finish(), 2000);
            } else {
                generateQuestion();
            }

        } else {
            timeLeft -= PENALTY_TIME;

            Toast.makeText(
                    this,
                    "❌ Wrong! -5 seconds",
                    Toast.LENGTH_SHORT
            ).show();

            if (timeLeft <= 0) {
                timer.cancel();
                if (beepSound.isPlaying()) beepSound.stop();
                boomSound.start();

                Toast.makeText(
                        this,
                        "💥 BOOM!",
                        Toast.LENGTH_LONG
                ).show();

                btndefuse.setEnabled(false);
            } else {
                generateQuestion();
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
    //User exit to home music will stop
    @Override
    protected void onStop() {
        super.onStop();
        if (beepSound != null && beepSound.isPlaying()) {
            beepSound.stop();
            beepSound.release();
            beepSound = null;

        }
    }
}
