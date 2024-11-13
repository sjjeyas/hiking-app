package com.example.project.classes;
import static com.google.common.primitives.UnsignedBytes.toInt;

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

    public boolean joinGroup(String r){
        if (members.size() < 4){
            members.put(r, true);
            return true;
        }else{
            return false;
        }

    }
    public void leaveGroup(String r){
        members.remove(r);
    }
}
