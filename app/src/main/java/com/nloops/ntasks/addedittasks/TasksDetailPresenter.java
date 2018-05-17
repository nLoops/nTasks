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

    @NonNull
    private final boolean isExistingTask;

    @Nullable
    private final Uri taskUri;

    /**
     * Public Constructor.
     *
     * @param loaderManager
     * @param taskLoader
     * @param localDataSource
     * @param taskView
     * @param existing
     * @param uri
     */
    public TasksDetailPresenter(
            @NonNull LoaderManager loaderManager,
            @NonNull TaskLoader taskLoader,
            @NonNull TasksLocalDataSource localDataSource,
            @NonNull TaskDetailContract.View taskView,
            @NonNull boolean existing,
            @Nullable Uri uri) {

        this.mLoaderManager = loaderManager;
        this.mTaskLoader = taskLoader;
        this.mLocalDataSource = localDataSource;
        this.mTasksView = taskView;
        this.isExistingTask = existing;
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
        initLoaderManager();
    }

    @Override
    public void updateTask(Task task, Uri uri) {
        mLocalDataSource.updateTask(task, uri);
        mTasksView.setUpdateTaskMessage();
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
