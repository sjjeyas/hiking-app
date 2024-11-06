package com.example.project.classes;
import java.util.HashMap;
import java.util.List;

public class Group {
    public String name;
    public HashMap<String, Object> members;
    public String trail;

    public Group(String n, String r, String t){
        name = n;
        members.put(r, true);
        trail = t;
    }
    public Group(){

    }

    void joinGroup(String r){
        members.put(r, true);
    }
    void leaveGroup (String r){
        members.remove(r);
    }
}
