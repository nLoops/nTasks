package com.nloops.ntasks.reminders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.nloops.ntasks.data.Task;
import com.nloops.ntasks.data.TasksDBContract;


/**
 * This Receiver will get the Scheduled Alarm from {@link AlarmScheduler} to fire the OnReceive
 * on the Exact time.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // when the device reboot all scheduling Alarms will be canceling and we need to be sure that
        // we keep reminding our user with his value tasks.
        if (intent.getAction() != null) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {

                String selection = TasksDBContract.TaskEntry.COLUMN_NAME_COMPLETE + "=? AND "
                        + TasksDBContract.TaskEntry.COLUMN_NAME_DATE + ">?";
                String[] selectionArgs = new String[]{String.valueOf(TasksDBContract.TaskEntry.STATE_NOT_COMPLETED)
                        , String.valueOf(System.currentTimeMillis())};
                Cursor cursor = context.getContentResolver().query(TasksDBContract.TaskEntry.CONTENT_TASK_URI,
                        null,
                        selection,
                        selectionArgs,
                        null);
                assert cursor != null;
                try {
                    while (cursor.moveToNext()) {
                        Task task = new Task(cursor);
                        AlarmScheduler.scheduleAlarm(context, task.getDate(), task.getTaskUri(),
                                AlarmReceiver.class, task.getType());
                    }
                } finally {
                    cursor.close();
                }
            }
        } else {
            // show the notification with TaskUri, to open task Detail fragment.
            AlarmScheduler.notifyUser(context, intent.getData(),
                    intent.getIntExtra("task_type", -1));
        }

    }
}
