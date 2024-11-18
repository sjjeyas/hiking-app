package com.example.project;

import static android.content.ContentValues.TAG;

import static java.lang.System.in;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
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

import com.example.project.classes.Registered;
import com.example.project.classes.Trail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class TrailActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private String trail = "Boy Scout Trail";
    private Button seereview;
    private Button addreview;
    private FirebaseAuth mAuth;
    private TextView descriptionView;
    private Button addtoList;
    /*
    public TrailActivity(String n){
        trail = n;
    }

    public void writeNewTrail(String name, String location, String description){
        Trail t = new Trail(name, location, description);
        mDatabase.child("trails").child(name).setValue(t)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "Trail added successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Error adding trail", e);
                });
    }*/

    public void addReview(){
        Intent intent = new Intent(this, addReviewActivity.class);
        intent.putExtra("trailname", trail);
        String userID ="";
        userID = FirebaseAuth.getInstance().getUid();
        intent.putExtra("user", userID);
        startActivity(intent);
    }

    public void seeReview(){
        Intent intent = new Intent(this, showReviewActivity.class);
        intent.putExtra("trailname", trail);
        String userID ="";
        userID = FirebaseAuth.getInstance().getUid();
        intent.putExtra("user", userID);
        startActivity(intent);
    }



    // Access a Cloud Firestore instance from your Activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trail);


        mAuth = FirebaseAuth.getInstance();
        Toolbar toolbar = findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        descriptionView = findViewById(R.id.traildescription);
        descriptionView.setMovementMethod(new ScrollingMovementMethod());
        String t = getIntent().getStringExtra("trailname");
        if (t != null){
            trail = t;
        }else {
            trail = "Aimee's Loop";
        }


        addreview = (Button) findViewById(R.id.addreview_button);
        seereview = (Button) findViewById(R.id.seereview_button);
        addtoList = (Button) findViewById(R.id.list_button);
        seereview.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d("TrailActivity", "seereviews button pushed");
                seeReview();
            }
        });
        addreview.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d("TrailActivity", "addreviews button pushed");
                addReview();
            }
        });
        addtoList.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d("TrailActivity", "addtolist button pushed");
                addTrail();
            }
        });

        FirebaseApp.initializeApp(this);
        Log.d("TrailActivity", "This is a debug message!");
        mDatabase = FirebaseDatabase.getInstance().getReference("trails");
        mDatabase.child(trail ).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("TrailActivity", "Error getting data", task.getException());
                }
                else {
                    Log.d("TrailActivity", String.valueOf(task.getResult().getValue()));
                    DataSnapshot ds = task.getResult();
                    Object result = ds.getValue();
                    assert result != null;
                    Trail newTrail = ds.getValue(Trail.class);
                    if (newTrail != null) {
                        Log.d("TrailActivity", "Loaded trail: " + newTrail.name);
                        TextView name = findViewById(R.id.trailname);
                        TextView description = findViewById(R.id.traildescription);
                        TextView location = findViewById(R.id.traillocation);
                        name.setText(newTrail.name);
                        description.setText(newTrail.description);
                        location.setText(newTrail.location);
                    } else {
                        Log.d("firebase", "No trail found");
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
    private void addTrail(){
        Intent intent = new Intent(this, pickListActivity.class);
        intent.putExtra("trailname", trail);
        String userID ="";
        userID = FirebaseAuth.getInstance().getUid();
        intent.putExtra("user", userID);
        startActivity(intent);
    }
}
