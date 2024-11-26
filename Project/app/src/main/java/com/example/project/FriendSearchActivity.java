package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FriendSearchActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private List<String> userList = new ArrayList<>();
    private FriendAdapter friendAdapter; // Custom adapter for displaying user names
    private FirebaseAuth mAuth;

    private void openUserProfile(String username) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.orderByChild("name").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Loop through the results to find the matching userID
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String userID = snapshot.getKey(); // Get the userID (key) of the user
                        if (userID != null) {
                            // Launch ProfileActivity with the found userID
                            Intent intent = new Intent(FriendSearchActivity.this, ProfileActivity.class);
                            intent.putExtra("user", userID);
                            startActivity(intent);
                            break; // Exit loop after finding the first match
                        }
                    }
                } else {
                    Toast.makeText(FriendSearchActivity.this, "User not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FriendsActivity", "Error querying user: ", databaseError.toException());
                Toast.makeText(FriendSearchActivity.this, "Error fetching user data.", Toast.LENGTH_SHORT).show();
            }
        });;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendsearch);

        FirebaseApp.initializeApp(this);
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        // Initialize FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        Toolbar toolbar = findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        ListView searchList = findViewById(R.id.userList);
        friendAdapter = new FriendAdapter(this, new ArrayList<>());
        searchList.setAdapter(friendAdapter);

        searchList.setOnItemClickListener((parent, view, position, id) -> {
            String userName = friendAdapter.getItem(position);
            Log.d("friendSearchActivity", "Clicked on user: " + userName);
            openUserProfile(userName);
        });

        SearchView searchView = findViewById(R.id.userSearch);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                friendAdapter.getFilter().filter(newText);
                return false;
            }
        });

        fetchUserListFromFirebase();
    }

    private void fetchUserListFromFirebase() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    HashMap<String, String> userMap = (HashMap<String, String>) userSnapshot.getValue();
                    if (userMap != null) {
                        String name = userMap.get("name"); // Extract user name
                        if (name != null) {
                            userList.add(name);
                        }
                    }
                }

                friendAdapter.updateData(userList); // Update the adapter with the new data
                Log.d("friendSearchActivity", "Loaded users from Firebase: " + userList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("friendSearchActivity", "loadPost:onCancelled", databaseError.toException());
                Toast.makeText(FriendSearchActivity.this, "Failed to load users.", Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent(this, SearchActivity.class);
            String userID = FirebaseAuth.getInstance().getUid();
            intent.putExtra("user", userID);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_friends) {
            Log.d("MainActivity", "Friends button clicked");
            Intent intent = new Intent(this, FriendsActivity.class);
            String userID ="";
            userID = FirebaseAuth.getInstance().getUid();
            intent.putExtra("user", userID);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_groupsearch) {
            Log.d("MainActivity", "Groups button clicked");
            Intent intent = new Intent(this, GroupActivity.class);
            String userID = FirebaseAuth.getInstance().getUid();
            intent.putExtra("user", userID);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
