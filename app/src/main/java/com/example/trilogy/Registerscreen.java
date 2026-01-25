package com.example.trilogy;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class Registerscreen extends AppCompatActivity {

    EditText user2, pass2;
    Button button2;
    ImageButton imageButton2;
    DatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registerscreen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user2 = findViewById(R.id.user2);
        pass2 = findViewById(R.id.pass2);
        imageButton2 = findViewById(R.id.imageButton2);
        button2 = findViewById(R.id.button2);

        addRegisterScreenAnimationPressAnimation(imageButton2);


        dbHelper = new DatabaseHelper(this);

        imageButton2.setOnClickListener(v -> {

            String username = user2.getText().toString().trim();
            String password = pass2.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = dbHelper.registerUser(username, password);

            if (success) {
                Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show();
                finish(); // back to login
            } else {
                Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
            }
        });

        button2.setOnClickListener(v -> finish());
    }
    private void addRegisterScreenAnimationPressAnimation(ImageButton button) {
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