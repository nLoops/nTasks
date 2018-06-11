package com.nloops.ntasks.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.nloops.ntasks.R;

/**
 * Implementation of App Widget functionality.
 */
public class TasksListWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.tasks_list_widget);
        Intent intent = new Intent(context, ListViewService.class);
        views.setRemoteAdapter(R.id.tasks_list_widget_lv, intent);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        WidgetIntentService.startActionChangeList(context);
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

