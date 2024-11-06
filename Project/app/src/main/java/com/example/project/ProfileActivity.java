package com.example.project;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project.classes.Group;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;
import java.util.Set;

public class ProfileActivity  extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private String user = "orange";
    private TextView zipView;
    private TextView userView;
    private TextView nameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        FirebaseApp.initializeApp(this);
        Log.d("ProfileActivity", "This is a debug message!");
        userView = findViewById(R.id.usernamefield);
        zipView = findViewById(R.id.locationfield);
        nameView = findViewById(R.id.namefield);

        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mDatabase.child(user).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("ProfileActivity", "Error getting data", task.getException());
                } else {
                    Log.d("ProfileActivity", String.valueOf(task.getResult().getValue()));
                    Map<String, Object> results = (Map<String, Object>) task.getResult().getValue();
                    if (results != null) {
                        Log.d("ProfileActivity", String.valueOf(results));
                        userView.setText(String.valueOf(results.get("username")));
                        nameView.setText(String.valueOf(results.get("name")));
                        zipView.setText(String.valueOf(results.get("zipcode")));
                    } else {
                        Log.d("ProfileActivity", "No friend found");
                    }

                }
            }
        });
    }

}
