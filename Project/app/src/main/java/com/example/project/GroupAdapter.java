package com.example.project;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.project.classes.Group;
import com.example.project.classes.Trail;

import java.util.ArrayList;
import java.util.List;

public class GroupAdapter extends ArrayAdapter<Group> {
    private List<Group> originalGroups;
    private List<Group> filteredGroups;
    private String user;

    public GroupAdapter(Context context, List<Group> groups, String u) {
        super(context, 0, groups);
        this.originalGroups = new ArrayList<>(groups);
        this.filteredGroups = new ArrayList<>(groups);
        this.user = u;
    }

    public void updateData(List<Group> groups) {
        this.originalGroups.clear();
        this.originalGroups.addAll(groups);
        this.filteredGroups.clear();
        this.filteredGroups.addAll(groups);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent){
        Log.e("GroupAdapter", "At Position: " + position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_group, parent, false);
        }
        Group group = getItem(position);
        if (group != null){
            Log.e("GroupAdapter", "Adapting group " + group.name);
            TextView groupName = convertView.findViewById(R.id.groupName);
            TextView groupTrail = convertView.findViewById(R.id.groupTrail);
            TextView groupCapacity = convertView.findViewById(R.id.groupCapacity);
            TextView groupMembers = convertView.findViewById(R.id.groupMembers);

            groupName.setText(group.name);
            groupTrail.setText(group.trail);
            groupCapacity.setText(getCapacityText(group));
            groupMembers.setText(getMembersText(group));
        }

        convertView.setOnClickListener(view ->{
            Intent intent = new Intent(GroupAdapter.this.getContext(), GroupActivity.class);

            assert group != null;
            intent.putExtra("groupname", group.name);
            intent.putExtra("user", user);

            getContext().startActivity(intent);
        });

        return convertView;
    }

    @Override
    public int getCount() {
        return filteredGroups.size();
    }

    @Override
    public Group getItem(int position) {
        return filteredGroups.get(position);
    }

    public String getCapacityText(Group group) {
        return String.format("%d/%d", group.members.size(), group.capacity);
    }

    public String getMembersText(Group group) {
        StringBuilder membersText = new StringBuilder();
        for (String member : group.members.keySet()){
            if (membersText.length() > 0) {
                membersText.append(", "); // Add a comma before appending next name
            }
            membersText.append(member);
        }

        return membersText.toString();
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                if (constraint == null || constraint.length() == 0) {
                    results.values = originalGroups;
                    results.count = originalGroups.size();
                } else {
                    List<Group> filteredList = new ArrayList<>();
                    for (Group group : originalGroups) {
                        if (group.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            filteredList.add(group);
                        }
                    }
                    results.values = filteredList;
                    results.count = filteredList.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredGroups.clear();
                filteredGroups.addAll((List<Group>) results.values);
                notifyDataSetChanged();
            }
        };
    }
}
