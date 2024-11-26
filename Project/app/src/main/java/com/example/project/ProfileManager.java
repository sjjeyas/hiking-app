package com.example.project;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

public class ProfileManager {

    private final FirebaseAuth mAuth;
    private final DatabaseReference mDatabase;

    public ProfileManager(FirebaseAuth auth, DatabaseReference database) {
        this.mAuth = auth;
        this.mDatabase = database;
    }

    public String getCurrentUserId() {
        FirebaseUser user = mAuth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    public void getUserProfile(String userId, OnCompleteListener<DataSnapshot> listener) {
        mDatabase.child(userId).get().addOnCompleteListener(listener);
    }

    public void addFriend(String userId, String friendName, OnCompleteListener<Void> listener) {
        mDatabase.child(userId).child("friends").child(friendName).setValue(true)
                .addOnCompleteListener(listener);
    }

    public void unfriend(String userId, String friendName, OnCompleteListener<Void> listener) {
        mDatabase.child(userId).child("friends").child(friendName).removeValue()
                .addOnCompleteListener(listener);
    }

    public void isFriend(String userId, String friendName, FriendStatusCallback callback) {
        mDatabase.child(userId).child("friends").child(friendName).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        callback.onStatusChecked(true);
                    } else {
                        callback.onStatusChecked(false);
                    }
                });
    }

    public interface FriendStatusCallback {
        void onStatusChecked(boolean isFriend);
    }

    public FirebaseAuth getmAuth(){
        return mAuth;
    }
}

