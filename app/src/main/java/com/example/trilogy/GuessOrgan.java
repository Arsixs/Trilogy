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
    TextView txt1, txt2, txtTimer, txtScore, txt4;
    Button btn1, btn2, btn3, btn4;

    // ---------- GAME STATE ----------
    int score = 0;
    int currentIndex = 0;
    long timeLeft = 20000;
    CountDownTimer countDownTimer;

    ArrayList<Organ> organs = new ArrayList<>();
    ArrayList<Question> questions = new ArrayList<>();

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

    // ---------- QUESTION MODEL ----------
    class Question {
        String question;
        String answer;

        Question(String question, String answer) {
            this.question = question;
            this.answer = answer;
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
        txt2 = findViewById(R.id.txt2);
        txtTimer = findViewById(R.id.txtTimer);
        txtScore = findViewById(R.id.txtScores);
        txt4 = findViewById(R.id.txt4);

        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);

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

        // ---------- LOAD TEXT QUESTIONS ----------
        questions.add(new Question("Pumps blood throughout \n the body", "Heart"));
        questions.add(new Question("Filters waste from the \n blood to make urine", "Kidney"));
        questions.add(new Question("Breaks down food and absorbs \n nutrients", "Small Intestine"));
        questions.add(new Question("Produces insulin to regulate \n blood sugar", "Pancreas"));
        questions.add(new Question("Stores urine before it is \n released from the body", "Bladder"));
        questions.add(new Question("Controls thinking, memory, \n and body movements", "Brain"));
        questions.add(new Question("Exchanges oxygen and carbon \n dioxide with the blood", "Lungs"));
        questions.add(new Question("Detoxifies chemicals and \n metabolizes drugs", "Liver"));
        questions.add(new Question("Stores and eliminates \n solid waste", "Large Intestine"));
        questions.add(new Question("Digests food and churns it \n into a liquid mixture", "Stomach"));

        Collections.shuffle(questions);

        // ---------- LOAD SOUNDS ----------
        soundCorrect = MediaPlayer.create(this, R.raw.correct);
        soundWrong = MediaPlayer.create(this, R.raw.wrong);

        // ---------- BUTTON LISTENER ----------
        View.OnClickListener listener = v -> {

            if (countDownTimer != null) countDownTimer.cancel();

            Button clicked = (Button) v;
            String selected = clicked.getText().toString();

            String correct;
            if (currentIndex < organs.size()) {
                correct = organs.get(currentIndex).name;
            } else {
                correct = questions.get(currentIndex - organs.size()).answer;
            }

            long elapsedTime = 20000 - timeLeft;

            if (selected.equals(correct)) {
                clicked.setBackgroundColor(Color.GREEN);

                if (elapsedTime <= 3000) score += 10;
                else if (elapsedTime <= 5000) score += 8;
                else score += 5;

                if (soundCorrect != null) soundCorrect.start();
                showTemporaryMessage("Correct!");
            } else {
                clicked.setBackgroundColor(Color.RED);
                revealCorrectAnswer(correct);
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

        loadQuestion();
    }

    // ---------- LOAD QUESTION ----------
    void loadQuestion() {

        // Update Level 1-20
        txt4.setText("Level " + (currentIndex + 1));

        if (currentIndex < organs.size()) {
            // Organ image levels
            imageView13.setVisibility(View.VISIBLE);
            txt2.setText("");

            Organ current = organs.get(currentIndex);
            imageView13.setImageResource(current.image);

            prepareChoices(current.name);

        } else if (currentIndex < organs.size() + questions.size()) {
            // Text question levels
            imageView13.setVisibility(View.INVISIBLE);

            Question current = questions.get(currentIndex - organs.size());
            txt2.setText(current.question);

            prepareChoices(current.answer);

        } else {
            // Game Over
            setButtonsEnabled(false);
            txtTimer.setText("");
            txt1.setText("Final Score: " + score);
            return;
        }

        resetButtons();
        setButtonsEnabled(true);
        startTimer();
    }

    // ---------- PREPARE CHOICES ----------
    void prepareChoices(String correctAnswer) {

        ArrayList<String> choices = new ArrayList<>();
        choices.add(correctAnswer);

        while (choices.size() < 4) {
            String random;
            if (currentIndex < organs.size()) {
                random = organs.get(new Random().nextInt(organs.size())).name;
            } else {
                random = questions.get(new Random().nextInt(questions.size())).answer;
            }
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

                String correct;
                if (currentIndex < organs.size()) {
                    correct = organs.get(currentIndex).name;
                } else {
                    correct = questions.get(currentIndex - organs.size()).answer;
                }

                revealCorrectAnswer(correct);
                nextQuestionDelay();
            }
        }.start();
    }

    void revealCorrectAnswer(String correct) {
        for (Button b : new Button[]{btn1, btn2, btn3, btn4}) {
            if (b.getText().toString().equals(correct)) {
                b.setBackgroundColor(Color.GREEN);
            }
        }
    }

    void showTemporaryMessage(String message) {
        txt1.setText(message);
        new Handler().postDelayed(() -> txt1.setText(""), 1000);
    }

    void resetButtons() {
        for (Button b : new Button[]{btn1, btn2, btn3, btn4}) {
            b.setBackgroundColor(Color.parseColor("#FFFACD"));
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
