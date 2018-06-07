package com.nloops.ntasks.addedittasks;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.nloops.ntasks.data.Task;
import com.nloops.ntasks.data.TaskLoader;
import com.nloops.ntasks.data.TasksDBContract;
import com.nloops.ntasks.data.TasksLocalDataSource;

public class TasksDetailPresenter implements TaskDetailContract.Presenter,
        LoaderManager.LoaderCallbacks<Cursor>,
        TasksLocalDataSource.LoadDataCallback {

    private static final int LOADER_ID = 55;

    private final TaskDetailContract.View mTasksView;

    @NonNull
    private final LoaderManager mLoaderManager;

    @NonNull
    private final TaskLoader mTaskLoader;

    @NonNull
    private final TasksLocalDataSource mLocalDataSource;


    @Nullable
    private final Uri taskUri;

    /**
     * Public Constructor.
     *
     * @param loaderManager   {@link LoaderManager}
     * @param taskLoader      {@link TaskLoader}
     * @param localDataSource {@link TasksLocalDataSource}
     * @param taskView        implementation of {@link TaskDetailContract.View} which fragment
     *                        in our situation.
     * @param uri             {@link Task} Uri.
     */
    public TasksDetailPresenter(
            @NonNull LoaderManager loaderManager,
            @NonNull TaskLoader taskLoader,
            @NonNull TasksLocalDataSource localDataSource,
            @NonNull TaskDetailContract.View taskView,
            @Nullable Uri uri) {

        this.mLoaderManager = loaderManager;
        this.mTaskLoader = taskLoader;
        this.mLocalDataSource = localDataSource;
        this.mTasksView = taskView;
        this.taskUri = uri;
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

    }

    @Override
    public void loadTaskData() {
        if (taskUri != null) {
            initLoaderManager();
        }
    }

    @Override
    public void updateTask(Task task, Uri uri) {
        mLocalDataSource.updateTask(task, uri);
        mTasksView.showTasksListUpdated();
    }

    @Override
    public void deleteTask(@NonNull Uri taskUri) {
        mLocalDataSource.deleteTask(taskUri);
        mTasksView.showTasksListDelete();
    }

    @Override
    public void saveTask(@NonNull Task task) {
        mLocalDataSource.saveTask(task);
        mTasksView.showTasksListAdded();
    }

    @Override
    public void launchDatePicker() {
        mTasksView.showDateTimePicker();
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
