package com.nloops.ntasks.taskslist;

import android.database.Cursor;
import android.net.Uri;
import com.nloops.ntasks.data.BaseView;
import com.nloops.ntasks.data.Task;

public interface TasksListContract {

  interface View extends BaseView<Presenter> {

    void setLoadingIndicator(boolean state);

    void showTasks(Cursor tasks);

    void showNoData();

    void showDataReset();

    void showAddEditUI(long taskID, int taskType);

    void showDeletedMessage();

    void showUpdatedMessage();

    void showAddedMessage();

    void showSettingsActivity();

    void showDenyPermissionsMessage();
  }


  interface Presenter {

    void result(int requestCode, int resultCode);

    void loadTasks();

    void showEmptyView();

    void loadAddEditActivity(long taskID, int taskType);

    void updateComplete(boolean state, long rawID);

    void updateTask(Task task, Uri uri);

    void removeLoader();

    void deleteTask(Uri taskUri);
  }


}
