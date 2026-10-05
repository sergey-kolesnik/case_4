package com.example.meetingnotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/** Инкапсулирует создание таблицы и CRUD-операции SQLite. */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "meeting_notes.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE = "notes";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " (" +
                "id TEXT PRIMARY KEY, " +
                "title TEXT NOT NULL, " +
                "content TEXT NOT NULL, " +
                "event_at INTEGER NOT NULL, " +
                "storage TEXT NOT NULL, " +
                "created_at INTEGER NOT NULL, " +
                "updated_at INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // В первой версии миграции не требуются. Здесь появятся ALTER TABLE для будущих версий.
    }

    public boolean save(Note note) {
        ContentValues values = new ContentValues();
        values.put("id", note.getId());
        values.put("title", note.getTitle());
        values.put("content", note.getContent());
        values.put("event_at", note.getEventAt());
        values.put("storage", Note.STORAGE_SQLITE);
        values.put("created_at", note.getCreatedAt());
        values.put("updated_at", note.getUpdatedAt());
        return getWritableDatabase().insertWithOnConflict(
                TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE) != -1;
    }

    public List<Note> getAll() {
        List<Note> result = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query(
                TABLE, null, null, null, null, null, "updated_at DESC")) {
            while (cursor.moveToNext()) {
                result.add(fromCursor(cursor));
            }
        }
        return result;
    }

    public boolean delete(String id) {
        return getWritableDatabase().delete(TABLE, "id = ?", new String[]{id}) > 0;
    }

    private Note fromCursor(Cursor cursor) {
        return new Note(
                cursor.getString(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("title")),
                cursor.getString(cursor.getColumnIndexOrThrow("content")),
                cursor.getLong(cursor.getColumnIndexOrThrow("event_at")),
                Note.STORAGE_SQLITE,
                cursor.getLong(cursor.getColumnIndexOrThrow("created_at")),
                cursor.getLong(cursor.getColumnIndexOrThrow("updated_at"))
        );
    }
}
