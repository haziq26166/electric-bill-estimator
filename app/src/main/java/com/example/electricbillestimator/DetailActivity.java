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
        btnDelete.setOnClickListener(v -> {
            db.deleteBill(billId);
            Toast.makeText(DetailActivity.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
            finish();
        });
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

            // Set spinner position
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.months_array, android.R.layout.simple_spinner_item);
            spinnerDetailMonth.setSelection(adapter.getPosition(month));

            tvDetailBreakdown.setText(String.format(Locale.getDefault(), "Total Charges: RM %.2f\nFinal Cost: RM %.2f", total, finalCost));
        }
        cursor.close();
    }

    private void updateData() {
        int kwh = Integer.parseInt(etDetailKwh.getText().toString());
        int rebatePercent = seekDetailRebate.getProgress();
        double totalCharge = MainActivity.calculateTotalCharge(kwh);
        double finalCost = totalCharge - (totalCharge * rebatePercent / 100.0);
        String month = spinnerDetailMonth.getSelectedItem().toString();

        db.updateBill(billId, month, kwh, rebatePercent, totalCharge, finalCost);
        Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}