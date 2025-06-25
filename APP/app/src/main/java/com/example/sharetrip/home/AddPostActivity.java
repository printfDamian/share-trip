package com.example.sharetrip.home;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sharetrip.R;
import com.example.sharetrip.api.ApiClient;
import com.example.sharetrip.api.post.AddPostRequest;
import com.example.sharetrip.api.post.AddPostResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_post);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        LinearLayout AddPost_llBack = findViewById(R.id.AddPost_llBack);
        EditText AddPost_inputTitle = findViewById(R.id.Profile_name);
        EditText AddPost_inputBody = findViewById(R.id.AddPost_inputBody);
        Button AddPost_btnPost = findViewById(R.id.AddPost_btnPost);

        AddPost_llBack.setOnClickListener(v -> {
            finish();
        });

        AddPost_btnPost.setOnClickListener(v -> {
            String title = AddPost_inputTitle.getText().toString().trim();
            String body = AddPost_inputBody.getText().toString().trim();

            if(title.isEmpty() || body.isEmpty()) {
                Toast.makeText(this, "Fill all the fields", Toast.LENGTH_SHORT).show();
            } else {
                addPost(title, body);
            }
        });
    }

    private void addPost(String title, String body) {

        AddPostRequest addPostRequest = new AddPostRequest(title, body);

        ApiClient.getApiService(this).newPost(addPostRequest).enqueue(new Callback<AddPostResponse>() {
            @Override
            public void onResponse(Call<AddPostResponse> call, Response<AddPostResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getApplicationContext(), "Post sent", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(AddPostActivity.this, "Erro ao carregar posts", Toast.LENGTH_SHORT).show();
                    System.out.println(response.toString());
                }
            }

            @Override
            public void onFailure(Call<AddPostResponse> call, Throwable t) {
                Toast.makeText(AddPostActivity.this, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}