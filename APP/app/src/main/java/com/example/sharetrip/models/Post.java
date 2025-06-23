package com.example.sharetrip.models;

public class Post {
    private int id;
    private User user;
    private Integer tripId;
    private Integer locationId;
    private String title;
    private String content;
    private String imageUrl;
    private String createdAt;

    public Post(int id, User user, Integer tripId, Integer locationId, String title, String content, String imageUrl, String createdAt) {
        this.id = id;
        this.user = user;
        this.tripId = tripId;
        this.locationId = locationId;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Integer getTripId() {
        return tripId;
    }

    public void setTripId(Integer tripId) {
        this.tripId = tripId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
