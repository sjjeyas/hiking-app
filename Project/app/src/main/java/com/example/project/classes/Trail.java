package com.example.project.classes;

import java.util.Collections;
import java.util.List;

public class Trail {
    public String name;
    public float difficulty;
    public float quality;
    public int zipcode;
    public String location;
    public String description;
    public List<Review> reviews;

    public Trail(String n, String l, String d){
        this.name = n;
        this.location = l;
        this.description = d;
        this.difficulty = 0;
        this.quality = 0;
        this.zipcode = 0;
        this.reviews = Collections.emptyList();
    }

    public Trail() {

    }
}
