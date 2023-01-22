package com.bdtask.cuminpass.model;

public class UserData {

    int id;
    String accountName;
    String accountKey;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountKey() {
        return accountKey;
    }

    public void setAccountKey(String accountKey) {
        this.accountKey = accountKey;
    }

    //for Sqlite Database
    public static final String TABLE_NAME = "user_data";
    public static final String COLUMN_ACCOUNT_ID = "id";
    public static final String COLUMN_ACCOUNT_NAME = "account_name";
    public static final String COLUMN_ACCOUNT_KEY = "account_key";


    // Create table SQL query
    public static final String CREATE_USER_DATA_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ACCOUNT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_ACCOUNT_NAME + " TEXT,"
                    + COLUMN_ACCOUNT_KEY+" TEXT "
                    + ")";
}



