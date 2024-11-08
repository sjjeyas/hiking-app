package com.example.project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.os.Bundle;
import android.text.TextUtils;
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

import com.example.project.classes.Trail;
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

public class showReviewActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private String trail = "Big Bear Lake Trail";
    private TextView titleReview;
    private TextView reviews;
    private Button back;

    private FirebaseAuth mAuth;


    private void backToTrail(){
        Intent intent = new Intent(this, TrailActivity.class);
        intent.putExtra("trailname", trail);
        startActivity(intent);
    }
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showreview);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        Toolbar toolbar = findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        String t = getIntent().getStringExtra("trailname");
        if (t != null){
            trail = t;
        }

        back = (Button) findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d("addReviewActivity", "go back button pushed");
                backToTrail();
            }
        });


        Log.d("showReviewActivity", "This is a debug message!");
        titleReview = findViewById(R.id.titlereview);
        String title = "Reviews: " + trail;
        titleReview.setText(title);
        mDatabase = FirebaseDatabase.getInstance().getReference("trails");
        mDatabase.child(trail).child("reviews").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("showReviewActivity", "Error getting data", task.getException());
                }
                else {
                    Log.d("showReviewActivity", String.valueOf(task.getResult().getValue()));

                    Map<String, String> results = (Map<String, String>) task.getResult().getValue();
//                    Map<String, String> reviews = (Map<String, String>) task.getResult().getValue();


                    if (results != null) {
                        Log.d("showReviewActivity", String.valueOf(results));
                        StringBuilder r = new StringBuilder();

                        for (Map.Entry<String, String> entry : results.entrySet()){
                            String reviewer = entry.getKey();       // Reviewer name
                            String reviewText = entry.getValue();   // Review text

                            r.append(reviewer).append(":\n")
                                    .append(reviewText).append("\n\n");
                        }
                        reviews = findViewById(R.id.reviews);
                        reviews.setText(r);
                    } else {
                        Log.d("showReviewActivity", "No review found");
                    }
                }
            }
        });
    }

//    private void backToTrail(){
//        Intent intent = new Intent(this, TrailActivity.class);
//        intent.putExtra("trailname", trail);
//        startActivity(intent);
//    }

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
            String userID ="";
            userID = FirebaseAuth.getInstance().getUid();
            intent.putExtra("user", userID);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
