package com.nloops.ntasks.reports;

import android.database.Cursor;
import android.graphics.Color;
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
import com.github.mikephil.charting.animation.Easing.EasingOption;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.nloops.ntasks.R;
import com.nloops.ntasks.data.TaskLoader;
import com.nloops.ntasks.data.TasksDBContract;
import com.nloops.ntasks.data.TasksDBContract.TaskEntry;
import java.util.ArrayList;
import java.util.Calendar;

public class WeeklyTasksReport extends Fragment implements
    LoaderManager.LoaderCallbacks<Cursor> {

  @BindView(R.id.tasks_pie_chart)
  PieChart mChart;
  /*get Ref of TaskLoader to get data*/
  private TaskLoader mTaskLoader;

  public WeeklyTasksReport() {
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.report_pie_chart, container, false);
    ButterKnife.bind(this, rootView);
    mTaskLoader = new TaskLoader(getContext());
    getActivity().getSupportLoaderManager().initLoader(0, null, this);
    return rootView;
  }

  @NonNull
  @Override
  public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
    return mTaskLoader.getAllTasks();
  }

  @Override
  public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
    int completed = 0;
    int rest = 0;
    if (data != null && data.getCount() > 0) {
      while (data.moveToNext()) {
        int rowState = TasksDBContract.getColumnInt(data, TaskEntry.COLUMN_NAME_COMPLETE);
        long taskDate = TasksDBContract.getColumnLong(data, TaskEntry.COLUMN_NAME_DATE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(taskDate);
        if (rowState == TaskEntry.STATE_COMPLETED) {
          completed++;
        } else {
          rest++;
        }
      }
      float[] tasksState = new float[]{completed, rest};
      String[] tasksLabels = new String[]{"Completed", "Rest"};
      setupChart(tasksState, tasksLabels);

    }
  }

  @Override
  public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    /*To implement in the future*/
  }

  private void setupChart(float[] values, String[] labels) {
    mChart.setUsePercentValues(true);
    mChart.getDescription().setEnabled(false);
    mChart.setExtraOffsets(5, 10, 5, 5);

    ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
    for (int i = 0; i < values.length; i++) {
      entries.add(new PieEntry(values[i], labels[i]));
    }

    PieDataSet dataSet = new PieDataSet(entries, "Tasks Report");
    ArrayList<Integer> colors = new ArrayList<Integer>();
    for (int c : ColorTemplate.JOYFUL_COLORS) {
      colors.add(c);
    }

    dataSet.setColors(colors);
    PieData data = new PieData(dataSet);
    data.setValueFormatter(new PercentFormatter());
    data.setValueTextSize(11f);
    data.setValueTextColor(Color.WHITE);
    mChart.setData(data);
    mChart.highlightValues(null);

    mChart.invalidate();
    mChart.animateY(1400, EasingOption.EaseInOutQuad);
  }


}
