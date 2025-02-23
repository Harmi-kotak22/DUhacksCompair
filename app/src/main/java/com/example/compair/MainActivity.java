package com.example.compair;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {
    private EditText editTextEmail, editTextPassword, editTextUsername;
    private Button buttonSignUp, buttonGoogleSignUp;
    private TextView textViewLogin;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private FirebaseFirestore firestore;
    private GoogleSignInClient googleSignInClient;

    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 100;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        firestore = FirebaseFirestore.getInstance();  // Initialize Firestore

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.signup_background));

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextUsername = findViewById(R.id.editTextUsername);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        buttonGoogleSignUp = findViewById(R.id.buttonGoogleSignUp);
        textViewLogin = findViewById(R.id.textViewLogin);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        buttonSignUp.setOnClickListener(v -> registerUser());
        buttonGoogleSignUp.setOnClickListener(v -> signUpWithGoogle());
        textViewLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, login.class);
            startActivity(intent);
        });
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String username = editTextUsername.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
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

        checkUsernameUnique(username, isUnique -> {
            if (isUnique) {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    saveUserToDatabase(user.getUid(), username, email);
                                }
                            } else {
                                Log.e(TAG, "Authentication failed: ", task.getException());
                                Toast.makeText(MainActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(MainActivity.this, "Username already taken. Choose a different one.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signUpWithGoogle() {
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            try {
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                if (account != null) {
                    authenticateWithGoogle(account);
                }
            } catch (ApiException e) {
                Log.e(TAG, "Google Sign-In failed: " + e.getMessage());
                Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void authenticateWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String email = account.getEmail();
                            String userId = user.getUid();
                            checkAndSaveGoogleUser(userId, account.getDisplayName(), email);
                        }
                    } else {
                        Log.e(TAG, "Google authentication failed: ", task.getException());
                        Toast.makeText(MainActivity.this, "Google Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkAndSaveGoogleUser(String userId, String googleName, String email) {
        AtomicReference<String> usernameRef = new AtomicReference<>(googleName.replaceAll("\\s", "").toLowerCase());

        checkUsernameUnique(usernameRef.get(), isUnique -> {
            if (!isUnique) {
                usernameRef.updateAndGet(v -> v + new Random().nextInt(1000)); // Append random number to make it unique
            }
            saveUserToDatabase(userId, usernameRef.get(), email);
        });
    }

    private void checkUsernameUnique(String username, final UsernameCheckCallback callback) {
        databaseReference.orderByChild("username").equalTo(username)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        callback.onCheck(false);
                    } else {
                        callback.onCheck(true);
                    }
                });
    }

    private void saveUserToDatabase(String userId, String username, String email) {
        HashMap<String, Object> userData = new HashMap<>();
        userData.put("userId", userId);
        userData.put("username", username);
        userData.put("email", email);

        // Save to Realtime Database
        databaseReference.child(userId).setValue(userData);

        // Save to Firestore
        firestore.collection("users").document(userId).set(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(MainActivity.this, "User registered successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, login.class));
                    finish();
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to save user data in Firestore: ", e));
    }

    interface UsernameCheckCallback {
        void onCheck(boolean isUnique);
    }
}
