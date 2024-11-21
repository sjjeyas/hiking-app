package com.example.project;

import android.content.Context;
import android.content.Intent;

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
    private String user;

    public TrailAdapter(Context context, List<Trail> trails, String u) {
        super(context, 0, trails);
        this.originalTrails = new ArrayList<>(trails);
        this.filteredTrails = new ArrayList<>(trails);
        this.user = u;
    }

    public void updateData(List<Trail> trails) {
        this.originalTrails.clear();
        this.originalTrails.addAll(trails);
        this.filteredTrails.clear();
        this.filteredTrails.addAll(trails);
        notifyDataSetChanged();  // Notify adapter to refresh the list view
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
        }

        convertView.setOnClickListener(view -> {
            Intent intent = new Intent(TrailAdapter.this.getContext(), TrailActivity.class);

            // Pass trail information to the detail activity
            assert trail != null;
            intent.putExtra("trailname", trail.getName());
            intent.putExtra("user", user);

            getContext().startActivity(intent);
        });
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
                    results.values = originalTrails;
                    results.count = originalTrails.size();
                } else {
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
                filteredTrails.clear();
                filteredTrails.addAll((List<Trail>) results.values);
                notifyDataSetChanged();
            }
        };
    }
}