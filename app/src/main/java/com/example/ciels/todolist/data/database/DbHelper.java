package com.example.ciels.todolist.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.ciels.todolist.di.ApplicationContext;
import javax.inject.Inject;
import javax.inject.Singleton;

import static com.example.ciels.todolist.DbContract.DB_NAME;
import static com.example.ciels.todolist.DbContract.DB_VERSION;

/**
 *
 */
@Singleton
public class DbHelper extends SQLiteOpenHelper {

    @Inject
    public DbHelper(@ApplicationContext Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Not required at version 1
    }
}
