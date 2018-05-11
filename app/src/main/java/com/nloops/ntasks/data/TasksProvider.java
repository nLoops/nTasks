package com.nloops.ntasks.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class TasksProvider extends ContentProvider {

    private static final String TAG = TasksProvider.class.getSimpleName();
    private static final int TASK = 100;
    private static final int TASK_ITEM = 101;
    private static final int TODOs = 200;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private TasksDBHelper mDbHelper;

    /**
     * Building UriMatcher to set a Switch case on to operate CRUD depending on what
     * collection of data need.
     *
     * @return UriMatcher.
     */
    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = TasksDBContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, TasksDBContract.TaskEntry.TABLE_NAME, TASK);
        matcher.addURI(authority, TasksDBContract.TaskEntry.TABLE_NAME + "/#", TASK_ITEM);
        matcher.addURI(authority, TasksDBContract.TodoEntry.TABLE_NAME, TODOs);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new TasksDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case TASK:
                retCursor = mDbHelper.getReadableDatabase().query(
                        TasksDBContract.TaskEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case TASK_ITEM:
                String[] where = {uri.getLastPathSegment()};
                retCursor = mDbHelper.getReadableDatabase().query(
                        TasksDBContract.TaskEntry.TABLE_NAME,
                        projection,
                        TasksDBContract.TaskEntry._ID + " = ?",
                        where,
                        null,
                        null,
                        sortOrder);
                break;
            case TODOs:
                retCursor = mDbHelper.getReadableDatabase().query(
                        TasksDBContract.TodoEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        long rawID;
        switch (match) {
            case TASK:
                rawID = db.insert(TasksDBContract.TaskEntry.TABLE_NAME, null, values);
                break;
            case TODOs:
                rawID = db.insert(TasksDBContract.TodoEntry.TABLE_NAME, null, values);
                break;
            default:
                throw new UnsupportedOperationException("Insert not supported for " + uri);
        }

        // if we failed to insert the raw
        if (rawID == -1) {
            Log.i(TAG, "failed to insert new task");
            return null;
        } else {
            getContext().getContentResolver().notifyChange(uri, null);
            return ContentUris.withAppendedId(uri, rawID);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowAffected;

        switch (match) {
            case TASK:
                rowAffected = db.delete(
                        TasksDBContract.TaskEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            case TASK_ITEM:
                String[] where = {uri.getLastPathSegment()};
                rowAffected = db.delete(
                        TasksDBContract.TaskEntry.TABLE_NAME,
                        TasksDBContract.TaskEntry._ID + " = ?",
                        where
                );
                break;

            case TODOs:
                rowAffected = db.delete(
                        TasksDBContract.TodoEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowAffected != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowAffected;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rawUpdated;
        switch (match) {
            case TASK_ITEM:
                String[] where = {uri.getLastPathSegment()};
                rawUpdated = db.update(
                        TasksDBContract.TaskEntry.TABLE_NAME,
                        values,
                        TasksDBContract.TaskEntry._ID + " = ?",
                        where
                );
                break;
            default:
                throw new UnsupportedOperationException("Updated not supported for " + uri);
        }
        if (rawUpdated <= 0) {
            Log.i(TAG, "update failed.");
            return 0;
        } else {
            getContext().getContentResolver().notifyChange(uri, null);
            return rawUpdated;
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}
