package com.nloops.ntasks.taskslist;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.nloops.ntasks.data.TaskLoader;
import com.nloops.ntasks.data.TasksLocalDataSource;

public class TasksPresenter implements TasksListContract.Presenter,
        LoaderManager.LoaderCallbacks<Cursor>, TasksLocalDataSource.LoadDataCallback {

    private static final int LOADER_ID = 1;

    private final TasksListContract.View mTaskView;

    @NonNull
    private final LoaderManager mLoaderManager;

    @NonNull
    private final TaskLoader mTasksLoader;

    @NonNull
    private final TasksLocalDataSource mLocalDataSource;

    public TasksPresenter(@NonNull TaskLoader taskLoader, @NonNull LoaderManager loaderManager,
                          @NonNull TasksListContract.View tasksView, @NonNull TasksLocalDataSource dataSource) {
        this.mTasksLoader = taskLoader;
        this.mLoaderManager = loaderManager;
        this.mLocalDataSource = dataSource;
        this.mTaskView = tasksView;
        mTaskView.setPresenter(this);

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
        mTaskView.setLoadingIndecator(true);
        return mTasksLoader.createTasksLoader();
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
        onDataReset();
    }

    @Override
    public void loadTasks() {
        initLoaderManager();
    }

    @Override
    public void showEmptyView() {
        mTaskView.setLoadingIndecator(false);
        mTaskView.showNoData();
    }

    @Override
    public void onDataLoaded(Cursor data) {
        mTaskView.setLoadingIndecator(false);
        mTaskView.showTasks(data);
    }

    @Override
    public void onDataEmpty() {
        showEmptyView();
    }

    @Override
    public void onDataNotAvailable() {

    }

    @Override
    public void onDataReset() {
        mTaskView.showDataReset();
    }
}
