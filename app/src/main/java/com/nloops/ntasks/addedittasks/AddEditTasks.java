package com.nloops.ntasks.addedittasks;

import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.nloops.ntasks.R;
import com.nloops.ntasks.data.TaskLoader;
import com.nloops.ntasks.data.TasksDBContract;
import com.nloops.ntasks.data.TasksLocalDataSource;

public class AddEditTasks extends AppCompatActivity {


    public static Uri TASK_URI;
    // constants for OnActivityResults to call the right SnackBar message.
    public static final int REQUEST_EDIT_TASK = 2;
    public static final int REQUEST_ADD_TASK = 3;
    public static final int RESULT_DELETE_TASK = 100;
    public static final int RESULT_UPDATE_TASK = 101;
    public static final int RESULT_ADD_TASK = 102;
    public static final String EXTRAS_TASK_TYPE = "task_type";
    public static int TASK_TYPE = TasksDBContract.TaskEntry.NO_TASK_TYPE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_tasks);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tasks_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setLogo(R.mipmap.ic_app_name);
        // get passed intent if available to load task data.
        TASK_URI = getIntent().getData();
        // get Task passed Task Type
        // first Check if the intent has this value.
        if (getIntent().hasExtra(EXTRAS_TASK_TYPE)) {
            // Second we have to get the Value and We make the Default as unassigned.
            TASK_TYPE = getIntent().getIntExtra(EXTRAS_TASK_TYPE,
                    TasksDBContract.TaskEntry.NO_TASK_TYPE);
        }
        // object of Loader that will return data using Cursor Adapter
        TaskLoader loader = new TaskLoader(this);
        // LocalDataSource that will apply operations on DB using ContentResolver.
        TasksLocalDataSource localDataSource = TasksLocalDataSource
                .getInstance(getContentResolver());
        // Check if we don't have instance of the fragment we will request a new one and link
        // it with the container on AddEditTasks layout.
        TaskDetailFragment taskDetailFragment =
                (TaskDetailFragment) getSupportFragmentManager().findFragmentById(R.id.task_detail_container);
        if (taskDetailFragment == null) {
            taskDetailFragment = TaskDetailFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.task_detail_container, taskDetailFragment);
            transaction.commit();
        }

        TasksDetailPresenter mPresenter;
        if (TASK_URI != null) {
            mPresenter = new TasksDetailPresenter(getSupportLoaderManager(),
                    loader,
                    localDataSource,
                    taskDetailFragment,
                    TASK_URI);
        } else {
            mPresenter = new TasksDetailPresenter(getSupportLoaderManager(),
                    loader,
                    localDataSource,
                    taskDetailFragment,
                    null);
        }
    }
}
