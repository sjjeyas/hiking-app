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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        fetchTrailListFromFirebase();

        ListView searchList = findViewById(R.id.searchList);

        trailAdapter = new TrailAdapter(this, new ArrayList<>());
        searchList.setAdapter(trailAdapter);

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

                for (DataSnapshot trailSnapshot : dataSnapshot.getChildren()) {
                    HashMap<String, String> trailMap = (HashMap<String, String>) trailSnapshot.getValue();
                    Trail trail = new Trail (trailMap.get("name"), trailMap.get("location"), trailMap.get("description"));

                    if (trail != null) {
                            trailList.add(trail);
                    }
                }

                trailAdapter.updateData(trailList);  // Update the adapter with the new data
                Log.d("SearchActivity", "Loaded trails from Firebase: " + trailList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("SearchActivity", "loadPost:onCancelled", databaseError.toException());
                Toast.makeText(SearchActivity.this, "Failed to load trails.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
