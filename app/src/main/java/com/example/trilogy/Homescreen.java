package com.example.trilogy;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.PopupWindow;

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

        Button playbtn = findViewById(R.id.playbtn);
        Button optionbtn = findViewById(R.id.optionbtn);


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
}
