package com.example.project.classes;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Trail {
    public String name;
    public float difficulty;
    public float quality;
    public int zipcode;
    public String location;
    public String description;
    public Map<String, Object> reviews;

    public Trail(String n, String l, String d){
        this.name = n;
        this.location = l;
        this.description = d;
        this.difficulty = 3;
        this.quality = 0;
        this.zipcode = 0;
        this.reviews = new HashMap<>();
    }

    public Trail() {

    }

    public String getName(){
        return name;
    }
    public String getLocation() {
        return location;
    }
    public String getDescription() {
        return description;
    }
    public float getDifficulty() {
        return difficulty;
    }

    public String getDifficultyString(){
        int count = Math.round(difficulty);

        StringBuilder result = new StringBuilder();
        for (int i  = 0; i < count; i++){
            result.append("★");
        }
        return result.toString();
    }
}

