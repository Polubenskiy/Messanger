package com.example.messenger;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;


public class DatabaseHelper extends SQLiteOpenHelper
{

    SQLiteDatabase database;
    private int key = 0;

    public int getKey()
    {
        return key;
    }

    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase database)
    {
        String sql = "CREATE TABLE MESSAGE (id int Primary key, Nickname txt, Information txt, portSender int, portRecipient int, timeMessage txt)";
        database.execSQL(sql);

        sql = "CREATE TABLE SETTING (id int Primary key, Nickname txt, portSender txt, portRecipient txt, address txt)";
        database.execSQL(sql);
    }

    public void AddMessage( String Nickname, int portSender, String Information, int portRecipient, String timeMessage)
    {
        database = getWritableDatabase();
        ContentValues insertValues = new ContentValues();

        this.key = CountMessage();

        insertValues.put("id", this.key);
        this.key++;
        insertValues.put("Nickname", Nickname);
        insertValues.put("portSender", portSender);
        insertValues.put("Information", Information);
        insertValues.put("portRecipient", portRecipient);
        insertValues.put("timeMessage", String.valueOf(timeMessage));

        database.insert("MESSAGE", null, insertValues);
    }

    public String SelectMessages(int key, String value)
    {
        String sql = "SELECT " + value + " FROM MESSAGE WHERE id = '" + key + "';";
        SQLiteDatabase db = getReadableDatabase();
        @SuppressLint("Recycle") Cursor cur = db.rawQuery(sql, null);
        if (cur.moveToFirst())
            return cur.getString(0);
        return "";
    }

    public int CountMessage()
    {
        String slq = "SELECT COUNT(*) FROM MESSAGE";
        database = getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery(slq, null);
        if (cursor != null && cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            return cursor.getInt(0);
        }
        return 0;
    }

    public void AddSetting(int id, String nickName,  String address, int sP, int rP)
    {
        database = getWritableDatabase();
        ContentValues insertValues = new ContentValues();

        insertValues.put("id", id);
        insertValues.put("Nickname", nickName);
        insertValues.put("portSender", sP);
        insertValues.put("address", address);
        insertValues.put("portRecipient", rP);

        database.insert("SETTING", null, insertValues);
    }

    public String SelectSetting(int key, String value)
    {
        String sql = "SELECT " + value + " FROM SETTING WHERE id = '" + key + "';";
        SQLiteDatabase db = getReadableDatabase();
        @SuppressLint("Recycle") Cursor cur = db.rawQuery(sql, null);
        if (cur.moveToFirst())
            return cur.getString(0);
        return "";
    }

    public void UpdateSetting(int key, String value, String oldValue)
    {
        String sql = "UPDATE SETTING SET " + oldValue + " = '" + value + "' WHERE id = '"+ key +"';";
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sql);
    }

    public void DeleteMessage()
    {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "DELETE FROM MESSAGE;";
        database.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }
}