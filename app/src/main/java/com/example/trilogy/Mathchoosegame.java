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

public class Mathchoosegame extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mathchoosegame);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ImageButton bdbtn = findViewById(R.id.bdbtn);
        ImageButton nsbtn = findViewById(R.id.smbtn);
        ImageButton smbtn = findViewById(R.id.nsbtn);
        //Animation
        addMathChooserGameAnimationPressAnimation(bdbtn);
        addMathChooserGameAnimationPressAnimation(nsbtn);
        addMathChooserGameAnimationPressAnimation(smbtn);

        bdbtn.setOnClickListener(v->{
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mathfragment, new Difficultychooser())
                    .addToBackStack(null)
                    .commit();

        });
        nsbtn.setOnClickListener(v-> {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mathfragment, new Difficultychooser())
                    .addToBackStack(null)
                    .commit();
        });
        smbtn.setOnClickListener(v-> {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mathfragment, new Difficultychooser())
                    .addToBackStack(null)
                    .commit();
        });
    }
    private void addMathChooserGameAnimationPressAnimation(ImageButton button) {
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
