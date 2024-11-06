package com.example.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.AuthKt;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
//    Intent intent = getIntent();
//    String UID = intent.getStringExtra("UID");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//        setContentView(R.layout.activity_search);
//        setContentView(R.layout.activity_main);
        Intent intent = new Intent(MainActivity.this, TrailActivity.class);
        startActivity(intent);



//        // Find toolbars by ID
//        Toolbar guestToolbar = findViewById(R.id.guestToolbar);
//        Toolbar userToolbar = findViewById(R.id.userToolbar);
//
//        // Enable the correct toolbar based on if the user is active
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//
//
//        // Set the toolbar as the action bar
//        if (currentUser != null) {
//            userToolbar.setVisibility(View.VISIBLE);
//        } else {
//            guestToolbar.setVisibility(View.VISIBLE);
//        }
//
//        View placeholderText = findViewById(R.id.mainContainer);
//        ConstraintLayout constraintLayout = findViewById(R.id.main);
//        ConstraintSet constraintSet = new ConstraintSet();
//        constraintSet.clone(constraintLayout);
//
//        if (currentUser != null) {
//            constraintSet.connect(placeholderText.getId(), ConstraintSet.TOP, userToolbar.getId(), ConstraintSet.BOTTOM);
//        } else {
//            constraintSet.connect(placeholderText.getId(), ConstraintSet.TOP, guestToolbar.getId(), ConstraintSet.BOTTOM);
//        }
//
//        // Apply the updated constraints
//        constraintSet.applyTo(constraintLayout);
    }
}