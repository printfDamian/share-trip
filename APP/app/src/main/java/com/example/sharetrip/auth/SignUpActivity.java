package com.example.sharetrip.auth;

import static com.example.sharetrip.utils.Utils.showToast;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.sharetrip.R;
import com.example.sharetrip.api.ApiClient;
import com.example.sharetrip.api.auth.LoginRequest;
import com.example.sharetrip.api.auth.LoginResponse;
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

        LinearLayout Signup_llBack = findViewById(R.id.Signup_llBack);
        EditText Signup_inputName = findViewById(R.id.Signup_inputName);
        EditText Signup_inputEmail = findViewById(R.id.Signup_inputEmail);
        EditText Signup_inputPassword = findViewById(R.id.Signup_inputPassword);
        Button Signup_btnSignup = findViewById(R.id.Signup_btnSignup);

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

                            showToast(SignUpActivity.this, "Login successful!", getLayoutInflater());

                            setResult(RESULT_OK);
                            finish();

                        } else {
                            showToast(SignUpActivity.this, SignupResponse.getMessage(), getLayoutInflater());
                        }
                    } else {
                        showToast(SignUpActivity.this, "Erro ao fazer login", getLayoutInflater());
                    }
                }

                @Override
                public void onFailure(Call<SignupResponse> call, Throwable t) {
                    System.out.println("Erro: " + t.getMessage());
                    showToast(SignUpActivity.this, "Erro: " + t.getMessage(), getLayoutInflater());
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