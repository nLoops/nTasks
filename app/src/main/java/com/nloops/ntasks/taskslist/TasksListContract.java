package com.nloops.ntasks.taskslist;

import android.database.Cursor;

import com.nloops.ntasks.data.BaseView;

public interface TasksListContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndecator(boolean state);

        void showTasks(Cursor tasks);

        void showNoData();

        void showDataReset();
    }


    interface Presenter {

        void loadTasks();
    }


}
