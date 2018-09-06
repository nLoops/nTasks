package com.nloops.ntasks.data;

import static com.nloops.ntasks.data.TasksDBContract.TodoEntry;
import static com.nloops.ntasks.data.TasksDBContract.getColumnInt;
import static com.nloops.ntasks.data.TasksDBContract.getColumnLong;
import static com.nloops.ntasks.data.TasksDBContract.getColumnString;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Model that represent task relative listTODOs
 */
public class Todo implements Parcelable {

    private long mID;
    private String mTodo;
    private int mIsCompleted;
    private int mTaskID;
    public static final Creator<Todo> CREATOR = new Creator<Todo>() {
        @Override
        public Todo createFromParcel(Parcel source) {
            return new Todo(source);
        }

        @Override
        public Todo[] newArray(int size) {
            return new Todo[size];
        }
    };

    /**
     * Empty Constructor for Firebase Server service.
     */
    public Todo() {
    }

    private long mDueDate = Long.MAX_VALUE;

    /**
     * Public Constructor.
     *
     * @param todoString the body of single todoString.
     */
    public Todo(String todoString, int isCompleted) {
        this.mTodo = todoString;
        this.mIsCompleted = isCompleted;
    }

    public long getID() {
        return mID;
    }

    public String getTodo() {
        return mTodo;
    }

    public int getIsCompleted() {
        return mIsCompleted;
    }

    public boolean isComplete() {
        return mIsCompleted == TodoEntry.STATE_COMPLETED;
    }

    public int getTaskID() {
        return mTaskID;
    }

    public void setTaskID(int mTaskID) {
        this.mTaskID = mTaskID;
    }

    public void setIsCompleted(int state) {
        this.mIsCompleted = state;
    }

    public Todo(Cursor cursor) {
        this.mID = getColumnLong(cursor, TodoEntry._ID);
        this.mTodo = getColumnString(cursor, TodoEntry.COLUMN_NAME_TODO);
        this.mTaskID = getColumnInt(cursor, TodoEntry.COLUMN_NAME_TASK_ID);
        this.mIsCompleted = getColumnInt(cursor, TodoEntry.COLUMN_NAME_COMPLETE);
        this.mDueDate = getColumnLong(cursor, TodoEntry.COLUMN_NAME_LIST_DATE);

    }

    protected Todo(Parcel in) {
        this.mID = in.readLong();
        this.mTodo = in.readString();
        this.mIsCompleted = in.readInt();
        this.mTaskID = in.readInt();
        this.mDueDate = in.readLong();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public long getDueDate() {
        return mDueDate;
    }

    public void setDueDate(long mDueDate) {
        this.mDueDate = mDueDate;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mID);
        dest.writeString(this.mTodo);
        dest.writeInt(this.mIsCompleted);
        dest.writeInt(this.mTaskID);
        dest.writeLong(this.mDueDate);
    }
}
