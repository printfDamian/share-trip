package com.example.sharetrip.api.auth;

public class LoginResponse {
    private boolean success;
    private String message;
    private Data data;

    public static class Data {
        private String token;
        private String email;
        private String username;

        public String getToken() {
            return token;
        }

        public String getEmail() {
            return email;
        }

        public String getUsername() {
            return username;
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

