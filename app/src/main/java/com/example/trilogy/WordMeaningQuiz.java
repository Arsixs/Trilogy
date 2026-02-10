package com.example.trilogy;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class WordMeaningQuiz extends AppCompatActivity {


    TextView tvQuestion, tvScore, tvLevel;
    Button btnA, btnB, btnC, btnD;

    ImageButton btnSubmit;
    ImageView heart1, heart2, heart3;

    int currentLevel = 1;

    int lives = 3;


    String selectedAnswer = "";
    int currentIndex = 0;
    int score = 0;

    ArrayList<String> questions = new ArrayList<>();
    ArrayList<String[]> choices = new ArrayList<>();
    ArrayList<String> answers = new ArrayList<>();

    MediaPlayer correctSound, wrongSound;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_word_meaning_quiz);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvQuestion = findViewById(R.id.tvQuestion);
        tvScore = findViewById(R.id.tvScore2);
        tvLevel = findViewById(R.id.tvLevel);

        btnA = findViewById(R.id.btnA);
        btnB = findViewById(R.id.btnB);
        btnC = findViewById(R.id.btnC);
        btnD = findViewById(R.id.btnD);
        btnSubmit = findViewById(R.id.btnSubmit);

        heart1 = findViewById(R.id.heart1);
        heart2 = findViewById(R.id.heart2);
        heart3 = findViewById(R.id.heart3);

        correctSound = MediaPlayer.create(this, R.raw.checksoundeffect);
        wrongSound = MediaPlayer.create(this, R.raw.wrongsoundeffect);

        loadQuestions();

        View.OnClickListener choiceClick = v -> {
            resetButtons();
            Button btn = (Button) v;
            selectedAnswer = btn.getText().toString();
            btn.setAlpha(0.6f);
        };

        btnA.setOnClickListener(choiceClick);
        btnB.setOnClickListener(choiceClick);
        btnC.setOnClickListener(choiceClick);
        btnD.setOnClickListener(choiceClick);

        btnSubmit.setOnClickListener(v -> {
            if (selectedAnswer.isEmpty()) return;

            if (selectedAnswer.equals(answers.get(currentIndex))) {
                score++;
                tvScore.setText("Score: " + score);

                correctSound.start();

            } else {
                lives--;
                updateHearts();

                wrongSound.start();

                if (lives == 0) {
                    goToScoreScreen();
                    return;
                }
            }

            currentIndex++;

            if (currentIndex < questions.size()) {
                loadQuestion();
            } else {
                goToScoreScreen();
            }
        });

        loadQuestion();
    }

    private void loadQuestion() {
        tvQuestion.setText(questions.get(currentIndex));

        btnA.setText(choices.get(currentIndex)[0]);
        btnB.setText(choices.get(currentIndex)[1]);
        btnC.setText(choices.get(currentIndex)[2]);
        btnD.setText(choices.get(currentIndex)[3]);

        currentLevel = currentIndex + 1;
        tvLevel.setText("Level " + currentLevel);

        selectedAnswer = "";
        resetButtons();
    }

    private void resetButtons() {
        btnA.setAlpha(1f);
        btnB.setAlpha(1f);
        btnC.setAlpha(1f);
        btnD.setAlpha(1f);
    }

    private void updateHearts() {
        heart1.setImageResource(lives >= 1 ? R.drawable.ic_heart_full : R.drawable.ic_heart_empty);
        heart2.setImageResource(lives >= 2 ? R.drawable.ic_heart_full : R.drawable.ic_heart_empty);
        heart3.setImageResource(lives >= 3 ? R.drawable.ic_heart_full : R.drawable.ic_heart_empty);
    }

    private void goToScoreScreen() {
        Intent intent = new Intent(this, ScoreActivityWMQ.class);
        intent.putExtra("score", score);
        intent.putExtra("level", currentLevel);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (correctSound != null) {
            correctSound.release();
            correctSound = null;
        }

        if (wrongSound != null) {
            wrongSound.release();
            wrongSound = null;
        }
    }

    private void loadQuestions() {

        questions.add("Happy");
        choices.add(new String[]{"Joyful", "Angry", "Sad", "Tired"});
        answers.add("Joyful");

        questions.add("Big");
        choices.add(new String[]{"Tiny", "Large", "Short", "Weak"});
        answers.add("Large");

        questions.add("Fast");
        choices.add(new String[]{"Quick", "Slow", "Late", "Soft"});
        answers.add("Quick");

        questions.add("Cold");
        choices.add(new String[]{"Hot", "Warm", "Cool", "Bright"});
        answers.add("Cool");

        questions.add("Brave");
        choices.add(new String[]{"Scared", "Fearless", "Weak", "Quiet"});
        answers.add("Fearless");

        questions.add("Silent");
        choices.add(new String[]{"Noisy", "Quiet", "Busy", "Fast"});
        answers.add("Quiet");

        questions.add("Confused");
        choices.add(new String[]{"Clear", "Lost", "Sure", "Happy"});
        answers.add("Lost");

        questions.add("Generous");
        choices.add(new String[]{"Selfish", "Kind", "Greedy", "Lazy"});
        answers.add("Kind");

        questions.add("Scarce");
        choices.add(new String[]{"Rare", "Plenty", "Cheap", "Large"});
        answers.add("Rare");

        questions.add("Diligent");
        choices.add(new String[]{"Lazy", "Hardworking", "Slow", "Careless"});
        answers.add("Hardworking");

        questions.add("Angry");
        choices.add(new String[]{"Mad", "Calm", "Happy", "Kind"});
        answers.add("Mad");

        questions.add("Easy");
        choices.add(new String[]{"Simple", "Hard", "Rough", "Slow"});
        answers.add("Simple");

        questions.add("Clean");
        choices.add(new String[]{"Dirty", "Pure", "Messy", "Wet"});
        answers.add("Pure");

        questions.add("Strong");
        choices.add(new String[]{"Weak", "Powerful", "Thin", "Slow"});
        answers.add("Powerful");

        questions.add("Old");
        choices.add(new String[]{"Young", "Ancient", "Fresh", "New"});
        answers.add("Ancient");

        questions.add("Small");
        choices.add(new String[]{"Tiny", "Huge", "Tall", "Wide"});
        answers.add("Tiny");

        questions.add("Rich");
        choices.add(new String[]{"Poor", "Wealthy", "Hungry", "Sad"});
        answers.add("Wealthy");

        questions.add("Smart");
        choices.add(new String[]{"Clever", "Dull", "Lazy", "Slow"});
        answers.add("Clever");

        questions.add("Quick");
        choices.add(new String[]{"Slow", "Fast", "Late", "Weak"});
        answers.add("Fast");

        questions.add("Nice");
        choices.add(new String[]{"Kind", "Mean", "Rude", "Cold"});
        answers.add("Kind");

        questions.add("Funny");
        choices.add(new String[]{"Serious", "Humorous", "Angry", "Sad"});
        answers.add("Humorous");

        questions.add("Busy");
        choices.add(new String[]{"Free", "Active", "Lazy", "Quiet"});
        answers.add("Active");

        questions.add("Bright");
        choices.add(new String[]{"Dark", "Shiny", "Dull", "Soft"});
        answers.add("Shiny");

        questions.add("Tired");
        choices.add(new String[]{"Sleepy", "Energetic", "Fast", "Strong"});
        answers.add("Sleepy");

        questions.add("Hungry");
        choices.add(new String[]{"Full", "Starving", "Happy", "Cold"});
        answers.add("Starving");

        questions.add("Calm");
        choices.add(new String[]{"Angry", "Peaceful", "Loud", "Busy"});
        answers.add("Peaceful");

        questions.add("Early");
        choices.add(new String[]{"Late", "On-time", "Fast", "Soon"});
        answers.add("On-time");

        questions.add("Safe");
        choices.add(new String[]{"Dangerous", "Secure", "Risky", "Wild"});
        answers.add("Secure");

        questions.add("Thin");
        choices.add(new String[]{"Slim", "Fat", "Wide", "Tall"});
        answers.add("Slim");

        questions.add("Hot");
        choices.add(new String[]{"Cold", "Warm", "Cool", "Freezing"});
        answers.add("Warm");

        questions.add("Strong");
        choices.add(new String[]{"Powerful", "Weak", "Soft", "Slow"});
        answers.add("Powerful");

        questions.add("Quiet");
        choices.add(new String[]{"Silent", "Loud", "Busy", "Fast"});
        answers.add("Silent");

        questions.add("Dirty");
        choices.add(new String[]{"Clean", "Messy", "Pure", "Bright"});
        answers.add("Messy");

        questions.add("Hard");
        choices.add(new String[]{"Easy", "Difficult", "Soft", "Light"});
        answers.add("Difficult");

        questions.add("Slow");
        choices.add(new String[]{"Fast", "Lazy", "Sluggish", "Quick"});
        answers.add("Sluggish");

        questions.add("Happy");
        choices.add(new String[]{"Cheerful", "Sad", "Angry", "Cold"});
        answers.add("Cheerful");

        questions.add("Loud");
        choices.add(new String[]{"Noisy", "Quiet", "Soft", "Silent"});
        answers.add("Noisy");

        questions.add("Simple");
        choices.add(new String[]{"Easy", "Complex", "Hard", "Confusing"});
        answers.add("Easy");

        questions.add("Observe");
        choices.add(new String[]{"Ignore", "Watch", "Destroy", "Forget"});
        answers.add("Watch");

        questions.add("Assist");
        choices.add(new String[]{"Help", "Block", "Delay", "Avoid"});
        answers.add("Help");

        questions.add("Rapid");
        choices.add(new String[]{"Slow", "Quick", "Late", "Weak"});
        answers.add("Quick");

        questions.add("Ancient");
        choices.add(new String[]{"Modern", "Old", "New", "Fresh"});
        answers.add("Old");

        questions.add("Enormous");
        choices.add(new String[]{"Tiny", "Huge", "Thin", "Weak"});
        answers.add("Huge");

        questions.add("Cautious");
        choices.add(new String[]{"Careful", "Reckless", "Fast", "Lazy"});
        answers.add("Careful");

        questions.add("Reliable");
        choices.add(new String[]{"Trustworthy", "Dishonest", "Weak", "Late"});
        answers.add("Trustworthy");

        questions.add("Brief");
        choices.add(new String[]{"Short", "Long", "Wide", "Slow"});
        answers.add("Short");

        questions.add("Polite");
        choices.add(new String[]{"Rude", "Courteous", "Lazy", "Loud"});
        answers.add("Courteous");

        questions.add("Expand");
        choices.add(new String[]{"Shrink", "Grow", "Hide", "Reduce"});
        answers.add("Grow");

        questions.add("Weary");
        choices.add(new String[]{"Tired", "Strong", "Happy", "Fast"});
        answers.add("Tired");

        questions.add("Accurate");
        choices.add(new String[]{"Correct", "Wrong", "False", "Lazy"});
        answers.add("Correct");

        questions.add("Frequent");
        choices.add(new String[]{"Rare", "Often", "Never", "Late"});
        answers.add("Often");

        questions.add("Hostile");
        choices.add(new String[]{"Friendly", "Angry", "Calm", "Kind"});
        answers.add("Angry");

        questions.add("Visible");
        choices.add(new String[]{"Hidden", "Clear", "Dark", "Quiet"});
        answers.add("Clear");

        questions.add("Reluctant");
        choices.add(new String[]{"Unwilling", "Eager", "Ready", "Happy"});
        answers.add("Unwilling");

        questions.add("Flexible");
        choices.add(new String[]{"Rigid", "Bendable", "Hard", "Sharp"});
        answers.add("Bendable");

        questions.add("Absurd");
        choices.add(new String[]{"Ridiculous", "Logical", "Normal", "Clear"});
        answers.add("Ridiculous");

        questions.add("Scarcity");
        choices.add(new String[]{"Shortage", "Abundance", "Plenty", "Wealth"});
        answers.add("Shortage");

        questions.add("Efficient");
        choices.add(new String[]{"Effective", "Slow", "Wasteful", "Lazy"});
        answers.add("Effective");

        questions.add("Vanish");
        choices.add(new String[]{"Appear", "Disappear", "Grow", "Shine"});
        answers.add("Disappear");

        questions.add("Sturdy");
        choices.add(new String[]{"Strong", "Weak", "Soft", "Thin"});
        answers.add("Strong");

        questions.add("Narrow");
        choices.add(new String[]{"Wide", "Slim", "Large", "Big"});
        answers.add("Slim");

        questions.add("Tidy");
        choices.add(new String[]{"Neat", "Dirty", "Messy", "Wild"});
        answers.add("Neat");

        questions.add("Harsh");
        choices.add(new String[]{"Severe", "Gentle", "Soft", "Kind"});
        answers.add("Severe");

        questions.add("Delight");
        choices.add(new String[]{"Please", "Annoy", "Hurt", "Anger"});
        answers.add("Please");

        questions.add("Lazy");
        choices.add(new String[]{"Idle", "Active", "Busy", "Fast"});
        answers.add("Idle");

        questions.add("Timid");
        choices.add(new String[]{"Shy", "Brave", "Bold", "Loud"});
        answers.add("Shy");

        questions.add("Grasp");
        choices.add(new String[]{"Hold", "Drop", "Throw", "Miss"});
        answers.add("Hold");

        questions.add("Consume");
        choices.add(new String[]{"Eat", "Save", "Store", "Hide"});
        answers.add("Eat");

        questions.add("Reduce");
        choices.add(new String[]{"Lower", "Increase", "Grow", "Expand"});
        answers.add("Lower");

        questions.add("Permit");
        choices.add(new String[]{"Allow", "Block", "Deny", "Stop"});
        answers.add("Allow");

        questions.add("Fragile");
        choices.add(new String[]{"Delicate", "Strong", "Hard", "Heavy"});
        answers.add("Delicate");

        questions.add("Eloquent");
        choices.add(new String[]{"Well-spoken", "Silent", "Rude", "Clumsy"});
        answers.add("Well-spoken");

        questions.add("Obsolete");
        choices.add(new String[]{"Outdated", "Modern", "New", "Useful"});
        answers.add("Outdated");

        questions.add("Meticulous");
        choices.add(new String[]{"Careful", "Lazy", "Careless", "Fast"});
        answers.add("Careful");

        questions.add("Ambiguous");
        choices.add(new String[]{"Unclear", "Clear", "Exact", "Certain"});
        answers.add("Unclear");

        questions.add("Resilient");
        choices.add(new String[]{"Tough", "Weak", "Fragile", "Soft"});
        answers.add("Tough");

        questions.add("Plausible");
        choices.add(new String[]{"Believable", "Impossible", "False", "Fake"});
        answers.add("Believable");

        questions.add("Relentless");
        choices.add(new String[]{"Persistent", "Lazy", "Gentle", "Weak"});
        answers.add("Persistent");

        questions.add("Conceal");
        choices.add(new String[]{"Hide", "Show", "Reveal", "Expose"});
        answers.add("Hide");

        questions.add("Inevitable");
        choices.add(new String[]{"Unavoidable", "Optional", "Preventable", "Rare"});
        answers.add("Unavoidable");

        questions.add("Prudent");
        choices.add(new String[]{"Wise", "Reckless", "Foolish", "Careless"});
        answers.add("Wise");

        questions.add("Tedious");
        choices.add(new String[]{"Boring", "Fun", "Exciting", "Fast"});
        answers.add("Boring");

        questions.add("Validate");
        choices.add(new String[]{"Confirm", "Deny", "Reject", "Ignore"});
        answers.add("Confirm");

        questions.add("Superficial");
        choices.add(new String[]{"Shallow", "Deep", "Meaningful", "Serious"});
        answers.add("Shallow");

        questions.add("Hinder");
        choices.add(new String[]{"Obstruct", "Help", "Assist", "Support"});
        answers.add("Obstruct");

        questions.add("Impartial");
        choices.add(new String[]{"Fair", "Biased", "Unjust", "Mean"});
        answers.add("Fair");

        questions.add("Elusive");
        choices.add(new String[]{"Hard to catch", "Easy", "Visible", "Clear"});
        answers.add("Hard to catch");

        questions.add("Profound");
        choices.add(new String[]{"Deep", "Shallow", "Simple", "Weak"});
        answers.add("Deep");

        questions.add("Scrutinize");
        choices.add(new String[]{"Examine closely", "Ignore", "Glance", "Skip"});
        answers.add("Examine closely");

        questions.add("Deteriorate");
        choices.add(new String[]{"Worsen", "Improve", "Fix", "Repair"});
        answers.add("Worsen");

        questions.add("Candid");
        choices.add(new String[]{"Honest", "Secretive", "False", "Deceptive"});
        answers.add("Honest");

        questions.add("Consequence");
        choices.add(new String[]{"Result", "Cause", "Reason", "Choice"});
        answers.add("Result");

        questions.add("Obscure");
        choices.add(new String[]{"Hidden", "Clear", "Bright", "Visible"});
        answers.add("Hidden");

        questions.add("Reconcile");
        choices.add(new String[]{"Settle", "Fight", "Argue", "Reject"});
        answers.add("Settle");

        questions.add("Inquisitive");
        choices.add(new String[]{"Curious", "Indifferent", "Lazy", "Silent"});
        answers.add("Curious");

        questions.add("Exemplary");
        choices.add(new String[]{"Outstanding", "Poor", "Bad", "Weak"});
        answers.add("Outstanding");

    }
}




