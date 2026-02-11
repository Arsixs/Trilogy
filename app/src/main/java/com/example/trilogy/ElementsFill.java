package com.example.trilogy;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Collections;

public class ElementsFill extends AppCompatActivity {

    TextView txtSco, el, shuf, txtTi, txtlev;
    EditText putans;
    Button btn;

    String[] symbols = {
            "H","He","Li","Be","B","C","N","O","F","Ne",
            "Na","Mg","Al","Si","P","S","Cl","Ar","K","Ca",
            "Fe","Cu","Zn","Ag","Au","Sn","Pb","Hg","I","Br"
    };

    String[] names = {
            "hydrogen","helium","lithium","beryllium","boron","carbon","nitrogen","oxygen","fluorine","neon",
            "sodium","magnesium","aluminum","silicon","phosphorus","sulfur","chlorine","argon","potassium","calcium",
            "iron","copper","zinc","silver","gold","tin","lead","mercury","iodine","bromine"
    };

    final int MAX_LEVEL = 30;

    int score = 0;
    int level = 1;
    int currentIndex;
    long timeLeft = 20000;

    CountDownTimer timer;
    MediaPlayer soundCorrect, soundWrong;

    ArrayList<Integer> questionOrder = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_elements_fill);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtSco = findViewById(R.id.txtSco);
        el = findViewById(R.id.el);
        shuf = findViewById(R.id.shuf);
        putans = findViewById(R.id.putans);
        btn = findViewById(R.id.btn);
        txtTi = findViewById(R.id.txtTi);
        txtlev = findViewById(R.id.txtlev);

        soundCorrect = MediaPlayer.create(this, R.raw.correct);
        soundWrong = MediaPlayer.create(this, R.raw.wrong);

        // Build shuffled question order (NO REPEATS)
        for (int i = 0; i < names.length; i++) {
            questionOrder.add(i);
        }
        Collections.shuffle(questionOrder);

        loadQuestion();

        btn.setOnClickListener(v -> checkAnswer());
    }

    // ---------- LOAD QUESTION ----------
    void loadQuestion() {

        if (level > MAX_LEVEL) {
            endGame();
            return;
        }

        currentIndex = questionOrder.get(level - 1);

        el.setText(symbols[currentIndex]);
        shuf.setText(shuffleWord(names[currentIndex]));
        putans.setText("");

        txtSco.setText("Score: " + score);
        txtlev.setText("Level " + level + " / " + MAX_LEVEL);

        startTimer();
    }

    // ---------- CHECK ANSWER ----------
    void checkAnswer() {
        if (timer != null) timer.cancel();

        String answer = putans.getText().toString().trim().toLowerCase();
        String correct = names[currentIndex];

        long elapsed = 20000 - timeLeft;

        if (answer.equals(correct)) {

            if (elapsed <= 3000) score += 10;
            else if (elapsed <= 5000) score += 8;
            else score += 5;

            if (soundCorrect != null) soundCorrect.start();
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();

        } else {
            if (soundWrong != null) soundWrong.start();
            Toast.makeText(this, "Wrong! Answer: " + correct, Toast.LENGTH_SHORT).show();
        }

        level++;

        if (level > MAX_LEVEL) {
            endGame();
        } else {
            nextQuestionDelay();
        }
    }

    // ---------- TIMER ----------
    void startTimer() {
        timeLeft = 20000;
        txtTi.setText("20");

        timer = new CountDownTimer(20000, 1000) {
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                txtTi.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                txtTi.setText("0");

                if (soundWrong != null) soundWrong.start();
                Toast.makeText(ElementsFill.this, "Time's up!", Toast.LENGTH_SHORT).show();

                level++;

                if (level > MAX_LEVEL) {
                    endGame();
                } else {
                    nextQuestionDelay();
                }
            }
        }.start();
    }

    // ---------- NEXT QUESTION ----------
    void nextQuestionDelay() {
        new Handler().postDelayed(this::loadQuestion, 1000);
    }

    // ---------- SHUFFLE WORD ----------
    String shuffleWord(String word) {
        ArrayList<Character> chars = new ArrayList<>();
        for (char c : word.toCharArray()) chars.add(c);
        Collections.shuffle(chars);

        StringBuilder shuffled = new StringBuilder();
        for (char c : chars) shuffled.append(c);

        return shuffled.toString();
    }

    // ---------- END GAME ----------
    void endGame() {
        if (timer != null) timer.cancel();

        btn.setEnabled(false);
        putans.setEnabled(false);

        el.setText("GAME OVER");
        shuf.setText("");

        txtlev.setText("FINAL SCORE: " + score);
        txtSco.setText("");
        txtTi.setText("");

        Toast.makeText(this, "Game Finished!", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundCorrect != null) soundCorrect.release();
        if (soundWrong != null) soundWrong.release();
        if (timer != null) timer.cancel();
    }
}
