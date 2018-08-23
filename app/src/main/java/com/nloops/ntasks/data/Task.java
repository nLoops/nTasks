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


  private long mID;
  private String mTitle;
  private String mBody;
  private int mType;
  private int mPriority;
  private long mDate;
  private int mCompleted;
  private int mRepeat;
  private String mPath;
  public static final Creator<Task> CREATOR = new Creator<Task>() {
    @Override
    public Task createFromParcel(Parcel source) {
      return new Task(source);
    }

    @Override
    public Task[] newArray(int size) {
      return new Task[size];
    }
  };
  private String mCurrentUser;
  private List<Todo> mTodos;

  public Task() {
    /*for Firebase Database*/
  }

  private String mSharedWith;

  public Task(String title, String body, int type, int priority,
      long date, int completed, int repeated, String path,
      String user, List<Todo> todo, String sharedWith) {
    this.mID = NO_ID;
    this.mTitle = title;
    this.mBody = body;
    this.mType = type;
    this.mPriority = priority;
    this.mDate = date;
    this.mCompleted = completed;
    this.mRepeat = repeated;
    this.mPath = path;
    this.mTodos = todo;
    this.mCurrentUser = user;
    this.mSharedWith = sharedWith;
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

  public int getRepeated() {
    return mRepeat;
  }

  public String getPath() {
    return mPath;
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
    this.mRepeat = getColumnInt(cursor, TaskEntry.COLUMN_NAME_REPEAT);
    this.mDate = getColumnLong(cursor, TaskEntry.COLUMN_NAME_DATE);
    this.mPath = getColumnString(cursor, TaskEntry.COLUMN_NAME_PATH);
    this.mCurrentUser = getColumnString(cursor, TaskEntry.COLUMN_NAME_USER);
    this.mSharedWith = getColumnString(cursor, TaskEntry.COLUMN_NAME_SHARED_WITH);
    this.mPriority = getColumnInt(cursor, TaskEntry.COLUMN_NAME_PRIORTY);
    this.mType = getColumnInt(cursor, TaskEntry.COLUMN_NAME_TYPE);
  }

  protected Task(Parcel in) {
    this.mID = in.readLong();
    this.mTitle = in.readString();
    this.mBody = in.readString();
    this.mType = in.readInt();
    this.mPriority = in.readInt();
    this.mDate = in.readLong();
    this.mCompleted = in.readInt();
    this.mRepeat = in.readInt();
    this.mPath = in.readString();
    this.mCurrentUser = in.readString();
    this.mSharedWith = in.readString();
    this.mTodos = in.createTypedArrayList(Todo.CREATOR);
    this.NO_ID = in.readLong();
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

  public boolean getIsRepeated() {
    return mRepeat != TaskEntry.REPEAT_NONE;
  }

  public boolean getIsPriority() {
    return mPriority == TaskEntry.PRIORTY_HIGH;
  }

  /*Constants for missing data*/
  private long NO_ID = -1;

  public void setDate(long date) {
    mDate = date;
  }

  public void setTitle(String mTitle) {
    this.mTitle = mTitle;
  }

  public void setBody(String mBody) {
    this.mBody = mBody;
  }

  public void setID(long mID) {
    this.mID = mID;
  }

  public void setType(int mType) {
    this.mType = mType;
  }

  public void setPriorty(int mPriorty) {
    this.mPriority = mPriorty;
  }

  public void setCompleted(int mCompleted) {
    this.mCompleted = mCompleted;
  }

  public String getCurrentUser() {
    return mCurrentUser;
  }

  public void setCurrentUser(String mCurrentUser) {
    this.mCurrentUser = mCurrentUser;
  }

  public String getSharedWith() {
    return mSharedWith;
  }

  public void setSharedWith(String mSharedWith) {
    this.mSharedWith = mSharedWith;
  }


  @Override
  public int describeContents() {
    return 0;
  }

  public void setPath(String mPath) {
    this.mPath = mPath;
  }

  public void setRepeated(int mRepeat) {
    this.mRepeat = mRepeat;
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
    dest.writeInt(this.mRepeat);
    dest.writeString(this.mPath);
    dest.writeString(this.mCurrentUser);
    dest.writeString(this.mSharedWith);
    dest.writeTypedList(this.mTodos);
    dest.writeLong(this.NO_ID);
  }
}
