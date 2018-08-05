package com.nloops.ntasks.reports;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.nloops.ntasks.R;
import com.nloops.ntasks.data.TaskLoader;
import com.nloops.ntasks.data.TasksDBContract;
import com.nloops.ntasks.data.TasksDBContract.TaskEntry;
import com.nloops.ntasks.utils.Constants;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * This Fragment will holds BarChart report of Active days of last week to keep user tracking his
 * measures
 */
public class ActiveDaysReport extends Fragment implements
    LoaderManager.LoaderCallbacks<Cursor> {

  /*get Ref of BarChart*/
  @BindView(R.id.active_bar_chart)
  BarChart mChart;
  private ArrayList<DayModel> days;
  /*get Ref of TaskLoader to get data*/
  private TaskLoader mTaskLoader;

  public ActiveDaysReport() {
    /*Empty Constructor Required by system*/
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.report_bar_chart, container, false);
    ButterKnife.bind(this, rootView);
    assert getContext() != null;
    mTaskLoader = new TaskLoader(getContext());

    /*Array list of object DayModel to calculate the total of tasks to show active days*/
    days = new ArrayList<>();
    days.add(new DayModel(Constants.SUNDAY, Constants.ZERO_VALUE));
    days.add(new DayModel(Constants.MONDAY, Constants.ZERO_VALUE));
    days.add(new DayModel(Constants.TUESDAY, Constants.ZERO_VALUE));
    days.add(new DayModel(Constants.WEDNESDAY, Constants.ZERO_VALUE));
    days.add(new DayModel(Constants.THURSDAY, Constants.ZERO_VALUE));
    days.add(new DayModel(Constants.FRIDAY, Constants.ZERO_VALUE));
    days.add(new DayModel(Constants.SATURDAY, Constants.ZERO_VALUE));

    assert getActivity() != null;
    getActivity().getSupportLoaderManager().initLoader(1, null, this);

    return rootView;
  }

  @NonNull
  @Override
  public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
    return mTaskLoader.getAllTasks();
  }

  @Override
  public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

    if (data != null && data.getCount() > 0) {
      while (data.moveToNext()) {
        long taskDate = TasksDBContract.getColumnLong(data, TaskEntry.COLUMN_NAME_DATE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(taskDate);
        setDaysCount(calendar.get(Calendar.DAY_OF_WEEK));
      }
      setupChart();
    }
  }

  @Override
  public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    /*To implemented in the future*/
  }

  /**
   * Helper method that init and stylish the {@link #mChart} for Chart Docs head to
   * https://github.com/PhilJay/MPAndroidChart/wiki/Getting-Started
   */
  private void setupChart() {
    mChart.setDrawBarShadow(false);
    mChart.setDrawValueAboveBar(true);
    mChart.getDescription().setEnabled(false);
    // if more than 60 entries are displayed in the chart, no values will be
    // drawn
    mChart.setMaxVisibleValueCount(60);
    // scaling can now only be done on x- and y-axis separately
    mChart.setPinchZoom(false);
    mChart.setDrawGridBackground(false);

    mChart.getXAxis().setPosition(XAxisPosition.BOTTOM);
    /*Set Data to the data-set*/
    ArrayList<BarEntry> entries = new ArrayList<>();
    entries.add(new BarEntry(0, days.get(0).getTotalTasks()));
    entries.add(new BarEntry(1, days.get(1).getTotalTasks()));
    entries.add(new BarEntry(2, days.get(2).getTotalTasks()));
    entries.add(new BarEntry(3, days.get(3).getTotalTasks()));
    entries.add(new BarEntry(4, days.get(4).getTotalTasks()));
    entries.add(new BarEntry(5, days.get(5).getTotalTasks()));
    entries.add(new BarEntry(6, days.get(6).getTotalTasks()));

    BarDataSet dataSet = new BarDataSet(entries, getString(R.string.rep_active));
    /*Set Collection of colors to the data-set*/
    dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

    BarData data = new BarData(dataSet);
    /*Create and Set the xAxis labels which is the week days*/
    String[] labels = new String[]{days.get(0).getDayName(),
        days.get(1).getDayName(), days.get(2).getDayName(), days.get(3).getDayName(),
        days.get(4).getDayName(), days.get(5).getDayName(), days.get(6).getDayName()};
    mChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
    /*Finally set data*/
    mChart.setData(data);
  }

  private void setDaysCount(int week) {
    switch (week) {
      case 1:
        days.get(0).addToTotal();
        break;
      case 2:
        days.get(1).addToTotal();
        break;
      case 3:
        days.get(2).addToTotal();
        break;
      case 4:
        days.get(3).addToTotal();
        break;
      case 5:
        days.get(4).addToTotal();
        break;
      case 6:
        days.get(5).addToTotal();
        break;
      case 7:
        days.get(6).addToTotal();
        break;
    }
  }
}
