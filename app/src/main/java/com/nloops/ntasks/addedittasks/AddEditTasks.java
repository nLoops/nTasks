package com.nloops.ntasks.addedittasks;

import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.nloops.ntasks.R;
import com.nloops.ntasks.data.TaskLoader;
import com.nloops.ntasks.data.TasksLocalDataSource;

public class AddEditTasks extends AppCompatActivity {


    public static Uri taskUri;
    // constants for OnActivityResults to call the right SnackBar message.
    public static final int REQUEST_EDIT_TASK = 2;
    public static final int REQUEST_ADD_TASK = 3;
    public static final int RESULT_DELETE_TASK = 100;
    public static final int RESULT_UPDATE_TASK = 101;
    public static final int RESULT_ADD_TASK = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_tasks);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tasks_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setLogo(R.mipmap.ic_app_name);
        // get passed intent if available to load task data.
        taskUri = getIntent().getData();
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
        if (taskUri != null) {
            mPresenter = new TasksDetailPresenter(getSupportLoaderManager(),
                    loader,
                    localDataSource,
                    taskDetailFragment,
                    taskUri);
        } else {
            mPresenter = new TasksDetailPresenter(getSupportLoaderManager(),
                    loader,
                    localDataSource,
                    taskDetailFragment,
                    null);
        }
    }
}
