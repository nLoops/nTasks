package com.nloops.ntasks.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.nloops.ntasks.data.TasksDBContract.TaskEntry;

public class TasksDBHelper extends SQLiteOpenHelper {

  private static final int DATABASE_VERSION = 1;

  private static final String DATABASE_NAME = "tasks.db";

  private static final String TEXT_TYPE = " TEXT";

  private static final String INTEGER_TYPE = " INTEGER";

  private static final String COMMA = ",";

  private static final String SQL_CREATE_TASK_ENTRY =
      "CREATE TABLE " + TasksDBContract.TaskEntry.TABLE_NAME + " (" +
          TasksDBContract.TaskEntry._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT," +
          TasksDBContract.TaskEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA +
          TasksDBContract.TaskEntry.COLUMN_NAME_BODY + TEXT_TYPE + COMMA +
          TasksDBContract.TaskEntry.COLUMN_NAME_PATH + TEXT_TYPE + COMMA +
          TasksDBContract.TaskEntry.COLUMN_NAME_COMPLETE + INTEGER_TYPE + COMMA +
          TasksDBContract.TaskEntry.COLUMN_NAME_DATE + INTEGER_TYPE + COMMA +
          TasksDBContract.TaskEntry.COLUMN_NAME_PRIORTY + INTEGER_TYPE + COMMA +
          TasksDBContract.TaskEntry.COLUMN_NAME_TYPE + INTEGER_TYPE + COMMA +
          TaskEntry.COLUMN_NAME_REPEAT + INTEGER_TYPE +
          ")";

  private static final String SQL_CREATE_TODO_ENTRY =
      "CREATE TABLE " + TasksDBContract.TodoEntry.TABLE_NAME + " (" +
          TasksDBContract.TodoEntry._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT," +
          TasksDBContract.TodoEntry.COLUMN_NAME_TODO + TEXT_TYPE + COMMA +
          TasksDBContract.TodoEntry.COLUMN_NAME_TASK_ID + INTEGER_TYPE + COMMA +
          TasksDBContract.TodoEntry.COLUMN_NAME_COMPLETE + INTEGER_TYPE +
          ")";


  public TasksDBHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(SQL_CREATE_TASK_ENTRY);
    db.execSQL(SQL_CREATE_TODO_ENTRY);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

  }
}
