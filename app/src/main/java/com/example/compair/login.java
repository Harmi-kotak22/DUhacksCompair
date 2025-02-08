package com.example.compair;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class login extends AppCompatActivity {
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private TextView textViewSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewSignUp = findViewById(R.id.textViewSignUp);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(login.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(login.this, "Invalid email format", Toast.LENGTH_SHORT).show();
                } else if (password.length() < 8) {
                    Toast.makeText(login.this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(login.this, "Login successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(login.this, home.class);
                    startActivity(intent);
                    finish();  // Close the login activity after redirecting to Home
                }
            }
        });

        textViewSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(login.this, MainActivity.class);
            startActivity(intent);
            finish();  // Ensure sign-up page opens fresh
        });
    }
}
