package com.sas_apps.people.data;
/*
 * Created by Shashank Shinde.
 */

import android.provider.BaseColumns;

class PeopleContract {

    static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "people.db";

    static abstract class PeopleTable implements BaseColumns {

        static final String TABLE_NAME = "people_table";
        static final String COL_NAME = "NAME";
        static final String COL_NUMBER = "PHONE_NUMBER";
        static final String COL_TYPE = "TYPE";
        static final String COL_EMAIL = "EMAIL";
        static final String COL_IMAGE = "PROFILE_PHOTO";


        static final String CREATE_PEOPLE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " ( " +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT, " +
                COL_NUMBER + " TEXT, " +
                COL_TYPE + " TEXT, " +
                COL_EMAIL + " TEXT, " +
                COL_IMAGE + " TEXT )";
        static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}

