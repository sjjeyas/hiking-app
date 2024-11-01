package com.example.project.classes;
import java.util.List;

public class Group {
    public String name;
    public List<Registered> rUser;
    public Trail trail;

    Group(String n, Registered r, Trail t){
        name = n;
        rUser.add(r);
        trail = t;
    }

    void joinGroup(Registered r){
        rUser.add(r);
    }
    void leaveGroup (Registered r){
        rUser.remove(r);
    }
}
