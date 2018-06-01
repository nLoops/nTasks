package com.nloops.ntasks.taskslist;


import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.nloops.ntasks.data.TasksDBContract.TaskEntry;

import com.nloops.ntasks.R;
import com.nloops.ntasks.adapters.TaskListAdapter;
import com.nloops.ntasks.addedittasks.AddEditTasks;
import com.nloops.ntasks.data.TaskLoader;
import com.nloops.ntasks.data.TasksDBContract;
import com.nloops.ntasks.data.TasksLocalDataSource;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

public class TasksFragment extends Fragment implements TasksListContract.View {

    private TasksListContract.Presenter mPresenter;
    private final int NO_TASK_ID = -1;

    private TaskListAdapter mAdapter;
    @BindView(R.id.tasks_list_recycle)
    RecyclerView mRecyclerView;
    @BindView(R.id.tasks_list_progress)
    ProgressBar mProgressBar;
    View mEmptyView;

    /**
     * Empty Constructor required by system.
     */
    public TasksFragment() {
    }

    public static TasksFragment newInstance() {
        return new TasksFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create new object of Cursor Loader
        TaskLoader loader = new TaskLoader(getActivity());
        // Create new instance of LocalDataSource
        TasksLocalDataSource dataSource = TasksLocalDataSource.getInstance(getActivity().getContentResolver(), getContext());
        // define Tasks.Presenter
        mPresenter = new TasksPresenter(loader, getActivity().getSupportLoaderManager(), this, dataSource);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_tasks_list, container, false);
        Context context = container.getContext();
        //Bind fragment layout elements
        ButterKnife.bind(this, rootView);
        // get ref of empty view lives into Activity.
        mEmptyView = (View) getActivity().findViewById(R.id.empty_view);
        mAdapter = new TaskListAdapter(null, getContext());
        mAdapter.setOnClickListener(onItemClickListener);
        mRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setHasFixedSize(true);
        // ref of Activity menu_fab
        FabSpeedDial fabSpeedDial = (FabSpeedDial) getActivity().findViewById(R.id.tasks_list_fab);
        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_note:
                        mPresenter.loadAddEditActivity(NO_TASK_ID, TaskEntry.TYPE_NORMAL_NOTE);
                        break;
                    case R.id.action_todo:
                        mPresenter.loadAddEditActivity(NO_TASK_ID, TaskEntry.TYPE_TODO_NOTE);
                        break;
                    case R.id.action_mic:
                        mPresenter.loadAddEditActivity(NO_TASK_ID, TaskEntry.TYPE_AUDIO_NOTE);
                        break;
                }
                return true;
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.loadTasks();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.removeLoader();
    }

    @Override
    public void setLoadingIndecator(boolean state) {
        if (state) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void showTasks(Cursor tasks) {
        if (mEmptyView.getVisibility() == View.VISIBLE) {
            mEmptyView.setVisibility(View.INVISIBLE);
        }
        mAdapter.swapCursor(tasks);
    }

    @Override
    public void showNoData() {
        mEmptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showDataReset() {
        mAdapter.swapCursor(null);
    }

    @Override
    public void showAddEditUI(long taskID, int taskType) {
        Intent addEditIntent = new Intent(getContext(), AddEditTasks.class);
        // if we have TaskID available that means user clicked on Task and in this case
        // we need to pass uri to Detail Activity to load data.
        if (taskID != NO_TASK_ID) {
            Uri taskUri = ContentUris.withAppendedId(TasksDBContract.TaskEntry.CONTENT_TASK_URI, taskID);
            addEditIntent.putExtra(AddEditTasks.EXTRAS_TASK_TYPE, taskType);
            addEditIntent.setData(taskUri);
            startActivityForResult(addEditIntent, AddEditTasks.REQUEST_EDIT_TASK);
        } else {
            addEditIntent.putExtra(AddEditTasks.EXTRAS_TASK_TYPE, taskType);
            startActivityForResult(addEditIntent, AddEditTasks.REQUEST_ADD_TASK);
        }

    }

    @Override
    public void showDeletedMessage() {
        showMessage(getString(R.string.msg_task_deleted));
    }

    @Override
    public void showUpdatedMessage() {
        showMessage(getString(R.string.msg_task_updated));
    }

    @Override
    public void showAddedMessage() {
        showMessage(getString(R.string.msg_task_added));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.result(requestCode, resultCode);
    }

    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void setPresenter(TasksListContract.Presenter presenter) {

    }

    TaskListAdapter.OnItemClickListener onItemClickListener = new TaskListAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View v, int position, int taskType) {
            long rawID = mAdapter.getItemId(position);
            mPresenter.loadAddEditActivity(rawID, taskType);
        }

        @Override
        public void onItemToggled(boolean active, int position) {
            long rawID = mAdapter.getItemId(position);
            mPresenter.updateComplete(active, rawID);
        }
    };
}
