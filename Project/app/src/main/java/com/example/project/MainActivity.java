package com.example.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.project.classes.Trail;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.AuthKt;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Toolbar guestToolbar;
    private Toolbar userToolbar;
    private FirebaseAuth mAuth;

    private LatLng defaultLocation;
    private DatabaseReference databaseReference;
    GoogleMap mMap;

    private EditText zipInput;
    private Button searchButton;
    private Button currentLocationButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        // Find toolbars by ID
        guestToolbar = findViewById(R.id.guestToolbar);
        userToolbar = findViewById(R.id.userToolbar);

        // Initialize zip input and buttons
        zipInput = findViewById(R.id.editZip);
        searchButton = findViewById(R.id.search_button);
        currentLocationButton = findViewById(R.id.default_location);

        // Get the current user and set database for trails
        FirebaseUser currentUser = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("trails");


        // Show the appropriate toolbar based on login status
        updateToolbarBasedOnLoginStatus(currentUser);

//        // Affix navbar (NO LONGER NEEDED, HARD CODED MARGIN)
//        ConstraintLayout constraintLayout = findViewById(R.id.main);
//        ConstraintSet constraintSet = new ConstraintSet();
//        constraintSet.clone(constraintLayout);
//
//        if (currentUser != null) {
//            constraintSet.connect(R.id.mainContainer, ConstraintSet.TOP, R.id.userToolbar, ConstraintSet.BOTTOM);
//        } else {
//            constraintSet.connect(R.id.mainContainer, ConstraintSet.TOP, R.id.guestToolbar, ConstraintSet.BOTTOM);
//        }
//
//        // Apply the updated constraints
//        constraintSet.applyTo(constraintLayout);

        // Get a handle to the fragment and register the callback.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragmentContainer);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Set up button listeners
        searchButton.setOnClickListener(v -> {
            String newZipcode = zipInput.getText().toString();
            if (!newZipcode.isEmpty()) {
                LatLng newLocation = getLocationFromZipcode(newZipcode);
                if (newLocation != null) {
                    updateMapWithNewLocation(newLocation);
                } else {
                    Toast.makeText(this, "Invalid zip code.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter a zip code.", Toast.LENGTH_SHORT).show();
            }
        });

        currentLocationButton.setOnClickListener(v -> {
            if (defaultLocation != null) {
                updateMapWithNewLocation(defaultLocation);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Get default location from the zipcode
        String zipcode = "90007";
        defaultLocation = getLocationFromZipcode(zipcode);

        if (defaultLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10));
            fetchTrailsAndDisplayMarkers(defaultLocation); //displays correct markers of nearby trails
        } else {
            Toast.makeText(this, "Unable to find default location.", Toast.LENGTH_SHORT).show();
        }
    }

    void updateMapWithNewLocation(LatLng location) {
        mMap.clear(); // Clear existing markers
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10));
        fetchTrailsAndDisplayMarkers(location);
    }


    void fetchTrailsAndDisplayMarkers(LatLng location) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Trail> trails = new ArrayList<>();
                LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

                for (DataSnapshot trailSnapshot : dataSnapshot.getChildren()) {
                    try {
                        Trail trail = trailSnapshot.getValue(Trail.class); // Deserialize each trail
                        if (trail != null) {
                            LatLng trailLocation = getLocationFromCity(trail.location); // Assume 'location' stores city
                            if (trailLocation != null) {
                                trail.zipcode = (int) calculateDistance(location, trailLocation); // Store distance temporarily
                                trails.add(trail);

                                // Add this location to the bounds
                                boundsBuilder.include(trailLocation);
                            }
                        }
                    } catch (Exception e) {
                        Log.e("TAG", "Error parsing trail: " + e.getMessage());
                    }
                }

                // Sort trails by distance and pick the closest 5
                Collections.sort(trails, Comparator.comparingInt(trail -> trail.zipcode));
                List<Trail> closestTrails = trails.subList(0, Math.min(trails.size(), 5));

                // Display markers for the closest trails
                for (Trail trail : closestTrails) {
                    LatLng trailLocation = getLocationFromCity(trail.location);
                    if (trailLocation != null) {
                        mMap.addMarker(new MarkerOptions()
                                .position(trailLocation)
                                .title(trail.name + " (" + trail.location + ")"));
                    }
                }

                // Adjust the camera to fit all markers if there are any trails
                if (!trails.isEmpty()) {
                    LatLngBounds bounds = boundsBuilder.build();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 9));
//                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50)); // 100 is padding
//                    mMap.animateCamera(CameraUpdateFactory.zoomBy(0.05f));
                } else {
                    Log.d("TAG", "No trails found to display markers.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TAG", "Database error: " + databaseError.getMessage());
            }
        });
    }

    LatLng getLocationFromZipcode(String zipcode) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(zipcode, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return new LatLng(address.getLatitude(), address.getLongitude());
            }
        } catch (IOException e) {
            Log.e("TAG", "Geocoder failed: " + e.getMessage());
        }
        return null;
    }

    private LatLng getLocationFromCity(String city) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(city, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return new LatLng(address.getLatitude(), address.getLongitude());
            }
        } catch (IOException e) {
            Log.e("TAG", "Geocoder failed for city: " + city + " - " + e.getMessage());
        }
        return null;
    }

    private double calculateDistance(LatLng start, LatLng end) {
        double earthRadius = 6371; // Radius of the Earth in kilometers
        double dLat = Math.toRadians(end.latitude - start.latitude);
        double dLng = Math.toRadians(end.longitude - start.longitude);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(start.latitude)) * Math.cos(Math.toRadians(end.latitude)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c; // Distance in kilometers
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

            //send login success toast message
            Toast.makeText(this, "Logout Successful.", Toast.LENGTH_SHORT).show();

            return true;
        } else if (id == R.id.action_login) {
            Log.d("MainActivity", "Login button clicked");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_signup) {
            Log.d("MainActivity", "Sign Up button clicked");
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
            return true;
//        } else if (id == R.id.action_home) {
//            Log.d("MainActivity", "Home button clicked");
//            Intent intent = new Intent(this, LoginActivity.class);
//            startActivity(intent);
//            return true;
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
        } else if (id == R.id.action_groupsearch) {
            Log.d("MainActivity", "Groups button clicked");
            Intent intent = new Intent(this, GroupActivity.class);
            String userID ="";
            userID = FirebaseAuth.getInstance().getUid();
            intent.putExtra("user", userID);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setGeocoder(Geocoder mockGeocoder) {
    }
}

