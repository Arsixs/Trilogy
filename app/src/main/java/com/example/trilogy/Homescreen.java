package com.example.trilogy;

import android.app.AlertDialog;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.view.animation.OvershootInterpolator;
import android.view.MotionEvent;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Homescreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_homescreen);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton playbtn = findViewById(R.id.playbtn);
        ImageButton optionbtn = findViewById(R.id.optionbtn);
        ImageButton exitbtn = findViewById(R.id.exitbtn);

        addPressAnimation(playbtn);
        addPressAnimation(optionbtn);
        addPressAnimation(exitbtn);


        playbtn.setOnClickListener(view -> {
            Intent play = new Intent(Homescreen.this, Playscreen.class);
            startActivity(play);
            finish();
        });
        //Option button listener
        optionbtn.setOnClickListener(view -> {
                showPopup();

        });

    }
    //Option button pop up
    private void showPopup() {
        // Inflate the popup_layout.xml
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.option_screen, null);

        // Create the PopupWindow
        int width = 600;  // width in pixels
        int height = 600; // height in pixels
        boolean focusable = true; // lets taps outside the popup dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // Show the popup at the center
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        // Optional: dismiss when clicking the image
        popupView.setOnClickListener(v -> popupWindow.dismiss());
    }
    //Animation button for Play,Option, Exit Btn
    private void addPressAnimation(ImageButton button) {
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
