package com.example.project;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.example.project.classes.CheckFunctions;
import com.example.project.classes.Group;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
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
    private EditText dateInput;
    private EditText timeInput;
    private CheckFunctions cf;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makegroup);
        String u = getIntent().getStringExtra("user");

        groupNameInput = findViewById(R.id.groupNameInput);
        trailNameInput = findViewById(R.id.trailNameInput);
        capacityInput = findViewById(R.id.capacityInput);
        dateInput = findViewById(R.id.dateInput);
        timeInput = findViewById(R.id.timeInput);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        Toolbar toolbar = findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        cf = new CheckFunctions(user, user);

        if (u != null){
            user = u;
        }else {
            user = mAuth.getCurrentUser().getUid();
        }

        dateInput.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(makeGroupActivity.this, (view, year, month, dayOfMonth) -> {
                String selectedDate = (month + 1) + "/" + dayOfMonth + "/" + year;
                dateInput.setText(selectedDate);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });
        timeInput.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new TimePickerDialog(makeGroupActivity.this, (view, hourOfDay, minute) -> {
                int hour = hourOfDay % 12;
                if (hour == 0) hour = 12;
                String period = (hourOfDay < 12) ? "AM" : "PM";

                String selectedTime = String.format("%02d:%02d %s", hour, minute, period);
                timeInput.setText(selectedTime);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();        });

        back = (Button) findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToGroups();
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
    private void goToGroup(String groupName){

        Intent intent = new Intent(this, GroupActivity.class);

        intent.putExtra("groupname", groupName);
        intent.putExtra("user", user);

        this.startActivity(intent);
    }
    private void goToGroups(){
        Intent intent = new Intent(this, GroupSearchActivity.class);

        intent.putExtra("user", user);

        this.startActivity(intent);
    }

    private void createGroup() {
        Map<String, Object> data = new HashMap<>();
        mDatabase.child("groups").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("makeGroupActivity", "Error getting data", task.getException());
                } else {
                    if (task.getResult().getValue() != null) {
                        Map<String, Object> results = (Map<String, Object>) task.getResult().getValue();

                        String name = groupNameInput.getText().toString();
                        String trail = trailNameInput.getText().toString();
                        /*
                        if (name.isEmpty() || trail.isEmpty() || capacityInput.getText().toString().isEmpty()) {
                            Toast.makeText(makeGroupActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                            return;
                        }
                         */

                        int capacity;
                        try {
                            capacity = Integer.parseInt(capacityInput.getText().toString());
                        } catch (NumberFormatException e) {
                            Toast.makeText(makeGroupActivity.this, "Please enter a number for capacity.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String date = dateInput.getText().toString();
                        String time = timeInput.getText().toString();

                        data.put("name", name);
                        data.put("trail", trail);
                        data.put("capacity", capacity);
                        data.put("date", date);
                        data.put("time", time);

                        if (!cf.validGroup(data)){
                            Log.e("CheckFunction", String.valueOf(data));
                            Toast.makeText(makeGroupActivity.this, "Please fill out the entire group info1", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Log.d("makeGroupActivity", "HERE AT 1");
                        mDatabase.child("groups").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (!task.isSuccessful()) {
                                    Log.e("makeListActivity", "Error getting data2", task.getException());
                                } else {
                                    if (task.getResult().getValue() != null) {
                                        results.put(name, data);
                                        mDatabase.child("groups").setValue(results).addOnSuccessListener(aVoid -> {
                                                    Log.d("makeGroupActivity", "Group added!");
                                                    Toast.makeText(getApplicationContext(),
                                                                    "Group creation successful!",
                                                                    Toast.LENGTH_LONG)
                                                            .show();
                                                    goToGroup(groupNameInput.getText().toString());
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("makeGroupActivity", "Error adding group", e);
                                                });
                                    } else {
                                        Map<String, Object> results = new HashMap<>();
                                        results.put(name, data);
                                        mDatabase.child("groups").child(name).setValue(results)
                                                .addOnSuccessListener(aVoid -> {
                                                    Log.d("makeGroupReviewActivity", "Group added successfully3");
                                                    Toast.makeText(getApplicationContext(),
                                                                    "Group creation successful!",
                                                                    Toast.LENGTH_LONG)
                                                            .show();
                                                    goToGroup(groupNameInput.getText().toString());
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("makeListActivity", "Error adding list3", e);
                                                });

                                    }
                                }
                            }
                        });
                    } else {
                        Log.e("makeGroupActivity", "Error getting data", task.getException());
                        String name = groupNameInput.getText().toString();
                        String trail = trailNameInput.getText().toString();
                        int capacity;
                        try {
                            capacity = Integer.parseInt(capacityInput.getText().toString());
                        } catch (NumberFormatException e) {
                            Toast.makeText(makeGroupActivity.this, "Please enter a number for capacity.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        data.put("name", name);
                        data.put("trail", trail);
                        data.put("capacity", capacity);

                        if (!cf.validGroup(data)){
                            Toast.makeText(makeGroupActivity.this, "Please fill out the entire group info2", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Log.d("makeGroupActivity", "HERE AT 2");
                        Map<String, Object> groups = new HashMap<>();
                        groups.put(name, data);
                        mDatabase.setValue(groups)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("makeGroupActivity", "Group added successfully1");
                                    Toast.makeText(getApplicationContext(),
                                                    "Group creation successful!",
                                                    Toast.LENGTH_LONG)
                                            .show();
                                    goToGroup(groupNameInput.getText().toString());
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("makeGroupActivity", "Error adding group", e);
                                });
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
            Intent intent = new Intent(this, TrailSearchActivity.class);
            String userID ="";
            userID = FirebaseAuth.getInstance().getUid();
            intent.putExtra("user", userID);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_friends) {
            Log.d("MainActivity", "Friends button clicked");
            Intent intent = new Intent(this, FriendsActivity.class);
            String userID = "";
            userID = FirebaseAuth.getInstance().getUid();
            intent.putExtra("user", userID);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_groupsearch) {
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
