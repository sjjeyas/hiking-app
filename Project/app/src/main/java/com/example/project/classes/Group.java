package com.example.project.classes;
import static com.google.common.primitives.UnsignedBytes.toInt;

import android.annotation.SuppressLint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Group {
    public String name;
    public HashMap<String, Object> members = new HashMap<>();
    public String trail;
    public int capacity;

    public Group(String n, String t, int c){
        name = n;
        trail = t;
        capacity = c;
    }
    public Group(){

    }

    public boolean joinGroup(String r){
        if (members.size() < capacity){
            members.put(r, true);
            return true;
        }else{
            return false;
        }

    }
    public void leaveGroup(String r){
        members.remove(r);
    }

    public String getName() {return name;}
}
