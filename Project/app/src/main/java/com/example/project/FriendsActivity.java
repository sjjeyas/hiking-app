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
import java.util.Set;

public class FriendsActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private String user = "sneha";
    /*
    public FriendsActivity(String username){
        user = username;
    }
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        FirebaseApp.initializeApp(this);
        Log.d("FriendsActivity", "This is a debug message!");
        TextView username = findViewById(R.id.user);
        username.setText(String.valueOf(user + "'s Friends"));
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mDatabase.child(user).child("friends").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("FriendsActivity", "Error getting data", task.getException());
                } else {
                    //Log.d("FriendsActivity", String.valueOf(task.getResult().getValue()));
                    // THIS IS THE CODE THAT ADDS A NEW USER BEFORE READING PREVIOUS USERS,
                    // YOU HAVE TO RETRIEVE OLD FRIENDS TO ADD A NEW FRIEND
                    //AND UPDATE THE WHOLE FRIENDS LIST IN THE DB
                    Map<String, Object> results = (Map<String, Object>) task.getResult().getValue();
                    results.put("fazle", true);
                    mDatabase.child(user).child("friends").setValue(results);
                    //
                    if (results != null) {
                        Log.d("FriendsActivity", String.valueOf(results));
                        String r = "";
                        Set<String> keys = results.keySet();
                        for (String k : results.keySet()){
                            r += k + "\n";
                        }
                        TextView friendslist = findViewById(R.id.friendslist);
                        friendslist.setText(r);
                    } else {
                        Log.d("FriendsActivity", "No friend found");
                    }

                }
            }
        });
        /*
        THIS CODE ADDS A USER AND THEIR FRIENDS
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        Map<String, String> data = new HashMap<>();
        Map<String, String> friends = new HashMap<>();
        data.put("username", "chao wang");
        friends.put("nicole", "true");
        mDatabase.child("chao wang").setValue(data);
        mDatabase.child("chao wang").child("friends").setValue(friends);
         */

        //THIS CODE ACCESSES AND READS FRIENDS
        /*
        TextView username = findViewById(R.id.user);
        username.setText(String.valueOf(user + "'s Friends"));
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mDatabase.child(user).child("friends").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("FriendsActivity", "Error getting data", task.getException());
                } else {
                    //Log.d("FriendsActivity", String.valueOf(task.getResult().getValue()));
                    Map<String, Object> results = (Map<String, Object>) task.getResult().getValue();
                    if (results != null) {
                        Log.d("FriendsActivity", String.valueOf(results));
                        String r = "";
                        Set<String> keys = results.keySet();
                        for (String k : results.keySet()){
                            r += k + "\n";
                        }
                        TextView friendslist = findViewById(R.id.friendslist);
                        friendslist.setText(r);
                    } else {
                        Log.d("FriendsActivity", "No friend found");
                    }

                }
            }
        });

         */
    }
}
