package com.example.electricbillestimator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "BillsDB";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_BILLS = "bills";
    public static final String COL_ID = "id";
    public static final String COL_MONTH = "month";
    public static final String COL_KWH = "kwh";
    public static final String COL_REBATE = "rebate";
    public static final String COL_TOTAL = "total_charge";
    public static final String COL_FINAL = "final_cost";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_BILLS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_MONTH + " TEXT, " + COL_KWH + " INTEGER, " +
                COL_REBATE + " INTEGER, " + COL_TOTAL + " REAL, " +
                COL_FINAL + " REAL)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BILLS);
        onCreate(db);
    }

    public long insertBill(String month, int kwh, int rebate, double total, double finalCost) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_MONTH, month);
        cv.put(COL_KWH, kwh);
        cv.put(COL_REBATE, rebate);
        cv.put(COL_TOTAL, total);
        cv.put(COL_FINAL, finalCost);
        return db.insert(TABLE_BILLS, null, cv);
    }

    public Cursor getAllBills() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_BILLS, null);
    }

    public void updateBill(int id, String month, int kwh, int rebate, double total, double finalCost) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_MONTH, month);
        cv.put(COL_KWH, kwh);
        cv.put(COL_REBATE, rebate);
        cv.put(COL_TOTAL, total);
        cv.put(COL_FINAL, finalCost);
        db.update(TABLE_BILLS, cv, COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void deleteBill(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BILLS, COL_ID + "=?", new String[]{String.valueOf(id)});
    }
}