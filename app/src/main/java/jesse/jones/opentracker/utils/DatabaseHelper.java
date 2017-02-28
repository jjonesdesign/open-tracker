package jesse.jones.opentracker.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jesse.jones.opentracker.utils.entity.local.ActivityEntry;

/**
 * Created by admin on 2/27/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "openTrackerDB";

    // Table Names
    private static final String TABLE_FILES = "activities";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_CREATED_AT = "created_at";

    // ACTIVITIES Table - column names
    private static final String KEY_NAME = "name";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_STATUS = "status";



    // Table Create Statements
    private static final String CREATE_TABLE_TODO =
            "CREATE TABLE " + TABLE_FILES + "(" +
                    KEY_ID + " INTEGER PRIMARY KEY," +
                    KEY_NAME + " TEXT," +
                    KEY_DESCRIPTION + " TEXT," +
                    KEY_LATITUDE + " TEXT," +
                    KEY_LONGITUDE + " TEXT," +
                    KEY_STATUS + " INTEGER," +
                    KEY_CREATED_AT + " DATETIME" + ")";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_TODO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILES);

        // create new tables
        onCreate(db);
    }


    /*
     * Creating a file entry
     */
    public long createActivityEntry(ActivityEntry activityEntry) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, activityEntry.getName());
        values.put(KEY_DESCRIPTION, activityEntry.getDescription());
        values.put(KEY_LATITUDE, activityEntry.getLatitude());
        values.put(KEY_LONGITUDE, activityEntry.getLongitude());
        values.put(KEY_STATUS, activityEntry.getStatus());
        values.put(KEY_CREATED_AT, getDateTime());

        // insert row
        long rowId = -1;
        try {
            rowId = db.insertOrThrow(TABLE_FILES, null, values);
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return rowId;
    }

    /*
    * get single ActivitiesEntry by id
    */
    public ActivityEntry getActivityEntry(int entryId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_FILES + " WHERE "
                + KEY_ID + " = " + entryId;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null) {
            c.moveToFirst();
        }

        ActivityEntry activityEntry = new ActivityEntry();
        activityEntry.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        activityEntry.setName((c.getString(c.getColumnIndex(KEY_NAME))));
        activityEntry.setDescription((c.getString(c.getColumnIndex(KEY_DESCRIPTION))));
        activityEntry.setLatitude((c.getString(c.getColumnIndex(KEY_LATITUDE))));
        activityEntry.setLongitude((c.getString(c.getColumnIndex(KEY_LONGITUDE))));
        activityEntry.setStatus((c.getInt(c.getColumnIndex(KEY_STATUS))));
        activityEntry.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));

        return activityEntry;
    }

    /*
    * get all activityEntries
    */
    public List<ActivityEntry> getActivityEntries() {
        List<ActivityEntry> activityEntries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_FILES;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {

                ActivityEntry activityEntry = new ActivityEntry();
                activityEntry.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                activityEntry.setName((c.getString(c.getColumnIndex(KEY_NAME))));
                activityEntry.setDescription((c.getString(c.getColumnIndex(KEY_DESCRIPTION))));
                activityEntry.setLatitude((c.getString(c.getColumnIndex(KEY_LATITUDE))));
                activityEntry.setLongitude((c.getString(c.getColumnIndex(KEY_LONGITUDE))));
                activityEntry.setStatus((c.getInt(c.getColumnIndex(KEY_STATUS))));
                activityEntry.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));

                // adding to media list
                activityEntries.add(activityEntry);
            } while (c.moveToNext());
        }

        return activityEntries;
    }

    public long updateActivtyEntry(ActivityEntry activityEntry) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, activityEntry.getName());
        values.put(KEY_DESCRIPTION, activityEntry.getDescription());
        values.put(KEY_LATITUDE, activityEntry.getLatitude());
        values.put(KEY_LONGITUDE, activityEntry.getLongitude());
        values.put(KEY_STATUS, activityEntry.getStatus());
        values.put(KEY_CREATED_AT, getDateTime());

        // updating row
        return db.update(TABLE_FILES, values, KEY_ID + " = ?", new String[] { String.valueOf(activityEntry.getId()) });
    }

    /*
    * Deleting a ActivityEntry by id
    */
    public void deleteActivityEntry(int activityId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FILES, KEY_ID + " = ?",
                new String[] { String.valueOf(activityId) });
    }

    /*
    * Deleting a ActivityEntry by reference
    */
    public void deleteActivityEntry(ActivityEntry activityEntry) {
        if (activityEntry == null) {
            return;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FILES, KEY_ID + " = ?",
                new String[] { String.valueOf(activityEntry.getId()) });
    }


    /**
     * get datetime
     * */
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    // closing database
    public void closeDataBase() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}
