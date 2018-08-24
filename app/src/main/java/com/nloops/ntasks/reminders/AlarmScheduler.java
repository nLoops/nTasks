package com.nloops.ntasks.reminders;


import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import com.nloops.ntasks.R;
import com.nloops.ntasks.addedittasks.AddEditTasks;
import com.nloops.ntasks.data.Task;
import com.nloops.ntasks.data.TasksDBContract;
import com.nloops.ntasks.utils.GeneralUtils;
import java.util.Date;

/**
 * Helper to manage scheduling the reminder alarm
 */
public class AlarmScheduler {

  // Notification Channel ID
  private static final String NOTIFICATION_CHANNEL_ID = "main_channel";

  /**
   * Schedule a reminder alarm at the specified time for the given task.
   *
   * @param context Local application or activity context
   * @param alarmTime Alarm start time
   * @param reminderTask Uri referencing the task in the content provider
   * @param cls {@link AlarmReceiver}
   * @param taskType TaskType.
   */
  public static void scheduleAlarm(Context context, long alarmTime, Uri reminderTask,
      Class<?> cls, int taskType) {
    // Enable a receiver
    ComponentName receiver = new ComponentName(context, cls);
    PackageManager pm = context.getPackageManager();
    pm.setComponentEnabledSetting(receiver,
        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
        PackageManager.DONT_KILL_APP);
    // get PendingIntent for the passed URI
    PendingIntent operation = getReminderPendingIntent(context, reminderTask, cls, taskType);
    //Schedule the alarm. Will update an existing item for the same task.
    AlarmManager manager = AlarmManagerProvider.getAlarmManager(context);
    // cancel if we have same PendingIntent set before.
    manager.cancel(operation);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      manager.setExact(AlarmManager.RTC, alarmTime, operation);
    }


  }

  /**
   * Notify user by Task Title when the Broadcast onReceive fires.
   *
   * @param context passed Context.
   * @param data Task Type.
   */
  public static void notifyUser(Context context, Uri data, int taskType) {
    NotificationManager manager = (NotificationManager) context
        .getSystemService(Context.NOTIFICATION_SERVICE);

    //Display a notification to view the task details
    Intent action = new Intent(context, AddEditTasks.class);
    action.putExtra("task_type", taskType);
    action.setData(data);
    PendingIntent operation = TaskStackBuilder.create(context)
        .addNextIntentWithParentStack(action)
        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

    //Grab the task taskTitle
    Cursor cursor = context.getContentResolver().query(data, null, null, null, null);
    String taskTitle = "";
    String taskPath = "";
    Task task = null;
    try {
      if (cursor != null && cursor.moveToFirst()) {
        task = new Task(cursor);
        taskTitle = TasksDBContract
            .getColumnString(cursor, TasksDBContract.TaskEntry.COLUMN_NAME_TITLE);
        taskPath = TasksDBContract
            .getColumnString(cursor, TasksDBContract.TaskEntry.COLUMN_NAME_PATH);
      }
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }

    // Ref of Notification Channel
    NotificationChannel mChannel;

    // Create Notification Channel this only for OS O and further.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      CharSequence cName = context.getString(R.string.notification_channel_name);
      String cDescription = context.getString(R.string.notification_channel_desc);
      int importance = NotificationManager.IMPORTANCE_DEFAULT;
      mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, cName, importance);
      mChannel.setDescription(cDescription);
      mChannel.setShowBadge(true);
      assert manager != null;
      manager.createNotificationChannel(mChannel);
    }
    // create unique notification ID
    int NOTIFICATION_ID = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

    // add Notifications Actions
    Intent completeIntent = new Intent(context, TaskOperationService.class);
    assert task != null;
    if (task.getIsRepeated()) {
      long nextDate = task.getDate() + GeneralUtils.getRepeatedValue(task.getRepeated());
      task.setDate(nextDate);
      completeIntent.setAction(TaskOperationService.ACTION_UPDATE_TASK_NOTIFICATION);
      completeIntent.putExtra(TaskOperationService.EXTRAS_UPDATE_TASK_DATA, task);
      completeIntent.putExtra(TaskOperationService.EXTRAS_NOTIFICATION_ID, NOTIFICATION_ID);
      completeIntent.setData(data);
    } else {
      // add Action filter
      completeIntent.setAction(TaskOperationService.ACTION_COMPLETE_TASK);
      // add Notification ID so after hit action button we will dismiss the notification bar
      completeIntent.putExtra(TaskOperationService.EXTRAS_NOTIFICATION_ID, NOTIFICATION_ID);
      // put passed uri to extract it's ID
      completeIntent.setData(data);
    }
    PendingIntent actionComplete = PendingIntent.getService(context, 1, completeIntent,
        PendingIntent.FLAG_UPDATE_CURRENT);

    // Setup ContentTitle For Notification
    String contentTitle = context.getString(R.string.reminder_title);
    switch (taskType) {
      case TasksDBContract.TaskEntry.TYPE_NORMAL_NOTE:
        contentTitle = context.getString(R.string.reminder_normaltask);
        break;
      case TasksDBContract.TaskEntry.TYPE_TODO_NOTE:
        contentTitle = context.getString(R.string.reminder_todolist);
        break;
      case TasksDBContract.TaskEntry.TYPE_AUDIO_NOTE:
        contentTitle = context.getString(R.string.reminder_audionote);
        break;
    }
    long[] pattern = {500, 500, 500, 500};
    // Build a Notification.
    NotificationCompat.Builder note = new NotificationCompat.Builder(context)
        .setColor(ContextCompat.getColor(context, R.color.colorAccent))
        .setContentTitle(contentTitle)
        .setContentText(taskTitle)
        .setSmallIcon(R.drawable.ic_done_white)
        .setContentIntent(operation)
        .setChannelId(NOTIFICATION_CHANNEL_ID)
        .addAction(R.drawable.ic_done_white, context.getString(R.string.notify_action_complete)
            , actionComplete)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
        .setVibrate(pattern)
        .setAutoCancel(true);

    if (!taskPath.isEmpty()) {
      Intent intent = new Intent(context, TaskOperationService.class);
      intent.setAction(TaskOperationService.ACTION_PLAY_NOTE_AUDIO);
      intent.putExtra(TaskOperationService.EXTRAS_NOTE_AUDIO_PATH, taskPath);
      PendingIntent actionPlayNote = PendingIntent.getService
          (context, 101, intent,
              PendingIntent.FLAG_UPDATE_CURRENT);
      note.addAction(R.drawable.ic_play_btn, context.getString(R.string.play), actionPlayNote);

    }

    assert manager != null;
    manager.notify(NOTIFICATION_ID, note.build());
  }


  /**
   * Return Pending intent of uri.
   *
   * @param context passed Context
   * @param uri passed Task URI
   * @param cls Receiver Class
   * @param taskType Task Type
   * @return PendingIntent
   */
  private static PendingIntent getReminderPendingIntent(Context context, Uri uri, Class<?> cls,
      int taskType) {
    Intent intent = new Intent(context, cls);
    intent.putExtra("task_type", taskType);
    intent.setData(uri);
    return PendingIntent.getBroadcast(context,
        0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
  }

  /**
   * Return Pending intent of uri.
   *
   * @param context passed Context
   * @param uri passed Task URI
   * @param cls Receiver Class
   * @return PendingIntent
   */
  public static PendingIntent getReminderPendingIntent(Context context, Uri uri, Class<?> cls) {
    Intent intent = new Intent(context, cls);
    intent.setData(uri);
    return PendingIntent.getBroadcast(context,
        0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
  }
}
