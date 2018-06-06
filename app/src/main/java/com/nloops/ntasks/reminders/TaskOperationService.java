package com.nloops.ntasks.reminders;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.nloops.ntasks.data.Task;
import com.nloops.ntasks.data.TasksDBContract;
import com.nloops.ntasks.data.TasksLocalDataSource;

public class TaskOperationService extends IntentService {

    private static final String TAG = TaskOperationService.class.getSimpleName();
    public static final String EXTRAS_NOTIFICATION_ID = "notification_id";
    public static final String ACTION_COMPLETE_TASK = "complete_task";
    public static final String ACTION_VIEW_TASK = "complete_task";
    private TasksLocalDataSource mTasksDataSource;


    public TaskOperationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        assert intent != null;
        if (intent.getAction().equals(ACTION_COMPLETE_TASK)) {
            performCompleteTask(intent);
        }
    }


    private void performCompleteTask(Intent intent) {
        mTasksDataSource = new TasksLocalDataSource(getContentResolver(),
                this);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        long rawID = ContentUris.parseId(intent.getData());
        mTasksDataSource.completeTask(true, rawID);
        assert manager != null;
        manager.cancel(intent.getIntExtra(EXTRAS_NOTIFICATION_ID, 0));
    }

}
