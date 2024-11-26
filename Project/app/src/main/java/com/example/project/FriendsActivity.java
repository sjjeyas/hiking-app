package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FriendsActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private String user;
    private FirebaseAuth mAuth;
    private ListView friendsListView;
    private Button findFriendsButton;
    private String displayname;
    private List<String> friendsList = new ArrayList<>();
    private ArrayAdapter<String> friendsAdapter;

    private void openFriendProfile(String friendname) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.orderByChild("name").equalTo(friendname).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Loop through the results to find the matching userID
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String userID = snapshot.getKey(); // Get the userID (key) of the user
                        if (userID != null) {
                            // Launch ProfileActivity with the found userID
                            Intent intent = new Intent(FriendsActivity.this, ProfileActivity.class);
                            intent.putExtra("user", userID);
                            startActivity(intent);
                            break; // Exit loop after finding the first match
                        }
                    }
                } else {
                    Toast.makeText(FriendsActivity.this, "User not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FriendsActivity", "Error querying user: ", databaseError.toException());
                Toast.makeText(FriendsActivity.this, "Error fetching user data.", Toast.LENGTH_SHORT).show();
            }
        });;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_friends);

        mAuth = FirebaseAuth.getInstance();
        Toolbar toolbar = findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        Log.d("FriendsActivity", "This is a debug message!");
        String u = getIntent().getStringExtra("user");
        if (u != null) {
            user = u;
        } else {
            user = mAuth.getCurrentUser().getUid();
        }

        friendsListView = findViewById(R.id.friends_list_view);
        friendsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, friendsList);
        friendsListView.setAdapter(friendsAdapter);

        friendsListView.setOnItemClickListener((parent, view, position, id) -> {
            String friendname = friendsList.get(position);
            Log.d("FriendsActivity", "Clicked on friend: " + friendname);
            openFriendProfile(friendname);
        });

        findFriendsButton = findViewById(R.id.find_friends_button);
        findFriendsButton.setOnClickListener(v -> {
            Log.d("FriendsActivity", "Find Friends button clicked");
            Intent intent = new Intent(FriendsActivity.this, FriendSearchActivity.class);
            intent.putExtra("user", user); // Pass the current user ID
            startActivity(intent);
        });

        DatabaseReference names = FirebaseDatabase.getInstance().getReference("users");
        names.child(user).child("name").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("FriendsActivity", "Error getting data", task.getException());
            } else {
                if (task.getResult().getValue() != null) {
                    displayname = (String) task.getResult().getValue();
                    Log.e("FriendsActivity", "Reading friends for name " + displayname);
                    TextView username = findViewById(R.id.user);
                    username.setText(String.valueOf(displayname + "'s Friends"));
                    mDatabase = FirebaseDatabase.getInstance().getReference("users");
                    mDatabase.child(user).child("friends").get().addOnCompleteListener(task1 -> {
                        if (!task1.isSuccessful()) {
                            Log.e("FriendsActivity", "Error getting data", task1.getException());
                        } else {
                            Map<String, Object> results = (Map<String, Object>) task1.getResult().getValue();
                            if (results != null) {
                                Log.d("FriendsActivity", String.valueOf(results));
                                Set<String> keys = results.keySet();
                                friendsList.clear();
                                friendsList.addAll(keys);
                                friendsAdapter.notifyDataSetChanged();
                            } else {
                                Log.d("FriendsActivity", "No friends found");
                                friendsList.clear();
                                friendsList.add("No friends found! :(");
                                friendsAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                } else {
                    Log.e("FriendsActivity", "Error getting data", task.getException());
                }
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navbar, menu); // Inflate your menu resource
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.d("MainActivity", "Menu item clicked with ID: " + id); // Log menu item clicks

        if (id == R.id.action_logout) {
            Log.d("MainActivity", "Logout clicked");
            mAuth.signOut();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            //send login success toast message
            Toast.makeText(this, "Logout Successful.", Toast.LENGTH_SHORT).show();

            return true;
        } else if (id == R.id.action_login) {
            Log.d("MainActivity", "Login button clicked");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_home) {
            Log.d("MainActivity", "Home button clicked");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_profile) {
            Log.d("MainActivity", "Profile button clicked");
            Intent intent = new Intent(this, ProfileActivity.class);
            String userID = FirebaseAuth.getInstance().getUid();
            intent.putExtra("user", userID);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_trailsearch) {
            Log.d("MainActivity", "Search button clicked");
            Intent intent = new Intent(this, TrailSearchActivity.class);
            String userID = FirebaseAuth.getInstance().getUid();
            intent.putExtra("user", userID);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_groupsearch) {
            Log.d("MainActivity", "Groups button clicked");
            Intent intent = new Intent(this, GroupSearchActivity.class);
            String userID = FirebaseAuth.getInstance().getUid();
            intent.putExtra("user", userID);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
