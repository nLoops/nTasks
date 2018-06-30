package com.nloops.ntasks.data;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import static com.nloops.ntasks.data.TasksDBContract.TaskEntry;
import static com.nloops.ntasks.data.TasksDBContract.getColumnInt;
import static com.nloops.ntasks.data.TasksDBContract.getColumnLong;
import static com.nloops.ntasks.data.TasksDBContract.getColumnString;

/**
 * Model that represent single task structure.
 */

public class Task implements Parcelable {

    /*Constants for missing data*/
    public static final long NO_ID = -1;
    public static final long NO_DATE = Long.MAX_VALUE;

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
    }

    public Task(String title, String body, int type, int priorty,
                long date, int completed, String path, List<Todo> todo) {
        this.mID = NO_ID;
        this.mTitle = title;
        this.mBody = body;
        this.mType = type;
        this.mPriority = priorty;
        this.mDate = date;
        this.mCompleted = completed;
        this.mPath = path;
        this.mTodos = todo;
    }

    /**
     * Create A Task from Cursor.
     *
     * @param cursor
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

    public int getPriorty() {
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

    public boolean isComplete() {
        return mCompleted == TaskEntry.STATE_COMPLETED;
    }

    public boolean isPriority() {
        return mPriority == TaskEntry.PRIORTY_HIGH;
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

    public void setDate(long mDate) {
        this.mDate = mDate;
    }

    public void setCompleted(int mCompleted) {
        this.mCompleted = mCompleted;
    }

    public void setPath(String mPath) {
        this.mPath = mPath;
    }

    public void setTodos(List<Todo> mTodos) {
        this.mTodos = mTodos;
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

    protected Task(Parcel in) {
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
}
