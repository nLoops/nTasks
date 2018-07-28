package com.nloops.ntasks.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import com.nloops.ntasks.R;
import com.nloops.ntasks.data.TasksDBContract.TaskEntry;
import java.util.Calendar;

/**
 * Loader Provider need to load single Tasks or List of Tasks into background Thread.
 */
public class TaskLoader {

  @NonNull
  private final Context mContext;

  public TaskLoader(@NonNull Context context) {
    this.mContext = context;
  }


  public Loader<Cursor> createTasksLoader() {
    // get SharedPreference to arrange Tasks sortBy
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    // get the Current Value of preference
    String sortByValue = sharedPreferences
        .getString(mContext.getString(R.string.settings_sortby_key),
            mContext.getString(R.string.settings_sortby_default));
    // By default we have Date first
    String sortBy = TasksDBContract.DATE_SORT;
    // if we have Priority first we change the sort of Tasks.
    if (sortByValue.equals(mContext.getString(R.string.settings_priority_value))) {
      sortBy = TasksDBContract.DEFAULT_SORT;
    }

    boolean pref = sharedPreferences.getBoolean(mContext.getString(R.string.settings_completed_key),
        mContext.getResources().getBoolean(R.bool.hide_complete_tasks));
    String selection = null;
    String[] selectionArgs = null;

    if (!pref) {
      selection = TasksDBContract.TaskEntry.COLUMN_NAME_COMPLETE + "=?";
      selectionArgs = new String[]{String.valueOf(TasksDBContract.TaskEntry.STATE_NOT_COMPLETED)};
    }
    return new CursorLoader(mContext,
        TasksDBContract.TaskEntry.CONTENT_TASK_URI,
        null,
        selection,
        selectionArgs,
        sortBy
    );


  }

  public Loader<Cursor> getAllTasks() {
    String selection = TaskEntry.COLUMN_NAME_DATE + ">=? and " + TaskEntry.COLUMN_NAME_DATE + "<=?";
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_WEEK, -7);
    String[] selectionArgs = {String.valueOf(calendar.getTimeInMillis())
        , String.valueOf(System.currentTimeMillis())};
    return new CursorLoader(mContext,
        TasksDBContract.TaskEntry.CONTENT_TASK_URI,
        null,
        selection,
        selectionArgs,
        null
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



