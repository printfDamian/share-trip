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
import com.example.sharetrip.api.auth.LoginRequest;
import com.example.sharetrip.api.auth.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        LinearLayout Login_llBack = findViewById(R.id.AddPost_llBack);
        EditText Login_inputEmail = findViewById(R.id.Login_inputEmail);
        EditText Login_inputPassword = findViewById(R.id.Login_inputPassword);
        Button Login_btnLogin = findViewById(R.id.Login_btnLogin);

        String prefsEmail = prefs.getString("email", "");

        if(!prefsEmail.isEmpty()) {
            Login_inputEmail.setText(prefsEmail);
        }

        Login_llBack.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        Login_btnLogin.setOnClickListener(v -> {
            String email = Login_inputEmail.getText().toString().trim();
            String password = Login_inputPassword.getText().toString().trim();

            LoginRequest request = new LoginRequest(email, password);

            ApiClient.getApiService(this).login(request).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        LoginResponse loginResponse = response.body();

                        if (loginResponse.isSuccess()) {
                            String token = loginResponse.getData().getToken();
                            String username = loginResponse.getData().getUsername();
                            String email = loginResponse.getData().getEmail();
                            // Armazenar token em SharedPreferences ou passar à próxima activity
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("token", token);
                            editor.putString("username", username);
                            editor.putString("email", email);
                            editor.apply();

                            Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                            // startActivity(...)

                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Erro ao fazer login", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    System.out.println("Erro: " + t.getMessage());
                    Toast.makeText(LoginActivity.this, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    // setResult(RESULT_CANCELED);
                    // finish();
                }
            });
        });
    }
}