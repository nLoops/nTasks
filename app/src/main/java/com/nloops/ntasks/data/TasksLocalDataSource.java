package com.nloops.ntasks.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.nloops.ntasks.utils.DatabaseValues;

/**
 * implements {@link TasksDataSource} to set behavior on how handle data from
 * Sqlite Database
 */
public class TasksLocalDataSource implements TasksDataSource {

    // singleton instance.
    private static TasksLocalDataSource INSTANCE;
    // need to access ContentProvider CRUD operations.
    private ContentResolver mContentResolver;


    // prevent direct instantiation.
    private TasksLocalDataSource(@NonNull ContentResolver resolver) {
        mContentResolver = resolver;
    }

    public static TasksLocalDataSource getInstance(@NonNull ContentResolver resolver) {
        if (INSTANCE == null) {
            INSTANCE = new TasksLocalDataSource(resolver);
        }
        return INSTANCE;
    }

    @Override
    public void getTasks(@NonNull LoadTasksCallback callback) {

    }

    @Override
    public void getTask(@NonNull int taskID, @NonNull GetTaskCallback callback) {

    }

    @Override
    public void saveTask(@NonNull Task task) {
        ContentValues values = DatabaseValues.from(task);
        mContentResolver.insert(TasksDBContract.TaskEntry.CONTENT_TASK_URI, values);
    }

    @Override
    public void clearCompletedTasks() {

    }

    @Override
    public void refreshTasks() {

    }

    @Override
    public void deleteAllTasks() {

    }

    @Override
    public void deleteTask(@NonNull int taskID) {

    }


    public interface LoadDataCallback {
        void onDataLoaded(Cursor data);

        void onDataEmpty();

        void onDataNotAvailable();

        void onDataReset();
    }
}
