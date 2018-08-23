package com.nloops.ntasks.addedittasks;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.nloops.ntasks.R;
import com.nloops.ntasks.data.TaskLoader;
import com.nloops.ntasks.data.TasksDBContract;
import com.nloops.ntasks.data.TasksDBContract.TaskEntry;
import com.nloops.ntasks.utils.GeneralUtils;

/**
 * This Activity will hold all app Tasks Type {@link TaskDetailFragment} {@link AudioNoteFragment}
 * {@link TaskTodoFragment} the fragment depends on what type passed from {@link
 * com.nloops.ntasks.taskslist.TasksList}
 */
public class AddEditTasks extends AppCompatActivity {


  // constants for OnActivityResults to call the right SnackBar message.
  public static final int REQUEST_EDIT_TASK = 2;
  public static final int REQUEST_ADD_TASK = 3;
  public static final int RESULT_DELETE_TASK = 100;
  public static final int RESULT_UPDATE_TASK = 101;
  public static final int RESULT_ADD_TASK = 102;
  public static final String EXTRAS_TASK_TYPE = "task_type";
  public static Uri TASK_URI;
  public static int TASK_TYPE = TasksDBContract.TaskEntry.NO_TASK_TYPE;
  private TasksDetailPresenter mPresenter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_edit_tasks);
    Toolbar toolbar = findViewById(R.id.tasks_detail_toolbar);
    setSupportActionBar(toolbar);
    assert getSupportActionBar() != null;
    getSupportActionBar().setDisplayShowTitleEnabled(false);
    // get passed intent if available to load task data.
    TASK_URI = getIntent().getData();
    // get Task passed Task Type
    // first Check if the intent has this value.
    if (getIntent().hasExtra(EXTRAS_TASK_TYPE)) {
      // Second we have to get the Value and We make the Default as unassigned.
      TASK_TYPE = getIntent().getIntExtra(EXTRAS_TASK_TYPE,
          TasksDBContract.TaskEntry.NO_TASK_TYPE);
    }
    // object of Loader that will return data using Cursor Adapter
    TaskLoader loader = new TaskLoader(this);
    switch (TASK_TYPE) {
      case TaskEntry.TYPE_NORMAL_NOTE:
        setupNormalNote(loader);
        break;
      case TaskEntry.TYPE_AUDIO_NOTE:
        setupAudioNote(loader);
        break;
      case TaskEntry.TYPE_TODO_NOTE:
        setupTodoNote(loader);
        break;
    }
  }

  /**
   * Helper method to setup a new Object of {@link TaskDetailFragment} however passed TASK has URI
   * or NOT.
   *
   * @param loader {@link TaskLoader}
   */
  private void setupNormalNote(TaskLoader loader) {
    // check if we have instance from the fragment it's okay
    // if not we get a new instance.
    TaskDetailFragment taskDetailFragment =
        (TaskDetailFragment) getSupportFragmentManager()
            .findFragmentById(R.id.task_detail_container);
    if (taskDetailFragment == null) {
      taskDetailFragment = TaskDetailFragment.newInstance();
      GeneralUtils.addFragmentToActivity(getSupportFragmentManager(),
          taskDetailFragment, R.id.task_detail_container);
    }
    // if statement to determine if passed task is for edit or a new one.
    if (TASK_URI != null) {
      mPresenter = new TasksDetailPresenter(getSupportLoaderManager(),
          loader,
          taskDetailFragment,
          TASK_URI, getApplicationContext());
    } else {
      mPresenter = new TasksDetailPresenter(getSupportLoaderManager(),
          loader,
          taskDetailFragment,
          null, getApplicationContext());
    }
  }

  /**
   * Helper method to setup a new Object of {@link AudioNoteFragment} however passed TASK has URI or
   * NOT.
   *
   * @param loader {@link TaskLoader}
   */
  private void setupAudioNote(TaskLoader loader) {
    // check if we have instance from the fragment it's okay
    // if not we get a new instance.
    AudioNoteFragment audioNoteFragment =
        (AudioNoteFragment) getSupportFragmentManager()
            .findFragmentById(R.id.task_detail_container);
    if (audioNoteFragment == null) {
      // Put Activity in Portrait Mode to prevent crash when Recording.
      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
      audioNoteFragment = AudioNoteFragment.newInstance();
      GeneralUtils.addFragmentToActivity(getSupportFragmentManager(),
          audioNoteFragment, R.id.task_detail_container);
    }
    // if statement to determine if passed task is for edit or a new one.
    if (TASK_URI != null) {
      mPresenter = new TasksDetailPresenter(getSupportLoaderManager(),
          loader,
          audioNoteFragment,
          TASK_URI, getApplicationContext());
    } else {
      mPresenter = new TasksDetailPresenter(getSupportLoaderManager(),
          loader,
          audioNoteFragment,
          null, getApplicationContext());
    }
  }

  /**
   * Helper method to setup a new Object of {@link TaskTodoFragment} however passed TASK has URI or
   * NOT.
   */
  private void setupTodoNote(TaskLoader loader) {
    // check if we have instance from the fragment it's okay
    // if not we get a new instance.
    TaskTodoFragment taskTodoFragment =
        (TaskTodoFragment) getSupportFragmentManager().findFragmentById(R.id.task_detail_container);
    if (taskTodoFragment == null) {
      taskTodoFragment = TaskTodoFragment.newInstance();
      GeneralUtils.addFragmentToActivity(getSupportFragmentManager(),
          taskTodoFragment, R.id.task_detail_container);
    }
    // if statement to determine if passed task is for edit or a new one.
    if (TASK_URI != null) {
      mPresenter = new TasksDetailPresenter(getSupportLoaderManager(),
          loader,
          taskTodoFragment,
          TASK_URI, getApplicationContext());
    } else {
      mPresenter = new TasksDetailPresenter(getSupportLoaderManager(),
          loader,
          taskTodoFragment,
          null, getApplicationContext());
    }
  }
}
