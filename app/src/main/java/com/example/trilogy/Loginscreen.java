package com.example.trilogy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Loginscreen extends AppCompatActivity {

    EditText uname1, pass1;
    Button button1;
    ImageButton imageButton;
    DatabaseHelper dbHelper;



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
        imageButton = findViewById(R.id.imageButton);
        button1 = findViewById(R.id.button1);

        dbHelper = new DatabaseHelper(this);

        imageButton.setOnClickListener(v -> {

            String username = uname1.getText().toString().trim();
            String password = pass1.getText().toString().trim();

            if (dbHelper.checkUser(username, password)) {
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Loginscreen.this, Homescreen.class));
                finish();
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        });

        button1.setOnClickListener(v -> {
            startActivity(new Intent(Loginscreen.this, Registerscreen.class));
        });
    }
}
