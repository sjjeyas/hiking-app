package com.example.project;

import static android.content.ContentValues.TAG;

import static java.lang.System.in;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project.classes.Registered;
import com.example.project.classes.Trail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
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

public class TrailActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private String trail = "Aimee's Loop";
    private Button seereview;
    private Button addreview;


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



    // Access a Cloud Firestore instance from your Activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trail);
        String t = getIntent().getStringExtra("trailname");
        if (t != null) {
            Log.d("TrailActivity", t);
            trail =  t;
        }
        addreview = (Button) findViewById(R.id.addreview_button);
        seereview = (Button) findViewById(R.id.seereview_button);
        seereview.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d("addReviewActivity", "seereviews button pushed");
                seeReview();
            }
        });
        addreview.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d("addReviewActivity", "seereviews button pushed");
                addReview();
            }
        });

        FirebaseApp.initializeApp(this);
        Log.d("MyActivity", "This is a debug message!");
        mDatabase = FirebaseDatabase.getInstance().getReference("trails");
        mDatabase.child(trail ).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    DataSnapshot ds = task.getResult();
                    Object result = ds.getValue();
                    assert result != null;
                    Trail newTrail = ds.getValue(Trail.class);
                    if (newTrail != null) {
                        Log.d("firebase", "Loaded trail: " + newTrail.name);
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
    public void addReview(){
        Intent intent = new Intent(this, addReviewActivity.class);
        intent.putExtra("trailname", trail);
        startActivity(intent);
    }

    public void seeReview(){
        Intent intent = new Intent(this, showReviewActivity.class);
        intent.putExtra("trailname", trail);
        startActivity(intent);
    }

}
