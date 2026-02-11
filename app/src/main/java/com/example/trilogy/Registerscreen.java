package com.example.trilogy;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.animation.OvershootInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Registerscreen extends AppCompatActivity {

    EditText username, password, confirmpass, calender_fill;
    Spinner favebook_fill;
    Button Loginhere;
    ImageButton imageButton2;
    ImageView registerProfilePic;

    private static final int GALLERY_REQUEST = 100;
    private static final int CAMERA_REQUEST = 200;

    private Uri imageUri;
    private Bitmap cameraBitmap;

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

        // Initialize UI
        username = findViewById(R.id.user2);
        password = findViewById(R.id.pass2);
        confirmpass = findViewById(R.id.confirmpass);
        calender_fill = findViewById(R.id.Calendar_fill);
        favebook_fill = findViewById(R.id.favbook_spinner);
        imageButton2 = findViewById(R.id.imageButton2);
        Loginhere = findViewById(R.id.Loginhere);
        registerProfilePic = findViewById(R.id.profilepic);

        registerProfilePic.setOnClickListener(v -> showImagePickerDialog());

        // Date Picker
        calender_fill.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(
                    this,
                    (view, year, month, day) ->
                            calender_fill.setText(day + "/" + (month + 1) + "/" + year),
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            dialog.show();
        });

        // Spinner
        String[] subjects = {"English", "Math", "Science"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                subjects
        );
        favebook_fill.setAdapter(adapter);

        addRegisterScreenAnimationPressAnimation(imageButton2);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Register button click
        imageButton2.setOnClickListener(v -> {
            String user = username.getText().toString().trim();
            String pass = password.getText().toString().trim();
            String birthday = calender_fill.getText().toString().trim();
            String favoriteBook = favebook_fill.getSelectedItem().toString();
            String confirmPass = confirmpass.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty() || birthday.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pass.equals(confirmPass)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            String hashedPassword = hashPassword(pass);

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("username", user);
            userMap.put("password", hashedPassword);
            userMap.put("birthday", birthday);
            userMap.put("favoriteBook", favoriteBook);

            // Handle profile picture
            if (imageUri != null || cameraBitmap != null) {
                Bitmap bitmap = cameraBitmap;
                if (bitmap == null && imageUri != null) {
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                    }
                }
                if (bitmap != null) {
                    userMap.put("profileImage", bitmapToBase64(bitmap));
                }
            } else {
                userMap.put("profileImage", ""); // default empty
            }

            // Save to Firestore
            db.collection("users")
                    .document(user)
                    .set(userMap)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                    );
        });

        Loginhere.setOnClickListener(view -> finish());
    }

    // Convert Bitmap to Base64
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] bytes = baos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    // Image picker dialog
    private void showImagePickerDialog() {
        String[] options = {"Camera", "Gallery"};

        new AlertDialog.Builder(this)
                .setTitle("Pick Image From")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    } else {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                        galleryIntent.setType("image/*");
                        startActivityForResult(galleryIntent, GALLERY_REQUEST);
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {

            if (requestCode == GALLERY_REQUEST) {
                imageUri = data.getData();
                cameraBitmap = null;
                registerProfilePic.setImageURI(imageUri);

            } else if (requestCode == CAMERA_REQUEST) {
                cameraBitmap = (Bitmap) data.getExtras().get("data");
                imageUri = null;
                registerProfilePic.setImageBitmap(cameraBitmap);
            }
        }
    }

    private void addRegisterScreenAnimationPressAnimation(ImageButton button) {
        button.setOnTouchListener((v, event) -> {

            v.setPivotX(v.getWidth() / 2f);
            v.setPivotY(v.getHeight() / 2f);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.92f).scaleY(0.92f).setDuration(80).start();
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f)
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
