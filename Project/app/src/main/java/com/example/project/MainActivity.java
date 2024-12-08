package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import com.example.project.MainActivityLocation;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Toolbar guestToolbar;
    private Toolbar userToolbar;
    private FirebaseAuth mAuth;

    private LatLng defaultLocation;
    private LatLng userlocation;
    private DatabaseReference databaseReference;
    private DatabaseReference zipLatLngRef;
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
        zipLatLngRef = FirebaseDatabase.getInstance().getReference("zipLatLng");

        // Show the appropriate toolbar based on login status
        updateToolbarBasedOnLoginStatus(currentUser);

        defaultLocation = new LatLng(34.03130461195253, -118.28911693516095);

//        defaultLocation = MainActivityLocation.getLocationFromZipcode(this, "90007");
        // Set default location
//        fetchLocationFromZipcode("90007", location -> {
//            defaultLocation = location;
//        });

        //set user location based off if user is logged in or not
//        if (currentUser != null) {
//            String userId = currentUser.getUid();
//            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
//
//            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot snapshot) {
//                    if (snapshot.exists()) {
//                        String zipcode = snapshot.child("zipcode").getValue(String.class); // Retrieve zipcode
//                        Log.d("MainActivity", "User zipcode pulled from database: " + zipcode);
//
//                        if (zipcode != null) {
//                            userlocation = MainActivityLocation.getLocationFromZipcode(MainActivity.this, zipcode);
//                            Log.d("MainActivity", "User location set from zipcode: " + userlocation);
//
//                            // Update the map after user location is set
//                            if (mMap != null) {
//                                updateMapWithNewLocation(userlocation);
//                            }
//                        } else {
//                            Log.e("MainActivity", "Zipcode is null for user.");
//                        }
//                    } else {
//                        Log.e("MainActivity", "No user data found.");
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError error) {
//                    Log.e("MainActivity", "Database error: " + error.getMessage());
//                }
//            });
//        } else {
//            Log.d("MainActivity", "No user is logged in. Using default location.");
//            userlocation = null;
//        }

        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String zipcode = snapshot.child("zipcode").getValue(String.class);
                        Log.d("MainActivity", "User zipcode pulled from database: " + zipcode);

                        if (zipcode != null) {
                            fetchLocationFromZipcode(zipcode, location -> {
                                if (location != null) {
                                    userlocation = location;
                                    Log.d("MainActivity", "User location set from zipcode: " + userlocation);

                                    if (mMap != null) {
                                        updateMapWithNewLocation(userlocation);
                                    }
                                } else {
                                    Log.e("MainActivity", "Location fetch returned null.");
                                }
                            });
                        } else {
                            Log.e("MainActivity", "Zipcode is null for user.");
                        }
                    } else {
                        Log.e("MainActivity", "No user data found.");
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.e("MainActivity", "Database error: " + error.getMessage());
                }
            });
        } else {
            Log.d("MainActivity", "No user is logged in. Using default location.");
            userlocation = null;
        }


        // Get a handle to the fragment and register the callback.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragmentContainer);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Set up button listeners
