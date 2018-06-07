package com.nloops.ntasks.utils;

import android.content.ContentValues;

import com.nloops.ntasks.data.Task;
import com.nloops.ntasks.data.TasksDBContract;
import com.nloops.ntasks.data.Todo;

/**
 * Helper Class that Contain Methods that get data from {@link Task} and mTodo
 * to return ContentValues used into Insert and Update Database Operations.
 */
public class DatabaseValues {

    public static ContentValues from(Task task) {
        ContentValues values = new ContentValues();
        values.put(TasksDBContract.TaskEntry.COLUMN_NAME_TITLE, task.getTitle());
        values.put(TasksDBContract.TaskEntry.COLUMN_NAME_BODY, task.getBody());
        values.put(TasksDBContract.TaskEntry.COLUMN_NAME_PATH, task.getPath());
        values.put(TasksDBContract.TaskEntry.COLUMN_NAME_TYPE, task.getType());
        values.put(TasksDBContract.TaskEntry.COLUMN_NAME_PRIORTY, task.getPriorty());
        values.put(TasksDBContract.TaskEntry.COLUMN_NAME_DATE, task.getDate());
        values.put(TasksDBContract.TaskEntry.COLUMN_NAME_COMPLETE, task.getCompleted());

        return values;
    }


    public static ContentValues from(Todo todo) {
        ContentValues values = new ContentValues();
        values.put(TasksDBContract.TodoEntry.COLUMN_NAME_TODO, todo.getTodo());
        values.put(TasksDBContract.TodoEntry.COLUMN_NAME_COMPLETE, todo.getIsCompleted());
        values.put(TasksDBContract.TodoEntry.COLUMN_NAME_TASK_ID, todo.getTaskID());

        return values;
    }
}
