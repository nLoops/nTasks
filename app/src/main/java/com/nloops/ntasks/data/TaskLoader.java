package com.nloops.ntasks.data;

import android.content.Context;
import android.database.Cursor;
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
                null
        );
    }

    public Loader<Cursor> createTaskLoader(long id) {
        return new CursorLoader(
                mContext,
                TasksDBContract.TaskEntry.buildTasksUriWith(id),
                null,
                null,
                new String[]{String.valueOf(id)},
                null
        );
    }
}



