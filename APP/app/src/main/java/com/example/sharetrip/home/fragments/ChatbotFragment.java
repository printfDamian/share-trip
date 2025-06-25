package com.example.sharetrip.home.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.sharetrip.R;
import com.example.sharetrip.api.ApiClient;
import com.example.sharetrip.api.chatbot.ChatbotRequest;
import com.example.sharetrip.api.chatbot.ChatbotResponse;
import com.example.sharetrip.auth.SignInActivity;
import com.example.sharetrip.home.adapter.ChatbotAdapter;
import com.example.sharetrip.models.Chat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatbotFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChatbotAdapter chatbotAdapter;
    private ProgressBar chatFrg_progressBar;
    //private String chatContext = "";
    List<Chat> conversation = new ArrayList<Chat>();

    SharedPreferences prefs;

    public ChatbotFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        prefs = requireActivity().getSharedPreferences("prefs", MODE_PRIVATE);

        return inflater.inflate(R.layout.fragment_chatbot, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.ChatFrg_recyclerView);
        chatFrg_progressBar = view.findViewById(R.id.ChatFrg_progressBar);
        ImageButton chatFrg_btnClear = view.findViewById(R.id.Profile_btnRemove);
        ImageButton chatFrg_btnSend = view.findViewById(R.id.Profile_btnSave);
        EditText chatFrg_promptInput = view.findViewById(R.id.ChatFrg_promptInput);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Gson gson = new Gson();
        String json = prefs.getString("conversation", null);

        if (json != null) {
            Type type = new TypeToken<List<Chat>>() {}.getType();
            conversation = gson.fromJson(json, type);

            chatbotAdapter = new ChatbotAdapter(getContext(), conversation);
            recyclerView.setAdapter(chatbotAdapter);
        }

        chatFrg_btnClear.setOnClickListener(v -> {
            clearChat();

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("conversation", null);
            editor.apply();
        });

        chatFrg_btnSend.setOnClickListener(v -> {
            String prompt = chatFrg_promptInput.getText().toString().trim();

            if(!prompt.isEmpty()) {
                chatFrg_progressBar.setVisibility(View.VISIBLE);
                chatFrg_promptInput.setText("");
                askQuestion(prompt);
            } else {
                Toast.makeText(getContext(), "Write a prompt before sending", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void askQuestion(String prompt) {
        Chat userChat = new Chat(
                1,
                prefs.getString("username", "Username not found"),
                prompt,
                getFormatedCurrentTime()
        );

        conversation.add(userChat);

        chatbotAdapter = new ChatbotAdapter(getContext(), conversation);
        recyclerView.setAdapter(chatbotAdapter);

        ChatbotRequest chatbotRequest = new ChatbotRequest(prompt, getChatContext());

        ApiClient.getApiService(getContext()).askQuestion(chatbotRequest).enqueue(new Callback<ChatbotResponse>() {
            @Override
            public void onResponse(Call<ChatbotResponse> call, Response<ChatbotResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    String botResponse = response.body().getResponse();

                    Chat botChat = new Chat(
                            0,
                            "Share Trip ChatBot",
                            botResponse,
                            getFormatedCurrentTime()
                    );

                    conversation.add(botChat);

                    chatbotAdapter = new ChatbotAdapter(getContext(), conversation);
                    recyclerView.setAdapter(chatbotAdapter);

                    Gson gson = new Gson();
                    String jsonConversation = gson.toJson(conversation);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("conversation", jsonConversation);
                    editor.apply();

                } else if(response.code() == 401) {
                    Intent intent = new Intent(getContext(), SignInActivity.class);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("token", "");
                    editor.apply();
                    requireActivity().finish();
                    startActivity(intent);

                } else {
                    Toast.makeText(requireActivity(), "Error sending the prompt", Toast.LENGTH_SHORT).show();
                    conversation.remove(userChat);
                }

                chatFrg_progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<ChatbotResponse> call, Throwable t) {
                Toast.makeText(requireActivity(), "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                chatFrg_progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void clearChat() {
        conversation.clear();
        chatbotAdapter = new ChatbotAdapter(getContext(), conversation);
        recyclerView.setAdapter(chatbotAdapter);
    }

    private String getChatContext() {
        StringBuilder chatContext = new StringBuilder();

        if(conversation.size() == 1) {
            return "";
        }

        for(Chat chat : conversation) {
            if(chat.getUser_id() > 0) {
                chatContext.append("User: ").append(chat.getContent());
            } else {
                chatContext.append("\nAssistant: ").append(chat.getContent()).append("\n\n");
            }
        }

        return chatContext.toString();
    }

    private String getFormatedCurrentTime() {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(currentDate);
    }
}
