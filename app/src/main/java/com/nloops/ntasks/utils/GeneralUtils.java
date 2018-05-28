package com.nloops.ntasks.utils;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SwitchCompat;

import com.nloops.ntasks.data.TasksDBContract;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        String savedPath = null;
        String storeLocation = Environment.getExternalStorageDirectory().getAbsolutePath();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        String audioFileName = "Note_" + timeStamp + ".3gp";
        File storageDir = new File(
                storeLocation, "/nTasks");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        return savedPath = storeLocation + "/nTasks/" + audioFileName;
    }

}
