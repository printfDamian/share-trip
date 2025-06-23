package com.example.sharetrip.auth;

import static com.example.sharetrip.utils.Utils.showToast;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sharetrip.R;
import com.example.sharetrip.utils.Utils;

public class SignInActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> signupLauncher;
    private ActivityResultLauncher<Intent> loginLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnSignup = findViewById(R.id.Signin_btnSignup);
        Button btnLogin = findViewById(R.id.Signin_btnLogin);

        signupLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    finish();
                } else {
                    showToast(this, "Signup failed or cancelled", getLayoutInflater());
                }
            }
        );

        btnSignup.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            signupLauncher.launch(intent);
        });


        loginLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        finish();
                    } else {
                        showToast(this, "Login failed or cancelled", getLayoutInflater());
                    }
                }
        );

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            loginLauncher.launch(intent);
        });
    }
}
