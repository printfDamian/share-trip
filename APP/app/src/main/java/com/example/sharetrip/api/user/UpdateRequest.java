package com.example.sharetrip.api.user;

public class UpdateRequest {
    private String name;
    private String password;

    public UpdateRequest(String name, String password) {
        this.name = name;
        this.password = password;
    }
}
