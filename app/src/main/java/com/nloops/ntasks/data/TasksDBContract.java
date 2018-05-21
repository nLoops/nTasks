package com.nloops.ntasks.data;


import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.nloops.ntasks.BuildConfig;

/**
 * The Contract that defines Database Tables content
 */
public class TasksDBContract {

    // General variables declaration that use to build content URI
    // to access data using Content Provider.
    public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID;
    private static final String CONTENT_SCHEME = "content://";
    public static final Uri BASE_CONTENT_URI = Uri.parse(CONTENT_SCHEME + CONTENT_AUTHORITY);
    private static final String SEPARATOR = "/";


    // to prevent to instantiating the class.
    private TasksDBContract() {
    }

    /**
     * inner class that contains Task Table columns
     */
    public static abstract class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "task";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_BODY = "body";
        public static final String COLUMN_NAME_DATE = "duedate";
        public static final String COLUMN_NAME_PRIORTY = "priorty";
        public static final String COLUMN_NAME_PATH = "path";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_COMPLETE = "is_complete";
        //Content Uri for table task
        public static final Uri CONTENT_TASK_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_NAME).build();

        // Helper method with Content URI

        public static Uri buildTasksUriWith(long id) {
            return ContentUris.withAppendedId(CONTENT_TASK_URI, id);
        }

        //Constant Values for Tasks type,state,priority
        public static final int NO_TASK_TYPE = -1;
        public static final int TYPE_NORMAL_NOTE = 0;
        public static final int TYPE_TODO_NOTE = 1;
        public static final int TYPE_AUDIO_NOTE = 2;
        public static final int STATE_NOT_COMPLETED = 0;
        public static final int STATE_COMPLETED = 1;
        public static final int PRIORTY_NORMAL = 0;
        public static final int PRIORTY_HIGH = 1;

    }

    /**
     * inner class that contains related Task TODOs table columns
     */
    public static abstract class TodoEntry implements BaseColumns {
        public static final String TABLE_NAME = "todo";
        public static final String COLUMN_NAME_TODO = "txttodo";
        //Content Uri for table_todo
        public static final Uri CONTENT_TODO_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_NAME).build();

    }

    /* Helpers to retrieve column values */
    public static String getColumnString(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndex(columnName));
    }

    public static int getColumnInt(Cursor cursor, String columnName) {
        return cursor.getInt(cursor.getColumnIndex(columnName));
    }

    public static long getColumnLong(Cursor cursor, String columnName) {
        return cursor.getLong(cursor.getColumnIndex(columnName));
    }
}
