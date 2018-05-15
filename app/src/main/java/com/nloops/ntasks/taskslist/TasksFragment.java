package com.nloops.ntasks.taskslist;


import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.nloops.ntasks.R;
import com.nloops.ntasks.adapters.TaskListAdapter;
import com.nloops.ntasks.addedittasks.AddEditTasks;
import com.nloops.ntasks.data.TasksDBContract;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TasksFragment extends Fragment implements TasksListContract.View {

    private TasksListContract.Presenter mPresenter;

    private TaskListAdapter mAdapter;
    @BindView(R.id.tasks_list_recycle)
    RecyclerView mRecyclerView;
    @BindView(R.id.tasks_list_progress)
    ProgressBar mProgressBar;

    /**
     * Empty Constructor required by system.
     */
    public TasksFragment() {
    }

    public static TasksFragment newInstance() {
        return new TasksFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_tasks_list, container, false);
        Context context = container.getContext();
        ButterKnife.bind(this, rootView);
        mAdapter = new TaskListAdapter(null);
        mAdapter.setOnClickListener(onItemClickListener);
        mRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.loadTasks();
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
        mAdapter.swapCursor(tasks);
    }

    @Override
    public void showNoData() {
        View view = getActivity().findViewById(R.id.empty_view);
        view.setVisibility(View.VISIBLE);
    }

    @Override
    public void showDataReset() {
        mAdapter.swapCursor(null);
    }

    @Override
    public void showAddEditUI(long taskID) {
        Intent addEditIntent = new Intent(getContext(), AddEditTasks.class);
        Uri taskUri = ContentUris.withAppendedId(TasksDBContract.TaskEntry.CONTENT_TASK_URI, taskID);
        addEditIntent.setData(taskUri);
        startActivity(addEditIntent);
    }

    @Override
    public void setPresenter(TasksListContract.Presenter presenter) {
        mPresenter = presenter;
    }

    TaskListAdapter.OnItemClickListener onItemClickListener = new TaskListAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {
            long rawID = mAdapter.getItemId(position);
            mPresenter.loadAddEditActivity(rawID);
        }

        @Override
        public void onItemToggled(boolean active, int position) {
            long rawID = mAdapter.getItemId(position);
            mPresenter.updateComplete(active, rawID);
        }
    };
}
