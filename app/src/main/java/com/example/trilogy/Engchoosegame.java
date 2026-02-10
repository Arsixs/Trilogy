package com.example.trilogy;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class Engchoosegame extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_engchoosegame);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ImageButton Wordbtn = findViewById(R.id.wordbtn);
        ImageButton Emojibtn = findViewById(R.id.emojibtn);
        ImageButton Worldbtn = findViewById(R.id.worldbtn);

        addEngChooseGamePressAnimation(Wordbtn);
        addEngChooseGamePressAnimation(Emojibtn);
        addEngChooseGamePressAnimation(Worldbtn);


        Wordbtn.setOnClickListener(v -> {
            Intent intent = new Intent(Engchoosegame.this, WordMeaningQuiz.class);
            startActivity(intent);
        });

        Emojibtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
        });

        Worldbtn.setOnClickListener(v -> {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.engfragment, new Difficultychooser())
                    .addToBackStack(null)
                    .commit();
        });
        

    }

    //EnglishChoosebtn Presser
    private void addEngChooseGamePressAnimation(ImageButton button) {
        button.setOnTouchListener((v, event) -> {

            v.setPivotX(v.getWidth() / 2f);
            v.setPivotY(v.getHeight() / 2f);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate()
                            .scaleX(0.92f)
                            .scaleY(0.92f)
                            .setDuration(80)
                            .start();
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(150)
                            .setInterpolator(new OvershootInterpolator())
                            .start();
                    break;
            }
            return false;
        });
    }
    }