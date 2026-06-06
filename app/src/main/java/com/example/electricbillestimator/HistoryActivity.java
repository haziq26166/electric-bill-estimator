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
            listData.add("No records found.");
        } else {
            while (cursor.moveToNext()) {
                listIds.add(cursor.getInt(0));
                String display = "Month: " + cursor.getString(1) + " | Final Cost: RM " + String.format("%.2f", cursor.getDouble(5));
                listData.add(display);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (listIds.size() > 0) {
                Intent intent = new Intent(HistoryActivity.this, DetailActivity.class);
                intent.putExtra("BILL_ID", listIds.get(position));
                startActivity(intent);
            }
        });
    }
}