package com.example.sharetrip.auth;

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

        });

        /*Login Sucesso
        Intent resultIntent = new Intent();
        // You can put extras if needed
        setResult(RESULT_OK, resultIntent);
        finish();*/
    }
}