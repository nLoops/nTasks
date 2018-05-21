package com.nloops.ntasks.addedittasks;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.nloops.ntasks.R;

import com.nloops.ntasks.data.Task;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskDetailFragment extends Fragment implements TaskDetailContract.View {

    private TaskDetailContract.Presenter mPresenter;

    @BindView(R.id.task_detail_title)
    EditText mTitle;
    @BindView(R.id.task_detail_body)
    EditText mBody;
    @BindView(R.id.task_detail_priority_switch)
    SwitchCompat mPriortySwitch;

    private Task mTask;


    /**
     * Empty constructor (Required)
     */
    public TaskDetailFragment() {
    }

    public static TaskDetailFragment newInstance() {
        return new TaskDetailFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // using to inflate fragment menu.
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.frag_task_detail, container, false);
        ButterKnife.bind(this, rootView);
        FloatingActionButton detailFAB = (FloatingActionButton) getActivity().findViewById(R.id.task_detail_fab);
        detailFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AddEditTasks.taskUri == null) {
                    Task task = new Task(mTitle.getText().toString(),
                            mBody.getText().toString(),
                            0, 0, System.currentTimeMillis(), 0, "", null);
                    mPresenter.saveTask(task);
                } else {
                    mTask.setTitle(mTitle.getText().toString());
                    mTask.setBody(mBody.getText().toString());
                    mPresenter.updateTask(mTask, AddEditTasks.taskUri);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.loadTaskData();
    }

    @Override
    public void displayTaskData(Task task) {
        mTask = task;
        mTitle.setText(task.getTitle());
        mBody.setText(task.getBody());

    }

    @Override
    public void showTasksListUpdated() {
        getActivity().setResult(AddEditTasks.RESULT_UPDATE_TASK);
        getActivity().finish();
    }

    @Override
    public void showTasksListDelete() {
        getActivity().setResult(AddEditTasks.RESULT_DELETE_TASK);
        getActivity().finish();
    }

    @Override
    public void showTasksListAdded() {
        getActivity().setResult(AddEditTasks.RESULT_ADD_TASK);
        getActivity().finish();
    }

    @Override
    public void setPresenter(TaskDetailContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.task_detail_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_detail_delete:
                mPresenter.deleteTask(AddEditTasks.taskUri);
                break;
            case R.id.action_detail_reminder:
                break;
        }

        return true;
    }
}
