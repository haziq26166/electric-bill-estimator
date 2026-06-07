package com.example.electricbillestimator;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {
    ListView listView;
    DBHelper db;
    ArrayList<String> listData;
    ArrayList<Integer> listIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        listView = findViewById(R.id.listView);
        db = new DBHelper(this);

        // Optional Back Arrow Setup
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Saved Calculations");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        Cursor cursor = db.getAllBills();
        listData = new ArrayList<>();
        listIds = new ArrayList<>();

        if (cursor.getCount() == 0) {
            listData.add("No calculation records found.");
        } else {
            while (cursor.moveToNext()) {
                listIds.add(cursor.getInt(0));
                // Enhanced, cleaner typography output string
                String display = cursor.getString(1) + " Record  •  Final Cost: RM " + String.format("%.2f", cursor.getDouble(5));
                listData.add(display);
            }
        }

        // Custom Layout Mapping (Using list_item_history & tvHistoryRowText target id)
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.list_item_history,
                R.id.tvHistoryRowText,
                listData
        );
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            // Guard clause to prevent navigation attempt on click if empty message row is visible
            if (listIds.size() > 0) {
                Intent intent = new Intent(HistoryActivity.this, DetailActivity.class);
                intent.putExtra("BILL_ID", listIds.get(position));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}