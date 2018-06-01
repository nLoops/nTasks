package com.nloops.ntasks.data;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import com.nloops.ntasks.addedittasks.AddEditTasks;
import com.nloops.ntasks.reminders.AlarmReceiver;
import com.nloops.ntasks.reminders.AlarmScheduler;
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
    // passed context
    private Context mContext;


    // prevent direct instantiation.
    private TasksLocalDataSource(@NonNull ContentResolver resolver, @NonNull Context context) {
        this.mContentResolver = resolver;
        this.mContext = context;
    }

    public static TasksLocalDataSource getInstance(@NonNull ContentResolver resolver, @NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new TasksLocalDataSource(resolver, context);
        }
        return INSTANCE;
    }

    @Override
    public void getTasks(@NonNull LoadTasksCallback callback) {

    }

    @Override
    public void getTask(int taskID, @NonNull GetTaskCallback callback) {

    }

    @Override
    public void saveTask(@NonNull Task task) {
        ContentValues values = DatabaseValues.from(task);
        Uri uri = mContentResolver.insert(TasksDBContract.TaskEntry.CONTENT_TASK_URI, values);
        // setup Alarm For this Reminder.
        if (task.getDate() != Long.MAX_VALUE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                AlarmScheduler.scheduleAlarm(mContext,
                        task.getDate(), uri, AlarmReceiver.class, task.getType());
            }
        }
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
    public void deleteTask(@NonNull Uri taskUri) {
        mContentResolver.delete(taskUri, null, null);
        PendingIntent operation =
                AlarmScheduler.getReminderPendingIntent(mContext, taskUri, AlarmReceiver.class);
        AlarmManager manager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(operation);
    }


    @Override
    public void completeTask(boolean state, long rawID) {
        Uri rawUri = ContentUris.withAppendedId(TasksDBContract.TaskEntry.CONTENT_TASK_URI,
                rawID);
        ContentValues values = new ContentValues(1);
        values.put(TasksDBContract.TaskEntry.COLUMN_NAME_COMPLETE, state ? 1 : 0);
        int count = mContentResolver.update(rawUri, values, null, null);

        PendingIntent operation =
                AlarmScheduler.getReminderPendingIntent(mContext, rawUri, AlarmReceiver.class);
        AlarmManager manager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(operation);
    }

    @Override
    public void updateTask(@NonNull Task task, @NonNull Uri uri) {
        ContentValues values = DatabaseValues.from(task);
        mContentResolver.update(uri,
                values,
                null, null);
        // setup Alarm For this Reminder.
        if (task.getDate() != Long.MAX_VALUE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                AlarmScheduler.scheduleAlarm(mContext,
                        task.getDate(), AddEditTasks.TASK_URI, AlarmReceiver.class, task.getType());
            }
        }
    }


    public interface LoadDataCallback {
        void onDataLoaded(Cursor data);

        void onDataEmpty();

        void onDataNotAvailable();

        void onDataReset();
    }
}
