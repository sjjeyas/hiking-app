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

import java.util.HashMap;
import java.util.Map;

public class FriendsActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        FirebaseApp.initializeApp(this);
        Log.d("FriendsActivity", "This is a debug message!");
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mDatabase.child("sneha").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("FriendsActivity", "Error getting data", task.getException());
                } else {
                    Log.d("FriendsActivity", String.valueOf(task.getResult().getValue()));
                    String input = String.valueOf(task.getResult().getValue());
                    TextView friendslist = findViewById(R.id.friendslist);
                    friendslist.setText(String.valueOf(task.getResult().getValue()));
                }
            }
        });
        /*
        mDatabase.child("maria").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    DataSnapshot ds = task.getResult();
                    Object result = ds.getValue();
                    assert result != null;
                    String friend = ds.getValue(String.class);
                    if (friend != null) {
                        Log.d("firebase", "Loaded friend: " + friend);
                    } else {
                        Log.d("firebase", "No friend found");
                    }
                }
            }
        });

         */
    }
}
