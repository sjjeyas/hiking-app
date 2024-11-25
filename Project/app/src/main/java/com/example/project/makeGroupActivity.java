package com.example.project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.project.classes.Group;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class makeGroupActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String user ;
    private Button submit;
    private Button back;
    private FirebaseAuth mAuth;
    private EditText groupNameInput;
    private EditText trailNameInput;
    private EditText capacityInput;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makegroup);
        String u = getIntent().getStringExtra("user");

        groupNameInput = findViewById(R.id.groupNameInput);
        trailNameInput = findViewById(R.id.trailNameInput);
        capacityInput = findViewById(R.id.capacityInput);
        mDatabase = FirebaseDatabase.getInstance().getReference("groups");
        Toolbar toolbar = findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        if (u != null){
            user = u;
        }else {
            user = mAuth.getCurrentUser().getUid();
        }
        back = (Button) findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToGroup();
            }
        });
        submit = (Button) findViewById(R.id.submit_button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("MakeGroupActivity", "Submit button pressed");
                createGroup();
            }
        });
    }
    private void goToGroup(){
        Intent intent = new Intent(this, GroupActivity.class);

        intent.putExtra("groupname", groupNameInput.getText().toString());
        intent.putExtra("user", user);

        this.startActivity(intent);
    }

    private void createGroup(){
        String name = groupNameInput.getText().toString();
        String trailName = trailNameInput.getText().toString();
        int capacity = Integer.parseInt(capacityInput.getText().toString());
        Group newGroup = new Group(name, user, trailName, capacity);

        mDatabase.push().setValue(newGroup).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.e("MakeGroupActivity", "Group added successfully");
                Toast.makeText(this, "Group created successfully", Toast.LENGTH_SHORT).show();
                goToGroup(); // Navigate after successful save
            } else {
                Log.e("MakeGroupActivity", "Error adding group", task.getException());
                Toast.makeText(this, "Failed to create group. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });


//        mDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if (!task.isSuccessful()){
//                    Log.e("makeGroupActivity", "Error getting data1", task.getException());
//                    //
//                }else{
//                    if(task.getResult().getValue() != null){
//                        Map<String, Object> results = (Map<String, Object>) task.getResult().getValue();
//                        Log.e("makeGroupActivity", "Writing for UID2 " + user);
//                        String name = groupNameInput.getText().toString();
//                        String trailName = trailNameInput.getText().toString();
//                        int capacity = Integer.parseInt(capacityInput.getText().toString());
//                        Group newGroup = new Group(name, user, trailName, capacity);
//                    }
//                }
//            }
//        });

        goToGroup();
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
