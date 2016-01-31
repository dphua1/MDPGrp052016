package com.example.android.MDPGrp052016;

/**
 * Created by desmondphua on 31/1/16.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{
    //version number to upgrade database version
    //each time if you Add, Edit table, you need to change the
    //version number.
    private static final int DATABASE_VERSION = 4;

    // Database Name
    private static final String DATABASE_NAME = "crud.db";

    public DBHelper(Context context ) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //All necessary tables you like to create will create here

        String CREATE_TABLE_CONFIG = "CREATE TABLE " + Config.TABLE  + "("
                + Config.KEY_name  + " TEXT ,"
                + Config.KEY_binding + " TEXT )";

        db.execSQL(CREATE_TABLE_CONFIG);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed, all data will be gone!!!
        db.execSQL("DROP TABLE IF EXISTS " + Config.TABLE);

        // Create tables again
        onCreate(db);

    }
}
