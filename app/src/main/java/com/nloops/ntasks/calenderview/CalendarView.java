package com.nloops.ntasks.calenderview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.nloops.ntasks.R;
import com.nloops.ntasks.UI.TimePickerFragment;
import com.nloops.ntasks.adapters.CalendarViewAdapter;
import com.nloops.ntasks.addedittasks.AddEditTasks;
import com.nloops.ntasks.data.Task;
import com.nloops.ntasks.data.TaskLoader;
import com.nloops.ntasks.data.TasksDBContract;
import com.nloops.ntasks.data.TasksDBContract.TaskEntry;
import com.nloops.ntasks.reminders.TaskOperationService;
import com.nloops.ntasks.utils.GeneralUtils;
import com.nloops.ntasks.utils.SharedPreferenceHelper;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * This Class will acts as Calendar View to give user a specific tasks on week days.
 */
public class CalendarView extends AppCompatActivity implements OnDateSelectedListener,
    LoaderManager.LoaderCallbacks<Cursor> {

  /*Bind Layout Views*/
  @BindView(R.id.calendar_view)
  MaterialCalendarView mCalendarView;
  @BindView(R.id.calendar_view_list)
  ListView mListView;
  @BindView(R.id.tv_calendar_today)
  TextView mTextViewToday;
  @BindView(R.id.empty_calendar_view)
  TextView mEmptyView;
  //  adds 3 hours into milliseconds
  private final long THREE_HOURS = 10800000;
  @BindView(R.id.bottom_sheet)
  View mBottomSheetView;
  @BindView(R.id.calendar_task_ed)
  TextInputEditText mNewTaskEditText;
  @BindView(R.id.calendar_btn_date)
  ImageButton mAlarmButton;
  @BindView(R.id.calendar_btn_save)
  ImageButton mSaveTaskButton;
  @BindView(R.id.tv_calendar_tap_on)
  TextView mAddNewTV;
  @BindView(R.id.calendar_view_container)
  CoordinatorLayout mLayoutContainer;
  @BindView(R.id.tv_calendar_date_view)
  TextView mTvDateView;
  /*Ref of Cursor to load the data*/
  private Cursor mCursor;
  /*FORMATTER to display Selected Date on ToolBar Title*/
  private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
  /*get Ref of TaskLoader to get data*/
  private TaskLoader mTaskLoader;
  /*get Ref of CalendarAdapter*/
  private CalendarViewAdapter mAdapter;
  //  Ref of Bottom Behavior
  private BottomSheetBehavior mBottomSheetBehavior;
  //  ref of Current mDueDate
  private long mDueDate;
  private final TimePickerDialog.OnTimeSetListener mSetTimePicker = new TimePickerDialog.OnTimeSetListener() {
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
      Calendar c = Calendar.getInstance();
      c.set(Calendar.YEAR, mCalendarView.getSelectedDate().getYear());
      c.set(Calendar.MONTH, mCalendarView.getSelectedDate().getMonth());
      c.set(Calendar.DAY_OF_MONTH, mCalendarView.getSelectedDate().getDay());
      c.set(Calendar.HOUR_OF_DAY, hourOfDay);
      c.set(Calendar.MINUTE, minute);

      setDateSelection(c.getTimeInMillis());
    }
  };

  /**
   * This helper method will force the keyboard to hide when Activity Started
   *
   * @param activity {@link Activity} to get SystemServices.
   */
  public static void hideKeyboard(Activity activity) {
    View view = activity.findViewById(android.R.id.content);
    if (view != null) {
      InputMethodManager imm = (InputMethodManager) activity
          .getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_calendar_view);
    ButterKnife.bind(this);
    GeneralUtils.checkIfSingedIn(CalendarView.this);
    /*Calendar View Setup*/
    mCalendarView.setOnDateChangedListener(this);
    Calendar instance = Calendar.getInstance();
    mCalendarView.setSelectedDate(instance.getTime());
    /*Set Min and Max Date to Calendar View for Best Performance*/
    Calendar instance1 = Calendar.getInstance();
    /*Get The Current Month and set it as the start of Calendar to improve performance*/
    CalendarDay startDay = CalendarDay.from(instance1.getTime());
    instance1.set(instance1.get(Calendar.YEAR), startDay.getMonth(), 1);

    Calendar instance2 = Calendar.getInstance();
    instance2.set(instance2.get(Calendar.YEAR), Calendar.DECEMBER, 31);

    mCalendarView.state().edit()
        .setMinimumDate(instance1.getTime())
        .setMaximumDate(instance2.getTime())
        .commit();
    /*Set Toolbar text for selected day*/
    CalendarDay day = CalendarDay.from(instance.getTime());
    mTextViewToday.setText(FORMATTER.format(day.getDate()));
    GeneralUtils.slideInFromTop(mTextViewToday, this);
//    init the DueDate
    long initDate = System.currentTimeMillis() + THREE_HOURS;
    setDateSelection(initDate);
    /*Init Adapter and ListView*/
    mAdapter = new CalendarViewAdapter(this, new ArrayList<Task>());
    mListView.setAdapter(mAdapter);
    mListView.setEmptyView(mEmptyView);

    mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheetView);


    /*Run Task to apply DotSpan on upcoming Tasks dates*/
    new ApiSimulator().executeOnExecutor(Executors.newSingleThreadExecutor());
    mTaskLoader = new TaskLoader(this);
    getSupportLoaderManager().initLoader(0, null, this);

    mListView.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*Create Intent with Filter Task Type to open TaskDetail Page.*/
        Intent addEditIntent = new Intent(CalendarView.this, AddEditTasks.class);
        Task currentTask = mAdapter.getItem(position);
        assert currentTask != null;
        Uri taskUri = ContentUris
            .withAppendedId(TasksDBContract.TaskEntry.CONTENT_TASK_URI, currentTask.getID());
        addEditIntent.putExtra(AddEditTasks.EXTRAS_TASK_TYPE, currentTask.getType());
        addEditIntent.setData(taskUri);
        startActivityForResult(addEditIntent, AddEditTasks.REQUEST_EDIT_TASK);
      }
    });

    mAddNewTV.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
          mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
          mNewTaskEditText.setVisibility(View.VISIBLE);
        } else {
          mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
          mNewTaskEditText.setVisibility(View.INVISIBLE);
        }
      }
    });

    mAlarmButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        getTimePicker();
      }
    });

    mSaveTaskButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        if (mNewTaskEditText.getText().length() > 0) {
          mSaveTaskButton.setEnabled(false);
          mAlarmButton.setEnabled(false);
          saveNewTask(getTask());
          new ApiSimulator().executeOnExecutor(Executors.newSingleThreadExecutor());
          mNewTaskEditText.setText("");
          mSaveTaskButton.setEnabled(true);
          mAlarmButton.setEnabled(true);

          Snackbar.make(mLayoutContainer, getString(R.string.msg_task_added)
              , Snackbar.LENGTH_LONG).show();
        }

      }
    });

  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        NavUtils.navigateUpFromSameTask(this);
        // set Navigation Animation
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @NonNull
  @Override
  public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
    return mTaskLoader.createTasksLoader();
  }

  @Override
  public void onDateSelected(@NonNull MaterialCalendarView materialCalendarView,
      @NonNull CalendarDay calendarDay, boolean b) {
    mTextViewToday.setText(b ? FORMATTER.format(calendarDay.getDate()) : "No Selection");
    GeneralUtils.slideInFromTop(mTextViewToday, CalendarView.this);
    getSupportLoaderManager().restartLoader(0, null, this);
    setDateSelection(calendarDay.getCalendar().getTimeInMillis());
  }

  @Override
  public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    mAdapter.clear();
  }

  /**
   * Simulate an API call to show how to add decorators
   */
  @SuppressLint("StaticFieldLeak")
  private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

    @Override
    protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      Calendar calendar = Calendar.getInstance();
      mCursor = getData();
      ArrayList<CalendarDay> dates = new ArrayList<>();
      while (mCursor.moveToNext()) {
        long date = TasksDBContract.getColumnLong(
            mCursor, TasksDBContract.TaskEntry.COLUMN_NAME_DATE);
        calendar.setTimeInMillis(date);
        CalendarDay day = CalendarDay.from(calendar);
        dates.add(day);
      }

      return dates;
    }

    @Override
    protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
      super.onPostExecute(calendarDays);

      if (isFinishing()) {
        return;
      }

      mCalendarView.addDecorator(new TaskEventDecorator(Color.WHITE, calendarDays));
      if (mCursor != null) {
        mCursor.close();
        mCursor = null;
      }
    }
  }

  @Override
  public void onBackPressed() {
    if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
      mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    } else {
      NavUtils.navigateUpFromSameTask(this);
      // set Navigation Animation
      overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
  }

  @Override
  public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
    ArrayList<Task> tasks = new ArrayList<>();
    Calendar calendar = Calendar.getInstance();
    if (data != null && data.getCount() > 0) {
      mAdapter.clear();
      while (data.moveToNext()) {
        Task currentTask = new Task(data);
        long taskDate = currentTask.getDate();
        calendar.setTimeInMillis(taskDate);
        CalendarDay day = CalendarDay.from(calendar);
        if (day.getDate().equals(mCalendarView.getSelectedDate().getDate())) {
          tasks.add(currentTask);
        }
      }
      mAdapter.addAll(tasks);
    }

    // Set it as collapsed initially
    int BOTTOM_SHEET_HEIGHT = 120;
    mBottomSheetBehavior.setPeekHeight(BOTTOM_SHEET_HEIGHT);
    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

  }

  /**
   * @return Cursor of {@link Task}
   */
  private Cursor getData() {
    String[] projections = new String[]{TasksDBContract.TaskEntry.COLUMN_NAME_DATE};
    String currentUserSelection = TaskEntry.COLUMN_NAME_USER + "=?";
    String selection = TasksDBContract.TaskEntry.COLUMN_NAME_COMPLETE + "=? and " +
        currentUserSelection;
    String[] selectionArgs = new String[]{"0", SharedPreferenceHelper
        .getInstance(CalendarView.this).getUID()};
    Cursor cursor;
    cursor = getContentResolver().query(TasksDBContract.TaskEntry.CONTENT_TASK_URI,
        projections, selection, selectionArgs, null);
    return cursor;
  }

  /**
   * This method will launch {@link TimePickerFragment} to let user choose his task time.
   */
  private void getTimePicker() {
    TimePickerFragment pickerFragment = new TimePickerFragment();
    pickerFragment.setOnTimeSetListener(mSetTimePicker);
    pickerFragment.show(getSupportFragmentManager(), "TimeFragmentCal");

  }

  private void setDateSelection(long selectedTimestamp) {
    mDueDate = selectedTimestamp;
    mTvDateView.setText(GeneralUtils.formatDate(mDueDate));

  }

  /**
   * This Helper method will return {@link Task} with current Data
   */
  private Task getTask() {

    String userUID = SharedPreferenceHelper.getInstance(getApplicationContext())
        .getUID();
    String currentUser = userUID.length() > 0 ? userUID : "UnKnown";
    return new Task(mNewTaskEditText.getText().toString(),
        "",
        TaskEntry.TYPE_NORMAL_NOTE,
        TaskEntry.PRIORTY_NORMAL,
        mDueDate,
        TasksDBContract.TaskEntry.STATE_NOT_COMPLETED,
        TaskEntry.REPEAT_NONE
        , "",
        currentUser,
        null, "");
  }

  /**
   * This Helper method will launch background service to add new Task
   *
   * @param task {@link Task}
   */
  private void saveNewTask(Task task) {
    Intent saveIntent = new Intent(CalendarView.this, TaskOperationService.class);
    saveIntent.setAction(TaskOperationService.ACTION_SAVE_NEW_TASK);
    saveIntent.putExtra(TaskOperationService.EXTRAS_SAVE_NEW_TASK_DATA, task);
    startService(saveIntent);
  }

}
