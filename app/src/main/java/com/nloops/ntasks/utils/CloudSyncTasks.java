package com.nloops.ntasks.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nloops.ntasks.R;
import com.nloops.ntasks.data.Task;
import com.nloops.ntasks.data.TasksDBContract;

import java.util.concurrent.TimeUnit;

/**
 * This class will implemented {@link FirebaseDatabase} to daily sync user tasks if user set
 * sync option to true and revert data back in case user lost or deleted and re-installed the app again
 */
public class CloudSyncTasks {

    private static final String TASKS_DATABASE_REFERENCE = "tasks";

    private static final int TWELVE_HOURS = 12;
    private static final int DAY_HOURS = 24;
    private static final int WEEK_HOURS = 168;
    private static final int FLEXTIME_MULTIPLER = 3;
    private static int SYNC_INTERVAL_HOURS;
    private static int SYNC_INTERVAL_SECONDS;
    private static int SYNC_FLEXTIME_SECONDS;

    private static boolean sInitialized;
    private static final String TASKS_TAG = "sync-tasks";

    public static void syncData(Cursor cursor, Context context) {
        // get ref of whole database
        FirebaseDatabase mFireDataBase = FirebaseDatabase.getInstance();
        // get UserID from SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String currentUser =
                preferences.getString
                        (context.getString(R.string.current_user_firebase), "");
        while (cursor.moveToNext()) {
            Task task = new Task(cursor);
            // if currentTask type is TODOlist we get todos data
            if (task.getType() == TasksDBContract.TaskEntry.TYPE_TODO_NOTE) {
                String[] selectionArgs = new String[]{String.valueOf(task.getID())};
                task.setTodos(GeneralUtils.getTodoData(context, selectionArgs));

            }
            // get ref of tasks node in the database.
            DatabaseReference mFireDatabaseReference =
                    mFireDataBase.getReference().child(TASKS_DATABASE_REFERENCE)
                            // The node will be like
                            // ***tasks root node
                            //   *****User
                            //        ***** Tasks Data
                            .child(currentUser)
                            .child(String.valueOf(task.getID()));
            // Push Data to RealTimeDB
            mFireDatabaseReference.setValue(task);
        }
    }


    private static void scheduleFirebaseJobDispatcherSync(@NonNull final Context context) {

        getSyncTime(context);

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job tasksJob = dispatcher.newJobBuilder()
                /* The Service that will be used to sync Tasks's data */
                .setService(TasksFirebaseJobService.class)
                /* Set the UNIQUE tag used to identify this Job */
                .setTag(TASKS_TAG)
                /*
                 * Network constraints on which this Job should run. We choose to run on any
                 * network, but you can also choose to run only on un-metered networks or when the
                 * device is charging. It might be a good idea to include a preference for this,
                 * as some users may not want to download any data on their mobile plan. ($$$)
                 */
                .setConstraints(Constraint.ON_ANY_NETWORK)
                /*
                 * setLifetime sets how long this job should persist. The options are to keep the
                 * Job "forever" or to have it die the next time the device boots up.
                 */
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                /*
                 * When should our job start we add a frame with start and end time and the
                 * job will be run within
                 */
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                /*
                 * If a Job with the tag with provided already exists, this new job will replace
                 * the old one.
                 */
                .setReplaceCurrent(true)
                /* Once the Job is ready, call the builder's build method to return the Job */
                .build();
        /* Schedule the Job with the dispatcher */
        dispatcher.schedule(tasksJob);
    }

    synchronized public static void initialize(@NonNull final Context context) {
        if (sInitialized) return;
        sInitialized = true;
        setScheduled(context, true);
        scheduleFirebaseJobDispatcherSync(context);
    }

    /**
     * Helper method to set Boolean Value to prevent unneeded schedules.
     *
     * @param context {@link Context}
     * @param value   {@link #sInitialized}
     */
    private static void setScheduled(Context context, boolean value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit()
                .putBoolean(context.getString(R.string.backup_schedule), value)
                .commit();
    }

    /**
     * Helper Method to get SyncTime Preference that chosen By user.
     *
     * @param context Passed {@link Context}
     */
    private static void getSyncTime(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String syncTime = preferences.getString(context.getString(R.string.settings_sync_time_key),
                context.getString(R.string.settings_sync_time_default));
        if (syncTime.equals(context.getString(R.string.settings_sync_twelve_key))) {
            SYNC_INTERVAL_HOURS = TWELVE_HOURS;
        } else if (syncTime.equals(context.getString(R.string.settings_sync_day_key))) {
            SYNC_INTERVAL_HOURS = DAY_HOURS;
        } else if (syncTime.equals(context.getString(R.string.settings_sync_week_key))) {
            SYNC_INTERVAL_HOURS = WEEK_HOURS;
        }
        // Convert Hours to Seconds
        SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
        // Set FlexTime to make a frame to fire Job within
        SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / FLEXTIME_MULTIPLER;
    }


}
