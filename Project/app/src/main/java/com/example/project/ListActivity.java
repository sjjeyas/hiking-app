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
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;
import java.util.Set;

public class ListActivity extends AppCompatActivity {
    private String user;
    private String listname;
    private TextView nameView;
    private Button back;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private LinearLayout trailslist;
    private String view;
    private ToggleButton perm;
    private String permission;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        nameView = findViewById(R.id.listname);
        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        trailslist = findViewById(R.id.trailslayout);

        String u = getIntent().getStringExtra("user");
        if (u != null){
            user = u;
        }else if (mAuth != null){
            user = mAuth.getCurrentUser().getUid();
        }else {
            user = "GKV2jTHo8mVcoaBTXbuP9whvCys2";
        }
        String v = getIntent().getStringExtra("view");
        if (v != null){
            view = v;
        }else {
            view = user;
        }
        String n = getIntent().getStringExtra("listname");
        if (n != null){
            listname = n;
        }
        nameView.setText(listname);

        perm = findViewById(R.id.permission_button);
        if (view.equals(user)){
            perm.setVisibility(View.VISIBLE);
        }else{
            perm.setVisibility(View.INVISIBLE);
        }
        perm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (perm.isChecked()) {
                    permission = "public";
                    changeVisibility();
                    Toast.makeText(getApplicationContext(),
                                    "List set to public!",
                                    Toast.LENGTH_LONG)
                            .show();
                } else {
                    permission = "private";
                    changeVisibility();
                    Toast.makeText(getApplicationContext(),
                                    "List set to private!",
                                    Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        back = (Button) findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLists();
            }
        });
        Log.e("ListActivity", "Db load of " + listname);
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mDatabase.child(view).child("lists").child(listname).child("trails").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()){
                    Log.e("ListActivity", "Error getting data1", task.getException());
                    TextView noTrails = new TextView(getApplicationContext());
                    noTrails.setText("No trails able to load!");
                    noTrails.setTextColor(R.color.black);
                    trailslist.addView(noTrails);
                    //
                }else{
                    if(task.getResult().getValue() != null){
                        Map<String, Object> trailsresults = (Map<String, Object>) task.getResult().getValue();
                        Log.e("ListActivity", String.valueOf(trailsresults));
                        if (trailsresults != null) {

                            String r = "";
                            Set<String> keys = trailsresults.keySet();
                            for (String k : trailsresults.keySet()){
                                TextView newTextView = new TextView(getApplicationContext());
                                Log.d("ListActivity", k);
                                newTextView.setText(String.valueOf(k));
                                newTextView.setHeight(175);
                                newTextView.setTextColor(R.color.black);
                                newTextView.setTextSize(20);
                                newTextView.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        goToTrail(k);
                                    }
                                });
                                trailslist.addView(newTextView);
                            }

                        } else {
                            Log.d("ListActivity", "No trail found");
                        }

                    }
                    else{
                        Log.e("ListActivity", "Error getting data2", task.getException());
                    }
                }

            }
        });
    }
    private void changeVisibility(){
        DatabaseReference list = FirebaseDatabase.getInstance().getReference("users");
        list.child(user).child("lists").child(listname).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()){
                    Log.e("ListActivity", "Error getting data for permission change1", task.getException());
                }else{
                    if(task.getResult().getValue() != null){
                        Map<String, Object> listinfo = (Map<String, Object>) task.getResult().getValue();
                        Log.e("ListActivity", String.valueOf(listinfo) + " data for permission change");
                        listinfo.put("permission", permission);
                        list.child(user).child("lists").child(listname).setValue(listinfo)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("ListActivity", "List permission changed successfully");

                                })
                                .addOnFailureListener(e -> {
                                    Log.e("ListActivity", "Error changing list permission", e);
                                });
                    }
                    else{
                        Log.e("ListActivity", "Error getting data for permission change2", task.getException());
                    }
                }
            }
        });
    }
    private void goToLists(){
        if (view.equals(user)){
            Intent intent = new Intent(this, myListsActivity.class);
            intent.putExtra("user", user);
            this.startActivity(intent);
        } else {
            Intent intent = new Intent(this, friendsListsActivity.class);
            intent.putExtra("user", view);
            this.startActivity(intent);
        }

    }
    private void goToTrail(String t){
        Intent intent = new Intent(this, TrailActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("trailname", t);
        this.startActivity(intent);
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
