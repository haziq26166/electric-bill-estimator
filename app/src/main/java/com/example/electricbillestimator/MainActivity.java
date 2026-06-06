package com.example.electricbillestimator;

import android.content.Intent;
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

public class MainActivity extends AppCompatActivity {

    Spinner spinnerMonth;
    EditText etKwh;
    SeekBar seekRebate;
    TextView tvRebateLabel, tvResult;
    Button btnCalculate, btnViewHistory, btnAbout;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DBHelper(this);

        spinnerMonth = findViewById(R.id.spinnerMonth);
        etKwh = findViewById(R.id.etKwh);
        seekRebate = findViewById(R.id.seekRebate);
        tvRebateLabel = findViewById(R.id.tvRebateLabel);
        tvResult = findViewById(R.id.tvResult);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnViewHistory = findViewById(R.id.btnViewHistory);
        btnAbout = findViewById(R.id.btnAbout);

        seekRebate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvRebateLabel.setText("Rebate Percentage: " + progress + "%");
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnCalculate.setOnClickListener(v -> calculateAndSave());
        btnViewHistory.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, HistoryActivity.class)));
        btnAbout.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AboutActivity.class)));
    }

    private void calculateAndSave() {
        String kwhStr = etKwh.getText().toString();
        if (kwhStr.isEmpty()) {
            Toast.makeText(this, "Please enter kWh usage.", Toast.LENGTH_SHORT).show();
            return;
        }

        int kwh = Integer.parseInt(kwhStr);
        if (kwh < 1 || kwh > 1000) {
            Toast.makeText(this, "Error: Range is 1-1000 kWh", Toast.LENGTH_LONG).show();
            return;
        }

        double totalCharge = calculateTotalCharge(kwh);
        int rebatePercent = seekRebate.getProgress();
        double finalCost = totalCharge - (totalCharge * rebatePercent / 100.0);

        String month = spinnerMonth.getSelectedItem().toString();
        db.insertBill(month, kwh, rebatePercent, totalCharge, finalCost);

        String resultStr = String.format(Locale.getDefault(), "Total Charges: RM %.2f\nFinal Cost after %d%% rebate: RM %.2f\n\nData Saved successfully!", totalCharge, rebatePercent, finalCost);
        tvResult.setText(resultStr);
    }

    // Calculation logic mapping to specific blocks
    public static double calculateTotalCharge(int kwh) {
        double total = 0;
        if (kwh > 600) {
            total += (kwh - 600) * 0.546;
            kwh = 600;
        }
        if (kwh > 300) {
            total += (kwh - 300) * 0.516;
            kwh = 300;
        }
        if (kwh > 200) {
            total += (kwh - 200) * 0.334;
            kwh = 200;
        }
        if (kwh > 0) {
            total += kwh * 0.218;
        }
        return total;
    }
}