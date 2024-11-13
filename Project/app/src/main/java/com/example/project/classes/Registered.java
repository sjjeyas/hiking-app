package com.example.project.classes;

import com.google.protobuf.Empty;

import java.util.List;


public class Registered {
    public String username;
    private String name;
    //private List<TrailsList> trails;
    //private List<Registered> friends;
    //private String profilePic;
    private String userID;
    private String location;
    /*

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

     */

    void changeUsername(String u){
        username = u;
    }

    public Registered(String u){
        username = u;
        //trails = null;
        //friends = null;
        //profilePic = null;
    }
    public Registered(){

    }

    Group createGroup(String name, String location){
        return new Group(name, location, this.username);
    }
}
