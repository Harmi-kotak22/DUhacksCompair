package com.example.compair;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private TextView userNameTextView, userEmailTextView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        userNameTextView = view.findViewById(R.id.userNameTextView);
        userEmailTextView = view.findViewById(R.id.userEmailTextView);

        loadUserProfile();

        return view;
    }

    private void loadUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            userEmailTextView.setText(user.getEmail());

            Log.d(TAG, "Fetching user data for ID: " + userId);

            DocumentReference userRef = db.collection("users").document(userId);
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                String name = null;
                if (documentSnapshot.exists()) {
                    name = documentSnapshot.getString("name");
                    Log.d(TAG, "User Name from Firestore: " + name);
                }

                // If Firestore doesn't have a name, fallback to FirebaseAuth display name
                if (name == null || name.trim().isEmpty()) {
                    name = user.getDisplayName();
                    Log.d(TAG, "Fallback to FirebaseAuth name: " + name);
                }

                // Set the name or fallback to email prefix as a last resort
                if (name == null || name.trim().isEmpty()) {
                    name = user.getEmail().split("@")[0];
                }
                userNameTextView.setText(name);
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Failed to load profile!", e);
                Toast.makeText(getActivity(), "Failed to load profile!", Toast.LENGTH_SHORT).show();
            });
        } else {
            Log.e(TAG, "User not signed in!");
            Toast.makeText(getActivity(), "User not signed in!", Toast.LENGTH_SHORT).show();
        }
    }
}
