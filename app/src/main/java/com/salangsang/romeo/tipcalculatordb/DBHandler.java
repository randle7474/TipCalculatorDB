package com.salangsang.romeo.tipcalculatordb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
/**
 * Created by romy7 on 7/22/2017.
 */

public class DBHandler extends SQLiteOpenHelper {
    private static final String DB_NAME = "money.db";
    private static final int DB_VERSION = 1;
    private static final String MONEY_TABLE = "money_table";
    private static final String COLUMN_ID = "_id";
    private static final int COLUMN_ID_NO = 0;
    private static final String COLUMN_BILL_DATE = "bill_date";
    private static final int COLUMN_BILL_DATE_ID = 1;
    private static final String COLUMN_BILL_AMOUNT = "bill_amount";
    private static final int COLUMN_BILL_AMOUNT_ID = 2;
    private static final String COLUMN_TIP_PERCENT = "tip_percent";
    private static final int COLUMN_TIP_PERCENT_ID = 3;
    ArrayList<Tip> tips = new ArrayList<Tip>();
    private int entries = 0;
    private static final String TAG = "DBHandler";
    private SQLiteDatabase database;
    private DBHandler dbHandler;


    public DBHandler(Context context, String name,
                     CursorFactory factory, int version) {
        super(context, DB_NAME, factory, DB_VERSION);

    }

    public static final String CREATE_TABLE = "CREATE TABLE " + MONEY_TABLE + "(" +

            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_BILL_DATE + " INTEGER, " +
            COLUMN_BILL_AMOUNT + " REAL, " +
            COLUMN_TIP_PERCENT + " REAL );";

    private void openDB() {
        database = dbHandler.getWritableDatabase();

    }

    private void closeDB() {
        if (database != null)
            database.close();
    }

    public void onCreate(SQLiteDatabase db) {
        // execute SQL
        db.execSQL(CREATE_TABLE);
        db.execSQL("INSERT INTO " + MONEY_TABLE + " VALUES (0, 0, 100.00, .15)");
        db.execSQL("INSERT INTO " + MONEY_TABLE + " VALUES (1, 0, 50.00, .25)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(" DROP TABLE IF EXIST " + MONEY_TABLE);
        onCreate(db);

    }

    public ArrayList<Tip> getTips(int num) {

        if (num == 1) {
            String query = "SELECT * FROM " + MONEY_TABLE + " WHERE 1 ";
            SQLiteDatabase db = getWritableDatabase();
            Cursor c = db.rawQuery(query, null);

            c.moveToFirst();

            while (!c.isAfterLast()) {
                int id = c.getInt(COLUMN_ID_NO);
                int dateMillis = c.getInt(COLUMN_BILL_DATE_ID);
                float billAmount = c.getFloat(COLUMN_BILL_AMOUNT_ID);
                float tipPercent = c.getFloat(COLUMN_TIP_PERCENT_ID);
                Tip tip = new Tip(id, dateMillis, billAmount, tipPercent);

                tips.add(tip);
                entries++;
                c.moveToNext();

            }
            db.close();

        } else {

        }
        return tips;

    }

    public void savedTipCalc(Tip tip) {
        ContentValues place = new ContentValues();
        place.put(COLUMN_BILL_DATE, tip.getDateMillis());
        place.put(COLUMN_BILL_AMOUNT, tip.getBillAmount());
        place.put(COLUMN_TIP_PERCENT, tip.getTipPercent());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(MONEY_TABLE, null, place);
        db.close();

    }

    public Tip getLastSaved() {
        Tip tip = new Tip();
        String query = "SELECT * FROM " + MONEY_TABLE + " WHERE 1";
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery(query, null);
        c.moveToLast();

        if (c.isLast()) {
            tip = cursorToTip(c);

        }
        db.close();
        return tip;

    }

    private Tip cursorToTip(Cursor c) {

        Tip tip = new Tip();
        tip.setId(c.getLong(0));
        tip.setDateMillis(c.getLong(1));
        tip.setBillAmount(c.getFloat(2));
        tip.setTipPercent(c.getFloat(3));

        return tip;
    }

    public Tip getAverageTip() {
        Tip tip = new Tip();
        String query = "SELECT AVG(" + COLUMN_TIP_PERCENT + ") FROM " + MONEY_TABLE;
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        if (c.isFirst()) {
            tip.setTipPercent(c.getFloat(0));
        }

        db.close();
        return tip;
    }

    public String databaseToString() {
        String dbString = " ";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM" + MONEY_TABLE + "WHERE 1";

        // Cursor point to a location in your result
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex("id")) != null) {
                dbString += c.getString(c.getColumnIndex("id"));
                dbString += "\n";
            }
        }
        db.close();
        return dbString;
    }
}