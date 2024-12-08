package com.example.project.classes;

import android.util.Log;

import java.util.Map;

public class CheckFunctions {
    public final String user;
    public final String view;

    public CheckFunctions(String user, String view) {
        this.user = user;
        this.view = view;
    }
    public boolean sameUser(){
        return user.equals(view);
    }

    public boolean validReview(Map<String, Object> review){
        if (review.get("text") != null && !review.get("text").equals("")){
            if(!(review.get("text") instanceof String)){
                return false;
            }
        }else {
            return false;
        }
        if (review.get("displayname") != null  && !review.get("displayname").equals("")){
            if(!(review.get("displayname") instanceof String)){
                return false;
            }
        }else {
            return false;
        }
        if (review.get("rating") != null){
            if(!(review.get("rating") instanceof Integer)){
                return false;
            }
        }else {
            return false;
        }
        return true;
    }

    public boolean validGroup(Map<String, Object> group){
        Object g =  group.get("name");
        Object t = group.get("trail");
        Object c = group.get("capacity");
        if (c == null){
            return false;
        }
        if (g != null && (group.get("name") instanceof String)){
            if(g.equals("")){
                return false;
            }
        }else {
            return false;
        }
        if (t != null && (group.get("trail") instanceof String)){
            if(t.equals("")){
                return false;
            }
        }else {
            return false;
        }
        return true;
    }
}
