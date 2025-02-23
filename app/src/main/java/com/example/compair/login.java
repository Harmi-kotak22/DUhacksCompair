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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class login extends AppCompatActivity {
    private EditText editTextEmailOrUsername, editTextPassword;
    private Button buttonLogin, buttonGoogleSignIn;
    private TextView textViewSignUp;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private FirebaseFirestore firestore;
    private GoogleSignInClient googleSignInClient;
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 100;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        firestore = FirebaseFirestore.getInstance();

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.signup_background));

        editTextEmailOrUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonGoogleSignIn = findViewById(R.id.buttonGoogleLogin);
        textViewSignUp = findViewById(R.id.textViewSignUp);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        buttonLogin.setOnClickListener(v -> loginUser());
        buttonGoogleSignIn.setOnClickListener(v -> signInWithGoogle());
        textViewSignUp.setOnClickListener(v -> {
            startActivity(new Intent(login.this, MainActivity.class));
            finish();
        });
    }

    private void loginUser() {
        String emailOrUsername = editTextEmailOrUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (emailOrUsername.isEmpty() || password.isEmpty()) {
            Toast.makeText(login.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Patterns.EMAIL_ADDRESS.matcher(emailOrUsername).matches()) {
            signInWithEmail(emailOrUsername, password);
        } else {
            usersRef.orderByChild("username").equalTo(emailOrUsername).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult().exists()) {
                            for (DataSnapshot snapshot : task.getResult().getChildren()) {
                                String email = snapshot.child("email").getValue(String.class);
                                signInWithEmail(email, password);
                                return;
                            }
                        } else {
                            Toast.makeText(login.this, "Username not found", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void signInWithEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(login.this, home.class));
                        finish();
                    } else {
                        Toast.makeText(login.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signInWithGoogle() {
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
                            saveUserToDatabase(user, account.getDisplayName());
                        }
                    } else {
                        Toast.makeText(login.this, "Google Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToDatabase(FirebaseUser user, String googleName) {
        String userId = user.getUid();
        String email = user.getEmail();
        String username = googleName != null ? googleName.replaceAll("\\s", "").toLowerCase() : userId;

        usersRef.child(userId).get().addOnCompleteListener(task -> {
            if (!task.getResult().exists()) {
                User newUser = new User(userId, email, username);
                usersRef.child(userId).setValue(newUser);
                firestore.collection("users").document(userId).set(newUser)
                        .addOnCompleteListener(saveTask -> {
                            startActivity(new Intent(login.this, home.class));
                            finish();
                        });
            } else {
                startActivity(new Intent(login.this, home.class));
                finish();
            }
        });
    }

    public static class User {
        public String userId, email, username;

        public User() {}

        public User(String userId, String email, String username) {
            this.userId = userId;
            this.email = email;
            this.username = username;
        }
    }
}
