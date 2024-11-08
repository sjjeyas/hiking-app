package com.example.project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.project.classes.Group;
import com.example.project.classes.Registered;
import com.example.project.classes.Trail;
import com.example.project.classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Set;


public class GroupActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Button join;
    private String user = "zaynmalik";
    private String groupname = "SoCal Hikers";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        FirebaseApp.initializeApp(this);
        Log.d("GroupActivity", "This is a debug message!");
        mDatabase = FirebaseDatabase.getInstance().getReference("groups");

        // Initialize FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        Toolbar toolbar = findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        join = (Button)findViewById(R.id.join_button);
        /*

        join = (Button)findViewById(R.id.join_button);
        HashMap<String, Object> data = new HashMap<String,Object>();
        HashMap<String, Object> members = new HashMap<String,Object>();
        members.put("sneha", true);
        members.put("nicole", true);
        members.put("yellow", true);
        members.put("orange", true);
        members.put("america", true);
        members.put("louis", true);
        members.put("niall", true);
        members.put("harry", true);
        members.put("", true);

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

        */

        mDatabase.child(groupname).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
                        join.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v){
                                Log.d("GroupActivity", "trying to join group");
                                boolean success = g.joinGroup(user);
                                HashMap<String, Object> members = g.members;
                                mDatabase.child(groupname).child("members").setValue(members);
                                if (success){
                                    Toast.makeText(getApplicationContext(),
                                                    "Joined group!!",
                                                    Toast.LENGTH_LONG)
                                            .show();
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
                                }else{
                                    Toast.makeText(getApplicationContext(),
                                                    "Too many people in group!!",
                                                    Toast.LENGTH_LONG)
                                            .show();
                                }
                            }
                        });
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

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navbar, menu); // Inflate your menu resource
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.d("MainActivity", "Menu item clicked with ID: " + id); // Log menu item clicks

        if (id == R.id.action_logout) {
            Log.d("MainActivity", "Logout clicked");
            mAuth.signOut();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            //send login success toast message
            Toast.makeText(this, "Logout Successful.", Toast.LENGTH_SHORT).show();

            return true;
        } else if (id == R.id.action_login) {
            Log.d("MainActivity", "Login button clicked");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_home) {
            Log.d("MainActivity", "Home button clicked");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_profile) {
            Log.d("MainActivity", "Profile button clicked");
            Intent intent = new Intent(this, ProfileActivity.class);
            String userID ="";
            userID = FirebaseAuth.getInstance().getUid();
            intent.putExtra("user", userID);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_trailsearch) {
            Log.d("MainActivity", "Search button clicked");
            Intent intent = new Intent(this, SearchActivity.class);
            String userID ="";
            userID = FirebaseAuth.getInstance().getUid();
            intent.putExtra("user", userID);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_friends) {
            Log.d("MainActivity", "Friends button clicked");
            Intent intent = new Intent(this, FriendsActivity.class);
            String userID ="";
            userID = FirebaseAuth.getInstance().getUid();
            intent.putExtra("user", userID);
            startActivity(intent);
            return true;
//        } else if (id == R.id.action_groupsearch) {
//            Log.d("MainActivity", "Groups button clicked");
//            Intent intent = new Intent(this, GroupActivity.class);
//            String userID ="";
//            userID = FirebaseAuth.getInstance().getUid();
//            intent.putExtra("user", userID);
//            startActivity(intent);
//            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
