package com.example.sharetrip.api.chatbot;

public class ChatbotResponse {
    private boolean success;
    private String message;
    private String response;

    public ChatbotResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ChatbotResponse(String response, boolean success) {
        this.response = response;
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getResponse() {
        return response;
    }
}
