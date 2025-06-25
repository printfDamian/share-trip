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
import com.example.sharetrip.models.Post;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private Context context;
    private List<Post> posts;

    public PostAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.textTitle.setText(post.getTitle());
        holder.textContent.setText(post.getContent());
        holder.textAuthor.setText(post.getUser_name());
        holder.textDate.setText(post.getCreatedAt());

        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            System.out.println(ApiClient.BASE_URL + "../images/" + post.getImageUrl());
            Glide.with(context)
                    .load(ApiClient.BASE_URL + "../images/" + post.getImageUrl())
                    .into(holder.imageView);

            holder.imageView.setVisibility(View.VISIBLE);

        } else {
            holder.imageView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textContent, textAuthor, textDate;
        ImageView imageView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.IP_tvTitle);
            textContent = itemView.findViewById(R.id.ICB_tvContent);
            textAuthor = itemView.findViewById(R.id.ICB_tvUsername);
            textDate = itemView.findViewById(R.id.ICB_tvDate);
            imageView = itemView.findViewById(R.id.IP_imgPostImage);
        }
    }
}
