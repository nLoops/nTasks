package com.nloops.ntasks.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.nloops.ntasks.R;
import com.nloops.ntasks.data.Task;
import com.nloops.ntasks.utils.GeneralUtils;
import java.util.ArrayList;

public class CalendarViewAdapter extends ArrayAdapter<Task> {

  public CalendarViewAdapter(@NonNull Context context, ArrayList<Task> tasksList) {
    super(context, 0, tasksList);
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    if (convertView == null) {
      convertView = LayoutInflater.from(getContext()).inflate(
          R.layout.calendar_view_item, parent, false);
    }

    Task currentTask = getItem(position);

    TextView taskTitle = convertView.findViewById(R.id.tv_calendar_title);
    assert currentTask != null;
    taskTitle.setText(currentTask.getTitle());

    TextView taskDate = convertView.findViewById(R.id.tv_calendar_date);
    if (currentTask.getDate() != Long.MAX_VALUE) {
      taskDate.setVisibility(View.VISIBLE);
      taskDate.setText(GeneralUtils.formatDate(currentTask.getDate()));
    } else {
      taskDate.setVisibility(View.INVISIBLE);
    }

    return convertView;
  }
}
