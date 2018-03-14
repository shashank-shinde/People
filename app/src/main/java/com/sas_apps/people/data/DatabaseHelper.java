package com.sas_apps.people.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.sas_apps.people.data.PeopleContract.PeopleTable;

import static android.provider.BaseColumns._ID;
import static com.sas_apps.people.data.PeopleContract.PeopleTable.COL_EMAIL;
import static com.sas_apps.people.data.PeopleContract.PeopleTable.COL_IMAGE;
import static com.sas_apps.people.data.PeopleContract.PeopleTable.COL_NAME;
import static com.sas_apps.people.data.PeopleContract.PeopleTable.COL_NUMBER;
import static com.sas_apps.people.data.PeopleContract.PeopleTable.COL_TYPE;
import static com.sas_apps.people.data.PeopleContract.PeopleTable.TABLE_NAME;

/*
 * Created by Shashank Shinde.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        super(context, PeopleContract.DATABASE_NAME, null, PeopleContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PeopleTable.CREATE_PEOPLE_TABLE);
        Log.d(TAG, PeopleContract.DATABASE_NAME + " created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(PeopleTable.DELETE_TABLE);
        onCreate(db);
    }

    public boolean addPeople(PeopleModel contact) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NAME, contact.getName());
        contentValues.put(COL_NUMBER, contact.getphoneNumber());
        contentValues.put(COL_TYPE, contact.getDevice());
        contentValues.put(COL_EMAIL, contact.getEmail());
        contentValues.put(COL_IMAGE, contact.getProfileImage());
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public Cursor getAllPeople() {
        db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    public Cursor getPeopleID(PeopleModel contact) {
        db = this.getWritableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + COL_NAME + " = '" + contact.getName() + "'" +
                " AND " + COL_NUMBER + " = '" + contact.getphoneNumber() + "'";
        return db.rawQuery(sql, null);
    }

    public Integer deletePeople(int id) {
        db = this.getWritableDatabase();
        int result = db.delete(TABLE_NAME, "_ID = ?", new String[]{String.valueOf(id)});
        return result;
    }

    public boolean updatePeople(PeopleModel contact, int id) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NAME, contact.getName());
        contentValues.put(COL_NUMBER, contact.getphoneNumber());
        contentValues.put(COL_TYPE, contact.getDevice());
        contentValues.put(COL_EMAIL, contact.getEmail());
        contentValues.put(COL_IMAGE, contact.getProfileImage());

        int update = db.update(TABLE_NAME, contentValues, _ID + " = ? ", new String[]{String.valueOf(id)});

        return update == 1;
    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }
}
