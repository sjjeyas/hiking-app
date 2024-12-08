package com.example.project.classes;

import android.util.Log;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Trail {
    public String name;
    public float rating;
    public float quality;
    public int zipcode;
    public String location;
    public String description;
    public Double latitude;
    public Double longitude;
    public double distanceFromUser;
    public Map<String, Object> reviews;

    public Trail(String n, String l, String d, Map<String, Object> r){
        this.name = n;
        this.location = l;
        this.description = d;
        this.rating = 0;
        this.quality = 0;
        this.zipcode = 0;
        this.reviews = r;
    }


    public Trail(String name, String location, Double latitude, Double longitude) {
        this.name = name;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
//        this.distanceFromUser = Double.parseDouble(null);
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

    public void updateRating() {
        Log.d("update Trail", "Updating Rating for " + name);
        if (reviews.isEmpty()) {
            rating = 0;
            Log.d("update Trail", "No Reviews!");
            return;
        }

        float sumRating = 0;
        int count = 0;

        for (Map.Entry<String, Object> entry : reviews.entrySet()) {
            Map<String, Object> review = (Map<String, Object>) entry.getValue();
            Object ratingObj = review.get("rating");
            Log.e("update Trail", " Review: " + review);
            if(ratingObj instanceof Number){
                float currRating = ((Number) ratingObj).floatValue();
                Log.e("update Trail", " Rating: " + currRating);
                sumRating += currRating;
            }
        }

        rating = sumRating / reviews.size(); // Calculate the average

        Log.d("update Trail", "Sum: " + sumRating + " Size: " + reviews.size());
        Log.d("update Trail", "Rating: " + rating );
    }

    public String getRatingString(){
        updateRating();
        int count = Math.round(rating);

        StringBuilder result = new StringBuilder();
        result.append(rating + " ");
        for (int i  = 0; i < 5; i++){
            if (i < count){
                result.append("★");
            }
            else {
                result.append("☆");
            }
        }
        return result.toString();
    }
}

