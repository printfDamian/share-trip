package com.example.sharetrip.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.sharetrip.R;
import com.example.sharetrip.auth.LoginActivity;
import com.example.sharetrip.auth.SignInActivity;
import com.example.sharetrip.auth.SignUpActivity;
import com.example.sharetrip.home.fragments.ChatbotFragment;
import com.example.sharetrip.home.fragments.MapFragment;
import com.example.sharetrip.home.fragments.PostsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    private Fragment fragment;
    private ActivityResultLauncher<Intent> profileLauncher;
    SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Home_coordinator), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FrameLayout Home_fragmentContainer = findViewById(R.id.Home_fragmentContainer);
        BottomNavigationView Home_bottomNavigation = findViewById(R.id.Home_bottomNavigation);
        ImageButton home_btnProfile = findViewById(R.id.Home_btnProfile);

        fragment = new PostsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.Home_fragmentContainer, fragment)
                .commit();

        Home_bottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_posts && !(fragment instanceof PostsFragment)) {
                fragment = new PostsFragment();
            } else if(item.getItemId() == R.id.nav_map && !(fragment instanceof MapFragment)) {
                fragment = new MapFragment();
            } else if(item.getItemId() == R.id.nav_chatbot && !(fragment instanceof ChatbotFragment)){
                fragment = new ChatbotFragment();
            } else {
                return false;
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.Home_fragmentContainer, fragment)
                    .commit();
            return true;
        });

        prefs = getSharedPreferences(LoginActivity.PREFS, MODE_PRIVATE);

        profileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == ProfileActivity.RESULT_LOGOUT) {
                        Intent intent = new Intent(this, SignInActivity.class);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(LoginActivity.TOKEN, "");
                        editor.apply();
                        finish();
                        startActivity(intent);
                    }
                }
        );

        home_btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            profileLauncher.launch(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_settings) {
            // startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getToken() {
        SharedPreferences prefs = getSharedPreferences(LoginActivity.PREFS, MODE_PRIVATE);
        return prefs.getString(LoginActivity.TOKEN, "");
    }
}