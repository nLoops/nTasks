package com.nloops.ntasks.data;

import android.annotation.SuppressLint;
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
import com.nloops.ntasks.data.TasksDBContract.TaskEntry;
import com.nloops.ntasks.reminders.AlarmReceiver;
import com.nloops.ntasks.reminders.AlarmScheduler;
import com.nloops.ntasks.utils.DatabaseValues;
import com.nloops.ntasks.utils.GeneralUtils;
import com.nloops.ntasks.widgets.WidgetIntentService;

/**
 * implements {@link TasksDataSource} to set behavior on how handle data from SQLite Database
 */
public class TasksLocalDataSource implements TasksDataSource {

  // ref of Class to hold instance
  @SuppressLint("StaticFieldLeak")
  private static TasksLocalDataSource INSTANCE;
  // need to access ContentProvider CRUD operations.
  private final ContentResolver mContentResolver;
  // passed context
  private final Context mContext;


  // prevent direct instantiation.
  private TasksLocalDataSource(@NonNull ContentResolver resolver, @NonNull Context context) {
    this.mContentResolver = resolver;
    this.mContext = context;
  }

  public static TasksLocalDataSource getInstance(@NonNull ContentResolver resolver,
      @NonNull Context context) {
    if (INSTANCE == null) {
      INSTANCE = new TasksLocalDataSource(resolver, context);
    }
    return INSTANCE;
  }

  @Override
  public void saveTask(@NonNull Task task) {
    ContentValues values = DatabaseValues.from(task);
    Uri uri = mContentResolver.insert(TasksDBContract.TaskEntry.CONTENT_TASK_URI, values);
    // add todolist items
    if (task.getTodos() != null) {
      assert uri != null;
      int taskID = Integer.valueOf(uri.getLastPathSegment());
      for (int i = 0; i < task.getTodos().size(); i++) {
        Todo todo = task.getTodos().get(i);
        todo.setTaskID(taskID);
        ContentValues todoValues = DatabaseValues.from(todo);
        mContentResolver.insert(TasksDBContract.TodoEntry.CONTENT_TODO_URI, todoValues);
      }
    }
    // setup Alarm For this Reminder.
    if (task.getDate() != Long.MAX_VALUE) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        AlarmScheduler.scheduleAlarm(mContext,
            task.getDate(), uri, AlarmReceiver.class, task.getType());
      }
    }

    // Update Home Widget
    WidgetIntentService.startActionChangeList(mContext);
  }

  @Override
  public void deleteTask(@NonNull Uri taskUri) {
    // delete Recorded Audio if exists
    Cursor cursor = mContentResolver.query(taskUri, null, null, null, null);
    assert cursor != null;
    if (cursor.moveToNext()) {
      Task task = new Task(cursor);
      if (task.getPath().length() > 0) {
        GeneralUtils.deleteRecordedAudio(task.getPath());
      }
    }
    cursor.close();

    mContentResolver.delete(taskUri, null, null);
    String[] selectionArgs = new String[]{taskUri.getLastPathSegment()};
    mContentResolver.delete(TasksDBContract.TodoEntry.CONTENT_TODO_URI, null, selectionArgs);

    // if this task has scheduled Reminder we will cancel it to prevent notify.
    PendingIntent operation =
        AlarmScheduler.getReminderPendingIntent(mContext, taskUri, AlarmReceiver.class);
    AlarmManager manager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    assert manager != null;
    manager.cancel(operation);

    // Update Home Widget
    WidgetIntentService.startActionChangeList(mContext);
  }


  @Override
  public void completeTask(boolean state, long rawID) {
    Uri rawUri = ContentUris.withAppendedId(TasksDBContract.TaskEntry.CONTENT_TASK_URI,
        rawID);
    ContentValues values = new ContentValues(1);
    values.put(TasksDBContract.TaskEntry.COLUMN_NAME_COMPLETE, state ? 1 : 0);
    // delete Recorded Audio if exists
    mContentResolver.update(rawUri, values, null, null);
    Cursor cursor = mContentResolver.query(rawUri, null, null, null, null);
    assert cursor != null;
    if (cursor.moveToNext()) {
      Task task = new Task(cursor);
      if (task.getPath().length() > 0) {
        GeneralUtils.deleteRecordedAudio(task.getPath());
      }
    }
    cursor.close();

    // if this task has scheduled Reminder we will cancel it to prevent notify.
    PendingIntent operation =
        AlarmScheduler.getReminderPendingIntent(mContext, rawUri, AlarmReceiver.class);
    AlarmManager manager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    assert manager != null;
    manager.cancel(operation);

    // Update Home Widget
    WidgetIntentService.startActionChangeList(mContext);
  }

  @Override
  public void completeTODO(boolean state, long rawID) {
    Uri rawUri = ContentUris.withAppendedId(TasksDBContract.TodoEntry.CONTENT_TODO_URI, rawID);
    ContentValues values = new ContentValues(1);
    values.put(TasksDBContract.TodoEntry.COLUMN_NAME_COMPLETE, state ? 1 : 0);
    mContentResolver.update(rawUri, values, null, null);
  }

  @Override
  public void deleteAll() {
    mContentResolver.delete(TaskEntry.CONTENT_TASK_URI, null, null);
  }

  @Override
  public void updateTask(@NonNull Task task, @NonNull Uri uri) {
    ContentValues values = DatabaseValues.from(task);
    mContentResolver.update(uri,
        values,
        null, null);

    // add todolist items
    if (task.getTodos() != null) {
      int taskID = Integer.valueOf(uri.getLastPathSegment());
      String[] selectionArgs = new String[]{String.valueOf(taskID)};
      // first we will delete old TODOs
      mContentResolver.delete(TasksDBContract.TodoEntry.CONTENT_TODO_URI, null, selectionArgs);
      // here we will insert the updates TODOs
      for (int i = 0; i < task.getTodos().size(); i++) {
        Todo todo = task.getTodos().get(i);
        todo.setTaskID(taskID);
        ContentValues todoValues = DatabaseValues.from(todo);
        mContentResolver.insert(TasksDBContract.TodoEntry.CONTENT_TODO_URI, todoValues);
      }

    }
    // first we will cancel previous Alarm
    PendingIntent operation =
        AlarmScheduler.getReminderPendingIntent(mContext, uri, AlarmReceiver.class);
    AlarmManager manager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    assert manager != null;
    manager.cancel(operation);

    // setup Alarm For this Reminder.
    if (task.getDate() != Long.MAX_VALUE) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        AlarmScheduler.scheduleAlarm(mContext,
            task.getDate(), uri, AlarmReceiver.class, task.getType());
      }
    }

    // Update Home Widget
    WidgetIntentService.startActionChangeList(mContext);
  }


  public interface LoadDataCallback {

    void onDataLoaded(Cursor data);

    void onDataEmpty();

    void onDataNotAvailable();

    void onDataReset();
  }
}
