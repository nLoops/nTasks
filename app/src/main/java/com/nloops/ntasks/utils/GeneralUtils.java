package com.nloops.ntasks.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SwitchCompat;
import android.text.format.DateUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.nloops.ntasks.R;
import com.nloops.ntasks.data.Task;
import com.nloops.ntasks.data.TasksDBContract;
import com.nloops.ntasks.data.TasksDBContract.TaskEntry;
import com.nloops.ntasks.data.Todo;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * This class will include common helper methods which needed in the different cases in the APP
 */
public class GeneralUtils {

  /**
   * Private Constructor
   */
  private GeneralUtils() {
  }

  /**
   * The {@code fragment} is added to the container view with id {@code frameId}. The operation is
   * performed by the {@code fragmentManager}.
   */
  public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager,
      @NonNull Fragment fragment, int frameId) {
    FragmentTransaction transaction = fragmentManager.beginTransaction();
    transaction.add(frameId, fragment);
    transaction.commit();
  }

  /**
   * This helper method will return the {@link com.nloops.ntasks.data.Task} Priority depends on
   * SwitchButton check state
   *
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

    return DateUtils.getRelativeTimeSpanString(time,
        System.currentTimeMillis(),
        0L,
        DateUtils.FORMAT_ABBREV_RELATIVE
    );
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
    assert cursor != null;
    while (cursor.moveToNext()) {
      todos.add(new Todo(cursor));
    }
    cursor.close();
    return todos;
  }

  /**
   * Helper Method to add a beautiful {@link Animation} to View
   *
   * @param view passed {@link View}
   * @param context passed {@link Context}
   */
  public static void slideInFromTop(View view, Context context) {
    Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_from_top);
    view.setAnimation(animation);
    view.animate();
    animation.start();
  }

  /**
   * Helper Method to return the current User Data Sort Preference.
   *
   * @param context passed {@link Context}
   */
  public static String getDataSort(Context context) {
    // get SharedPreference to arrange Tasks sortBy
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    // get the Current Value of preference
    String sortByValue = sharedPreferences
        .getString(context.getString(R.string.settings_sortby_key),
            context.getString(R.string.settings_sortby_default));
    // By default we have Date first
    String sortBy = TasksDBContract.DATE_SORT;
    // if we have Priority first we change the sort of Tasks.
    if (sortByValue.equals(context.getString(R.string.settings_priority_value))) {
      sortBy = TasksDBContract.DEFAULT_SORT;
    }
    return sortBy;
  }


  /**
   * Helper method to getMonth Name
   *
   * @param month Calendar Month Int
   * @return Month Name.
   */
  public static String getMonthName(int month) {
    String strMonth;
    switch (month) {
      case 1:
        strMonth = Constants.JANUARY;
        break;
      case 2:
        strMonth = Constants.FEBRUARY;
        break;
      case 3:
        strMonth = Constants.MARCH;
        break;
      case 4:
        strMonth = Constants.APRIL;
        break;
      case 5:
        strMonth = Constants.MAY;
        break;
      case 6:
        strMonth = Constants.JUNE;
        break;
      case 7:
        strMonth = Constants.JULY;
        break;
      case 8:
        strMonth = Constants.AUGUST;
        break;
      case 9:
        strMonth = Constants.SEPTEMBER;
        break;
      case 10:
        strMonth = Constants.OCTOBER;
        break;
      case 11:
        strMonth = Constants.NOVEMBER;
        break;
      case 12:
        strMonth = Constants.DECEMBER;
        break;
      default:
        strMonth = Constants.NOT_AVAILABLE;
    }
    return strMonth;
  }

  /**
   * This Method takes the index of repeat and return the value.
   *
   * @param value repeatValue
   * @return value of index
   */
  public static long getRepeatedValue(int value) {
    long repeatValue = 0;
    if (value == TaskEntry.REPEAT_DAILY) {
      repeatValue = Constants.DAY_IN_MILLIS;
    } else if (value == TaskEntry.REPEAT_WEEKLY) {
      repeatValue = Constants.WEEK_IN_MILLIS;
    } else if (value == TaskEntry.REPEAT_MONTHLY) {
      repeatValue = Constants.MONTH_IN_MILLIS;
    } else if (value == TaskEntry.REPEAT_YEARLY) {
      repeatValue = Constants.YEAR_IN_MILLIS;
    }
    return repeatValue;
  }

}
