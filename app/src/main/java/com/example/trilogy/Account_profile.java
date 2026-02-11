package com.example.trilogy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

public class Account_profile extends Fragment {

    private ImageView profileImage;
    private TextView usernameTop, usernameAcc, passAcc, calendarAcc, favSubAcc;
    private Button loginhere;
    private String username;

    public Account_profile() {
        // Required empty public constructor
    }

    public static Account_profile newInstance(String username) {
        Account_profile fragment = new Account_profile();
        Bundle args = new Bundle();
        args.putString("username", username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString("username");
        }
        SharedPreferences prefs = requireActivity().getSharedPreferences("UserSession", getContext().MODE_PRIVATE);

        String savedUser = prefs.getString("username", null);
        if (savedUser != null) {
            username = savedUser;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account_profile, container, false);

        // Initialize Views
        usernameTop = view.findViewById(R.id.Userprofile);
        usernameAcc = view.findViewById(R.id.useracc);
        passAcc = view.findViewById(R.id.passacc);
        calendarAcc = view.findViewById(R.id.calendaracc);
        favSubAcc = view.findViewById(R.id.favsub_acc);
        loginhere = view.findViewById(R.id.Loginhere);
        profileImage = view.findViewById(R.id.Profilepic);

        loginhere.setOnClickListener(v -> {
            Intent log = new Intent(requireActivity(), Loginscreen.class);
            startActivityForResult(log, 1);
        });

        // Load profile if already logged in
        loadUserData();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == requireActivity().RESULT_OK) {
            username = data.getStringExtra("username");
            loadUserData();
        }
    }

    private void loadUserData() {

        if (username == null || username.isEmpty()) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(username).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {

                String birthday = documentSnapshot.getString("birthday");
                String favoriteBook = documentSnapshot.getString("favoriteBook");
                String profileBase64 = documentSnapshot.getString("profileImage");

                usernameTop.setText(username);
                usernameAcc.setText(username);
                passAcc.setText("••••••••");
                calendarAcc.setText(birthday);
                favSubAcc.setText(favoriteBook);

                if (profileBase64 != null && !profileBase64.isEmpty()) {
                    Bitmap bitmap = base64ToBitmap(profileBase64);
                    if (bitmap != null) {
                        profileImage.setImageBitmap(bitmap);
                    }
                }

                // Hide login button after successful login
                loginhere.setVisibility(View.GONE);

            } else {
                Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show());
    }

    private Bitmap base64ToBitmap(String base64String) {
        try {
            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }
}