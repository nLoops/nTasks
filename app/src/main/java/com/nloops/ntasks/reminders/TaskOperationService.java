package com.nloops.ntasks.reminders;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.ContentUris;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.nloops.ntasks.data.Task;

import com.nloops.ntasks.audiorecording.TasksMediaPlayer;
import com.nloops.ntasks.data.TasksLocalDataSource;

public class TaskOperationService extends IntentService {

    private static final String TAG = TaskOperationService.class.getSimpleName();
    public static final String EXTRAS_NOTIFICATION_ID = "notification_id";
    public static final String ACTION_COMPLETE_TASK = "complete_task";
    public static final String ACTION_PLAY_NOTE_AUDIO = "audio_note";
    public static final String EXTRAS_NOTE_AUDIO_PATH = "audiopath";
    private TasksLocalDataSource mTasksDataSource;


    public TaskOperationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        assert intent != null;
        if (intent.getAction().equals(ACTION_COMPLETE_TASK)) {
            performCompleteTask(intent);
        } else if (intent.getAction().equals(ACTION_PLAY_NOTE_AUDIO)) {
            String path = intent.getStringExtra(EXTRAS_NOTE_AUDIO_PATH);
            performPlayNote(path);
        }
    }


    /**
     * This Helper method will perform as {@link NotificationCompat.Action}
     * when user hit DONE the task will perform Complete on SQLite.
     *
     * @param intent passed Intent for {@link Task}
     */
    private void performCompleteTask(Intent intent) {
        mTasksDataSource = new TasksLocalDataSource(getContentResolver(),
                this);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        long rawID = ContentUris.parseId(intent.getData());
        mTasksDataSource.completeTask(true, rawID);
        assert manager != null;
        manager.cancel(intent.getIntExtra(EXTRAS_NOTIFICATION_ID, 0));
    }

    /**
     * This Helper method will perform as {@link NotificationCompat.Action}
     * when user hit Play will get {@link TasksMediaPlayer} and play passed
     * audio String.
     *
     * @param audioPath {@link Task} audioPath
     */
    private void performPlayNote(String audioPath) {
        TasksMediaPlayer mediaPlayer = TasksMediaPlayer.getInstance(this);
        mediaPlayer.handleMediaPlayer(audioPath);
    }

}
