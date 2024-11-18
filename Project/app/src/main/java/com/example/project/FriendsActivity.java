package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.project.classes.Group;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FriendsActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private String user;
    private FirebaseAuth mAuth;
    private Button back;
    private String displayname;

    private void backToProfile(){
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("user", (String)user);
        startActivity(intent);
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
        if (u != null){
            user = u;
        }else {
            user = mAuth.getCurrentUser().getUid();
        }

        back = (Button)findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d("FriendsActivity", "go back button pushed");
                backToProfile();
            }
        });

//        Log.d("FriendsActivity", user);
//        TextView username = findViewById(R.id.user);
//        username.setText(String.valueOf(user + "'s Friends"));
//        mDatabase = FirebaseDatabase.getInstance().getReference("users");
//        mDatabase.child(user).child("friends").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if (!task.isSuccessful()) {
//                    Log.e("FriendsActivity", "Error getting data", task.getException());
//                } else {
//                    //Log.d("FriendsActivity", String.valueOf(task.getResult().getValue()));
//                    // THIS IS THE CODE THAT ADDS A NEW USER BEFORE READING PREVIOUS USERS,
//                    // YOU HAVE TO RETRIEVE OLD FRIENDS TO ADD A NEW FRIEND
//                    //AND UPDATE THE WHOLE FRIENDS LIST IN THE DB
//                    Map<String, Object> results = (Map<String, Object>) task.getResult().getValue();
//                    results.put("fazle", true);
//                    mDatabase.child(user).child("friends").setValue(results);
//                    //
//                    if (results != null) {
//                        Log.d("FriendsActivity", String.valueOf(results));
//                        String r = "";
//                        Set<String> keys = results.keySet();
//                        for (String k : results.keySet()){
//                            r += k + "\n";
//                        }
//                        TextView friendslist = findViewById(R.id.friendslist);
//                        friendslist.setText(r);
//                    } else {
//                        Log.d("FriendsActivity", "No friend found");
//                    }
//
//                }
//            }
//        });

        /*
        //THIS CODE ADDS A USER AND THEIR FRIENDS
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        Map<String, String> friends = new HashMap<>();
        friends.put("princessdiana", "true");
        friends.put("niallh", "true");
        friends.put("zaynmalik", "true");
        mDatabase.child(user).child("friends").setValue(friends);
        */

        //THIS CODE ACCESSES AND READS FRIENDS

        DatabaseReference names  = FirebaseDatabase.getInstance().getReference("users");
        names.child(user).child("name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()){
                    Log.e("FriendsActivity", "Error getting data", task.getException());
                }else{
                    if(task.getResult().getValue() != null){
                        displayname = (String) task.getResult().getValue();
                        Log.e("FriendsActivity", "Reading friends for name " + displayname);
                        TextView username = findViewById(R.id.user);
                        username.setText(String.valueOf(displayname + "'s Friends"));
                        mDatabase = FirebaseDatabase.getInstance().getReference("users");
                        mDatabase.child(user).child("friends").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (!task.isSuccessful()) {
                                    Log.e("FriendsActivity", "Error getting data", task.getException());
                                } else {
                                    //Log.d("FriendsActivity", String.valueOf(task.getResult().getValue()));
                                    Map<String, Object> results = (Map<String, Object>) task.getResult().getValue();
                                    if (results != null) {
                                        Log.d("FriendsActivity", String.valueOf(results));
                                        String r = "";
                                        Set<String> keys = results.keySet();
                                        for (String k : results.keySet()){
                                            r += k + "\n";
                                        }
                                        TextView friendslist = findViewById(R.id.friendslist);
                                        friendslist.setText(r);
                                    } else {
                                        TextView friendslist = findViewById(R.id.friendslist);
                                        friendslist.setText("No friends found! :(");
                                        Log.d("FriendsActivity", "No friend found");
                                    }

                                }
                            }
                        });
                    }
                    else{
                        Log.e("addReviewActivity", "Error getting data", task.getException());
                    }
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
            String userID ="";
            userID = FirebaseAuth.getInstance().getUid();
            intent.putExtra("user", userID);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_trailsearch) {
            Log.d("MainActivity", "Search button clicked");
            Intent intent = new Intent(this, SearchActivity.class);
            String userID ="";
            userID = FirebaseAuth.getInstance().getUid();
            intent.putExtra("user", userID);
            startActivity(intent);
            return true;
//        } else if (id == R.id.action_friends) {
//            Log.d("MainActivity", "Friends button clicked");
//            Intent intent = new Intent(this, FriendsActivity.class);
//            String userID ="";
//            userID = FirebaseAuth.getInstance().getUid();
//            intent.putExtra("user", userID);
//            startActivity(intent);
//            return true;
        } else if (id == R.id.action_groupsearch) {
            Log.d("MainActivity", "Groups button clicked");
            Intent intent = new Intent(this, GroupActivity.class);
            String userID ="";
            userID = FirebaseAuth.getInstance().getUid();
            intent.putExtra("user", userID);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
