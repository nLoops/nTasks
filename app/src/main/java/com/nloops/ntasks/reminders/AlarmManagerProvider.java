package com.nloops.ntasks.reminders;


import android.app.AlarmManager;
import android.content.Context;

/**
 * Interface to provide access to an {@link AlarmManager} instance that can be configured
 * during automated unit tests.
 * <p>
 * NO MODIFICATIONS SHOULD BE MADE TO THIS CLASS OR ITS USAGE.
 */
public class AlarmManagerProvider {

    private static AlarmManager sAlarmManager;

    static synchronized AlarmManager getAlarmManager(Context context) {
        if (sAlarmManager == null) {
            sAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }
        return sAlarmManager;
    }
}