//        searchButton.setOnClickListener(v -> {
//            String newZipcode = zipInput.getText().toString();
//            if (!newZipcode.isEmpty()) {
//                fetchLocationFromZipcode(newZipcode, newLocation -> {
//                    if (newLocation != null) {
//                        updateMapWithNewLocation(newLocation);
//                    } else {
//                        Toast.makeText(this, "Invalid zip code.", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            } else {
//                Toast.makeText(this, "Please enter a zip code.", Toast.LENGTH_SHORT).show();
//            }
//        });
        searchButton.setOnClickListener(v -> {
            String newZipcode = zipInput.getText().toString();
            if (!newZipcode.isEmpty()) {
                zipLatLngRef.child(newZipcode).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Zipcode exists in the database; fetch its location
                            Double latitude = snapshot.child("latitude").getValue(Double.class);
                            Double longitude = snapshot.child("longitude").getValue(Double.class);
                            if (latitude != null && longitude != null) {
                                LatLng location = new LatLng(latitude, longitude);
                                updateMapWithNewLocation(location);
                            } else {
                                Toast.makeText(MainActivity.this, "Invalid location data for ZIP code: " + newZipcode, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Zipcode does not exist in the database; use Geocoder to fetch the location
                            LatLng newLocation = MainActivityLocation.getLocationFromZipcode(MainActivity.this, newZipcode);
                            if (newLocation != null) {
                                // Add the new zipcode and its coordinates to the database
                                zipLatLngRef.child(newZipcode).child("latitude").setValue(newLocation.latitude);
                                zipLatLngRef.child(newZipcode).child("longitude").setValue(newLocation.longitude);
                                Log.d("MainActivity", "Added new ZIP code to database: " + newZipcode);

                                // Update the map with the new location
                                updateMapWithNewLocation(newLocation);
                            } else {
                                Toast.makeText(MainActivity.this, "Unable to fetch location for ZIP code: " + newZipcode, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("MainActivity", "Database error: " + error.getMessage());
                        Toast.makeText(MainActivity.this, "Error accessing location data.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Please enter a ZIP code.", Toast.LENGTH_SHORT).show();
            }
        });


        currentLocationButton.setOnClickListener(v -> {
            if (userlocation != null) {
                updateMapWithNewLocation(userlocation);
            }
            else {
                updateMapWithNewLocation(defaultLocation);
            }
        });
    }


    // Fetch latitude and longitude from "zipLatLng" database
    private void fetchLocationFromZipcode(String zipcode, OnLocationFetchedListener listener) {
        zipLatLngRef.child(zipcode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Double latitude = snapshot.child("latitude").getValue(Double.class);
                    Double longitude = snapshot.child("longitude").getValue(Double.class);

                    if (latitude != null && longitude != null) {
                        listener.onLocationFetched(new LatLng(latitude, longitude));
                        Log.e("MainActivity", "Latitude is: " + latitude + "Longitude is: " +longitude);

                    } else {
                        Log.e("MainActivity", "Latitude or Longitude is null for ZIP code: " + zipcode);
                        Toast.makeText(MainActivity.this, "Invalid location data for ZIP code: " + zipcode, Toast.LENGTH_SHORT).show();
                        listener.onLocationFetched(null);
                    }
                } else {
                    Log.e("MainActivity", "ZIP code not found in database: " + zipcode);
                    Toast.makeText(MainActivity.this, "ZIP code not found: " + zipcode, Toast.LENGTH_SHORT).show();
                    listener.onLocationFetched(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("MainActivity", "Database error: " + error.getMessage());
                Toast.makeText(MainActivity.this, "Error accessing location data.", Toast.LENGTH_SHORT).show();
                listener.onLocationFetched(null);
            }
        });
    }


    interface OnLocationFetchedListener {
        void onLocationFetched(LatLng location);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (userlocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userlocation, 10));
            fetchTrailsAndDisplayMarkers(userlocation); //displays correct markers of nearby trails
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10));
            fetchTrailsAndDisplayMarkers(defaultLocation); //displays correct markers of nearby trails
        }
    }

//    void updateMapWithNewLocation(LatLng location) {
//        mMap.clear(); // Clear existing markers
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10));
//        fetchTrailsAndDisplayMarkers(location);
//    }

    void updateMapWithNewLocation(LatLng location) {
        if (location == null) {
            Log.e("MainActivity", "Cannot update map with a null location.");
            Toast.makeText(this, "Unable to update map: Invalid location.", Toast.LENGTH_SHORT).show();
            return; // Exit early to prevent further execution
        }

        mMap.clear(); // Clear existing markers
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10));
        fetchTrailsAndDisplayMarkers(location);
    }

    void fetchTrailsAndDisplayMarkers(LatLng location) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Trail> trails = new ArrayList<>();

                for (DataSnapshot trailSnapshot : dataSnapshot.getChildren()) {
                    try {
                        Trail trail = trailSnapshot.getValue(Trail.class); // Deserialize each trail
                        if (trail != null) {
                            trails.add(trail);
                        }
                    } catch (Exception e) {
                        Log.e("MainActivity", "Error parsing trail: " + e.getMessage());
                    }
                }

                // Calculate distances and sort trails
                List<Trail> trailsWithDistances = MainActivityLocation.getTrailsWithDistances(trails, location);
                List<Trail> closestTrails = MainActivityLocation.getClosestTrails(trailsWithDistances, 5);

                // Display markers for the closest trails
                LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                for (Trail trail : closestTrails) {
                    LatLng trailLocation = new LatLng(trail.latitude, trail.longitude);
                    mMap.addMarker(new MarkerOptions()
                            .position(trailLocation)
                            .title(trail.name + " (" + trail.location + ")"));
                    boundsBuilder.include(trailLocation);
                }

                // Adjust the camera to fit all markers if there are any trails
                if (!closestTrails.isEmpty()) {
                    LatLngBounds bounds = boundsBuilder.build();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                } else {
                    Log.d("MainActivity", "No trails found to display markers.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("MainActivity", "Database error: " + databaseError.getMessage());
            }
        });
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
            userlocation = null;

            //send login success toast message
            Toast.makeText(this, "Logout Successful.", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }else if (id == R.id.action_signup) {
            Log.d("MainActivity", "Signup clicked");

            // Update the toolbar to show guest navbar
            updateToolbarBasedOnLoginStatus(null);
            userlocation = null;
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_login) {
            Log.d("MainActivity", "Login button clicked");
            Intent intent = new Intent(this, LoginActivity.class);
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
        } else if (id == R.id.action_groupsearch) {
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

    public void setGeocoder(Geocoder mockGeocoder) {
    }
}

