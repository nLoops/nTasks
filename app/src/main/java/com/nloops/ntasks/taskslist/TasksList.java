package com.nloops.ntasks.taskslist;

import android.Manifest;
import android.annotation.TargetApi;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.nloops.ntasks.R;
import com.nloops.ntasks.data.TaskLoader;
import com.nloops.ntasks.data.TasksLocalDataSource;
import com.nloops.ntasks.widgets.WidgetIntentService;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class TasksList extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final int PERMISSION_REQ_CODE = 225;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tasks_list_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setLogo(R.mipmap.ic_app_name);

        TasksFragment tasksFragment =
                (TasksFragment) getSupportFragmentManager().findFragmentById(R.id.tasks_list_container);
        if (tasksFragment == null) {
            // Create a new instance.
            tasksFragment = TasksFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.tasks_list_container, tasksFragment);
            transaction.commit();
        }

        // update Widget List with data.
        WidgetIntentService.startActionChangeList(this);

        // we need to get required permissions to allow app to RECORD AUDIO_NOTES and SAVE FILES
        getPermissions();

    }

    /**
     * This Method will check if we have the required permissions to RECORD and SAVE files,
     * if not we will alert USER to get the permissions.
     */
    @TargetApi(23)
    private void getPermissions() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO};
        if (!EasyPermissions.hasPermissions(TasksList.this, permissions)) {
            EasyPermissions.requestPermissions(this,
                    getString(R.string.permissions_required),
                    PERMISSION_REQ_CODE, permissions);
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
// TODO : we need to consider if the USER denied the permissions and need to add an AUIDo note, in this case we need to Display AlertDialog to grant the Permissions.

    }
}
