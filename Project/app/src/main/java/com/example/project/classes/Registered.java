package com.example.project.classes;

import java.util.List;

public class Registered {
    public String username;
    private List<TrailsList> trails;
    private List<Registered> friends;
    private String profilePic;

    void changePic(String path){
        this.profilePic = path;
    }

    String getProfilePic(){
        return profilePic;
    }

    void createTrailList(TrailsList t){
        trails.add(t);
    }

    void deleteTrailList (TrailsList t){
        trails.remove(t);
    }

    void addFriend(Registered r){
        friends.add(r);
    }

    void removeFriend(Registered r){
        friends.remove(r);
    }

    void changeUsername(String u){
        username = u;
    }

    Group createGroup(Trail l, String name){
        return new Group(name, this, l);
    }
}
