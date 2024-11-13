package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private Button register;
    private EditText emailTextView;
    private EditText passwordTextView;
    private EditText nameTextView;
    private EditText zipTextView;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        // taking instance of FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // initialising all views through id defined above
        emailTextView = findViewById(R.id.editusername);
        passwordTextView = findViewById(R.id.editPassword);
        nameTextView = findViewById(R.id.editName);
        zipTextView = findViewById(R.id.editZip);
        register = findViewById(R.id.signup_button);
        Log.d("SignUpActivity", "Started!!!");

        login = findViewById(R.id.switchLogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goLogin();
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUserAccount();
            }
        });

    }
    private void goLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
    private void createUserAccount() {
        String email, password;
        String zipcode;
        String name;
        email = emailTextView.getText().toString();
        password = passwordTextView.getText().toString();
        name = nameTextView.getText().toString();
        zipcode = zipTextView.getText().toString();

        // validations for input email and password
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter email!!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter password!!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // User created successfully
                        mDatabase = FirebaseDatabase.getInstance().getReference("users");
                        Map<String, String> data = new HashMap<>();
                        data.put("username", email);
                        data.put("name", name);
                        data.put("zipcode", zipcode);
                        String u = mAuth.getCurrentUser().getUid();
                        data.put("userID", u);
                        mDatabase.child(u).setValue(data)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("SignUpActivity", "User added successfully");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("SignUpActivity", "Error adding user", e);
                                });

                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(getApplicationContext(), "User created: " + user.getUid(), Toast.LENGTH_LONG)
                                .show();
                        return;
                    } else {
                        // If sign in fails, display a message to the user.
                        Exception exception = task.getException();
                        Toast.makeText(getApplicationContext(),"Error: " + exception.getMessage(), Toast.LENGTH_LONG)
                                .show();
                        return;
                    }
                });
    }
}
