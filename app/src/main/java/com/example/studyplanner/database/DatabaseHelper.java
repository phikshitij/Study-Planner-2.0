package com.example.studyplanner.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.studyplanner.models.ChatMessage;
import com.example.studyplanner.models.ChatMessage;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "StudyPlannerDB";
    private static final int DATABASE_VERSION = 1;

    // Table name
    private static final String TABLE_CHAT = "chat_messages";

    // Column names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_MESSAGE = "message";
    private static final String COLUMN_IS_USER = "is_user";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    // Create table SQL query
    private static final String CREATE_TABLE_CHAT = "CREATE TABLE " + TABLE_CHAT + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_MESSAGE + " TEXT,"
            + COLUMN_IS_USER + " INTEGER,"
            + COLUMN_TIMESTAMP + " INTEGER"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CHAT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT);
        onCreate(db);
    }

    // Insert a new message
    public long insertMessage(ChatMessage message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MESSAGE, message.getMessage());
        values.put(COLUMN_IS_USER, message.isUser() ? 1 : 0);
        values.put(COLUMN_TIMESTAMP, System.currentTimeMillis());

        long id = db.insert(TABLE_CHAT, null, values);
        db.close();
        return id;
    }

    // Get all messages
    public List<ChatMessage> getAllMessages() {
        List<ChatMessage> messages = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CHAT + " ORDER BY " + COLUMN_TIMESTAMP + " ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ChatMessage message = new ChatMessage(
                    cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_IS_USER)) == 1
                );
                messages.add(message);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return messages;
    }

    // Delete all messages
    public void deleteAllMessages() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CHAT, null, null);
        db.close();
    }
}
