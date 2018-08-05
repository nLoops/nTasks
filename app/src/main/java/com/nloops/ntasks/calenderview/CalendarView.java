package com.nloops.ntasks.calenderview;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.nloops.ntasks.R;
import com.nloops.ntasks.adapters.CalendarViewAdapter;
import com.nloops.ntasks.addedittasks.AddEditTasks;
import com.nloops.ntasks.data.Task;
import com.nloops.ntasks.data.TaskLoader;
import com.nloops.ntasks.data.TasksDBContract;
import com.nloops.ntasks.utils.GeneralUtils;
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
  /*Ref of Cursor to load the data*/
  private Cursor mCursor;
  /*FORMATTER to display Selected Date on ToolBar Title*/
  private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
  /*get Ref of TaskLoader to get data*/
  private TaskLoader mTaskLoader;
  /*get Ref of CalendarAdapter*/
  private CalendarViewAdapter mAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_calendar_view);
    ButterKnife.bind(this);
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
    /*Init Adapter and ListView*/
    mAdapter = new CalendarViewAdapter(this, new ArrayList<Task>());
    mListView.setAdapter(mAdapter);
    mListView.setEmptyView(mEmptyView);

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
  }

  @Override
  public void onDateSelected(@NonNull MaterialCalendarView materialCalendarView,
      @NonNull CalendarDay calendarDay, boolean b) {
    mTextViewToday.setText(b ? FORMATTER.format(calendarDay.getDate()) : "No Selection");
    GeneralUtils.slideInFromTop(mTextViewToday, CalendarView.this);
    getSupportLoaderManager().restartLoader(0, null, this);
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    NavUtils.navigateUpFromSameTask(this);
    // set Navigation Animation
    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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


  /**
   * @return Cursor of {@link Task}
   */
  private Cursor getData() {
    String[] projections = new String[]{TasksDBContract.TaskEntry.COLUMN_NAME_DATE};
    String selection = TasksDBContract.TaskEntry.COLUMN_NAME_COMPLETE + "=?";
    String[] selectionArgs = new String[]{"0"};
    Cursor cursor;
    cursor = getContentResolver().query(TasksDBContract.TaskEntry.CONTENT_TASK_URI,
        projections, selection, selectionArgs, null);
    return cursor;
  }

}
