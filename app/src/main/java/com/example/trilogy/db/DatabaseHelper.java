package com.example.trilogy.db;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "game.db";
    private static final int DB_VERSION = 3;
    private static final String TABLE_Q = "q";

    public DatabaseHelper(Context c) {
        super(c, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TABLE_Q + " (" +
                        "id TEXT PRIMARY KEY, " +
                        "i1 TEXT, " +
                        "i2 TEXT, " +
                        "a TEXT, " +
                        "used INTEGER DEFAULT 0)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Q);
        onCreate(db);
    }

    public void insertQuestion(String id, String i1, String i2, String a) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("id", id);
        cv.put("i1", i1);
        cv.put("i2", i2);
        cv.put("a", a);

        db.insertWithOnConflict(TABLE_Q, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public Cursor getQuestion() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT * FROM " + TABLE_Q +
                        " WHERE used=0 ORDER BY RANDOM() LIMIT 1",
                null
        );

        if (c.getCount() == 0) {
            resetUsed();
            c.close();
            c = db.rawQuery(
                    "SELECT * FROM " + TABLE_Q +
                            " WHERE used=0 ORDER BY RANDOM() LIMIT 1",
                    null
            );
        }

        return c;
    }

    public void markUsed(String id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("used", 1);
        db.update(TABLE_Q, cv, "id=?", new String[]{id});
    }

    private void resetUsed() {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("used", 0);
        db.update(TABLE_Q, cv, null, null);
    }

    public boolean isEmpty() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_Q, null);
        c.moveToFirst();
        boolean empty = c.getInt(0) == 0;
        c.close();
        return empty;
    }
}


