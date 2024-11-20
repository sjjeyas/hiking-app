package com.example.project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private String currentUserID;
    private String viewingUserID; // User whose profile is being viewed
    private String viewingUsername; // Username of the user being viewed
    private TextView zipView, userView, nameView;
    private Button dynamicButton, myListsButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        FirebaseApp.initializeApp(this);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        // Determine which profile to display
        viewingUserID = getIntent().getStringExtra("user");
        if (viewingUserID == null) {
            viewingUserID = currentUserID; // Default to logged-in user
        }

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Initialize views
        userView = findViewById(R.id.usernamefield);
        zipView = findViewById(R.id.locationfield);
        nameView = findViewById(R.id.namefield);
        dynamicButton = findViewById(R.id.dynamic_button); // Button for "See Friends" or "Add Friend"
        myListsButton = findViewById(R.id.lists_button); // Button for lists

        // Load profile data
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        loadUserProfile();

        // Configure buttons
        configureListsButton();
    }

    private void loadUserProfile() {
        mDatabase.child(viewingUserID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("ProfileActivity", "Error getting data", task.getException());
                } else {
                    Map<String, Object> results = (Map<String, Object>) task.getResult().getValue();
                    if (results != null) {
                        Log.d("ProfileActivity", "Loaded profile: " + results);
                        userView.setText(String.valueOf(results.get("username")));
                        nameView.setText(String.valueOf(results.get("name")));
                        zipView.setText(String.valueOf(results.get("zipcode")));
                        viewingUsername = String.valueOf(results.get("name"));

                        // Configure dynamic button after retrieving the username
                        configureDynamicButton();
                    } else {
                        Log.d("ProfileActivity", "No user found");
                    }
                }
            }
        });
    }

    private void configureDynamicButton() {
        if (viewingUserID.equals(currentUserID)) {
            dynamicButton.setText("See Friends");
            dynamicButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, FriendsActivity.class);
                intent.putExtra("user", viewingUserID);
                startActivity(intent);
            });
        } else {
            checkFriendStatus(isFriend -> {
                if (isFriend) {
                    dynamicButton.setText("Unfriend");
                    dynamicButton.setOnClickListener(v -> unfriendUser());
                } else {
                    dynamicButton.setText("Add Friend");
                    dynamicButton.setOnClickListener(v -> addFriend());
                }
            });
        }
    }

    private void configureListsButton() {
        if (viewingUserID.equals(currentUserID)) {
            myListsButton.setText("My Lists");
        } else {
            myListsButton.setText("View Public Lists");
        }
        myListsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, myListsActivity.class);
            intent.putExtra("user", viewingUserID);
            startActivity(intent);
        });
    }

    private void addFriend() {
        if (viewingUsername == null) {
            Toast.makeText(ProfileActivity.this, "Unable to add friend. Try again later.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add friend using username as key
        mDatabase.child(currentUserID).child("friends").child(viewingUsername).setValue(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this, "Friend added!", Toast.LENGTH_SHORT).show();
                        configureDynamicButton(); // Refresh button state
                    } else {
                        Toast.makeText(ProfileActivity.this, "Failed to add friend.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void unfriendUser() {
        if (viewingUsername == null) {
            Toast.makeText(ProfileActivity.this, "Unable to remove friend. Try again later.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Remove friend using username as key
        mDatabase.child(currentUserID).child("friends").child(viewingUsername).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this, "Friend removed!", Toast.LENGTH_SHORT).show();
                        configureDynamicButton(); // Refresh button state
                    } else {
                        Toast.makeText(ProfileActivity.this, "Failed to remove friend.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkFriendStatus(FriendStatusCallback callback) {
        if (viewingUsername == null) {
            callback.onStatusChecked(false);
            return;
        }

        mDatabase.child(currentUserID).child("friends").child(viewingUsername).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        callback.onStatusChecked(true);
                    } else {
                        callback.onStatusChecked(false);
                    }
                });
    }

    interface FriendStatusCallback {
        void onStatusChecked(boolean isFriend);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.d("ProfileActivity", "Menu item clicked with ID: " + id); // Log menu item clicks

        if (id == R.id.action_logout) {
            Log.d("ProfileActivity", "Logout clicked");
            mAuth.signOut();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            //send login success toast message
            Toast.makeText(this, "Logout Successful.", Toast.LENGTH_SHORT).show();

            return true;
        } else if (id == R.id.action_login) {
            Log.d("ProfileActivity", "Login button clicked");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_home) {
            Log.d("ProfileActivity", "Home button clicked");
            Intent intent = new Intent(this, MainActivity.class);

            intent.putExtra("user", currentUserID);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_profile) {
            Log.d("MainActivity", "Profile button clicked");
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("user", currentUserID);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_trailsearch) {
            Log.d("ProfileActivity", "Search button clicked");
            Intent intent = new Intent(this, SearchActivity.class);

            intent.putExtra("user", currentUserID);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_friends) {
            Log.d("ProfileActivity", "Friends button clicked");
            Intent intent = new Intent(this, FriendsActivity.class);

            intent.putExtra("user", currentUserID);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_groupsearch) {
            Log.d("ProfileActivity", "Groups button clicked");
            Intent intent = new Intent(this, GroupActivity.class);
            intent.putExtra("user", currentUserID);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
