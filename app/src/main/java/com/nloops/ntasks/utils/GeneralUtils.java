package com.nloops.ntasks.utils;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SwitchCompat;
import android.text.format.DateUtils;

import com.nloops.ntasks.data.Task;
import com.nloops.ntasks.data.TasksDBContract;
import com.nloops.ntasks.data.Todo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * This class will include common helper methods which needed in the
 * different cases in the APP
 */
public class GeneralUtils {


    /**
     * The {@code fragment} is added to the container view with id {@code frameId}.
     * The operation is performed by the {@code fragmentManager}.
     */
    public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager,
                                             @NonNull Fragment fragment, int frameId) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(frameId, fragment);
        transaction.commit();
    }

    /**
     * This helper method will return the {@link com.nloops.ntasks.data.Task} Priority
     * depends on SwitchButton check state
     *
     * @param switchCompat
     * @return TaskPriority
     */
    public static int getTaskPriority(SwitchCompat switchCompat) {
        int taskPriority;
        if (switchCompat.isChecked()) {
            taskPriority = TasksDBContract.TaskEntry.PRIORTY_HIGH;
        } else {
            taskPriority = TasksDBContract.TaskEntry.PRIORTY_NORMAL;
        }
        return taskPriority;
    }


    /**
     * Get path to save recorded Audio Notes
     *
     * @return savedPath
     */
    public static String getSavedPath() {
        String storeLocation = Environment.getExternalStorageDirectory().getAbsolutePath();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        String audioFileName = "Note_" + timeStamp + ".3gp";
        File storageDir = new File(
                storeLocation, "/nTasks");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        return storeLocation + "/nTasks/" + audioFileName;
    }

    /**
     * This helper method takes stored Millis and return into String shape.
     *
     * @param time Stored Date
     * @return Formatted Date into String
     */
    public static CharSequence formatDate(Long time) {
        CharSequence formatted = DateUtils.getRelativeTimeSpanString(time,
                System.currentTimeMillis(),
                0L,
                DateUtils.FORMAT_ABBREV_RELATIVE
        );

        return formatted;
    }


    /**
     * Helper method that grab data from SQLite database
     *
     * @param context passed Context
     * @return Cursor with {@link Task } that stored into DB
     */
    public static Cursor getData(Context context) {
        return context.getContentResolver().query(TasksDBContract.TaskEntry.CONTENT_TASK_URI,
                null, null, null, null);
    }

    public static List<Todo> getTodoData(Context context, String[] selectionArgs) {
        List<Todo> todos = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(TasksDBContract.TodoEntry.CONTENT_TODO_URI,
                null, null, selectionArgs, null);
        while (cursor.moveToNext()) {
            todos.add(new Todo(cursor));
        }
        return todos;
    }

}
