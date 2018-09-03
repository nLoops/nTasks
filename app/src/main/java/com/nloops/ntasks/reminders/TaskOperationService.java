package com.nloops.ntasks.reminders;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.ContentUris;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import com.nloops.ntasks.audiorecording.TasksMediaPlayer;
import com.nloops.ntasks.data.Task;
import com.nloops.ntasks.data.TasksLocalDataSource;

public class TaskOperationService extends IntentService {

  private static final String TAG = TaskOperationService.class.getSimpleName();
  public static final String EXTRAS_NOTIFICATION_ID = "notification_id";
  public static final String ACTION_COMPLETE_TASK = "complete_task";
  public static final String ACTION_PLAY_NOTE_AUDIO = "audio_note";
  public static final String EXTRAS_NOTE_AUDIO_PATH = "audio_path";
  public static final String ACTION_SAVE_NEW_TASK = "save_new_task";
  public static final String EXTRAS_SAVE_NEW_TASK_DATA = "new_task_data";
  public static final String ACTION_UPDATE_TASK = "update_existing_task";
  public static final String EXTRAS_UPDATE_TASK_DATA = "update_task_data";
  public static final String ACTION_DELETE_TASK = "delete-existing-task";
  public static final String ACTION_COMPLETE_EXISTING_TASK = "complete-existing-task";
  public static final String ACTION_COMPLETE_TODO = "complete-existing-todo";
  public static final String EXTRAS_TASK_STATE = "task_state";
  public static final String EXTRAS_TASK_ID = "task_id";
  public static final String EXTRAS_TODO_STATE = "todo_state";
  public static final String EXTRAS_TODO_TASK_ID = "item_task_id";
  public static final String EXTRAS_TODO_ID = "todo_id";
  public static final String ACTION_UPDATE_TASK_NOTIFICATION = "notification_update";


  public TaskOperationService() {
    super(TAG);
  }

  @Override
  protected void onHandleIntent(@Nullable Intent intent) {
    assert intent != null;
    assert intent.getAction() != null;
    switch (intent.getAction()) {
      case ACTION_COMPLETE_TASK:
        performCompleteTask(intent);
        break;
      case ACTION_PLAY_NOTE_AUDIO:
        String path = intent.getStringExtra(EXTRAS_NOTE_AUDIO_PATH);
        performPlayNote(path);

        break;
      case ACTION_SAVE_NEW_TASK: {
        Task data = intent.getParcelableExtra(EXTRAS_SAVE_NEW_TASK_DATA);
        TasksLocalDataSource mTasksDataSource = TasksLocalDataSource
            .getInstance(getContentResolver(),
                this);
        mTasksDataSource.saveTask(data);

        break;
      }
      case ACTION_UPDATE_TASK: {
        assert intent.getData() != null;
        Task data = intent.getParcelableExtra(EXTRAS_UPDATE_TASK_DATA);
        TasksLocalDataSource mTasksDataSource = TasksLocalDataSource
            .getInstance(getContentResolver(), this);
        mTasksDataSource.updateTask(data, intent.getData());

        break;
      }
      case ACTION_DELETE_TASK: {
        assert intent.getData() != null;
        TasksLocalDataSource mTasksDataSource = TasksLocalDataSource
            .getInstance(getContentResolver(), this);
        mTasksDataSource.deleteTask(intent.getData());
        break;
      }
      case ACTION_COMPLETE_EXISTING_TASK: {
        boolean state = intent.getBooleanExtra(EXTRAS_TASK_STATE, false);
        long id = intent.getLongExtra(EXTRAS_TASK_ID, -1);
        TasksLocalDataSource mTasksDataSource = TasksLocalDataSource
            .getInstance(getContentResolver(), this);
        mTasksDataSource.completeTask(state, id);

        break;
      }
      case ACTION_COMPLETE_TODO: {
        boolean state = intent.getBooleanExtra(EXTRAS_TODO_STATE, false);
        long id = intent.getLongExtra(EXTRAS_TODO_ID, -1);
        long taskID = intent.getLongExtra(EXTRAS_TODO_TASK_ID, -1);
        TasksLocalDataSource mTasksDataSource = TasksLocalDataSource
            .getInstance(getContentResolver(), this);
        mTasksDataSource.completeTODO(state, id, taskID);
        break;
      }
      case ACTION_UPDATE_TASK_NOTIFICATION:
        performUpdateTask(intent);
        break;
    }
  }


  /**
   * This Helper method will perform as {@link NotificationCompat.Action} when user hit DONE the
   * task will perform Complete on SQLite.
   *
   * @param intent passed Intent for {@link Task}
   */
  private void performCompleteTask(Intent intent) {
    // if user playing audio note we stop it.
    TasksMediaPlayer mediaPlayer = TasksMediaPlayer.getInstance(this);
    if (mediaPlayer.isPlaying()) {
      mediaPlayer.stopPlaying();
    }
    TasksLocalDataSource mTasksDataSource = TasksLocalDataSource.getInstance(getContentResolver(),
        this);
    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    long rawID = ContentUris.parseId(intent.getData());
    mTasksDataSource.completeTask(true, rawID);
    assert manager != null;
    manager.cancel(intent.getIntExtra(EXTRAS_NOTIFICATION_ID, 0));
  }

  /**
   * This Helper method will perform as {@link NotificationCompat.Action} when user hit Play will
   * get {@link TasksMediaPlayer} and play passed audio String.
   *
   * @param audioPath {@link Task} audioPath
   */
  private void performPlayNote(String audioPath) {
    TasksMediaPlayer mediaPlayer = TasksMediaPlayer.getInstance(this);
    mediaPlayer.handleMediaPlayer(audioPath);
  }

  private void performUpdateTask(Intent intent) {
    assert intent.getData() != null;
    Task data = intent.getParcelableExtra(EXTRAS_UPDATE_TASK_DATA);
    TasksLocalDataSource mTasksDataSource = TasksLocalDataSource
        .getInstance(getContentResolver(), this);
    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    mTasksDataSource.updateTask(data, intent.getData());
    assert manager != null;
    manager.cancel(intent.getIntExtra(EXTRAS_NOTIFICATION_ID, 0));
  }

}
