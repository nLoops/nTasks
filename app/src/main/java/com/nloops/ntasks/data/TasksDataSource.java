package com.nloops.ntasks.data;


import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * following the explanation of the Android Architecture
 * (https://github.com/googlesamples/android-architecture)
 * This Interface will be the Main Entry Point to access {@link Task} data using
 * Callbacks for Single Task and List<Tasks></>
 */
public interface TasksDataSource {

    interface LoadTasksCallback {

        void onTasksLoaded(List<Task> tasks);

        void onDataNotAvaialbe();
    }

    interface GetTaskCallback {

        void onTaskLoaded(Task task);

        void onDataNotAvailabe();
    }

    void getTasks(@NonNull LoadTasksCallback callback);

    void getTask(@NonNull int taskID, @NonNull GetTaskCallback callback);

    void saveTask(@NonNull Task task);

    void clearCompletedTasks();

    void refreshTasks();

    void deleteAllTasks();

    void deleteTask(@NonNull Uri taskUri);

    void completeTask(@NonNull boolean state, @NonNull long rawID);

    void updateTask(@NonNull Task task, @NonNull Uri uri);

}
