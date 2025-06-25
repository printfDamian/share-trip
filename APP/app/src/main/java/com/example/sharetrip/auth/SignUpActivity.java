package com.example.sharetrip.auth;

import android.content.SharedPreferences;
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
import com.example.sharetrip.api.auth.SignupRequest;
import com.example.sharetrip.api.auth.SignupResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        LinearLayout Signup_llBack = findViewById(R.id.AddPost_llBack);
        EditText Signup_inputName = findViewById(R.id.Profile_name);
        EditText Signup_inputEmail = findViewById(R.id.AddPost_inputBody);
        EditText Signup_inputPassword = findViewById(R.id.Signup_inputPassword);
        Button Signup_btnSignup = findViewById(R.id.AddPost_btnPost);

        Signup_llBack.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        Signup_btnSignup.setOnClickListener(v -> {
            String name = Signup_inputName.getText().toString().trim();
            String email = Signup_inputEmail.getText().toString().trim();
            String password = Signup_inputPassword.getText().toString().trim();

            SignupRequest request = new SignupRequest(name, email, password);

            ApiClient.getApiService(this).register(request).enqueue(new Callback<SignupResponse>() {
                @Override
                public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        SignupResponse signupResponse = response.body();

                        if (signupResponse.isSuccess()) {
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("email", email);
                            editor.apply();

                            Toast.makeText(SignUpActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                            setResult(RESULT_OK);
                            finish();

                        } else {
                            Toast.makeText(SignUpActivity.this, SignupResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SignUpActivity.this, "Erro ao fazer login", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<SignupResponse> call, Throwable t) {
                    System.out.println("Erro: " + t.getMessage());
                    Toast.makeText(SignUpActivity.this, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();

                    // setResult(RESULT_CANCELED);
                    // finish();
                }
            });
        });

        /*Login Sucesso
        Intent resultIntent = new Intent();
        // You can put extras if needed
        setResult(RESULT_OK, resultIntent);
        finish();*/
    }
}