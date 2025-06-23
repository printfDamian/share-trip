package com.example.sharetrip.api.auth;

public class LoginResponse {
    private boolean success;
    private String message;
    private Data data;

    public static class Data {
        private String token;

        public String getToken() {
            return token;
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
    }
}

