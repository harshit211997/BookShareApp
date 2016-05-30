package com.example.abhishek.bookshareapp.api.models.LocalBooks;

import com.google.gson.annotations.SerializedName;

public class Book {

    String id;
    String title;
    String author;
    float rating;
    @SerializedName("ratings_count")
    Long ratingsCount;
    @SerializedName("gr_id")
    String grId;
    @SerializedName("gr_img_url")
    String grImgUrl;
    @SerializedName("email")
    String email;

    public String getAuthor() {
        return author;
    }

    public String getEmail() {
        return email;
    }

    public String getGrId() {
        return grId;
    }

    public String getGrImgUrl() {
        return grImgUrl;
    }

    public String getId() {
        return id;
    }

    public float getRating() {
        return rating;
    }

    public Long getRatingsCount() {
        return ratingsCount;
    }

    public String getTitle() {
        return title;
    }

}
