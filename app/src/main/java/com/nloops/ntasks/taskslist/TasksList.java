package com.nloops.ntasks.taskslist;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.nloops.ntasks.R;
import com.nloops.ntasks.data.TaskLoader;
import com.nloops.ntasks.data.TasksLocalDataSource;

public class TasksList extends AppCompatActivity {

    private TasksPresenter mTasksPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tasks_list_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setLogo(R.mipmap.ic_app_name);

        TaskLoader loader = new TaskLoader(this);
        TasksLocalDataSource dataSource = TasksLocalDataSource.getInstance(getContentResolver());

        TasksFragment tasksFragment =
                (TasksFragment) getSupportFragmentManager().findFragmentById(R.id.tasks_list_container);
        if (tasksFragment == null) {
            // Create a new instance.
            tasksFragment = TasksFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.tasks_list_container, tasksFragment);
            transaction.commit();
        }

        mTasksPresenter = new TasksPresenter(
                loader,
                getSupportLoaderManager(),
                tasksFragment,
                dataSource
        );


    }
}
