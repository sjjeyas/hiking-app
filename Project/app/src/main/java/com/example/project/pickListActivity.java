package com.example.project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class pickListActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private String user;
    private FirebaseAuth mAuth;
    private Button back;
    private String displayname;
    private TextView dNameView;
    private TextView listsView;
    private Button newList;
    private LinearLayout listdisplay;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mylists);

        Toolbar toolbar = findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        String u = getIntent().getStringExtra("user");
        if (u != null){
            user = u;
        }else {
            user = mAuth.getCurrentUser().getUid();
        }

        dNameView = findViewById(R.id.user);
        //listsView = findViewById(R.id.mylists);
        listdisplay = (LinearLayout) findViewById(R.id.trailslayout);
        back = (Button) findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProfile();
            }
        });
        newList = (Button) findViewById(R.id.newList_button);
        newList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeList();
            }
        });

        DatabaseReference names  = FirebaseDatabase.getInstance().getReference("users");
        Map<String, String> data = new HashMap<>();
        names.child(user).child("name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()){
                    Log.e("pickListActivity", "Error getting data", task.getException());
                }else{
                    if(task.getResult().getValue() != null){
                        displayname = (String) task.getResult().getValue();
                        Log.e("pickListActivity", "Lists for name " + displayname);
                        dNameView.setText(String.valueOf(displayname+"'s Lists"));
                    }
                    else{
                        Log.e("pickListActivity", "Error getting data", task.getException());
                    }
                }

            }
        });
        names.child(user).child("lists").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()){
                    Log.e("pickListActivity", "Error getting data", task.getException());
                }else{
                    if(task.getResult().getValue() != null){
                        Map<String, Object> lists = (Map<String, Object>) task.getResult().getValue();
                        Log.e("pickListActivity", String.valueOf(lists));

                        String r = "";
                        List<String> TLists = new ArrayList<>();
                        for (Map.Entry<String, Object> entry : lists.entrySet()){
                            HashMap<String, Object> reviewBody = (HashMap<String, Object>) entry.getValue(); // Review text
                            String name = (String) reviewBody.get("name");
                            r = name;
                            TLists.add(r);
                        }
                        for (String l : TLists){
                            TextView newTextView = new TextView(getApplicationContext());

                            newTextView.setText(l);
                            newTextView.setHeight(175);
                            newTextView.setTextColor(R.color.black);
                            newTextView.setTextSize(20);
                            newTextView.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    addtoList(l);
                                    Log.e("pickListActivity", "Error getting data", task.getException());
                                }
                            });
                            listdisplay.addView(newTextView);
                        }
                        //listsView.setText(r);
                        //listsView.setMovementMethod(new ScrollingMovementMethod());
                    }
                    else{
                        Log.e("pickListActivity", "Error getting data", task.getException());
                    }
                }

            }
        });
    }

    private void addtoList(String l){
        DatabaseReference names  = FirebaseDatabase.getInstance().getReference("users");
        Map<String, String> data = new HashMap<>();
        Log.e("pickListActivity", l);
        names.child(user).child("name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()){
                    Log.e("pickListActivity", "Error getting data", task.getException());
                }else{
                    if(task.getResult().getValue() != null){
                        displayname = (String) task.getResult().getValue();
                        Log.e("pickListActivity", "Writing for name " + displayname);
                        String trailname = getIntent().getStringExtra("trailname");
                        mDatabase = FirebaseDatabase.getInstance().getReference("users");
                        mDatabase.child(user).child("lists").child(l).child("trails").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (!task.isSuccessful()) {
                                    Log.e("pickListActivity", "Error getting data", task.getException());
                                } else {
                                    if (task.getResult().getValue() != null){
                                        Map<String, Object> results = (Map<String, Object>) task.getResult().getValue();
                                        results.put(trailname, true);
                                        mDatabase.child(user).child("lists").child(l).child("trails").setValue(results)
                                                .addOnSuccessListener(aVoid -> {
                                                    Log.d("pickListActivity", "Trail added successfully");
                                                    Toast.makeText(getApplicationContext(),
                                                                    "Added trail " + trailname + " to " + l,
                                                                    Toast.LENGTH_LONG)
                                                            .show();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("pickListActivity", "Error adding trail", e);
                                                });
                                        //
                                    }else{
                                        Map<String, Object> results = new HashMap<>();
                                        results.put(trailname, true);
                                        mDatabase.child(user).child("lists").child(l).child("trails").setValue(results)
                                                .addOnSuccessListener(aVoid -> {
                                                    Log.d("pickListActivity", "Trail added successfully");
                                                    Toast.makeText(getApplicationContext(),
                                                                    "Added trail " + trailname + " to " + l,
                                                                    Toast.LENGTH_LONG)
                                                            .show();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("pickListActivity", "Error adding trail", e);
                                                    Toast.makeText(getApplicationContext(),
                                                                    "Failed to add trail: " + trailname + " to " + l,
                                                                    Toast.LENGTH_LONG)
                                                            .show();
                                                });
                                    }

                                }
                            }
                        });
                    }
                    else{
                        Log.e("pickListActivity", "Error getting data", task.getException());
                    }
                }

            }
        });
        goToProfile();
    }

    private void goToProfile(){
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("user", user);
        this.startActivity(intent);
    }
    private void goToList(String l){
        Intent intent = new Intent (this, ListActivity.class);
        intent.putExtra("listname", l);
        intent.putExtra("user", user);
        this.startActivity(intent);
    }
    private void makeList(){
        Intent intent = new Intent(this, makeListActivity.class);
        intent.putExtra("user", user);
        this.startActivity(intent);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navbar, menu); // Inflate your menu resource
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.d("pickListActivity", "Menu item clicked with ID: " + id); // Log menu item clicks

        if (id == R.id.action_logout) {
            Log.d("pickListActivity", "Logout clicked");
            mAuth.signOut();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            //send login success toast message
            Toast.makeText(this, "Logout Successful.", Toast.LENGTH_SHORT).show();

            return true;
        } else if (id == R.id.action_login) {
            Log.d("pickListActivity", "Login button clicked");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_home) {
            Log.d("pickListActivity", "Home button clicked");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_profile) {
            Log.d("pickListActivity", "Profile button clicked");
            Intent intent = new Intent(this, ProfileActivity.class);
            String userID ="";
            userID = FirebaseAuth.getInstance().getUid();
            intent.putExtra("user", userID);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_trailsearch) {
            Log.d("pickListActivity", "Search button clicked");
            Intent intent = new Intent(this, TrailSearchActivity.class);
            String userID ="";
            userID = FirebaseAuth.getInstance().getUid();
            intent.putExtra("user", userID);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_friends) {
            Log.d("pickListActivity", "Friends button clicked");
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
