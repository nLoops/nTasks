package com.nloops.ntasks.data;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import static com.nloops.ntasks.data.TasksDBContract.TodoEntry;
import static com.nloops.ntasks.data.TasksDBContract.getColumnInt;
import static com.nloops.ntasks.data.TasksDBContract.getColumnLong;
import static com.nloops.ntasks.data.TasksDBContract.getColumnString;

/**
 * Model that represent task relative TODOs
 */
public class Todo implements Parcelable {

    private long mID;
    private String mTodo;
    private int mIsCompleted;
    private int mTaskID;

    /**
     * Empty Constructor for Firebase Server service.
     */
    public Todo() {
    }

    /**
     * public Constructor.
     *
     * @param id              todo_ID
     * @param todoDescription the description of singleTodo
     * @param isCompleted     todoState
     */
    public Todo(int id, String todoDescription, int isCompleted) {
        this.mID = id;
        this.mTodo = todoDescription;
        this.mIsCompleted = isCompleted;

    }

    public Todo(Cursor cursor) {
        this.mID = getColumnLong(cursor, TodoEntry._ID);
        this.mTodo = getColumnString(cursor, TodoEntry.COLUMN_NAME_TODO);
        this.mTaskID = getColumnInt(cursor, TodoEntry.COLUMN_NAME_TASK_ID);
        this.mIsCompleted = getColumnInt(cursor, TodoEntry.COLUMN_NAME_COMPLETE);

    }

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

    public void setTodo(String mTodo) {
        this.mTodo = mTodo;
    }

    public int getIsCompleted() {
        return mIsCompleted;
    }

    public boolean isComplete() {
        return mIsCompleted == TodoEntry.STATE_COMPLETED;
    }

    public void setIsCompleted(int mIsCompleted) {
        this.mIsCompleted = mIsCompleted;
    }

    public int getTaskID() {
        return mTaskID;
    }

    public void setTaskID(int mTaskID) {
        this.mTaskID = mTaskID;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mID);
        dest.writeString(this.mTodo);
        dest.writeInt(this.mIsCompleted);
        dest.writeInt(this.mTaskID);
    }

    protected Todo(Parcel in) {
        this.mID = in.readLong();
        this.mTodo = in.readString();
        this.mIsCompleted = in.readInt();
        this.mTaskID = in.readInt();
    }

    public static final Parcelable.Creator<Todo> CREATOR = new Parcelable.Creator<Todo>() {
        @Override
        public Todo createFromParcel(Parcel source) {
            return new Todo(source);
        }

        @Override
        public Todo[] newArray(int size) {
            return new Todo[size];
        }
    };
}
