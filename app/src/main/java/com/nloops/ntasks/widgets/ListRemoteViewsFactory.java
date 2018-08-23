package com.nloops.ntasks.widgets;

import android.content.Context;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import com.nloops.ntasks.R;
import com.nloops.ntasks.data.TasksDBContract;
import com.nloops.ntasks.data.TasksDBContract.TaskEntry;
import com.nloops.ntasks.utils.Constants;
import com.nloops.ntasks.utils.GeneralUtils;


public class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

  private final Context mContext;
  private Cursor mCursor;

  public ListRemoteViewsFactory(Context context) {
    this.mContext = context;

  }

  @Override
  public void onCreate() {

  }

  @Override
  public void onDataSetChanged() {
    mCursor = getData();
  }

  @Override
  public void onDestroy() {
    mCursor = null;
  }

  @Override
  public int getCount() {
    if (null == mCursor) {
      return 0;
    }
    return mCursor.getCount();
  }

  @Override
  public RemoteViews getViewAt(int position) {
    RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(),
        R.layout.tasks_list_widget_item);
    if (mCursor.moveToPosition(position)) {
      String title = TasksDBContract.getColumnString
          (mCursor, TasksDBContract.TaskEntry.COLUMN_NAME_TITLE);
      long date = TasksDBContract.getColumnLong(
          mCursor, TasksDBContract.TaskEntry.COLUMN_NAME_DATE);
      remoteViews.setTextViewText(R.id.widget_list_tasktitle,
          title);
      if (date != Long.MAX_VALUE) {
        remoteViews.setTextViewText(R.id.widget_list_taskdate, GeneralUtils.formatDate(date));
      }

      int taskImage = R.drawable.ic_note;
      int taskType = TasksDBContract.getColumnInt(
          mCursor, TaskEntry.COLUMN_NAME_TYPE);
      if (taskType == TaskEntry.TYPE_TODO_NOTE) {
        taskImage = R.drawable.ic_list;
      } else if (taskType == TaskEntry.TYPE_AUDIO_NOTE) {
        taskImage = R.drawable.ic_mic;
      }
      remoteViews.setImageViewResource(R.id.widget_list_taskImage, taskImage);
    }

    return remoteViews;
  }

  @Override
  public RemoteViews getLoadingView() {
    return null;
  }

  @Override
  public int getViewTypeCount() {
    return 1;
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public boolean hasStableIds() {
    return true;
  }

  private Cursor getData() {
    //    String of current user saved
    String currentUserSelection = TaskEntry.COLUMN_NAME_USER + "=?";
    String selection = TasksDBContract.TaskEntry.COLUMN_NAME_COMPLETE
        + "=? and " + currentUserSelection;
    String[] selectionArgs = new String[]{"0", Constants.UID};
    if (mCursor != null) {
      mCursor.close();
      mCursor = null;
    }
    Cursor cursor;
    cursor = mContext.getContentResolver().query(TaskEntry.CONTENT_TASK_URI,
        null, selection, selectionArgs, GeneralUtils.getDataSort(mContext));
    return cursor;
  }

}
