package com.example.compair;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private EditText editTextEmail, editTextPassword;
    private Button buttonSignUp;
    private TextView textViewLogin;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference; // Reference to Firebase Realtime Database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users"); // Root node "Users"

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        textViewLogin = findViewById(R.id.textViewLogin);
        progressBar = findViewById(R.id.progressBar);

        buttonSignUp.setOnClickListener(v -> registerUser());

        textViewLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, login.class);
            startActivity(intent);
        });
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(MainActivity.this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 8) {
            Toast.makeText(MainActivity.this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveUserToDatabase(user);
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToDatabase(FirebaseUser user) {
        String userId = user.getUid(); // Unique user ID
        String email = user.getEmail();

        HashMap<String, Object> userData = new HashMap<>();
        userData.put("userId", userId);
        userData.put("email", email);

        databaseReference.child(userId).setValue(userData)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "User registered successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, home.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to save user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
