package com.example.sharetrip.api.post;

public class AddPostRequest {
    private Integer trip_id;
    private Integer location_id;
    private String title;
    private String content;

    public AddPostRequest(String title, String content) {
        this.trip_id = 1;
        this.location_id = 1;
        this.title = title;
        this.content = content;
    }
}
