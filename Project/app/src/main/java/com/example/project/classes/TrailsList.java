package com.example.project.classes;
import java.util.List;

public class TrailsList {
    public List<Trail> tList;
    public User author;
    public String name;
    private boolean visibility;
    // true visibility = public, false visibility = private
    boolean getVisibility(){
        return this.visibility;
    }
    void changeVisibility(boolean change){
        this.visibility = change;
    }
    void changeName(String n){
        this.name = n;
    }
    void addTrail(Trail t){
        tList.add(t);
    }
    void removeTrail(Trail t){
        tList.remove(t);
    }
}
