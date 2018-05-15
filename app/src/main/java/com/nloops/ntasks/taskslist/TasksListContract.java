package com.nloops.ntasks.taskslist;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.nloops.ntasks.data.BaseView;

public interface TasksListContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndecator(boolean state);

        void showTasks(Cursor tasks);

        void showNoData();

        void showDataReset();

        void showAddEditUI(long taskID);
    }


    interface Presenter {

        void loadTasks();

        void showEmptyView();

        void loadAddEditActivity(long taskID);

        void updateComplete(@NonNull boolean state, @NonNull long rawID);
    }


}
