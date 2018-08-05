package com.nloops.ntasks.data;

import static com.nloops.ntasks.data.TasksDBContract.TaskEntry;
import static com.nloops.ntasks.data.TasksDBContract.getColumnInt;
import static com.nloops.ntasks.data.TasksDBContract.getColumnLong;
import static com.nloops.ntasks.data.TasksDBContract.getColumnString;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

/**
 * Model that represent single task structure.
 */

public class Task implements Parcelable {

  public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {
    @Override
    public Task createFromParcel(Parcel source) {
      return new Task(source);
    }

    @Override
    public Task[] newArray(int size) {
      return new Task[size];
    }
  };
  /*Constants for missing data*/
  private static final long NO_ID = -1;
  private long mID;
  private String mTitle;
  private String mBody;
  private int mType;
  private int mPriority;
  private long mDate;
  private int mCompleted;
  private String mPath;
  private List<Todo> mTodos;

  public Task() {
    /*for Firebase Database*/
  }

  public Task(String title, String body, int type, int priority,
      long date, int completed, String path, List<Todo> todo) {
    this.mID = NO_ID;
    this.mTitle = title;
    this.mBody = body;
    this.mType = type;
    this.mPriority = priority;
    this.mDate = date;
    this.mCompleted = completed;
    this.mPath = path;
    this.mTodos = todo;
  }

  /**
   * Create A Task from Cursor.
   *
   * @param cursor {@link Cursor}
   */
  public Task(Cursor cursor) {
    this.mID = getColumnLong(cursor, TaskEntry._ID);
    this.mTitle = getColumnString(cursor, TaskEntry.COLUMN_NAME_TITLE);
    this.mBody = getColumnString(cursor, TaskEntry.COLUMN_NAME_BODY);
    this.mCompleted = getColumnInt(cursor, TaskEntry.COLUMN_NAME_COMPLETE);
    this.mDate = getColumnLong(cursor, TaskEntry.COLUMN_NAME_DATE);
    this.mPath = getColumnString(cursor, TaskEntry.COLUMN_NAME_PATH);
    this.mPriority = getColumnInt(cursor, TaskEntry.COLUMN_NAME_PRIORTY);
    this.mType = getColumnInt(cursor, TaskEntry.COLUMN_NAME_TYPE);
  }


  private Task(Parcel in) {
    this.mID = in.readLong();
    this.mTitle = in.readString();
    this.mBody = in.readString();
    this.mType = in.readInt();
    this.mPriority = in.readInt();
    this.mDate = in.readLong();
    this.mCompleted = in.readInt();
    this.mPath = in.readString();
    this.mTodos = in.createTypedArrayList(Todo.CREATOR);
  }

  public long getID() {
    return mID;
  }

  public String getTitle() {
    return mTitle;
  }

  public String getBody() {
    return mBody;
  }

  public int getType() {
    return mType;
  }

  public int getPriority() {
    return mPriority;
  }

  public long getDate() {
    return mDate;
  }

  public int getCompleted() {
    return mCompleted;
  }

  public String getPath() {
    return mPath;
  }

  public List<Todo> getTodos() {
    return mTodos;
  }

  public void setTodos(List<Todo> mTodos) {
    this.mTodos = mTodos;
  }

  public boolean isComplete() {
    return mCompleted == TaskEntry.STATE_COMPLETED;
  }

  public boolean getIsPriority() {
    return mPriority == TaskEntry.PRIORTY_HIGH;
  }

  @Override
  public String toString() {
    return "Task{" +
        "mID=" + mID +
        ", mTitle='" + mTitle + '\'' +
        ", mBody='" + mBody + '\'' +
        ", mType=" + mType +
        ", mPriority=" + mPriority +
        ", mDate=" + mDate +
        ", mCompleted=" + mCompleted +
        ", mPath='" + mPath + '\'' +
        ", mTodos=" + mTodos +
        '}';
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(this.mID);
    dest.writeString(this.mTitle);
    dest.writeString(this.mBody);
    dest.writeInt(this.mType);
    dest.writeInt(this.mPriority);
    dest.writeLong(this.mDate);
    dest.writeInt(this.mCompleted);
    dest.writeString(this.mPath);
    dest.writeTypedList(this.mTodos);
  }
}
