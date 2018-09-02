package com.nloops.ntasks.data;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;
import com.nloops.ntasks.data.TasksDBContract.TaskEntry;
import com.nloops.ntasks.data.TasksDBContract.TodoEntry;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TasksDBHelper extends SQLiteOpenHelper {

  private static final String TAG = TasksDBHelper.class.getSimpleName();

  private static final int DATABASE_VERSION = 1;

  private static final String DATABASE_NAME = "tasks.db";

  private static final String TEXT_TYPE = " TEXT";

  private static final String INTEGER_TYPE = " INTEGER";

  private static final String COMMA = ",";

  private Context mConext;

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
          TaskEntry.COLUMN_NAME_REPEAT + INTEGER_TYPE + COMMA +
          TaskEntry.COLUMN_NAME_USER + TEXT_TYPE + COMMA +
          TaskEntry.COLUMN_NAME_SHARED_WITH + TEXT_TYPE +
          ")";

  private static final String SQL_CREATE_TODO_ENTRY =
      "CREATE TABLE " + TasksDBContract.TodoEntry.TABLE_NAME + " (" +
          TasksDBContract.TodoEntry._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT," +
          TasksDBContract.TodoEntry.COLUMN_NAME_TODO + TEXT_TYPE + COMMA +
          TasksDBContract.TodoEntry.COLUMN_NAME_TASK_ID + INTEGER_TYPE + COMMA +
          TasksDBContract.TodoEntry.COLUMN_NAME_COMPLETE + INTEGER_TYPE + COMMA +
          TodoEntry.COLUMN_NAME_LIST_DATE + INTEGER_TYPE +
          ")";


  public TasksDBHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
    mConext = context;
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(SQL_CREATE_TASK_ENTRY);
    db.execSQL(SQL_CREATE_TODO_ENTRY);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.e(TAG, "Updating table from " + oldVersion + " to " + newVersion);
// You will not need to modify this unless you need to do some android specific things.
    // When upgrading the database, all you need to do is add a file to the assets folder and name it:
    // from_1_to_2.sql with the version that you are upgrading to as the last version.
    for (int i = oldVersion; i < newVersion; ++i) {
      String migrationName = String.format("from_%d_to_%d.sql", i, (i + 1));
      readAndExecuteSQLScript(db, mConext, migrationName);
    }
  }

  private void readAndExecuteSQLScript(SQLiteDatabase db, Context ctx, String fileName) {
    if (TextUtils.isEmpty(fileName)) {
      Log.d(TAG, "SQL script file name is empty");
      return;
    }

    Log.d(TAG, "Script found. Executing...");
    AssetManager assetManager = ctx.getAssets();
    BufferedReader reader = null;

    try {
      InputStream is = assetManager.open(fileName);
      InputStreamReader isr = new InputStreamReader(is);
      reader = new BufferedReader(isr);
      executeSQLScript(db, reader);
    } catch (IOException e) {
      Log.e(TAG, "IOException:", e);
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
          Log.e(TAG, "IOException:", e);
        }
      }
    }

  }

  private void executeSQLScript(SQLiteDatabase db, BufferedReader reader) throws IOException {
    String line;
    StringBuilder statement = new StringBuilder();
    while ((line = reader.readLine()) != null) {
      statement.append(line);
      statement.append("\n");
      if (line.endsWith(";")) {
        db.execSQL(statement.toString());
        statement = new StringBuilder();
      }
    }
  }
}
