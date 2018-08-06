package com.nloops.ntasks.addedittasks;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import com.nloops.ntasks.data.Task;
import com.nloops.ntasks.data.TaskLoader;
import com.nloops.ntasks.data.TasksLocalDataSource;
import com.nloops.ntasks.reminders.TaskOperationService;

public class TasksDetailPresenter implements TaskDetailContract.Presenter,
    LoaderManager.LoaderCallbacks<Cursor>,
    TasksLocalDataSource.LoadDataCallback {

  private static final int LOADER_ID = 55;

  private final TaskDetailContract.View mTasksView;

  @NonNull
  private final LoaderManager mLoaderManager;

  @NonNull
  private final TaskLoader mTaskLoader;


  @Nullable
  private final Uri taskUri;

  @NonNull
  private final Context mContext;

  /**
   * Public Constructor.
   *
   * @param loaderManager {@link LoaderManager}
   * @param taskLoader {@link TaskLoader}
   * @param taskView implementation of {@link TaskDetailContract.View} which fragment in our
   * situation.
   * @param uri {@link Task} Uri.
   */
  public TasksDetailPresenter(
      @NonNull LoaderManager loaderManager,
      @NonNull TaskLoader taskLoader,
      @NonNull TaskDetailContract.View taskView,
      @Nullable Uri uri,
      @NonNull Context context
  ) {

    this.mLoaderManager = loaderManager;
    this.mTaskLoader = taskLoader;
    this.mTasksView = taskView;
    this.taskUri = uri;
    this.mContext = context;
    mTasksView.setPresenter(this);
  }

  private void initLoaderManager() {
    if (mLoaderManager.getLoader(LOADER_ID) == null) {
      mLoaderManager.initLoader(LOADER_ID, null, this);
    } else {
      mLoaderManager.restartLoader(LOADER_ID, null, this);
    }
  }


  @NonNull
  @Override
  public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
    return mTaskLoader.createTaskLoader(taskUri);
  }

  @Override
  public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
    if (data != null && data.getCount() > 0) {
      onDataLoaded(data);
    } else {
      onDataEmpty();
    }
  }

  @Override
  public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    /*implement in the future*/
  }

  @Override
  public void loadTaskData() {
    if (taskUri != null) {
      initLoaderManager();
    }
  }

  @Override
  public void updateTask(Task task, Uri uri) {
    Intent updateIntent = new Intent(mContext, TaskOperationService.class);
    updateIntent.setAction(TaskOperationService.ACTION_UPDATE_TASK);
    updateIntent.putExtra(TaskOperationService.EXTRAS_UPDATE_TASK_DATA, task);
    updateIntent.setData(uri);
    mContext.startService(updateIntent);
    mTasksView.showTasksListUpdated();
  }

  @Override
  public void deleteTask(@NonNull Uri taskUri) {
    Intent deleteIntent = new Intent(mContext, TaskOperationService.class);
    deleteIntent.setData(taskUri);
    deleteIntent.setAction(TaskOperationService.ACTION_DELETE_TASK);
    mContext.startService(deleteIntent);
    mTasksView.showTasksListDelete();
  }

  @Override
  public void saveTask(@NonNull Task task) {
    Intent saveIntent = new Intent(mContext, TaskOperationService.class);
    saveIntent.setAction(TaskOperationService.ACTION_SAVE_NEW_TASK);
    saveIntent.putExtra(TaskOperationService.EXTRAS_SAVE_NEW_TASK_DATA, task);
    mContext.startService(saveIntent);
    mTasksView.showTasksListAdded();
  }

  @Override
  public void launchDatePicker() {
    mTasksView.showDateTimePicker();
  }

  @Override
  public void completeTODO(boolean state, long rawID) {
    Intent completeIntent = new Intent(mContext, TaskOperationService.class);
    completeIntent.setAction(TaskOperationService.ACTION_COMPLETE_TODO);
    completeIntent.putExtra(TaskOperationService.EXTRAS_TODO_STATE, state);
    completeIntent.putExtra(TaskOperationService.EXTRAS_TODO_ID, rawID);
    mContext.startService(completeIntent);
  }

  @Override
  public void completeTask(boolean state, long rawID) {
    Intent completeIntent = new Intent(mContext, TaskOperationService.class);
    completeIntent.setAction(TaskOperationService.ACTION_COMPLETE_EXISTING_TASK);
    completeIntent.putExtra(TaskOperationService.EXTRAS_TASK_STATE, state);
    completeIntent.putExtra(TaskOperationService.EXTRAS_TASK_ID, rawID);
    mContext.startService(completeIntent);
    mTasksView.showTasksListUpdated();
  }


  @Override
  public void onDataLoaded(Cursor data) {
    data.moveToFirst();
    Task task = new Task(data);
    mTasksView.displayTaskData(task);
  }

  @Override
  public void onDataEmpty() {

  }

  @Override
  public void onDataNotAvailable() {

  }

  @Override
  public void onDataReset() {

  }
}
