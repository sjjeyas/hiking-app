package com.example.project;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;

public class showReviewActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private String trail = "Aimee's Loop";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showreview);
        FirebaseApp.initializeApp(this);
        Log.d("showReviewActivity", "This is a debug message!");
        /*
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
         */

    }
}
