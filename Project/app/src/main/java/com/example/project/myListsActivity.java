package com.example.project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
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

public class myListsActivity extends AppCompatActivity {
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
                    Log.e("myListsActivity", "Error getting data", task.getException());
                }else{
                    if(task.getResult().getValue() != null){
                        displayname = (String) task.getResult().getValue();
                        Log.e("myListsActivity", "Lists for name " + displayname);
                        dNameView.setText(String.valueOf(displayname+"'s Lists"));
                    }
                    else{
                        Log.e("myListsActivity", "Error getting data", task.getException());
                    }
                }

            }
        });
        names.child(user).child("lists").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()){
                    Log.e("myListsActivity", "Error getting data", task.getException());
                }else{
                    if(task.getResult().getValue() != null){
                        Map<String, Object> lists = (Map<String, Object>) task.getResult().getValue();
                        Log.e("myListsActivity", String.valueOf(lists));

                        String r = "";
                        List<String> TLists = new ArrayList<>();
                        for (Map.Entry<String, Object> entry : lists.entrySet()){
                            HashMap<String, Object> reviewBody = (HashMap<String, Object>) entry.getValue(); // Review text
                            String name = (String) reviewBody.get("name");
                            TLists.add(name);
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
                                    goToList(l);
                                }
                            });
                            listdisplay.addView(newTextView);
                        }
                        //listsView.setText(r);
                        //listsView.setMovementMethod(new ScrollingMovementMethod());
                    }
                    else{
                        Log.e("myListsActivity", "Error getting data", task.getException());
                    }
                }

            }
        });
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
