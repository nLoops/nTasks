package com.nloops.ntasks.data;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.nloops.ntasks.data.TasksDBContract.*;

import java.util.List;

/**
 * Model that represent single task structure.
 */

public class Task {

    /*Constants for missing data*/
    public static final long NO_ID = -1;
    public static final long NO_DATE = Long.MAX_VALUE;

    private long mID;
    @NonNull
    private String mTitle;
    @NonNull
    private String mBody;

    private int mType;

    private int mPriority;

    private long mDate;

    private int mCompleted;

    @Nullable
    private String mPath;
    @Nullable
    private List<Todo> mTodos;

    public Task(@NonNull String title, @NonNull String body, int type, int priorty,
                long date, int completed, String path, @Nullable List<Todo> todo) {
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

    @NonNull
    public String getTitle() {
        return mTitle;
    }

    @NonNull
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

    @Nullable
    public String getPath() {
        return mPath;
    }

    @Nullable
    public List<Todo> getTodos() {
        return mTodos;
    }

    public boolean isComplete() {
        return mCompleted == TaskEntry.STATE_COMPLETED;
    }

    public boolean isPriority() {
        return mPriority == TaskEntry.PRIORTY_HIGH;
    }

    public void setTitle(@NonNull String mTitle) {
        this.mTitle = mTitle;
    }

    public void setBody(@NonNull String mBody) {
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

    public void setPath(@Nullable String mPath) {
        this.mPath = mPath;
    }

    public void setTodos(@Nullable List<Todo> mTodos) {
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
}
