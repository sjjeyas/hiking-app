package com.example.project;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.project.classes.Trail;

import java.util.ArrayList;
import java.util.List;

public class TrailAdapter extends ArrayAdapter<Trail> {
    private List<Trail> originalTrails;
    private List<Trail> filteredTrails;

    public TrailAdapter(Context context, List<Trail> trails) {
        super(context, 0, trails);
        this.originalTrails = trails;
        this.filteredTrails = new ArrayList<>(trails); // Initialize with all trails
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_trail, parent, false);
        }

        Trail trail = getItem(position);

        if (trail != null) {
            TextView trailName = convertView.findViewById(R.id.trailName);
            TextView trailLocation = convertView.findViewById(R.id.trailLocation);
            TextView trailDescription = convertView.findViewById(R.id.trailDescription);
            TextView trailRating = convertView.findViewById(R.id.trailRating);

            trailName.setText(trail.getName());
            trailLocation.setText(trail.getLocation());
            trailDescription.setText(trail.getDescription());
            trailRating.setText(trail.getDifficultyString());

            Log.d("TrailAdapter", "Binding item at position: " + position + " with name: " + trail.getName());
        }
        return convertView;
    }
    @Override
    public int getCount() {
        return filteredTrails.size();
    }

    @Override
    public Trail getItem(int position) {
        return filteredTrails.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                if (constraint == null || constraint.length() == 0) {
                    // If no filter, return the original list
                    results.values = originalTrails;
                    results.count = originalTrails.size();
                } else {
                    // Filter the list based on the trail name
                    List<Trail> filteredList = new ArrayList<>();
                    for (Trail trail : originalTrails) {
                        if (trail.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            filteredList.add(trail);
                        }
                    }
                    results.values = filteredList;
                    results.count = filteredList.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                // Update the filtered list with the results
                filteredTrails = (List<Trail>) results.values;
                notifyDataSetChanged();
            }
        };
    }

}