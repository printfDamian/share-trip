package com.example.sharetrip.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.sharetrip.R;
import com.example.sharetrip.home.fragments.MapFragment;
import com.example.sharetrip.home.fragments.PostsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

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

        // System.out.println(getToken());

        FrameLayout Home_fragmentContainer = findViewById(R.id.Home_fragmentContainer);
        BottomNavigationView Home_bottomNavigation = findViewById(R.id.Home_bottomNavigation);

        Home_bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment;
            if (item.getItemId() == R.id.nav_posts) {
                fragment = new PostsFragment();
            } else if(item.getItemId() == R.id.nav_map) {
                fragment = new MapFragment();
            } else {
                fragment = new PostsFragment(); // Futuramente para o chatbot
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.Home_fragmentContainer, fragment)
                    .commit();
            return true;
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
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        return prefs.getString("token", "");
    }
}