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

public class MainActivity extends AppCompatActivity {
    private EditText editTextEmail, editTextPassword;
    private Button buttonSignUp;
    private TextView textViewLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        textViewLogin = findViewById(R.id.textViewLogin);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(MainActivity.this, "Invalid email format", Toast.LENGTH_SHORT).show();
                } else if (password.length() < 8) {
                    Toast.makeText(MainActivity.this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Sign-up successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, home.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        textViewLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, login.class);
            startActivity(intent);
        });
    }
}
