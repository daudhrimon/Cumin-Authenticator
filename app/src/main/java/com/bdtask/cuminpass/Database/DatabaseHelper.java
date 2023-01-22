package com.bdtask.cuminpass.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

import com.bdtask.cuminpass.model.UserData;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 12;
    // Database Name
    private static final String DATABASE_NAME = "userData.db";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(UserData.CREATE_USER_DATA_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + UserData.TABLE_NAME);
        onCreate(db);
    }


    //!..................... User Data Operation...........................!
    public long insertUserData(String accountName, String accountKey) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UserData.COLUMN_ACCOUNT_NAME,accountName);
        values.put(UserData.COLUMN_ACCOUNT_KEY,accountKey);


        // insert row
        long result = db.insert(UserData.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return result;
    }

    public int updateData(int id,String accountName, String accountKey){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(UserData.COLUMN_ACCOUNT_NAME,accountName);
        contentValues.put(UserData.COLUMN_ACCOUNT_KEY,accountKey);

        int status = sqLiteDatabase.update(UserData.TABLE_NAME,contentValues,"id=?",new String[]{String.valueOf(id)});


        return status;
    }


    public List<UserData> getAllUserData() {

        List<UserData> allStatesData = new ArrayList<>();


        String selectQuery = "SELECT  * FROM " + UserData.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                UserData userData = new UserData();
                userData.setId(cursor.getInt(cursor.getColumnIndex(UserData.COLUMN_ACCOUNT_ID)));
                userData.setAccountName(cursor.getString(cursor.getColumnIndex(UserData.COLUMN_ACCOUNT_NAME)));
                userData.setAccountKey(cursor.getString(cursor.getColumnIndex(UserData.COLUMN_ACCOUNT_KEY)));

                allStatesData.add(userData);

            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return  list
        return allStatesData;
    }



    public void deleteUserData(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE  FROM " + UserData.TABLE_NAME + " WHERE " + UserData.COLUMN_ACCOUNT_ID + "='" +id+"'");
        db.close();
    }



}
