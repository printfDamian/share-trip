package com.example.sharetrip.api.post;

import com.example.sharetrip.models.Post;

import java.util.List;

public class AddPostResponse {
    private boolean success;
    private Post data;
    private String message;

    public AddPostResponse(boolean success, Post data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    public AddPostResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public Post getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
