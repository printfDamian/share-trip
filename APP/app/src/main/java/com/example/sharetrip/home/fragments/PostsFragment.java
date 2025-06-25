package com.example.sharetrip.home.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.sharetrip.R;
import com.example.sharetrip.api.ApiClient;
import com.example.sharetrip.api.post.PostsResponse;
import com.example.sharetrip.auth.LoginActivity;
import com.example.sharetrip.auth.SignInActivity;
import com.example.sharetrip.home.adapter.PostAdapter;
import com.example.sharetrip.models.Post;
import com.example.sharetrip.home.AddPostActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostsFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private ProgressBar PostFrg_progressBar;
    SharedPreferences prefs;
    private ActivityResultLauncher<Intent> addPostLauncher;

    public PostsFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        prefs = requireActivity().getSharedPreferences(LoginActivity.PREFS, MODE_PRIVATE);

        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.PostFrg_recyclerView);
        PostFrg_progressBar = view.findViewById(R.id.PostFrg_progressBar);
        ImageButton PostFrg_btnAddPost = view.findViewById(R.id.PostFrg_btnAddPost);

        PostFrg_progressBar.setVisibility(View.VISIBLE);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadPosts();

        addPostLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        loadPosts();
                    }
                }
        );

        PostFrg_btnAddPost.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddPostActivity.class);
            addPostLauncher.launch(intent);
        });
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
                    editor.putString(LoginActivity.TOKEN, "");
                    editor.apply();
                    requireActivity().finish();
                    startActivity(intent);
                } else {
                    Toast.makeText(requireActivity(), getContext().getString(R.string.err_load_posts), Toast.LENGTH_SHORT).show();
                    System.out.println(response.toString());
                }
                PostFrg_progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<PostsResponse> call, Throwable t) {
                Toast.makeText(requireActivity(), getContext().getString(R.string.error) + t.getMessage(), Toast.LENGTH_SHORT).show();
                PostFrg_progressBar.setVisibility(View.GONE);
            }
        });
    }
}
