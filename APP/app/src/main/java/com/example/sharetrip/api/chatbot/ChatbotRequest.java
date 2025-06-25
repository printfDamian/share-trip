package com.example.sharetrip.api.chatbot;

public class ChatbotRequest {
    private String prompt;
    private String context;

    public ChatbotRequest(String prompt, String context) {
        this.prompt = prompt;
        this.context = context;
    }
}
