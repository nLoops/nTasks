package com.nloops.ntasks.data;

import android.database.Cursor;

import static com.nloops.ntasks.data.TasksDBContract.*;

/**
 * Model that represent task relative TODOs
 */
public class Todo {

    private long mID;
    private String mTodo;
    private int mIsCompleted;
    private int mTaskID;

    /**
     * public Constructor.
     *
     * @param id
     * @param
     */
    public Todo(int id, String todo, int isCompleted) {
        this.mID = id;
        this.mTodo = todo;
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
}
