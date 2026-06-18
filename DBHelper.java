package com.example.melikeproje;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DBNAME = "CampusEvents.db";
    public static final int DBVERSION = 10; 

    public DBHelper(Context context) {
        super(context, DBNAME, null, DBVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE COLLATE NOCASE, " +
                "password TEXT, " +
                "role TEXT, " +
                "email TEXT UNIQUE COLLATE NOCASE, " +
                "bio TEXT, " +
                "school TEXT, " +
                "department TEXT, " +
                "club_name TEXT, " +
                "profile_image TEXT)");

        db.execSQL("CREATE TABLE events (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "description TEXT, " +
                "date TEXT, " +
                "location TEXT, " +
                "owner_id INTEGER)");

        db.execSQL("CREATE TABLE participations (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "event_id INTEGER, " +
                "username TEXT, " +
                "status TEXT)");

        db.execSQL("CREATE TABLE posts (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT, " +
                "content TEXT, " +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS events");
        db.execSQL("DROP TABLE IF EXISTS participations");
        db.execSQL("DROP TABLE IF EXISTS posts");
        onCreate(db);
    }

    public boolean insertUser(String username, String password, String role, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username.trim());
        cv.put("password", password.trim());
        cv.put("role", role);
        cv.put("email", email.trim());
        long result = db.insert("users", null, cv);
        return result != -1;
    }

    public int getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM users WHERE username = ?", new String[]{username});
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            cursor.close();
            return id;
        }
        cursor.close();
        return -1;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ? COLLATE NOCASE AND password = ?", 
                new String[]{username.trim(), password.trim()});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean checkUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ? COLLATE NOCASE", new String[]{username.trim()});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean checkEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ? COLLATE NOCASE", new String[]{email.trim()});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean updatePassword(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("password", password.trim());
        return db.update("users", cv, "username = ? COLLATE NOCASE", new String[]{username.trim()}) > 0;
    }

    public boolean updatePasswordByEmail(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("password", password.trim());
        return db.update("users", cv, "email = ? COLLATE NOCASE", new String[]{email.trim()}) > 0;
    }

    public String getUserRole(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT role FROM users WHERE username = ? COLLATE NOCASE", new String[]{username.trim()});
        if (cursor.moveToFirst()) {
            String role = cursor.getString(0);
            cursor.close();
            return role;
        }
        cursor.close();
        return null;
    }

    public Cursor getUserData(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM users WHERE username = ? COLLATE NOCASE", new String[]{username.trim()});
    }

    public boolean updateProfile(String username, String bio, String school, String department, String clubName, String profileImage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("bio", bio);
        cv.put("school", school);
        cv.put("department", department);
        cv.put("club_name", clubName);
        cv.put("profile_image", profileImage);
        return db.update("users", cv, "username = ? COLLATE NOCASE", new String[]{username.trim()}) > 0;
    }

    public boolean insertEvent(String name, String description, String date, String location, int owner_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("description", description);
        cv.put("date", date);
        cv.put("location", location);
        cv.put("owner_id", owner_id);
        return db.insert("events", null, cv) != -1;
    }

    public Cursor getAllEvents() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM events", null);
    }

    public boolean joinEvent(int eventId, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Zaten katılmış mı kontrol et
        Cursor c = db.rawQuery("SELECT * FROM participations WHERE event_id = ? AND username = ?", 
                new String[]{String.valueOf(eventId), username});
        if (c.getCount() > 0) {
            c.close();
            return true; 
        }
        c.close();
        
        ContentValues cv = new ContentValues();
        cv.put("event_id", eventId);
        cv.put("username", username);
        cv.put("status", "joined");
        return db.insert("participations", null, cv) != -1;
    }

    public Cursor getJoinedEvents(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT events.* FROM events " +
                "INNER JOIN participations ON events.id = participations.event_id " +
                "WHERE participations.username = ?";
        return db.rawQuery(query, new String[]{username});
    }

    public boolean insertPost(String username, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("content", content);
        return db.insert("posts", null, cv) != -1;
    }

    public Cursor getAllPostsWithProfile() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT posts.*, users.profile_image FROM posts " +
                "LEFT JOIN users ON posts.username = users.username " +
                "ORDER BY posts.timestamp DESC";
        return db.rawQuery(query, null);
    }
}
