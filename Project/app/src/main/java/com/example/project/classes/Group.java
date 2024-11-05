package com.example.project.classes;
import java.util.List;

public class Group {
    public String name;
    public List<String> members;
    public String trail;

    public Group(String n, String r, String t){
        name = n;
        members.add(r);
        trail = t;
    }
    public Group(){

    }

    void joinGroup(String r){
        members.add(r);
    }
    void leaveGroup (String r){
        members.remove(r);
    }
}
