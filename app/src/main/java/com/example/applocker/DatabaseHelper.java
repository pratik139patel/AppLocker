package com.example.applocker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    public static final String TABLE_NAME = "ApplicationInfo_table";
    private static final String COL1 = "ID";
    private static final String COL2 = "packageInfo";
    private static final String COL3 = "userPassword";

    public DatabaseHelper(Context context) {

        super(context, TABLE_NAME, null, 1);

    }

    /*
    * Create table that holds the string 'package name' for each saved
    *  item.
    */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL2 + " TEXT, " +COL3+ " TEXT)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /*
    * isPresent:
    *  Checks to see if the item is already present in the database.
    * Returns true if found, else false.
    */
    private boolean isPresent(String searchItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = { COL2 };  // locating packageInfo (COL2)
        String selection = COL2 + " =?";
        String[] selectionArgs = { searchItem };
        String limit = "1";  // How many should be present in the database

        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null, limit);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        // return if the item is found
        return exists;
    }

    /*
    * addData:
    *  taking the provided item, add it to the database if it does not exist.
    * @param: item to be added into database
    * @post-condition: returns 0 if item not added, returns 1 if added, returns 2
    *    if already present
    */
    public int addData(String item, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, item);
        contentValues.put(COL3, password);

        if (!isPresent(item)) {
            long result = db.insert(TABLE_NAME, null, contentValues);
            if (result == -1) {
                return 0;
            }
            return 1;
        }
        Log.e(TAG, "addData: Failed to add Item, it already exists in the db!");
        return 2;
    }

    /*
     * updateCol:
     *  Updates the provided password for the app.
     */
    public void updateCol(String name, String updatedPassword) {

        // Get app id
        Cursor data = getItemID(name);
        int id = -1;
        while (data.moveToNext()) {
            id = data.getInt(0);
        }
        // Update the table here if the item was located
        if (id > -1) {
            SQLiteDatabase db= this.getWritableDatabase();
            ContentValues cv = new ContentValues();

            cv.put(COL3, updatedPassword);  // Add the updated password
            db.update(TABLE_NAME, cv, "ID = ?", new String[]{Integer.toString(id)});
        }
    }


    /*
    * getData:
    *  Returns data set that is stored into the current writeable database.
    *  Returns a cursor holding the row information
    */
    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    /*
    * getItemId:
    *  gets the ID of the provided item.
    *  Returns the cursor holding the row information
    */
    public Cursor getItemID(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL1 + " FROM " + TABLE_NAME +
                " WHERE " + COL2 + " = '" + name + "'";
        Log.d(TAG, "getItemID: query: " + query);
        return db.rawQuery(query, null);
    }

    /*
    * deleteApp:
    *  Removes the app if it exists in the database
    */
    public void deleteApp(String name) {

        // Get app id
        Cursor data = getItemID(name);
        int id = -1;
        while (data.moveToNext()) {
            id = data.getInt(0);
        }
        // Update the table here if the item was located
        if (id > -1) {
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "DELETE FROM " + TABLE_NAME + " WHERE " +
                    COL1 + " = '" + id + "' AND " + COL2 + " ='" + name + "'";

            Log.d(TAG, "deleteApp: query: " + query);
            Log.d(TAG, "deleteApp: Deleting: " + name + " from database");

            db.execSQL(query);
        } else {
            Log.e(TAG, "onItemClick: THE ID WAS NOT ABLE TO BE FOUND");
        }
    }
}

