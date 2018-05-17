package com.nloops.ntasks.addedittasks;

import android.net.Uri;

import com.nloops.ntasks.data.BaseView;
import com.nloops.ntasks.data.Task;

public interface TaskDetailContract {

    interface View extends BaseView<Presenter> {

        void displayTaskData(Task task);

        void setUpdateTaskMessage();

    }


    interface Presenter {

        void loadTaskData();

        void updateTask(Task task, Uri uri);


    }
}
