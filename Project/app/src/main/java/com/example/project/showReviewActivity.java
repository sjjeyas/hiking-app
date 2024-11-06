package com.example.project;

import android.os.Bundle;
import android.util.Log;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project.classes.Trail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class showReviewActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private String trail = "Bear Gulch Cave Trail";
    private TextView titleReview;
    private TextView reviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showreview);
        FirebaseApp.initializeApp(this);
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
                    Map<String, Object> results = (Map<String, Object>) task.getResult().getValue();
                    if (results != null) {
                        Log.d("showReviewActivity", String.valueOf(results));
                        String r = "";
                        Set<String> keys = results.keySet();
                        for (String k : results.keySet()){
                            r += k + "\n";
                            String toparse = String.valueOf(results.get(k));
                            toparse = toparse.replaceAll("[{}]", "");
                            String[] parsed = toparse.split("=");
                            r += parsed[1].trim() + "\n" + "\n";
                        }
                        reviews = findViewById(R.id.reviews);
                        reviews.setText(r);
                    } else {
                        Log.d("showReviewActivity", "No review found");
                    }
                }
            }
        });
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
