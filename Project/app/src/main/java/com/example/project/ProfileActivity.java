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

import com.example.project.classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
//    DatabaseReference mDatabase;
//    FirebaseAuth mAuth;

    private ProfileManager profileManager;


    //    User curr_user;
    String currentUserID;
    String viewingUserID; // User whose profile is being viewed
    private String viewingUsername; // Username of the user being viewed
    private TextView zipView, userView, nameView;
    private Button dynamicButton, myListsButton;

//
//    public ProfileActivity() {
//        // Required empty public constructor
//    }
//
//    //for JUnit testing
//    public ProfileActivity(FirebaseAuth mAuth) {
//        this.mAuth = mAuth;
//    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
//        FirebaseApp.initializeApp(this);

        profileManager = new ProfileManager(FirebaseAuth.getInstance(), FirebaseDatabase.getInstance().getReference("users"));

//        mAuth = FirebaseAuth.getInstance();
//        currentUserID = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        currentUserID = profileManager.getCurrentUserId();
        setUserID();


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
//        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        loadUserProfile();

        // Configure buttons
        configureListsButton();
    }

    public void setUserID(){
        viewingUserID = getIntent().getStringExtra("user");
        if (viewingUserID == null) {
            viewingUserID = currentUserID; // Default to logged-in user
        }
    }

//    private void loadUserProfile() {
//        mDatabase.child(viewingUserID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if (!task.isSuccessful()) {
//                    Log.e("ProfileActivity", "Error getting data", task.getException());
//                } else {
//                    Map<String, Object> results = (Map<String, Object>) task.getResult().getValue();
//                    if (results != null) {
//                        Log.d("ProfileActivity", "Loaded profile: " + results);
//                        userView.setText(String.valueOf(results.get("username")));
//                        nameView.setText(String.valueOf(results.get("name")));
//                        zipView.setText(String.valueOf(results.get("zipcode")));
//                        viewingUsername = String.valueOf(results.get("name"));
//
//                        // Configure dynamic button after retrieving the username
//                        configureDynamicButton();
//                    } else {
//                        Log.d("ProfileActivity", "No user found");
//                    }
//                }
//            }
//        });
//    }

    private void loadUserProfile() {
        profileManager.getUserProfile(viewingUserID, task -> {
            if (!task.isSuccessful()) {
                Log.e("ProfileActivity", "Error getting data", task.getException());
            } else {
                DataSnapshot snapshot = task.getResult();
                Map<String, Object> results = (Map<String, Object>) snapshot.getValue();
                if (results != null) {
                    userView.setText(String.valueOf(results.get("username")));
                    nameView.setText(String.valueOf(results.get("name")));
                    zipView.setText(String.valueOf(results.get("zipcode")));
                    viewingUsername = String.valueOf(results.get("name"));
//                     Configure dynamic button after retrieving the username
                    configureDynamicButton();
                } else {
                    Log.d("ProfileActivity", "No user found");
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
                    dynamicButton.setText("unfriend");
                    dynamicButton.setOnClickListener(v -> unfriendUser());
                } else {
                    dynamicButton.setText("add friend");
                    dynamicButton.setOnClickListener(v -> addFriend());
                }
            });
        }
    }

    private void configureListsButton() {
        if (viewingUserID == null || currentUserID == null) {
            Log.e("ProfileActivity", "viewingUserID or currentUserID is null");
        } else {
            Log.e("ProfileActivity", "viewingUserID: " + viewingUserID + ", currentUserID: " + currentUserID);
        }
        if (viewingUserID.equals(currentUserID)) {
            myListsButton.setText("My Lists");
            myListsButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, myListsActivity.class);
                intent.putExtra("user", viewingUserID);
                startActivity(intent);
                Log.e("ProfileActivity", "myLists trying to load");
            });
        } else {
            myListsButton.setText("View Public Lists");
            myListsButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, friendsListsActivity.class);
                intent.putExtra("user", viewingUserID);
                startActivity(intent);
                Log.e("ProfileActivity", "friendsLists trying to load");
            });
        }
    }

//    private void addFriend() {
//        if (viewingUsername == null) {
//            Toast.makeText(ProfileActivity.this, "Unable to add friend. Try again later.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Add friend using username as key
//        mDatabase.child(currentUserID).child("friends").child(viewingUsername).setValue(true)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        Toast.makeText(ProfileActivity.this, "Friend added!", Toast.LENGTH_SHORT).show();
//                        configureDynamicButton(); // Refresh button state
//                    } else {
//                        Toast.makeText(ProfileActivity.this, "Failed to add friend.", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//
//    private void unfriendUser() {
//        if (viewingUsername == null) {
//            Toast.makeText(ProfileActivity.this, "Unable to remove friend. Try again later.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Remove friend using username as key
//        mDatabase.child(currentUserID).child("friends").child(viewingUsername).removeValue()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        Toast.makeText(ProfileActivity.this, "Friend removed!", Toast.LENGTH_SHORT).show();
//                        configureDynamicButton(); // Refresh button state
//                    } else {
//                        Toast.makeText(ProfileActivity.this, "Failed to remove friend.", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//
//    private void checkFriendStatus(FriendStatusCallback callback) {
//        if (viewingUsername == null) {
//            callback.onStatusChecked(false);
//            return;
//        }
//
//        mDatabase.child(currentUserID).child("friends").child(viewingUsername).get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful() && task.getResult().exists()) {
//                        callback.onStatusChecked(true);
//                    } else {
//                        callback.onStatusChecked(false);
//                    }
//                });
//    }

    private void addFriend() {
        if (viewingUsername == null) {
            Toast.makeText(ProfileActivity.this, "Unable to add friend. Try again later.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use ProfileManager to add a friend
        profileManager.addFriend(currentUserID, viewingUsername, task -> {
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

        // Use ProfileManager to remove a friend
        profileManager.unfriend(currentUserID, viewingUsername, task -> {
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

        // Use ProfileManager to check friend status
        profileManager.isFriend(currentUserID, viewingUsername, callback::onStatusChecked);
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
            profileManager.getmAuth().signOut();
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
            Intent intent = new Intent(this, TrailSearchActivity.class);

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
            Intent intent = new Intent(this, GroupSearchActivity.class);
            intent.putExtra("user", currentUserID);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
