package com.example.sharetrip.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sharetrip.R;
import com.example.sharetrip.api.ApiClient;
import com.example.sharetrip.models.Chat;
import com.example.sharetrip.models.Post;

import java.util.List;

public class ChatbotAdapter extends RecyclerView.Adapter<ChatbotAdapter.ChatbotViewHolder> {
    private Context context;
    private List<Chat> conversation;

    public ChatbotAdapter(Context context, List<Chat> conversation) {
        this.context = context;
        this.conversation = conversation;
    }

    @NonNull
    @Override
    public ChatbotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chatbot, parent, false);
        return new ChatbotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatbotViewHolder holder, int position) {
        Chat chat = conversation.get(position);
        holder.textContent.setText(chat.getContent());
        holder.textAuthor.setText(chat.getUsername());
        holder.textDate.setText(chat.getCreatedAt());
    }

    @Override
    public int getItemCount() {
        return conversation.size();
    }

    public static class ChatbotViewHolder extends RecyclerView.ViewHolder {
        TextView textContent, textAuthor, textDate;

        public ChatbotViewHolder(@NonNull View itemView) {
            super(itemView);
            textContent = itemView.findViewById(R.id.ICB_tvContent);
            textAuthor = itemView.findViewById(R.id.ICB_tvUsername);
            textDate = itemView.findViewById(R.id.ICB_tvDate);
        }
    }
}
