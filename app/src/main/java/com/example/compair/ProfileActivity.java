package com.example.compair;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView profileImage;
    private EditText etName, etEmail, etPhone, etAddress;
    private Button btnChangePhoto, btnEdit, btnSave, btnLogout;
    private static final int PICK_IMAGE_REQUEST = 1;
    private SharedPreferences sharedPreferences;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Toolbar
        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        } else {
            Toast.makeText(this, "Toolbar not found!", Toast.LENGTH_SHORT).show();
        }

        // Initialize Views
        profileImage = findViewById(R.id.profile_image);
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etAddress = findViewById(R.id.et_address);
        btnChangePhoto = findViewById(R.id.btn_change_photo);
        btnEdit = findViewById(R.id.btn_edit);
        btnSave = findViewById(R.id.btn_save);
        btnLogout = findViewById(R.id.btn_logout);

        sharedPreferences = getSharedPreferences("UserProfile", Context.MODE_PRIVATE);

        // Load User Data
        loadUserData();

        // Change Profile Picture
        btnChangePhoto.setOnClickListener(v -> openGallery());

        // Edit Profile
        btnEdit.setOnClickListener(v -> enableEditing(true));

        // Save Changes
        btnSave.setOnClickListener(v -> {
            saveProfile();
            enableEditing(false);
            Toast.makeText(ProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
        });

        // Logout
        btnLogout.setOnClickListener(v -> {
            Toast.makeText(ProfileActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProfileActivity.this, login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            if (imageUri != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    profileImage.setImageBitmap(bitmap);
                    saveProfileImage(imageUri.toString());
                } catch (IOException e) {
                    Toast.makeText(this, "Failed to load image!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadUserData() {
        String name = sharedPreferences.getString("name", "John Doe");
        String email = sharedPreferences.getString("email", "johndoe@example.com");
        String phone = sharedPreferences.getString("phone", "+1 234 567 890");
        String address = sharedPreferences.getString("address", "123 Main St, New York, NY");
        String imageUriString = sharedPreferences.getString("profile_image", "");

        etName.setText(name);
        etEmail.setText(email);
        etPhone.setText(phone);
        etAddress.setText(address);

        if (!imageUriString.isEmpty()) {
            imageUri = Uri.parse(imageUriString);
            profileImage.setImageURI(imageUri);
        }
    }

    private void enableEditing(boolean enable) {
        etName.setEnabled(enable);
        etEmail.setEnabled(enable);
        etPhone.setEnabled(enable);
        etAddress.setEnabled(enable);
        btnSave.setVisibility(enable ? View.VISIBLE : View.GONE);
        btnEdit.setVisibility(enable ? View.GONE : View.VISIBLE);
    }

    private void saveProfile() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", etName.getText().toString().trim());
        editor.putString("email", etEmail.getText().toString().trim());
        editor.putString("phone", etPhone.getText().toString().trim());
        editor.putString("address", etAddress.getText().toString().trim());
        if (imageUri != null) {
            editor.putString("profile_image", imageUri.toString());
        }
        editor.apply();
    }

    private void saveProfileImage(String uriString) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("profile_image", uriString);
        editor.apply();
    }
}
