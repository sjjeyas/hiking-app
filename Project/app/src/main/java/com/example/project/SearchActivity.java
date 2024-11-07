package com.example.project;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.example.project.classes.Trail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SearchActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private List<Trail> trailList = new ArrayList<>();
    private TrailAdapter trailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        FirebaseApp.initializeApp(this);
        mDatabase = FirebaseDatabase.getInstance().getReference("trails");

        Log.d("SearchActivity", "OnCreate() called");

        fetchTrailListFromFirebase();

        ListView searchList = findViewById(R.id.searchList);
        trailAdapter = new TrailAdapter(this, trailList);
        searchList.setAdapter(trailAdapter);
        Log.d("SearchActivity", "Adapter set with trailList of size: " + trailAdapter.getCount());

        SearchView sv = findViewById(R.id.searchView);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                trailAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    private void fetchTrailListFromFirebase() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                trailList.clear();

                // Loop through each child node in "trails"
                for (DataSnapshot trailSnapshot : dataSnapshot.getChildren()) {
                    // Convert each entry to a Trail object
                    Trail trail = trailSnapshot.getValue(Trail.class);
                    if (trail != null) {
                        trailList.add(trail);
                    }
                }

                trailAdapter.notifyDataSetChanged();
                Log.d("SearchActivity", "Loaded trails from Firebase: " + trailList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("SearchActivity", "loadPost:onCancelled", databaseError.toException());
                Toast.makeText(SearchActivity.this, "Failed to load trails.", Toast.LENGTH_SHORT).show();
            }
        });
        Log.d("SearchActivity", "fetched trails from Firebase");

//        // Listen for changes to the "trails" node
//        mDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                // Clear the previous list to avoid duplicates
//                trailList.clear();
//
//                // Loop through the data snapshot and get each trail
//                for (DataSnapshot trailSnapshot : dataSnapshot.getChildren()) {
//                    Trail trail = trailSnapshot.getValue(Trail.class);
//                    if (trail != null) {
//                        trailList.add(trail);
//                    }
//                }
//
//                // Notify the adapter that the data has changed
//                trailAdapter.notifyDataSetChanged();
//                Log.d("SearchActivity", "Loaded trails from Firebase: " + trailList.size());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.w("SearchActivity", "loadPost:onCancelled", databaseError.toException());
//                Toast.makeText(SearchActivity.this, "Failed to load trails.", Toast.LENGTH_SHORT).show();
//            }
//        });
//        Log.d("SearchActivity", "fetched trails from Firebase");
    }

}