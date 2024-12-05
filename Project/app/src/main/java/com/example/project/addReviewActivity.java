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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class addReviewActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private String displayname;
    private TextView addReviewView;
    private String trail;
    private Button submit;
    private Button back;
    private String user ;
    private TextView reviewTextView;
    private TextView ratingTextView;
    private FirebaseAuth mAuth;
    private String title;


    private void backToTrail(){
        Intent intent = new Intent(this, TrailActivity.class);
        intent.putExtra("trailname", trail);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addreview);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        Toolbar toolbar = findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        Log.d("addReviewActivity", "This is a debug message!");
        reviewTextView = findViewById(R.id.editreview);
        addReviewView = findViewById(R.id.addreview);
        ratingTextView = findViewById(R.id.rating);
        String t = getIntent().getStringExtra("trailname");
        if (t != null){
            trail = t;
        }else{
            trail = "Aimee's Loop";
        }
        String u = getIntent().getStringExtra("user");
        if (u != null){
            user = u;
        }else {
            user = "cpE14NyyLWMRRmEQvkXIZeeZ3O42";
        }
        title = "Add Review: "+trail;
        addReviewView.setText(title);
        submit = findViewById(R.id.sendreview_button);
        back = (Button)findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v){
               Log.d("addReviewActivity", "go back button pushed");
               backToTrail();
           }
        });
        mDatabase= FirebaseDatabase.getInstance().getReference("trails");
        mDatabase.child(trail).child("reviews").child(user).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>(){
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task){
                if (!task.isSuccessful()){
                    Log.e("addReviewActivity", "Error getting data", task.getException());
                }else{
                    if(task.getResult().getValue() != null){
                        Map<String, Object> results = (Map<String, Object>) task.getResult().getValue();
                        ratingTextView.setText(String.valueOf(results.get("rating")));
                        reviewTextView.setText(String.valueOf(results.get("text")));
                        title = "Edit Review: "+trail;
                        addReviewView.setText(title);
                    }
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeReview();
                backToTrail();
            }
        });

    }




    private void writeReview(){
        DatabaseReference names  = FirebaseDatabase.getInstance().getReference("users");
        Map<String, String> data = new HashMap<>();
        names.child(user).child("name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()){
                    Log.e("addReviewActivity", "Error getting data", task.getException());
                }else{
                    if(task.getResult().getValue() != null){
                        displayname = (String) task.getResult().getValue();
                        Log.e("addReviewActivity", "Writing for name " + displayname);
                        String review = reviewTextView.getText().toString();
                        String rating = ratingTextView.getText().toString();
                        data.put("text", review);
                        data.put("rating", rating);
                        data.put("displayname", displayname);
                        Log.d("addReviewActivity", review);
                        mDatabase = FirebaseDatabase.getInstance().getReference("trails");
                        mDatabase.child(trail).child("reviews").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (!task.isSuccessful()) {
                                    Log.e("addReviewActivity", "Error getting data", task.getException());
                                } else {
                                    if (task.getResult().getValue() != null){
                                        Map<String, Object> results = (Map<String, Object>) task.getResult().getValue();
                                        results.put(user, data);
                                        mDatabase.child(trail).child("reviews").setValue(results)
                                                .addOnSuccessListener(aVoid -> {
                                                    Log.d("addReviewActivity", "Review added successfully");

                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("addReviewActivity", "Error adding review", e);
                                                });
                                        //
                                    }else{
                                        Map<String, Object> results = new HashMap<>();
                                        results.put(user, data);
                                        mDatabase.child(trail).child("reviews").setValue(results)
                                                .addOnSuccessListener(aVoid -> {
                                                    Log.d("addReviewActivity", "Review added successfully");

                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("addReviewActivity", "Error adding review", e);
                                                });
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
            Intent intent = new Intent(this, TrailSearchActivity.class);
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
            Intent intent = new Intent(this, GroupSearchActivity.class);
            String userID ="";
            userID = FirebaseAuth.getInstance().getUid();
            intent.putExtra("user", userID);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
