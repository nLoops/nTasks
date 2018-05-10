package com.nloops.ntasks.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

/**
 * Model that represent single task structure.
 */

public class Task {

    private String mTitle;
    private String mBody;
    private int mDate;
    private int mPriorty;
    private String mPath;
    private ArrayList<Todo> mTodos;


    /**
     * Public Constructor
     *
     * @param title
     * @param body
     * @param date
     * @param priorty
     * @param path
     * @param todos
     */
    public Task(@NonNull String title, @NonNull String body, @NonNull int date,
                @NonNull int priorty, @Nullable String path, @Nullable ArrayList<Todo> todos) {
        this.mTitle = title;
        this.mBody = body;
        this.mDate = date;
        this.mPriorty = priorty;
        this.mPath = path;
        this.mTodos = todos;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getBody() {
        return mBody;
    }

    public int getDate() {
        return mDate;
    }

    public int getPriorty() {
        return mPriorty;
    }

    public String getPath() {
        return mPath;
    }

    public ArrayList<Todo> getTodos() {
        return mTodos;
    }
}
