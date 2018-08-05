package com.nloops.ntasks.widgets;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import com.nloops.ntasks.R;

public class WidgetIntentService extends IntentService {

    private static final String ACTION_CHANGE_WIDGET_LIST = "com.nloops.ntasks.ACTON_CHANGE_LIST";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public WidgetIntentService() {
        super("WidgetIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (intent != null) {
            final String action = intent.getAction();
            assert action != null;
            if (action.equals(ACTION_CHANGE_WIDGET_LIST)) {
                handleActionChangeList();
            }
        }
    }

    public static void startActionChangeList(Context context) {
        Intent intent = new Intent(context, WidgetIntentService.class);
        intent.setAction(ACTION_CHANGE_WIDGET_LIST);
        context.startService(intent);
    }

    private void handleActionChangeList() {
        // get WidgetManager
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        // get AllWidgets
        int[] appWidgetIds = manager.getAppWidgetIds
                (new ComponentName(this, TasksListWidget.class));
        // Notify Data Changed
        manager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.tasks_list_widget_lv);
        // Update Widget
        TasksListWidget.updateTasksWidgets(
                this,
                manager,
                appWidgetIds
        );


    }
}
