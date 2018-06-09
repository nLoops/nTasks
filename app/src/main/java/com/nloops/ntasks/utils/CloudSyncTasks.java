package com.nloops.ntasks.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nloops.ntasks.data.Task;

/**
 * This class will implemented {@link FirebaseDatabase} to daily sync user tasks if user set
 * sync option to true and revert data back in case user lost or deleted and re-installed the app again
 */
public class CloudSyncTasks {

    private static final String TASKS_DATABASE_REFERENCE = "tasks";

    public static void syncData(Task task) {
        // get ref of whole database
        FirebaseDatabase mFireDataBase = FirebaseDatabase.getInstance();
        // get ref of tasks node in the database.
        DatabaseReference mFireDatabaseReference = mFireDataBase.getReference().child(TASKS_DATABASE_REFERENCE);
        // Push Data to RealTimeDB
        mFireDatabaseReference.push().setValue(task);
    }
}
