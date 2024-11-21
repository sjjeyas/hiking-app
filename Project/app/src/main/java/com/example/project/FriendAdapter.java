package com.example.project;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class FriendAdapter extends ArrayAdapter<String> {
    private List<String> originalUsers; // Original list of users
    private List<String> filteredUsers; // Filtered list of users

    public FriendAdapter(Context context, List<String> users) {
        super(context, android.R.layout.simple_list_item_1, users);
        this.originalUsers = new ArrayList<>(users);
        this.filteredUsers = new ArrayList<>(users);
    }

    public void updateData(List<String> users) {
        this.originalUsers.clear();
        this.originalUsers.addAll(users);
        this.filteredUsers.clear();
        this.filteredUsers.addAll(users);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return filteredUsers.size();
    }

    @Override
    public String getItem(int position) {
        return filteredUsers.get(position);
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(getContext(), android.R.layout.simple_list_item_1, null);
        }
        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
        textView.setText(getItem(position));
        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    results.values = originalUsers;
                    results.count = originalUsers.size();
                } else {
                    List<String> filteredList = new ArrayList<>();
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (String user : originalUsers) {
                        if (user.toLowerCase().contains(filterPattern)) {
                            filteredList.add(user);
                        }
                    }

                    results.values = filteredList;
                    results.count = filteredList.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredUsers.clear();
                filteredUsers.addAll((List<String>) results.values);
                notifyDataSetChanged();
            }
        };
    }
}
