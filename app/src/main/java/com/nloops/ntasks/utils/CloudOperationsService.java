package com.nloops.ntasks.utils;

import static com.nloops.ntasks.data.TasksDBContract.getTaskUri;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nloops.ntasks.data.Task;
import com.nloops.ntasks.data.TasksDBContract;
import com.nloops.ntasks.data.TasksDBContract.TaskEntry;
import com.nloops.ntasks.data.Todo;
import com.nloops.ntasks.reminders.AlarmReceiver;
import com.nloops.ntasks.reminders.AlarmScheduler;
import java.util.ArrayList;
import java.util.List;

public class CloudOperationsService extends IntentService {

  public static final String ACTION_PUSH_DATA = "com.nloops.ACTION_PUSH_DATA";
  public static final String ACTION_PULL_DATA = "com.nloops.ACTION_PULL_DATA";
  private static final String TAG = CloudOperationsService.class.getSimpleName();

  public CloudOperationsService() {
    super(TAG);
  }

  @Override
  protected void onHandleIntent(@Nullable Intent intent) {
    assert intent != null;
    assert intent.getAction() != null;
    if (intent.getAction().equals(ACTION_PUSH_DATA)) {
      pushDataToServer();
    } else if (intent.getAction().equals(ACTION_PULL_DATA)) {
      pullDatafromServer();
    }

  }

  /**
   * This helper method will use {@link DatabaseReference} and {@link FirebaseDatabase} to push data
   * to server under user tasks node.
   */
  private void pushDataToServer() {
    if (GeneralUtils.isNetworkConnected(this)) {
      // get ref of whole database
      FirebaseDatabase mFireDataBase = FirebaseDatabase.getInstance();
      // get data from local SQLite DB
      Cursor cursor = getContentResolver()
          .query(TaskEntry.CONTENT_TASK_URI, null,
              null, null, null);

      while (cursor.moveToNext()) {
        Task task = new Task(cursor);
        /*we got list-todos data if existing*/
        if (task.getType() == TasksDBContract.TaskEntry.TYPE_TODO_NOTE) {
          String[] selectionArgs = new String[]{String.valueOf(task.getID())};
          task.setTodos(GeneralUtils.getTodoData(this, selectionArgs));
        }
        // get ref of tasks node in the database.
        DatabaseReference mFireDatabaseReference =
            mFireDataBase.getReference().child(Constants.TASKS_DATABASE_REFERENCE)
                // The node will be like
                // ***tasks root node
                //   *****User
                //        ***** Tasks Data
                .child(SharedPreferenceHelper.getInstance(getApplicationContext()).getUID())
                .child(String.valueOf(task.getID()));
        // Push Data to RealTimeDB
        mFireDatabaseReference.setValue(task);

      }

    }
  }

  /**
   * This helper method will use {@link DatabaseReference} and {@link FirebaseDatabase} to pull data
   * from server under user tasks node to local SQLiteDB
   */
  private void pullDatafromServer() {
    // get ref of whole database
    final FirebaseDatabase mFireDataBase = FirebaseDatabase.getInstance();
    // get ref of tasks node in the database.
    DatabaseReference mFireDatabaseReference =
        mFireDataBase.getReference().child(Constants.TASKS_DATABASE_REFERENCE)
            .child(SharedPreferenceHelper.getInstance(getApplicationContext()).getUID());
    mFireDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
          /*Declare List of Task to hold the data.*/
          List<Task> serverData = new ArrayList<>();
          List<Task> updateTasks = new ArrayList<>();
          for (DataSnapshot child : dataSnapshot.getChildren()) {
            /* get CurrentTask*/
            Task task = child.getValue(Task.class);
            if (isExist(task)) {
              updateTasks.add(task);
            } else {
              /* Add Current Task to the Array*/
              serverData.add(task);
            }
          }
          saveDataToLocalDB(serverData);
        }

      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {
        /*implemented later*/
      }
    });
  }

  /**
   * This Method will handle Inserting data into DB and Schedule future tasks alarm.
   *
   * @param taskList list of {@link Task}
   */
  private void saveDataToLocalDB(List<Task> taskList) {
//    first remove local saved tasks.
    getContentResolver().delete(TaskEntry.CONTENT_TASK_URI, null, null);
    ContentValues[] values = new ContentValues[taskList.size()];
    for (int i = 0; i < taskList.size(); i++) {
      ContentValues value = DatabaseValues.from(taskList.get(i));
      values[i] = value;
      /*Schedule tasks from Server*/
      Task currentTask = taskList.get(i);
      /*Check if this task has TODOS items*/
      if (currentTask.getTodos() != null) {
        List<Todo> todos = currentTask.getTodos();
        for (int x = 0; x < todos.size(); x++) {
          ContentValues todoValue = DatabaseValues.from(todos.get(x));
          getContentResolver().insert(TasksDBContract.TodoEntry.CONTENT_TODO_URI,
              todoValue);
        }
      }
      /*if the task date is not null and also bigger than now*/
      if (currentTask.getDate() != Long.MAX_VALUE
          && currentTask.getDate() > System.currentTimeMillis()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
          AlarmScheduler.scheduleAlarm(this,
              currentTask.getDate(),
              getTaskUri(currentTask),
              AlarmReceiver.class, currentTask.getType());
        }
      }
    }
    /*Insert data to DB*/
    getContentResolver().bulkInsert(TasksDBContract.TaskEntry.CONTENT_TASK_URI,
        values);

  }

  private boolean isExist(Task task) {
    Cursor cursor = getContentResolver().query(getTaskUri(task), null,
        null, null, null);

    return cursor.getCount() > 0;
  }
}
