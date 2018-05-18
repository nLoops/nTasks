package com.nloops.ntasks.taskslist;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nloops.ntasks.data.BaseView;

public interface TasksListContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndecator(boolean state);

        void showTasks(Cursor tasks);

        void showNoData();

        void showDataReset();

        void showAddEditUI(long taskID);

        void showDeletedMessage();

        void showUpdatedMessage();

        void showAddedMessage();
    }


    interface Presenter {

        void result(int requestCode, int resultCode);

        void loadTasks();

        void showEmptyView();

        void loadAddEditActivity(long taskID);

        void updateComplete(boolean state, long rawID);
    }


}
