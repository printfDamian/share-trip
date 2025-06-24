package com.example.sharetrip.home.fragments;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

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
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.sharetrip.R;
import com.example.sharetrip.api.ApiClient;
import com.example.sharetrip.api.post.PostsResponse;
import com.example.sharetrip.auth.SignInActivity;
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
    SharedPreferences prefs;

    public PostsFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        prefs = requireActivity().getSharedPreferences("prefs", MODE_PRIVATE);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.PostFrg_recyclerView);
        PostFrg_progressBar = view.findViewById(R.id.PostFrg_progressBar);

        PostFrg_progressBar.setVisibility(View.VISIBLE);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadPosts();
    }

    private void loadPosts() {
        ApiClient.getApiService(getContext()).getAllPosts().enqueue(new Callback<PostsResponse>() {
            @Override
            public void onResponse(Call<PostsResponse> call, Response<PostsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Post> postList = response.body().getData();
                    postAdapter = new PostAdapter(getContext(), postList);
                    recyclerView.setAdapter(postAdapter);
                } else if(response.code() == 401) {
                    Intent intent = new Intent(getContext(), SignInActivity.class);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("token", "");
                    editor.apply();
                    requireActivity().finish();
                    startActivity(intent);
                } else {
                    Utils.showToast(getContext(), "Erro ao carregar posts", getLayoutInflater());
                    System.out.println(response.toString());
                }
                PostFrg_progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<PostsResponse> call, Throwable t) {
                Utils.showToast(getContext(), "Erro: " + t.getMessage(), getLayoutInflater());
                PostFrg_progressBar.setVisibility(View.GONE);
            }
        });
    }
}