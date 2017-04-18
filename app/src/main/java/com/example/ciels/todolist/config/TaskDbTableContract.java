package com.example.ciels.todolist.config;

import android.provider.BaseColumns;

import static com.example.ciels.todolist.config.DbContract.BOOLEAN_TYPE;
import static com.example.ciels.todolist.config.DbContract.COMMA;
import static com.example.ciels.todolist.config.DbContract.TEXT_TYPE;

/**
 *
 */
public final class TaskDbTableContract {

    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE "
                                                    + TaskEntry.TABLE_NAME
                                                    + " ("
                                                    + TaskEntry._ID
                                                    + TEXT_TYPE
                                                    + " PRIMARY KEY,"
                                                    + TaskEntry.COLUMN_NAME_ENTRY_ID
                                                    + TEXT_TYPE
                                                    + COMMA
                                                    + TaskEntry.COLUMN_NAME_TITLE
                                                    + TEXT_TYPE
                                                    + COMMA
                                                    + TaskEntry.COLUMN_NAME_DESCRIPTION
                                                    + TEXT_TYPE
                                                    + COMMA
                                                    + TaskEntry.COLUMN_NAME_COMPLETED
                                                    + BOOLEAN_TYPE
                                                    + " )";

    private TaskDbTableContract() {
    }

    public static abstract class TaskEntry implements BaseColumns {

        public static final String TABLE_NAME = "task";
        public static final String COLUMN_NAME_ENTRY_ID = "entryid";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_COMPLETED = "completed";
    }
}
