package com.nloops.ntasks.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
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
}
