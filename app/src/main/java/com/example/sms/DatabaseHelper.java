package com.example.sms;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "sms_db";
    private static final String TABLE_NAME = "messages";
    private static final String COL_1 = "id";
    private static final String COL_2 = "sender";
    private static final String COL_3 = "message";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, SENDER TEXT, MESSAGE TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Method to insert a message
    public void insertMessage(String sender, String message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, sender);
        contentValues.put(COL_3, message);
        db.insert(TABLE_NAME, null, contentValues);
    }

    // Method to get all messages
    public ArrayList<String> getAllMessages() {
        ArrayList<String> messages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                messages.add("From: " + cursor.getString(1) + "\n" + cursor.getString(2));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return messages;
    }
}
