package com.example.sharetrip.api.markers;

import com.example.sharetrip.models.Post;

import java.util.List;

public class MarksResponse {
    private boolean success;
    private Object data;

    public boolean isSuccess() {
        return success;
    }

    public Object getData() {
        return data;
    }
}

