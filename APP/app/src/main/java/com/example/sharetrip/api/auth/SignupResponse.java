package com.example.sharetrip.api.auth;

import com.example.sharetrip.models.User;

public class SignupResponse {
    private boolean success;
    private static String message;
    private Data data;

    public static class Data {
        private User user;

        public User getUser() {
            return user;
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public static String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
    }
}

