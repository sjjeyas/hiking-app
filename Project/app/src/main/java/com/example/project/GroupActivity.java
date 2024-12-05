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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class GroupActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Button join;
    private String user ;
    private String groupname;
    private boolean joined = false;
    private String displayname;

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
        String u = getIntent().getStringExtra("user");
        if (u != null){
            user = u;
        }
        groupname = getIntent().getStringExtra("groupname");


        DatabaseReference names  = FirebaseDatabase.getInstance().getReference("users");
        Map<String, String> data = new HashMap<>();
        names.child(user).child("name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()){
                    Log.e("GroupActivity", "Error getting data", task.getException());
                }else{
                    if(task.getResult().getValue() != null){
                        displayname = (String) task.getResult().getValue();
                        Log.e("GroupActivity", "Writing for name " + displayname);
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
                                                joinGroup(g);
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
                    else{
                        Log.e("addReviewActivity", "Error getting data", task.getException());
                    }
                }

            }
        });

    }

    public void joinGroup(Group g){
        if(!joined){
            Log.d("GroupActivity", "trying to join group");
            boolean success = g.joinGroup(displayname);
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
                join.setText("LEAVE");
                joined=true;
            }else{
                Toast.makeText(getApplicationContext(),
                                "Too many people in group!!",
                                Toast.LENGTH_LONG)
                        .show();
            }
        }
        else{
            Log.d("GroupActivity", "leaving group");
            g.leaveGroup(displayname);
            HashMap<String, Object> members = g.members;
            mDatabase.child(groupname).child("members").setValue(members);
            Toast.makeText(getApplicationContext(),
                            "Left group!!",
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
            join.setText("JOIN");
            joined = false;
        }
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
            Intent intent = new Intent(this, TrailSearchActivity.class);
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
        } else if (id == R.id.action_groupsearch) {
            Log.d("MainActivity", "Groups button clicked");
            Intent intent = new Intent(this, GroupSearchActivity.class);
            String userID ="";
            userID = FirebaseAuth.getInstance().getUid();
            intent.putExtra("user", userID);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
