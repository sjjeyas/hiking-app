package com.example.project;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project.classes.Group;
import com.example.project.classes.Registered;
import com.example.project.classes.Trail;
import com.example.project.classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class GroupActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        FirebaseApp.initializeApp(this);
        Log.d("GroupActivity", "This is a debug message!");
        mDatabase = FirebaseDatabase.getInstance().getReference("groups");
        mDatabase.child("oregon hikers").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
                    Group g = ds.getValue(Group.class);
                    if (g != null) {
                        Log.d("firebase", "Loaded group: " + g.name);
                        TextView name = findViewById(R.id.groupname);
                        TextView location = findViewById(R.id.trailname);
                        TextView users = findViewById(R.id.userlist);
                        name.setText(g.name);
                        location.setText(g.trail);
                        users.setText(String.join(", ", g.members));
                    } else {
                        Log.d("firebase", "No trail found");
                    }
                }
            }
        });


    }
}
