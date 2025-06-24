package com.example.sharetrip.auth;

import static com.example.sharetrip.utils.Utils.showToast;

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

        LinearLayout Login_llBack = findViewById(R.id.Signup_llBack);
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
                            // Armazenar token em SharedPreferences ou passar à próxima activity
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("token", token);
                            editor.apply();

                            showToast(LoginActivity.this, "Login successful!", getLayoutInflater());

                            // startActivity(...)

                            // You can put extras if needed
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            showToast(LoginActivity.this, loginResponse.getMessage(), getLayoutInflater());
                        }
                    } else {
                        showToast(LoginActivity.this, "Erro ao fazer login", getLayoutInflater());
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    System.out.println("Erro: " + t.getMessage());
                    showToast(LoginActivity.this, "Erro: " + t.getMessage(), getLayoutInflater());
                    // setResult(RESULT_CANCELED);
                    // finish();
                }
            });
        });
    }
}