package com.example.trilogy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class Playscreen extends AppCompatActivity {
    private String username, navigateTo;
    MediaPlayer mediaPlayer;
    DrawerLayout drawerlayout;
    NavigationView nv_side;
    ActionBarDrawerToggle toggle;


    float currentVolume = 0.8f; // default volume

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playscreen);
        username = getIntent().getStringExtra("username");
        navigateTo = getIntent().getStringExtra("navigateTo");


//        username = getIntent().getStringExtra("username");
//
//        if (username == null) {
//            Toast.makeText(this, "Username not received!", Toast.LENGTH_LONG).show();
//        }

        drawerlayout = findViewById(R.id.main);
        nv_side = findViewById(R.id.nv_side);


        mediaPlayer = MediaPlayer.create(this, R.raw.background);
        mediaPlayer.setVolume(currentVolume, currentVolume);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        View headerView = nv_side.getHeaderView(0);
        ImageButton headerAvatar = headerView.findViewById(R.id.header_avatar);

        if ("profile".equals(navigateTo)) {

            Account_profile profileFragment = Account_profile.newInstance(username);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, profileFragment)
                    .commit();

            // Optional: highlight nav item
            nv_side.setCheckedItem(R.id.nv_side);
        }


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
        //profile to Account logic
        headerAvatar.setOnClickListener(v -> {
            Account_profile fragment = new Account_profile();

            Bundle bundle = new Bundle();
            bundle.putString("username", username);
            fragment.setArguments(bundle);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();

            drawerlayout.closeDrawers();
        });
        //Sidebar Clickable
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

                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setView(getLayoutInflater().inflate(R.layout.dialog_settings, null))
                        .create();

                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();

                View view = dialog.findViewById(android.R.id.content);

                SeekBar musicSeek = view.findViewById(R.id.seek_music);
                ImageButton closeBtn = view.findViewById(R.id.btn_close);

                // Use CURRENT volume, not reset
                musicSeek.setProgress((int) (currentVolume * 100));

                musicSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        currentVolume = progress / 100f;
                        mediaPlayer.setVolume(currentVolume, currentVolume);
                    }

                    @Override public void onStartTrackingTouch(SeekBar seekBar) {}
                    @Override public void onStopTrackingTouch(SeekBar seekBar) {}
                });

                closeBtn.setOnClickListener(v -> dialog.dismiss());
            }

            if (item.getItemId() == R.id.nav_logout) {
                finishAffinity();
            }

            drawerlayout.closeDrawers();
            return true;
        });
    }
//public void playaudio(View view){
//        mediaPlayer.start();
//}
//public void stopaudio(View view){
//        mediaPlayer.pause();
//}

//User exit to home music will stop
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//            mediaPlayer.stop();
//            mediaPlayer.release();
//            mediaPlayer = null;
//
//        }
//    }
//User return then music play
    @Override
    protected void onStart() {
        super.onStart();
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.background); // re‑initialize
            mediaPlayer.start();
        }
    }


    // User really quit the app, music will stop
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}