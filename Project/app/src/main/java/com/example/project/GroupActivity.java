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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Set;


public class GroupActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        FirebaseApp.initializeApp(this);
        Log.d("GroupActivity", "This is a debug message!");
        mDatabase = FirebaseDatabase.getInstance().getReference("groups");

        HashMap<String, Object> data = new HashMap<String,Object>();
        HashMap<String, Object> members = new HashMap<String,Object>();
        members.put("sneha", true);
        members.put("nicole", true);
        data.put("trail", "Aimee's Loop");
        data.put("name", "NewGroup");
        data.put("members", members);
        Log.d("GroupActivity", "Write error");
        mDatabase.child("NewGroup").setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("GroupActivity", "Write Successful");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("GroupActivity", "Write failure");
                    }
                });



        mDatabase.child("NewGroup").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("GroupActivity", "Error getting data", task.getException());
                }
                else {
                    Log.d("GroupActivity", String.valueOf(task.getResult().getValue()));
                    DataSnapshot ds = task.getResult();
                    Object result = ds.getValue();
                    assert result != null;
                    Group g = ds.getValue(Group.class);
                    if (g != null) {
                        Log.d("GroupActivity", "Loaded group: " + g.name);
                        TextView name = findViewById(R.id.groupname);
                        TextView location = findViewById(R.id.trailname);
                        TextView users = findViewById(R.id.userlist);
                        name.setText(g.name);
                        location.setText(g.trail);
                        String r = "";
                        Set<String> keys = g.members.keySet();
                        for (String k : g.members.keySet()){
                            r += k + "\n";
                        }
                        users.setText(r);
                    } else {
                        Log.d("GroupActivity", "No trail found");
                    }
                }
            }
        });






    }
}
