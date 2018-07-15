package com.nloops.ntasks.audiorecording;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import java.io.IOException;

public class TasksMediaPlayer {

  private static TasksMediaPlayer INSTANCE;


  /* ref of MediaPlayer to allow app playback the recorded notes */
  private MediaPlayer mMediaPlayer;

  /*Declare a global AudioManager to control the behavior of Playing Audio States */
  private final AudioManager mAudioManager;

  /* Private ref to release the Resources once our MediaPlayBack is done */
  private final MediaPlayer.OnCompletionListener mOnCompletionListener =
      new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
          stopPlaying();
        }
      };

  // Set OnAudioChange instructions to run for different audio states
  private final AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener
      = new AudioManager.OnAudioFocusChangeListener() {
    @Override
    public void onAudioFocusChange(int i) {
      switch (i) {
        case AudioManager.AUDIOFOCUS_GAIN:
          // if we got the focus
          mMediaPlayer.start();
          break;
        case AudioManager.AUDIOFOCUS_LOSS:
          // if we lost the focus
          // we release the resources because we no longer need them
          stopPlaying();
          break;
        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
          // if we lost the focus for a temporary period
          mMediaPlayer.pause();
          // pause the Audio and Start from beginning
          mMediaPlayer.seekTo(0);
          break;
      }
    }
  };

  private boolean isPlaying = false;


  /**
   * Private Constructor to prevent direct instantiation.
   *
   * @param context passed {@link Context}
   */
  private TasksMediaPlayer(Context context) {
    mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
  }

  /**
   * Create Singleton of Class to prevent multi creation.
   *
   * @param context passed {@link Context}
   * @return Exists or New {@link TasksMediaPlayer}
   */
  public static TasksMediaPlayer getInstance(Context context) {
    if (INSTANCE == null) {
      INSTANCE = new TasksMediaPlayer(context);
    }
    return INSTANCE;
  }

  public void handleMediaPlayer(String path) {
    if (!isPlaying) {
      playRecording(path);
    } else {
      stopPlaying();
    }
  }

  private void playRecording(String audioPath) {
    // first we need to be sure that we released the prev resources before start a new play.
    releaseMediaPlayer();
    mMediaPlayer = new MediaPlayer();
    int audioResult = mAudioManager.requestAudioFocus(
        // instructions for different states of focus
        mOnAudioFocusChangeListener,
        // the type of our app Audio
        AudioManager.STREAM_MUSIC,
        // we don't need the service for a long time
        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
    if (audioResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
      try {
        isPlaying = true;
        mMediaPlayer.setDataSource(audioPath);
        mMediaPlayer.prepare();
        mMediaPlayer.start();
        mMediaPlayer.setOnCompletionListener(mOnCompletionListener);

      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }


  public void stopPlaying() {
    isPlaying = false;
    releaseMediaPlayer();
  }


  private void releaseMediaPlayer() {
    if (mMediaPlayer != null) {
      mMediaPlayer.release();
      mMediaPlayer = null;
      mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
    }
  }

  public boolean isPlaying() {
    return isPlaying;
  }

}
