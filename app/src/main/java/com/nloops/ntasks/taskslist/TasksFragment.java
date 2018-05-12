package com.nloops.ntasks.taskslist;


import android.content.Context;
import android.database.Cursor;
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


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_tasks_list, container, false);
        Context context = container.getContext();
        ButterKnife.bind(this, rootView);
        mAdapter = new TaskListAdapter(null);
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

    }

    @Override
    public void showDataReset() {
        mAdapter.swapCursor(null);
    }

    @Override
    public void setPresenter(TasksListContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
