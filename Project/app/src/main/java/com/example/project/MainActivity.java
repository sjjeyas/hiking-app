package com.example.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.AuthKt;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Toolbar guestToolbar;
    private Toolbar userToolbar;
    private FirebaseAuth mAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        // Find toolbars by ID
        guestToolbar = findViewById(R.id.guestToolbar);
        userToolbar = findViewById(R.id.userToolbar);

        // Get the current user
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Show the appropriate toolbar based on login status
        updateToolbarBasedOnLoginStatus(currentUser);

        View placeholderText = findViewById(R.id.mainContainer);
        ConstraintLayout constraintLayout = findViewById(R.id.main);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);

        if (currentUser != null) {
            constraintSet.connect(placeholderText.getId(), ConstraintSet.TOP, userToolbar.getId(), ConstraintSet.BOTTOM);
        } else {
            constraintSet.connect(placeholderText.getId(), ConstraintSet.TOP, guestToolbar.getId(), ConstraintSet.BOTTOM);
        }

        // Apply the updated constraints
        constraintSet.applyTo(constraintLayout);

        // Get a handle to the fragment and register the callback.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragmentContainer);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Set a default location (e.g., New York City) and zoom level
        LatLng defaultLocation = new LatLng(40.7128, -74.0060); // New York City
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10));

        // Add a marker at the default location
        googleMap.addMarker(new MarkerOptions()
                .position(defaultLocation)
                .title("Default Location"));
    }

    private void updateToolbarBasedOnLoginStatus(FirebaseUser currentUser) {
        if (currentUser != null) {
            // User is logged in
            userToolbar.setVisibility(View.VISIBLE);
            guestToolbar.setVisibility(View.GONE);
            setSupportActionBar(userToolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }

        } else {
            // User is not logged in
            guestToolbar.setVisibility(View.VISIBLE);
            userToolbar.setVisibility(View.GONE);
            setSupportActionBar(guestToolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }
        invalidateOptionsMenu(); // Force menu to refresh
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            getMenuInflater().inflate(R.menu.navbar, menu); // Load user menu
            Log.d("MainActivity", "User menu loaded");
        } else {
            getMenuInflater().inflate(R.menu.guestnavbar, menu); // Load guest menu
            Log.d("MainActivity", "Guest menu loaded");
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.d("MainActivity", "Menu item clicked with ID: " + id); // Log menu item clicks

        if (id == R.id.action_logout) {
            Log.d("MainActivity", "Logout clicked");
            mAuth.signOut();
            // Update the toolbar to show guest navbar
            updateToolbarBasedOnLoginStatus(null);

            return true;
        } else if (id == R.id.action_login) {
            Log.d("MainActivity", "Login button clicked");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

