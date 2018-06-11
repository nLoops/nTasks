package com.nloops.ntasks.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.nloops.ntasks.R;
import com.nloops.ntasks.addedittasks.AddEditTasks;
import com.nloops.ntasks.data.TasksDBContract;

/**
 * Implementation of App Widget functionality.
 */
public class AddNoteWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.add_note_widget);

        // Normal Note Pending Intent.
        Intent normalIntent = new Intent(context, AddEditTasks.class);
        normalIntent.putExtra(AddEditTasks.EXTRAS_TASK_TYPE, TasksDBContract.TaskEntry.TYPE_NORMAL_NOTE);
        PendingIntent normalPending = PendingIntent.getActivity(context, 0, normalIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.add_normal_note_widget, normalPending);

        // TodoNote Pending Intent
        Intent todoIntent = new Intent(context, AddEditTasks.class);
        todoIntent.putExtra(AddEditTasks.EXTRAS_TASK_TYPE, TasksDBContract.TaskEntry.TYPE_TODO_NOTE);
        PendingIntent todoPending = PendingIntent.getActivity(context, 1, todoIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.add_todo_note_widget,
                todoPending);
        // Audio Note Pending Intent
        Intent audioIntent = new Intent(context, AddEditTasks.class);
        audioIntent.putExtra(AddEditTasks.EXTRAS_TASK_TYPE, TasksDBContract.TaskEntry.TYPE_AUDIO_NOTE);
        PendingIntent audioPending = PendingIntent.getActivity(context, 2, audioIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.add_audio_note_widget,
                audioPending);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
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

