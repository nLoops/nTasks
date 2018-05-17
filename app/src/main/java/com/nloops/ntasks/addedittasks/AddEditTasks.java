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
        if (taskUri != null) {
            TaskLoader loader = new TaskLoader(this);
            TasksLocalDataSource localDataSource = TasksLocalDataSource
                    .getInstance(getContentResolver());
            TaskDetailFragment taskDetailFragment =
                    (TaskDetailFragment) getSupportFragmentManager().findFragmentById(R.id.task_detail_container);
            if (taskDetailFragment == null) {
                taskDetailFragment = TaskDetailFragment.newInstance();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.task_detail_container, taskDetailFragment);
                transaction.commit();
            }

            TasksDetailPresenter mPresenter = new TasksDetailPresenter(getSupportLoaderManager(),
                    loader,
                    localDataSource,
                    taskDetailFragment,
                    true,
                    taskUri);

        }
    }
}
