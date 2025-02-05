package com.example.studyplanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "StudyPlanner.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_TIMETABLE = "timetable";
    private static final String TABLE_SUBJECTS = "subjects";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_EMAIL = "email";

    // Users Table Columns
    private static final String KEY_NAME = "name";
    private static final String KEY_AGE = "age";
    private static final String KEY_COURSE = "course";
    private static final String KEY_PERFORMANCE = "performance";
    private static final String KEY_STUDY_HOURS = "study_hours";
    private static final String KEY_PASSWORD = "password";

    // Create Users Table
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS +
            "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            KEY_NAME + " TEXT," +
            KEY_EMAIL + " TEXT UNIQUE," +
            KEY_AGE + " INTEGER," +
            KEY_COURSE + " TEXT," +
            KEY_PERFORMANCE + " TEXT," +
            KEY_STUDY_HOURS + " INTEGER," +
            KEY_PASSWORD + " TEXT" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_USERS);
            Log.d(TAG, "Users table created successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error creating users table: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            onCreate(db);
            Log.d(TAG, "Database upgraded from " + oldVersion + " to " + newVersion);
        } catch (Exception e) {
            Log.e(TAG, "Error upgrading database: " + e.getMessage());
        }
    }

    public boolean addUser(UserModel user) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(KEY_NAME, user.getName());
            values.put(KEY_EMAIL, user.getEmail());
            values.put(KEY_AGE, user.getAge());
            values.put(KEY_COURSE, user.getCourse());
            values.put(KEY_PERFORMANCE, user.getPerformanceLevel());
            values.put(KEY_STUDY_HOURS, user.getStudyHoursPerDay());
            values.put(KEY_PASSWORD, user.getPassword());

            long result = db.insert(TABLE_USERS, null, values);
            Log.d(TAG, "User added successfully: " + user.getEmail());
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "Error adding user: " + e.getMessage());
            return false;
        }
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            String[] columns = {KEY_ID};
            String selection = KEY_EMAIL + " = ? AND " + KEY_PASSWORD + " = ?";
            String[] selectionArgs = {email, password};

            cursor = db.query(TABLE_USERS, columns, selection, selectionArgs,
                    null, null, null);
            
            boolean exists = cursor != null && cursor.getCount() > 0;
            Log.d(TAG, "User login attempt: " + email + " - " + (exists ? "Success" : "Failed"));
            return exists;
        } catch (Exception e) {
            Log.e(TAG, "Error checking user: " + e.getMessage());
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public UserModel getUser(String email) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            UserModel user = new UserModel();

            String[] columns = {KEY_ID, KEY_NAME, KEY_EMAIL, KEY_AGE, KEY_COURSE, 
                              KEY_PERFORMANCE, KEY_STUDY_HOURS};
            String selection = KEY_EMAIL + " = ?";
            String[] selectionArgs = {email};

            cursor = db.query(TABLE_USERS, columns, selection, selectionArgs,
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                user.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                user.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
                user.setAge(cursor.getInt(cursor.getColumnIndex(KEY_AGE)));
                user.setCourse(cursor.getString(cursor.getColumnIndex(KEY_COURSE)));
                user.setPerformanceLevel(cursor.getString(cursor.getColumnIndex(KEY_PERFORMANCE)));
                user.setStudyHoursPerDay(cursor.getInt(cursor.getColumnIndex(KEY_STUDY_HOURS)));
                Log.d(TAG, "User retrieved successfully: " + email);
                return user;
            }
            Log.d(TAG, "User not found: " + email);
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Error getting user: " + e.getMessage());
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
