package com.example.sharetrip.models;

public class Post {
    private int id;
    private Integer userId;
    private Integer tripId;
    private Integer locationId;
    private String title;
    private String content;
    private String createdAt;
    private String updatedAt;
    private String user_name;
    private String trip_title;
    private String imageUrl;

    public Post(int id, Integer userId, Integer tripId, Integer locationId, String title, String content, String imageUrl, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.tripId = tripId;
        this.locationId = locationId;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
    }

    public Post(int id, Integer userId, Integer tripId, Integer locationId, String title, String content, String createdAt, String updatedAt, String user_name, String trip_title, String imageUrl) {
        this.id = id;
        this.userId = userId;
        this.tripId = tripId;
        this.locationId = locationId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.user_name = user_name;
        this.trip_title = trip_title;
        this.imageUrl = imageUrl;
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

    public Integer getUser() {
        return userId;
    }

    public void setUser(Integer userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getTrip_title() {
        return trip_title;
    }

    public void setTrip_title(String trip_title) {
        this.trip_title = trip_title;
    }
}
