package com.example.trilogy;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Dictionary extends AppCompatActivity {
    private static final String PREF_NAME = "wordle_stats";
    private static final String KEY_GUESSED_WORDS = "guessed_words";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dictionary);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView wordListText = findViewById(R.id.wordListText);

        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String words = prefs.getString(KEY_GUESSED_WORDS, "");

        if (words.isEmpty()) {
            wordListText.setText("No words guessed yet.");
        } else {
            String formatted = words.replace(",", "\n");
            wordListText.setText(formatted);
        }
    }
}