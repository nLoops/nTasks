package com.nloops.ntasks.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

/**
 * Loader Provider need to load single Tasks or List of Tasks into background
 * Thread.
 */
public class TaskLoader {

    @NonNull
    private final Context mContext;

    public TaskLoader(@NonNull Context context) {
        this.mContext = context;
    }


    public Loader<Cursor> createTasksLoader() {
        return new CursorLoader(mContext,
                TasksDBContract.TaskEntry.CONTENT_TASK_URI,
                null,
                null,
                null,
                TasksDBContract.DEFAULT_SORT
        );
    }

    public Loader<Cursor> createTaskLoader(Uri uri) {
        return new CursorLoader(
                mContext,
                uri,
                null,
                null,
                null,
                null
        );
    }

    public Loader<Cursor> createTODOLoader(String taskID) {
        String[] selectionArgs = new String[]{taskID};
        return new CursorLoader(
                mContext,
                TasksDBContract.TodoEntry.CONTENT_TODO_URI,
                null,
                null,
                selectionArgs,
                null
        );
    }
}



