package com.example.trilogy;

import android.content.Intent;
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

import com.example.trilogy.db.DatabaseHelper;
import com.google.firebase.firestore.FirebaseFirestore;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class Loginscreen extends AppCompatActivity {

    EditText uname1, pass1;
    Button button1;
    ImageButton imageButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loginscreen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        uname1 = findViewById(R.id.uname1);
        pass1 = findViewById(R.id.pass1);
        imageButton = findViewById(R.id.loginbtn);
        button1 = findViewById(R.id.button1);
        //Animation button
        addLoginScreenAnimationPressAnimation(imageButton);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        imageButton.setOnClickListener(v -> {

            String username = uname1.getText().toString().trim();
            String password = pass1.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String hashedPassword = hashPassword(password);

            db.collection("users")
                    .document(username)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        if (documentSnapshot.exists()) {
                            String storedPassword = documentSnapshot.getString("password");

                            if (hashedPassword.equals(storedPassword)) {
                                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Loginscreen.this, Playscreen.class);
                                intent.putExtra("username", username);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Login error", Toast.LENGTH_SHORT).show()
                    );
        });




        button1.setOnClickListener(v -> {
            startActivity(new Intent(Loginscreen.this, Registerscreen.class));
        });
    }
    private void addLoginScreenAnimationPressAnimation(ImageButton button) {
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
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (Exception e) {
            return null;
        }
    }

}
//
//        dbHelper = new DatabaseHelper(this);
//
//        imageButton.setOnClickListener(v -> {
//
//            String username = uname1.getText().toString().trim();
//            String password = pass1.getText().toString().trim();
//
//            if (dbHelper.checkUser(username, password)) {
//                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(Loginscreen.this, Homescreen.class));
//                finish();
//            } else {
//                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
//            }
//        });