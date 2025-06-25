package com.example.sharetrip.home;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sharetrip.R;

public class ViewPoiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_poi);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        LinearLayout viewPoi_llBack = findViewById(R.id.ViewPoi_llBack);

        viewPoi_llBack.setOnClickListener(v -> {
            finish();
        });

        String poiName = getIntent().getStringExtra("ViewPoi_name");
        String poiDescription = getIntent().getStringExtra("ViewPoi_description");
        String poiType = getIntent().getStringExtra("ViewPoi_type");
        String poiAddress = getIntent().getStringExtra("ViewPoi_address");
        String tripTitle = getIntent().getStringExtra("ViewPoi_tripTitle");

        TextView nameView = findViewById(R.id.ViewPoi_name);
        TextView descriptionView = findViewById(R.id.ViewPoi_description);
        TextView typeView = findViewById(R.id.ViewPoi_type);
        TextView addressView = findViewById(R.id.ViewPoi_address);
        TextView tripView = findViewById(R.id.ViewPoi_trip);

        if (nameView != null) nameView.setText(poiName);
        if (descriptionView != null) descriptionView.setText(poiDescription);
        if (typeView != null) typeView.setText(poiType);
        if (addressView != null) addressView.setText(poiAddress);
        if (tripView != null) tripView.setText(tripTitle);
    }
}