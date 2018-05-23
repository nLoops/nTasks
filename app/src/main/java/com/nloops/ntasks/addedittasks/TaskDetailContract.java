package com.nloops.ntasks.addedittasks;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.nloops.ntasks.data.BaseView;
import com.nloops.ntasks.data.Task;

public interface TaskDetailContract {

    interface View extends BaseView<Presenter> {

        void displayTaskData(Task task);

        void showTasksListUpdated();

        void showTasksListDelete();

        void showTasksListAdded();

        void showDateTimePicker();

    }


    interface Presenter {

        void loadTaskData();

        void updateTask(Task task, Uri uri);

        void deleteTask(@NonNull Uri taskUri);

        void saveTask(@NonNull Task task);

        void launchDatePicker();


    }
}
