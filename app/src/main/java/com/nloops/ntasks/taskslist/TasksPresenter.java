package com.nloops.ntasks.taskslist;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import com.nloops.ntasks.addedittasks.AddEditTasks;
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
        mTaskView.setLoadingIndicator(true);
        return mTasksLoader.createTasksLoader();
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            if (data.moveToLast() & data.getCount() > 0) {
                onDataLoaded(data);
            } else {
                onDataEmpty();
            }
        } else {
            onDataNotAvailable();
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        onDataReset();
    }

    @Override
    public void result(int requestCode, int resultCode) {
        if (AddEditTasks.REQUEST_ADD_TASK == requestCode
                && AddEditTasks.RESULT_ADD_TASK == resultCode) {
            mTaskView.showAddedMessage();
        } else if (AddEditTasks.REQUEST_EDIT_TASK == requestCode) {
            if (AddEditTasks.RESULT_UPDATE_TASK == resultCode) {
                mTaskView.showUpdatedMessage();
            } else if (AddEditTasks.RESULT_DELETE_TASK == resultCode) {
                mTaskView.showDeletedMessage();
            }
        }
    }

    @Override
    public void loadTasks() {
        initLoaderManager();
    }

    @Override
    public void showEmptyView() {
        mTaskView.setLoadingIndicator(false);
        mTaskView.showNoData();
    }

    @Override
    public void loadAddEditActivity(long taskID, int taskType) {
        mTaskView.showAddEditUI(taskID, taskType);
    }

    @Override
    public void updateComplete(boolean state, long rawID) {
        mLocalDataSource.completeTask(state, rawID);
        removeLoader();
        initLoaderManager();
    }

    @Override
    public void removeLoader() {
        if (mLoaderManager.getLoader(LOADER_ID) != null) {
            mLoaderManager.destroyLoader(LOADER_ID);
        }
    }

    @Override
    public void deleteTask(Uri taskUri) {
        mLocalDataSource.deleteTask(taskUri);
        removeLoader();
        initLoaderManager();
    }


    @Override
    public void onDataLoaded(Cursor data) {
        mTaskView.setLoadingIndicator(false);
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
