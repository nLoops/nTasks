package com.nloops.ntasks.data;


import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * following the explanation of the Android Architecture
 * (https://github.com/googlesamples/android-architecture)
 * This Interface will be the Main Entry Point to access {@link Task} data using
 * Callbacks for Single Task and List<Tasks></>
 */
public interface TasksDataSource {

    void saveTask(@NonNull Task task);

    void deleteTask(@NonNull Uri taskUri);

    void completeTask(boolean state, long rawID);

    void updateTask(@NonNull Task task, @NonNull Uri uri);

    void completeTODO(boolean state, long rawID, long taskID);

    void deleteAll();

}
