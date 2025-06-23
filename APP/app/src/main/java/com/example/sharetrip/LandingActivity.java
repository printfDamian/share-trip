package com.example.sharetrip;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sharetrip.auth.SignInActivity;

public class LandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (isUserLoggedIn()) {
                startActivity(new Intent(this, HomeActivity.class));
            } else {
                startActivity(new Intent(this, SignInActivity.class));
            }
            finish();
        }, 1000);
    }

    private boolean isUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        return prefs.getBoolean("isLoggedIn", false);
    }
}