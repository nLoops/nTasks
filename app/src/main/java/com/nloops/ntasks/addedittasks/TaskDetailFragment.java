package com.nloops.ntasks.addedittasks;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
        View rootView = inflater.inflate(R.layout.frag_task_detail, container, false);
        ButterKnife.bind(this, rootView);
        FloatingActionButton detailFAB = (FloatingActionButton) getActivity().findViewById(R.id.task_detail_fab);
        detailFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTask.setTitle(mTitle.getText().toString());
                mTask.setBody(mBody.getText().toString());
                mPresenter.updateTask(mTask, AddEditTasks.taskUri);
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
    public void setUpdateTaskMessage() {
        getActivity().finish();
    }

    @Override
    public void setPresenter(TaskDetailContract.Presenter presenter) {
        mPresenter = presenter;
    }

}
