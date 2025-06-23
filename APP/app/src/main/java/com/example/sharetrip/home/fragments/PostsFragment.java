package com.example.sharetrip.home.fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.sharetrip.R;
import com.example.sharetrip.api.ApiClient;
import com.example.sharetrip.home.adapter.PostAdapter;
import com.example.sharetrip.models.Post;
import com.example.sharetrip.utils.Utils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostsFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private ProgressBar PostFrg_progressBar;

    public PostsFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.PostFrg_recyclerView);
        PostFrg_progressBar = view.findViewById(R.id.PostFrg_progressBar);

        PostFrg_progressBar.setVisibility(View.VISIBLE);
        loadPosts();
        new android.os.Handler().postDelayed(() -> {
            PostFrg_progressBar.setVisibility(View.GONE);
        }, 500);
    }

    private void loadPosts() {
        ApiClient.getApiService().getAllPosts().enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Post> postList = response.body();
                    postAdapter = new PostAdapter(getContext(), postList);
                    recyclerView.setAdapter(postAdapter);
                } else {
                    Utils.showToast(getContext(), "Erro ao carregar posts", getLayoutInflater());
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Utils.showToast(getContext(), "Erro: " + t.getMessage(), getLayoutInflater());
            }
        });
    }
}