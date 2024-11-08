package com.example.project.classes;
import static com.google.common.primitives.UnsignedBytes.toInt;

import java.util.HashMap;
import java.util.List;

public class Group {
    public String name;
    public HashMap<String, Object> members;
    public String trail;
    //public String people;

    public Group(String n, String r, String t){
        name = n;
        members.put(r, true);
        trail = t;
        //people = "1";
    }
    public Group(){

    }

    public boolean joinGroup(String r){
        if (members.size() < 5){
            members.put(r, true);
            return true;
        }else{
            return false;
        }

    }
    public void leaveGroup (String r){
        members.remove(r);
    }
}
