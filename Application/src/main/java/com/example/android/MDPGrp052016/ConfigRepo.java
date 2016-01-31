package com.example.android.MDPGrp052016;

/**
 * Created by desmondphua on 31/1/16.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.HashMap;
public class ConfigRepo {
    private DBHelper dbHelper;
    public ConfigRepo(Context context) {
        dbHelper = new DBHelper(context);
    }
    public int insert(Config config) {

        //Open connection to write data
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Config.KEY_name, config.name);
        values.put(Config.KEY_binding, config.binding);


        // Inserting Row
        long config_Id = db.insert(Config.TABLE, null, values);
        db.close(); // Closing database connection
        return (int) config_Id;
    }
    public void delete(String config_name) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // It's a good practice to use parameter ?, instead of concatenate string
        db.delete(Config.TABLE, Config.KEY_name + "= ?", new String[] { (config_name) });
        db.close(); // Closing database connection
    }

    public void update(Config config) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Config.KEY_name, config.name);
        values.put(Config.KEY_binding, config.binding);

        // It's a good practice to use parameter ?, instead of concatenate string
        db.update(Config.TABLE, values, Config.KEY_name + "= ?", new String[]{(config.name)});
        db.close(); // Closing database connection
    }

    public ArrayList<HashMap<String, String>>  getConfigList() {
        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Config.KEY_name + "," +
                Config.KEY_binding +
                " FROM " + Config.TABLE;

        //Student student = new Student();
        ArrayList<HashMap<String, String>> configList = new ArrayList<HashMap<String, String>>();

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> config = new HashMap<String, String>();
                config.put("name", cursor.getString(cursor.getColumnIndex(Config.KEY_name)));
                config.put("binding", cursor.getString(cursor.getColumnIndex(Config.KEY_binding)));
                configList.add(config);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return configList;

    }

    public Config getconfigByname(String name){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Config.KEY_name + "," +
                Config.KEY_binding +
                " FROM " + Config.TABLE
                + " WHERE " +
                Config.KEY_name + "=?";// It's a good practice to use parameter ?, instead of concatenate string

        int iCount =0;
        Config config = new Config();

        Cursor cursor = db.rawQuery(selectQuery, new String[] { (name) } );

        if (cursor.moveToFirst()) {
            do {
                config.name =cursor.getString(cursor.getColumnIndex(Config.KEY_name));
                config.binding =cursor.getString(cursor.getColumnIndex(Config.KEY_binding));

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return config;
    }

    public int getConfigCount() {
        String countQuery = "SELECT  * FROM " + Config.TABLE;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        db.close();
        return cnt;
    }

}
