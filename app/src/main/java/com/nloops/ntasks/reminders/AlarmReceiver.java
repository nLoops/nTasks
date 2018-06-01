package com.nloops.ntasks.reminders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

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
        if (intent.getAction() != null && context != null) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {

                Cursor cursor = context.getContentResolver().query(intent.getData(),
                        null, null, null, null);
                long alarmTime = System.currentTimeMillis();
                int taskType = TasksDBContract.TaskEntry.NO_TASK_TYPE;

                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        alarmTime = TasksDBContract.getColumnLong(cursor, TasksDBContract.TaskEntry.COLUMN_NAME_DATE);
                        taskType = TasksDBContract.getColumnInt(cursor, TasksDBContract.TaskEntry.COLUMN_NAME_TYPE);
                    }
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }

                AlarmScheduler.scheduleAlarm(context, alarmTime, intent.getData(), AlarmReceiver.class, taskType);

            }
        }
        // show the notification with TaskUri, to open task Detail fragment.
        AlarmScheduler.notifyUser(context, intent.getData(),
                intent.getIntExtra("task_type", -1));
    }
}
