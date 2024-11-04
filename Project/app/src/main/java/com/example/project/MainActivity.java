package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.project.classes.User;

public class MainActivity extends AppCompatActivity {
    Intent intent = getIntent();
    String UID = intent.getStringExtra("UID");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}