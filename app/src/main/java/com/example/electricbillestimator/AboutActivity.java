package com.example.electricbillestimator;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Action Bar support configuration (Optional: un-comment if using local action bar navigation)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("About Developer");
        }
    }

    // Handles the top header back arrow tap smoothly if your app theme utilizes action bars
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}