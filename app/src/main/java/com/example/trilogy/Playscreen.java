package com.example.trilogy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class Playscreen extends AppCompatActivity {
    MediaPlayer mediaPlayer;
    DrawerLayout drawerlayout;
    NavigationView nv_side;
    ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playscreen);

        drawerlayout = findViewById(R.id.main);
        nv_side = findViewById(R.id.nv_side);
        mediaPlayer = MediaPlayer.create(this, R.raw.background);
        mediaPlayer.start();
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        toggle = new ActionBarDrawerToggle(
                this,
                drawerlayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerlayout.addDrawerListener(toggle);
        toggle.syncState();

        // Load fragment ONCE
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new PlayscreenFrag())
                    .commit();
        }

        nv_side.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, PlayscreenFrag.class, null)
                        .commit();
            }
            else if (item.getItemId() == R.id.nav_games) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, TrilogyGameFrag.class, null)
                        .commit();
            }

            else if (item.getItemId() == R.id.nav_settings) {
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
            }

            if (item.getItemId() == R.id.nav_logout) {
                finishAffinity();
            }

            drawerlayout.closeDrawers();
            return true;
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}