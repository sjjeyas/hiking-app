package com.example.project.classes;

public class Review {
    public int rating;
    public int quality;
    public String feedback;
    public User author;
    void editReview(String f){
        feedback = f;
    }

    int getRating() {return rating;};
}


