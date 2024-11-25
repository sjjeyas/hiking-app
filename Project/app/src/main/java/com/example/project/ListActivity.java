package com.example.project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import java.util.Set;

public class ListActivity extends AppCompatActivity {
    private String user;
    private String listname;
    private TextView nameView;
    private Button back;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private LinearLayout trailslist;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        nameView = findViewById(R.id.listname);
        mAuth = FirebaseAuth.getInstance();

        trailslist = findViewById(R.id.trailslayout);

        String u = getIntent().getStringExtra("user");
        if (u != null){
            user = u;
        }else if (mAuth != null){
            user = mAuth.getCurrentUser().getUid();
        }else {
            user = "GKV2jTHo8mVcoaBTXbuP9whvCys2";
        }
        String n = getIntent().getStringExtra("listname");
        if (n != null){
            listname = n;
        }
        nameView.setText(listname);

        back = (Button) findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLists();
            }
        });
        Log.e("ListActivity", "Db load of " + listname);
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mDatabase.child(user).child("lists").child(listname).child("trails").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
    private void goToLists(){
        Intent intent = new Intent(this, myListsActivity.class);
        intent.putExtra("user", user);
        this.startActivity(intent);
    }
    private void goToTrail(String t){
        Intent intent = new Intent(this, TrailActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("trailname", t);
        this.startActivity(intent);
    }
}
