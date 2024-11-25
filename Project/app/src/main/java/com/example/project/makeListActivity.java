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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class makeListActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String user ;
    private Button submit;
    private Button back;
    private FirebaseAuth mAuth;
    private EditText nameEditText;
    private EditText descriptionEditText;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makelist);
        String u = getIntent().getStringExtra("user");
        nameEditText = findViewById(R.id.listname);
        descriptionEditText = findViewById(R.id.listdescription);
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
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
                goToLists();
            }
        });
        submit = (Button) findViewById(R.id.submit_button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("makeListActivity", "Submit button pressed");
                createList();
            }
        });
    }
    private void goToLists(){
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("user", user);
        this.startActivity(intent);
    }

    private void createList(){
        Map<String, String> data = new HashMap<>();
        mDatabase.child(user).child("lists").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()){
                    Log.e("makeListActivity", "Error getting data1", task.getException());
                    //
                }else{
                    if(task.getResult().getValue() != null){
                        Map<String, Object> results = (Map<String, Object>) task.getResult().getValue();
                        Log.e("makeListActivity", "Writing for UID2 " + user);
                        String name = nameEditText.getText().toString();
                        String description = descriptionEditText.getText().toString();
                        data.put("name", name);
                        data.put("description", description);
                        Log.d("makeListActivity", name + "1");
                        mDatabase.child(user).child("lists").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (!task.isSuccessful()) {

                                    Log.e("makeListActivity", "Error getting data2", task.getException());

                                } else {
                                    if (task.getResult().getValue() != null){
                                        results.put(name, data);
                                        mDatabase.child(user).child("lists").setValue(results)
                                                .addOnSuccessListener(aVoid -> {
                                                    Log.d("makeListActivity", "List added successfully2");
                                                    Toast.makeText(getApplicationContext(),
                                                                    "List creation successful!",
                                                                    Toast.LENGTH_LONG)
                                                            .show();

                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("makeListActivity", "Error adding list2", e);
                                                });
                                        //
                                    }else{
                                        Map<String, Object> results = new HashMap<>();
                                        results.put(user, data);
                                        mDatabase.child(user).child("lists").child(name).setValue(results)
                                                .addOnSuccessListener(aVoid -> {
                                                    Log.d("makeListReviewActivity", "List added successfully3");
                                                    Toast.makeText(getApplicationContext(),
                                                                    "List creation successful!",
                                                                    Toast.LENGTH_LONG)
                                                            .show();

                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("makeListActivity", "Error adding list3", e);
                                                });
                                    }

                                }
                            }
                        });
                    }
                    else{
                        Log.e("makeListActivity", "Error getting data4", task.getException());
                        Log.e("makeListActivity", "Writing for UID1 " + user);
                        String name = nameEditText.getText().toString();
                        String description = descriptionEditText.getText().toString();
                        data.put("name", name);
                        data.put("description", description);
                        Log.d("makeListActivity", name + "2");
                        Map<String, Object> lists = new HashMap<>();
                        lists.put(name, data);
                        mDatabase.child(user).child("lists").setValue(lists)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("makeListActivity", "List added successfully1");
                                    Toast.makeText(getApplicationContext(),
                                                    "List creation successful!",
                                                    Toast.LENGTH_LONG)
                                            .show();

                                })
                                .addOnFailureListener(e -> {
                                    Log.e("makeListActivity", "Error adding list1", e);
                                });
                    }
                }

            }
        });

        goToLists();
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
