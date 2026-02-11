package com.example.trilogy;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class Account_profile extends Fragment {

    private ImageView profileImage;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account_profile, container, false);

        // TextViews
        TextView usernameTop = view.findViewById(R.id.Userprofile);
        TextView usernameAcc = view.findViewById(R.id.useracc);
        TextView passAcc = view.findViewById(R.id.passacc);
        TextView calendarAcc = view.findViewById(R.id.calendaracc);
        TextView favSubAcc = view.findViewById(R.id.favsub_acc);
        profileImage = view.findViewById(R.id.Profilepic);

        if (username != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users")
                    .document(username)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String birthday = documentSnapshot.getString("birthday");
                            String favoriteBook = documentSnapshot.getString("favoriteBook");
                            String profileBase64 = documentSnapshot.getString("profileImage"); // <-- Base64 from Register

                            usernameTop.setText(username);
                            usernameAcc.setText(username);
                            passAcc.setText("••••••••");
                            calendarAcc.setText(birthday);
                            favSubAcc.setText(favoriteBook);

                            // Decode Base64 image if exists
                            if (profileBase64 != null && !profileBase64.isEmpty()) {
                                Bitmap bitmap = base64ToBitmap(profileBase64);
                                if (bitmap != null) {
                                    profileImage.setImageBitmap(bitmap);
                                }
                            }
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show());
        }

        return view;
    }

    // ==========================
    // BASE64 DECODING
    // ==========================
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
