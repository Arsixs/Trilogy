package com.example.trilogy;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GuessOrgan extends AppCompatActivity {

    // ---------- VIEWS ----------
    ImageView imageView13;
    TextView txt1, txtTimer, txtScore;
    Button btn1, btn2, btn3, btn4;

    // ---------- GAME STATE ----------
    int score = 0;
    int currentIndex = 0;
    long timeLeft = 20000; // 20 seconds per question
    CountDownTimer countDownTimer;
    ArrayList<Organ> organs = new ArrayList<>();

    // Sounds
    MediaPlayer soundCorrect;
    MediaPlayer soundWrong;

    // ---------- ORGAN MODEL ----------
    class Organ {
        int image;
        String name;

        Organ(int image, String name) {
            this.image = image;
            this.name = name;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_guess_organ);

        // ---------- INIT VIEWS ----------
        imageView13 = findViewById(R.id.imageView13);
        txt1 = findViewById(R.id.txt1);
        txtTimer = findViewById(R.id.txtTimer);
        txtScore = findViewById(R.id.txtScores);

        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);

        // Avoid system bars overlapping score
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.txtScores), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ---------- LOAD ORGAN IMAGES ----------
        organs.add(new Organ(R.drawable.brain, "Brain"));
        organs.add(new Organ(R.drawable.heart, "Heart"));
        organs.add(new Organ(R.drawable.lungs, "Lungs"));
        organs.add(new Organ(R.drawable.liver, "Liver"));
        organs.add(new Organ(R.drawable.stomach, "Stomach"));
        organs.add(new Organ(R.drawable.kidney, "Kidney"));
        organs.add(new Organ(R.drawable.small_intestine, "Small Intestine"));
        organs.add(new Organ(R.drawable.large_intestine, "Large Intestine"));
        organs.add(new Organ(R.drawable.pancreas, "Pancreas"));
        organs.add(new Organ(R.drawable.bladder, "Bladder"));
        Collections.shuffle(organs);

        // ---------- LOAD SOUNDS ----------
        soundCorrect = MediaPlayer.create(this, R.raw.correct); // optional
        soundWrong = MediaPlayer.create(this, R.raw.wrong);     // optional

        // ---------- BUTTON LISTENER ----------
        View.OnClickListener listener = v -> {
            if (countDownTimer != null) countDownTimer.cancel();

            Button clicked = (Button) v;
            String selected = clicked.getText().toString();
            String correct = organs.get(currentIndex).name;

            long elapsedTime = 20000 - timeLeft; // milliseconds

            if (selected.equals(correct)) {
                clicked.setBackgroundColor(Color.GREEN);

                // ---------- SCORING ----------
                if (elapsedTime <= 3000) score += 10;
                else if (elapsedTime <= 5000) score += 8;
                else score += 5;

                if (soundCorrect != null) soundCorrect.start();
                showTemporaryMessage("Correct!");
            } else {
                clicked.setBackgroundColor(Color.RED);
                revealCorrectAnswer();
                if (soundWrong != null) soundWrong.start();
                showTemporaryMessage("Wrong!");
            }

            txtScore.setText(String.valueOf(score));
            nextQuestionDelay();
        };

        btn1.setOnClickListener(listener);
        btn2.setOnClickListener(listener);
        btn3.setOnClickListener(listener);
        btn4.setOnClickListener(listener);

        // ---------- START GAME ----------
        loadQuestion();
    }

    // ---------- LOAD QUESTION ----------
    void loadQuestion() {
        if (currentIndex >= organs.size()) {
            setButtonsEnabled(false);
            if (countDownTimer != null) countDownTimer.cancel();
            txtTimer.setText("");
            txt1.setText("Final Score: " + score);
            return;
        }

        resetButtons();
        setButtonsEnabled(true);
        startTimer();

        Organ current = organs.get(currentIndex);
        imageView13.setImageResource(current.image);

        ArrayList<String> choices = new ArrayList<>();
        choices.add(current.name);

        while (choices.size() < 4) {
            String random = organs.get(new Random().nextInt(organs.size())).name;
            if (!choices.contains(random)) choices.add(random);
        }

        Collections.shuffle(choices);

        btn1.setText(choices.get(0));
        btn2.setText(choices.get(1));
        btn3.setText(choices.get(2));
        btn4.setText(choices.get(3));
    }

    // ---------- TIMER ----------
    void startTimer() {
        timeLeft = 20000;
        txtTimer.setText("20");

        countDownTimer = new CountDownTimer(20000, 1000) {
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                txtTimer.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                txtTimer.setText("0");
                // No points if user didn't answer
                revealCorrectAnswer();
                nextQuestionDelay();
            }
        }.start();
    }

    // ---------- SHOW CORRECT ----------
    void revealCorrectAnswer() {
        String correct = organs.get(currentIndex).name;
        for (Button b : new Button[]{btn1, btn2, btn3, btn4}) {
            if (b.getText().toString().equals(correct)) {
                b.setBackgroundColor(Color.GREEN);
            }
        }
    }

    // ---------- TEMPORARY MESSAGE ----------
    void showTemporaryMessage(String message) {
        txt1.setText(message);
        new Handler().postDelayed(() -> txt1.setText(""), 1000); // clears after 1 second
    }

    // ---------- RESET BUTTONS ----------
    void resetButtons() {
        for (Button b : new Button[]{btn1, btn2, btn3, btn4}) {
            b.setBackgroundColor(Color.parseColor("#FFFACD")); //LTGRAY
        }
    }

    void setButtonsEnabled(boolean enabled) {
        btn1.setEnabled(enabled);
        btn2.setEnabled(enabled);
        btn3.setEnabled(enabled);
        btn4.setEnabled(enabled);
    }

    void nextQuestionDelay() {
        setButtonsEnabled(false);
        new Handler().postDelayed(() -> {
            currentIndex++;
            loadQuestion();
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundCorrect != null) soundCorrect.release();
        if (soundWrong != null) soundWrong.release();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}


