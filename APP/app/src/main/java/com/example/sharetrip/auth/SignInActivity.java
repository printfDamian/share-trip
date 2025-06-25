package com.example.sharetrip.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sharetrip.home.HomeActivity;
import com.example.sharetrip.R;

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
                        Intent intent = new Intent(this, LoginActivity.class);
                        loginLauncher.launch(intent);
                    } else {
                        Toast.makeText(SignInActivity.this, getString(R.string.signup_fail), Toast.LENGTH_SHORT).show();
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
                        Intent intent = new Intent(this, HomeActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(SignInActivity.this, getString(R.string.login_fail), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            loginLauncher.launch(intent);
        });
    }
}
