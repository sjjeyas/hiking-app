package com.example.project;

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

public class addReviewActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private String trail = "Antelope Trail South Loop";
    private Button submit;
    private Button back;
    private String user = "sneha";
    private TextView reviewTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addreview);
        FirebaseApp.initializeApp(this);
        Log.d("addReviewActivity", "This is a debug message!");
        reviewTextView = findViewById(R.id.editreview);
        submit = findViewById(R.id.sendreview_button);
        back = findViewById(R.id.back_button);
        mDatabase= FirebaseDatabase.getInstance().getReference("trails");
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeReview();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToTrail();
            }
        });
    }

    private void writeReview(){
        Map<String, String> data = new HashMap<>();
        String review = reviewTextView.getText().toString();
        data.put(user, review);
        Log.d("addReviewActivity", review);
        mDatabase.child(trail).child("reviews").setValue(data)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("addReviewActivity", "Review added successfully");
                        })
                        .addOnFailureListener(e -> {
                            Log.e("addReviewActivity", "Error adding review", e);
                        });
    }

    private void backToTrail(){

    }
}
