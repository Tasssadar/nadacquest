package com.tassadar.nadacquest;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;

public class NadacDB {
    private NadacDB(SQLiteDatabase db) {
        m_db = db;
    }

    private static NadacDB instance = null;
    public static NadacDB Get() {
        return instance;
    }

    public static void Destroy() {
        instance.Close();
        instance = null;
    }

    public static NadacDB Open(Context ctx) {
        String path = Utils.extractAsset(ctx, "nadacdb.sqlite");
        if(path == null)
            return null;

        SQLiteDatabase db = null;
        try {
            db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        } catch(SQLiteException e) {
            e.printStackTrace();
            return null;
        }
        instance = new NadacDB(db);
        return instance;
    }

    public void Close() {
        m_db.close();
        m_db = null;
        m_nadaci.clear();
    }

    public boolean LoadAll() {
        if(m_db == null)
            return false;

        m_nadaci.clear();
        m_schools.clear();

        //                        0     1       2       3       4          5
        final String[] cols = { "id", "name", "school", "year", "hobbies", "photo" };
        Cursor c = m_db.query("nadaci", cols, "photo_url!=''", null, null, null, null);

        while(c.moveToNext()) {
            Nadac n = new Nadac();
            n.id = c.getInt(0);
            n.name = c.getString(1);
            n.school = c.getString(2);
            n.year = c.getInt(3);
            n.hobbies = c.getString(4);
            byte[] data = c.getBlob(5);
            if(data != null)
                n.photo = BitmapFactory.decodeByteArray(data, 0, data.length);
            m_nadaci.add(n);
        }
        c.close();

        final String[] school_cols = { "school" };
        c = m_db.query("nadaci", school_cols, null, null, "school", null, null);
        while(c.moveToNext()) {
            m_schools.add(c.getString(0));
        }
        c.close();
        return true;
    }

    public Bitmap LoadPhoto(Nadac n) {
        if(n.photo != null)
            return n.photo;

        if(m_db == null)
            return null;

        //                      0
        final String[] cols = { "photo" };
        final String[] args = { String.valueOf(n.id) };
        Cursor c = m_db.query("nadaci", cols, "id=?", args, null, null, null);
        if(!c.moveToNext())
            return null;

        byte[] data = c.getBlob(0);
        if(data == null)
            return null;

        n.photo = BitmapFactory.decodeByteArray(data, 0, data.length);
        c.close();
        return n.photo;
    }

    public ArrayList<Nadac> getNadace() { return m_nadaci; }
    public ArrayList<String> getSchools() { return m_schools; }


    private SQLiteDatabase m_db;
    private ArrayList<Nadac> m_nadaci = new ArrayList<Nadac>();
    private ArrayList<String> m_schools = new ArrayList<>();
}
