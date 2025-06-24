package com.example.sharetrip.api.post;

import com.example.sharetrip.models.Post;
import com.example.sharetrip.models.User;

import java.util.List;

public class PostsResponse {
    private boolean success;
    private List<Post> data;

    public boolean isSuccess() {
        return success;
    }

    public List<Post> getData() {
        return data;
    }
}

