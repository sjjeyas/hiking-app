package com.example.project;

import android.annotation.SuppressLint;
import android.content.Intent;
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
    private String trail = "Big Bear Lake Trail";
    private TextView titleReview;
    private TextView reviews;
    private Button back;

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
    }


}
