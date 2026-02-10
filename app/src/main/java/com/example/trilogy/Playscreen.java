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

public class Playscreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_playscreen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ImageButton englishbtn= findViewById(R.id.englishbtn);
        ImageButton mathbtn= findViewById(R.id.mathbtn);
        ImageButton sciencebtn= findViewById(R.id.sciencebtn);

        addPressPlayScreenAnimation(englishbtn);
        addPressPlayScreenAnimation(mathbtn);
        addPressPlayScreenAnimation(sciencebtn);

        englishbtn.setOnClickListener(v->{
            Intent engpress = new Intent(Playscreen.this, Engchoosegame.class);
            startActivity(engpress);

        });
        mathbtn.setOnClickListener(v->{
            Intent mathpress = new Intent(Playscreen.this, Mathchoosegame.class);
            startActivity(mathpress);

        });
        sciencebtn.setOnClickListener(v->{
            Intent sciencepress = new Intent(Playscreen.this,Scichoosegame.class);
            startActivity(sciencepress);

        });
    }
    //Press Animation for English,MAth, and Science button
    private void addPressPlayScreenAnimation(ImageButton button) {
        button.setOnTouchListener((v, event) -> {
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
            return false; // keeps onClick working
        });
    }
}