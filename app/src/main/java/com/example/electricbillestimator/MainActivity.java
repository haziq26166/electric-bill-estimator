package com.example.electricbillestimator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View; // Required import statement added for view visibility handling
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
    Button btnCalculate, btnSave, btnViewHistory, btnAbout;
    DBHelper db;

    private String calcMonth = "";
    private int calcKwh = 0;
    private int calcRebatePercent = 0;
    private double calcTotalCharge = 0.0;
    private double calcFinalCost = 0.0;
    private boolean isCalculated = false;

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
        btnSave = findViewById(R.id.btnSave);
        btnViewHistory = findViewById(R.id.btnViewHistory);
        btnAbout = findViewById(R.id.btnAbout);

        // Explicitly ensuring the button is completely hidden on a fresh initialization
        btnSave.setVisibility(View.GONE);

        seekRebate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvRebateLabel.setText("Rebate Percentage: " + progress + "%");
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnCalculate.setOnClickListener(v -> calculateBill());
        btnSave.setOnClickListener(v -> saveRecord());

        btnViewHistory.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, HistoryActivity.class)));
        btnAbout.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AboutActivity.class)));
    }

    private void calculateBill() {
        String kwhStr = etKwh.getText().toString().trim();
        if (kwhStr.isEmpty()) {
            etKwh.setError("Please enter kWh usage.");
            Toast.makeText(this, "Please enter kWh usage.", Toast.LENGTH_SHORT).show();
            return;
        }

        int kwh = Integer.parseInt(kwhStr);
        if (kwh < 1 || kwh > 1000) {
            etKwh.setError("Error: Range is 1-1000 kWh");
            Toast.makeText(this, "Error: Range is 1-1000 kWh", Toast.LENGTH_LONG).show();
            return;
        }

        calcKwh = kwh;
        calcRebatePercent = seekRebate.getProgress();
        calcTotalCharge = calculateTotalCharge(calcKwh);
        calcFinalCost = calcTotalCharge - (calcTotalCharge * calcRebatePercent / 100.0);
        calcMonth = spinnerMonth.getSelectedItem().toString();
        isCalculated = true;

        String resultStr = String.format(Locale.getDefault(),
                "Total Charges: RM %.2f\nFinal Cost after %d%% rebate: RM %.2f",
                calcTotalCharge, calcRebatePercent, calcFinalCost);
        tvResult.setText(resultStr);

        // CHANGED: Dynamically reveal the save button directly under the calculate button
        btnSave.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Calculation complete! Ready to save.", Toast.LENGTH_SHORT).show();
    }

    private void saveRecord() {
        if (!isCalculated) {
            Toast.makeText(this, "Please calculate values first.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.insertBill(calcMonth, calcKwh, calcRebatePercent, calcTotalCharge, calcFinalCost);

        Toast.makeText(this, "Data Saved successfully!", Toast.LENGTH_SHORT).show();
        tvResult.append("\n\nStatus: Saved to History");

        // CHANGED: Hide the button again following a successful save operation to prevent duplicates
        btnSave.setVisibility(View.GONE);
        isCalculated = false;
    }

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