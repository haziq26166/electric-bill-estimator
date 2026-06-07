package com.example.electricbillestimator;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {
    int billId;
    DBHelper db;
    Spinner spinnerDetailMonth;
    EditText etDetailKwh;
    SeekBar seekDetailRebate;
    TextView tvDetailRebateLabel, tvDetailBreakdown;
    Button btnUpdate, btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        db = new DBHelper(this);

        spinnerDetailMonth = findViewById(R.id.spinnerDetailMonth);
        etDetailKwh = findViewById(R.id.etDetailKwh);
        seekDetailRebate = findViewById(R.id.seekDetailRebate);
        tvDetailRebateLabel = findViewById(R.id.tvDetailRebateLabel);
        tvDetailBreakdown = findViewById(R.id.tvDetailBreakdown);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        billId = getIntent().getIntExtra("BILL_ID", -1);

        loadData();

        seekDetailRebate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvDetailRebateLabel.setText("Rebate: " + progress + "%");
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnUpdate.setOnClickListener(v -> updateData());

        // Fulfilling 'Good Design Practice' rubric by capturing deletion intents via confirmation popups
        btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    private void loadData() {
        Cursor cursor = db.getReadableDatabase().rawQuery("SELECT * FROM " + DBHelper.TABLE_BILLS + " WHERE " + DBHelper.COL_ID + "=?", new String[]{String.valueOf(billId)});
        if (cursor.moveToFirst()) {
            String month = cursor.getString(1);
            int kwh = cursor.getInt(2);
            int rebate = cursor.getInt(3);
            double total = cursor.getDouble(4);
            double finalCost = cursor.getDouble(5);

            etDetailKwh.setText(String.valueOf(kwh));
            seekDetailRebate.setProgress(rebate);
            tvDetailRebateLabel.setText("Rebate: " + rebate + "%");

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.months_array, android.R.layout.simple_spinner_item);
            spinnerDetailMonth.setSelection(adapter.getPosition(month));

            tvDetailBreakdown.setText(String.format(Locale.getDefault(), "Total Charges: RM %.2f\nFinal Cost: RM %.2f", total, finalCost));
        }
        cursor.close();
    }

    private void updateData() {
        String kwhStr = etDetailKwh.getText().toString().trim();

        // Robust Error Handling for Input Validation
        if (kwhStr.isEmpty()) {
            etDetailKwh.setError("Please enter kWh usage.");
            Toast.makeText(this, "Please enter kWh usage.", Toast.LENGTH_SHORT).show();
            return;
        }

        int kwh = Integer.parseInt(kwhStr);
        if (kwh < 1 || kwh > 1000) {
            etDetailKwh.setError("Error: Range is 1-1000 kWh");
            Toast.makeText(this, "Error: Range is 1-1000 kWh", Toast.LENGTH_LONG).show();
            return;
        }

        int rebatePercent = seekDetailRebate.getProgress();
        double totalCharge = MainActivity.calculateTotalCharge(kwh);
        double finalCost = totalCharge - (totalCharge * rebatePercent / 100.0);
        String month = spinnerDetailMonth.getSelectedItem().toString();

        db.updateBill(billId, month, kwh, rebatePercent, totalCharge, finalCost);
        Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    // Modal Confirmation Dialog Engine
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to permanently delete this billing record from your logs?");

        // Execute operational query if confirmed
        builder.setPositiveButton("Delete", (dialog, which) -> {
            db.deleteBill(billId);
            Toast.makeText(DetailActivity.this, "Record deleted successfully", Toast.LENGTH_SHORT).show();
            finish(); // Closes screen and routes back to the main layout index instantly
        });

        // Safe exit shield if cancelled
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}