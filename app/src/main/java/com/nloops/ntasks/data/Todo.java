package com.nloops.ntasks.data;

/**
 * Model that represent task relative TODOs
 */
public class Todo {

    private int mID;
    private String mTodo;

    /**
     * public Constructor.
     *
     * @param id
     * @param
     */
    public Todo(int id, String todo) {
        this.mID = id;
        this.mTodo = todo;
    }

    /**
     * Public Constructor.
     *
     * @param todo
     */
    public Todo(String todo) {
        this.mTodo = todo;
    }

    public int getID() {
        return mID;
    }

    public void setID(int mID) {
        this.mID = mID;
    }

    public String getTodo() {
        return mTodo;
    }

    public void setTodo(String mTodo) {
        this.mTodo = mTodo;
    }


}
