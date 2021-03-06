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
  /* Sort order constants */
  //Priority first, Completed last, the rest by date
  public static final String DEFAULT_SORT = String.format("%s ASC, %s DESC, %s ASC",
      TaskEntry.COLUMN_NAME_COMPLETE, TaskEntry.COLUMN_NAME_PRIORTY, TaskEntry.COLUMN_NAME_DATE);
  //Completed last, then by date, followed by priority
  public static final String DATE_SORT = String.format("%s ASC, %s ASC, %s DESC",
      TaskEntry.COLUMN_NAME_COMPLETE, TaskEntry.COLUMN_NAME_DATE, TaskEntry.COLUMN_NAME_PRIORTY);
  private static final String CONTENT_SCHEME = "content://";
  private static final Uri BASE_CONTENT_URI = Uri.parse(CONTENT_SCHEME + CONTENT_AUTHORITY);

  // to prevent to instantiating the class.
  private TasksDBContract() {
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

  /**
   * We will using this method to help re-schedule the pending Alarms after reboot
   *
   * @return Task Uri.
   */
  public static Uri getTaskUri(Task task) {
    return ContentUris.withAppendedId(TaskEntry.CONTENT_TASK_URI, task.getID());
  }

  /**
   * We will using this method to help re-schedule the pending Alarms after reboot
   *
   * @return todoUri
   */
  public static Uri getTodoUri(Todo item) {
    return ContentUris.withAppendedId(TodoEntry.CONTENT_TODO_URI, item.getID());
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
    public static final String COLUMN_NAME_REPEAT = "is_repeat";
    public static final String COLUMN_NAME_USER = "current_user";
    public static final String COLUMN_NAME_SHARED_WITH = "shared_with";
    //Content Uri for table task
    public static final Uri CONTENT_TASK_URI = BASE_CONTENT_URI.buildUpon()
        .appendPath(TABLE_NAME).build();

    //Constant Values for Tasks type,state,priority
    public static final int NO_TASK_TYPE = -1;
    public static final int TYPE_NORMAL_NOTE = 0;
    public static final int TYPE_TODO_NOTE = 1;
    public static final int TYPE_AUDIO_NOTE = 2;
    public static final int STATE_NOT_COMPLETED = 0;
    public static final int STATE_COMPLETED = 1;
    public static final int PRIORTY_NORMAL = 0;
    public static final int PRIORTY_HIGH = 1;
    public static final int REPEAT_NONE = 0;
    public static final int REPEAT_DAILY = 1;
    public static final int REPEAT_WEEKLY = 2;
    public static final int REPEAT_MONTHLY = 3;
    public static final int REPEAT_YEARLY = 4;

  }

  /**
   * inner class that contains related Task TODOs table columns
   */
  public static abstract class TodoEntry implements BaseColumns {

    public static final String TABLE_NAME = "todo";
    public static final String COLUMN_NAME_TODO = "txttodo";
    public static final String COLUMN_NAME_TASK_ID = "taskid";
    public static final String COLUMN_NAME_COMPLETE = "is_complete";
    public static final String COLUMN_NAME_LIST_DATE = "due_date";
    //Content Uri for tabletodo
    public static final Uri CONTENT_TODO_URI = BASE_CONTENT_URI.buildUpon()
        .appendPath(TABLE_NAME).build();

    // public constants for todotable
    public static final int STATE_NOT_COMPLETED = 0;
    public static final int STATE_COMPLETED = 1;

  }
}
