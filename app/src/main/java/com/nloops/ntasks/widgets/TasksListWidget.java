package com.nloops.ntasks.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import com.nloops.ntasks.R;
import com.nloops.ntasks.taskslist.TasksList;
import com.nloops.ntasks.utils.GeneralUtils;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Implementation of App Widget functionality.
 */
public class TasksListWidget extends AppWidgetProvider {

  private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
      int appWidgetId) {
    // Construct the RemoteViews object
    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.tasks_list_widget);
    Intent intent = new Intent(context, ListViewService.class);
    views.setRemoteAdapter(R.id.tasks_list_widget_lv, intent);
    views.setEmptyView(R.id.tasks_list_widget_lv, R.id.widget_list_empty_view);
    //Set Calendar of today
    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
    int thisMonth = calendar.get(Calendar.MONTH);
    String month = GeneralUtils.getMonthName(thisMonth + 1);
    views.setTextViewText(R.id.tasks_list_widget_month,
        month);
    views.setTextViewText(R.id.tasks_list_widget_day,
        String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
    // Add New Task Action
    Intent tasksListIntent = new Intent(context, TasksList.class);
    PendingIntent pendingIntent = PendingIntent
        .getActivity(context, 0, tasksListIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    views.setOnClickPendingIntent(R.id.tasks_list_widget_add_new, pendingIntent);
    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views);
  }

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    // There may be multiple widgets active, so update all of them
    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.tasks_list_widget_lv);
    updateTasksWidgets(context, appWidgetManager, appWidgetIds);
  }

  public static void updateTasksWidgets(Context context, AppWidgetManager manager,
      int[] appWidgetsIds) {
    for (int appWidgetId : appWidgetsIds) {
      updateAppWidget(context, manager, appWidgetId);
    }
  }

  @Override
  public void onEnabled(Context context) {
    // Enter relevant functionality for when the first widget is created
  }

  @Override
  public void onDisabled(Context context) {
    // Enter relevant functionality for when the last widget is disabled
  }
}

