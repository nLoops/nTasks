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
    @NonNull
    private int mType;
    @NonNull
    private int mPriorty;
    @NonNull
    private long mDate;
    @Nullable
    private int mCompleted;
    @Nullable
    private String mPath;
    @Nullable
    private List<Todo> mTodos;

    public Task(@NonNull String title, @NonNull String body, @NonNull int type, @NonNull int priorty,
                @NonNull long date, int completed, String path, List<Todo> todo) {
        this.mID = NO_ID;
        this.mTitle = title;
        this.mBody = body;
        this.mType = type;
        this.mPriorty = priorty;
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
        this.mPriorty = getColumnInt(cursor, TaskEntry.COLUMN_NAME_PRIORTY);
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

    @NonNull
    public int getType() {
        return mType;
    }

    @NonNull
    public int getPriorty() {
        return mPriorty;
    }

    @NonNull
    public long getDate() {
        return mDate;
    }

    @Nullable
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
        return mCompleted == 1;
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

    public void setType(@NonNull int mType) {
        this.mType = mType;
    }

    public void setPriorty(@NonNull int mPriorty) {
        this.mPriorty = mPriorty;
    }

    public void setDate(@NonNull long mDate) {
        this.mDate = mDate;
    }

    public void setCompleted(@Nullable int mCompleted) {
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
                ", mPriorty=" + mPriorty +
                ", mDate=" + mDate +
                ", mCompleted=" + mCompleted +
                ", mPath='" + mPath + '\'' +
                ", mTodos=" + mTodos +
                '}';
    }
}
