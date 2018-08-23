package com.nloops.ntasks.audiorecording;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioEncoder;
import android.media.MediaRecorder.OutputFormat;
import android.preference.PreferenceManager;
import android.widget.Toast;
import com.nloops.ntasks.R;
import com.nloops.ntasks.addedittasks.AudioNoteFragment;
import com.nloops.ntasks.audiorecording.AudioRecordingContract.Presenter;
import com.nloops.ntasks.utils.GeneralUtils;
import java.io.File;
import java.io.IOException;

/**
 * This class will implemented {@link Presenter} interface to support Audio Recording and Playback
 * method related to {@link AudioNoteFragment}
 */
public class AudioRecordingPresenter implements AudioRecordingContract.Presenter {

  /* ref of View to trigger actions related to fragment views */
  private final AudioRecordingContract.View mView;
  /* this variable will hold the path of recorded item */
  /* onRecord will create a new path, onPlaying will pass it from SqlDB */
  private String mFileName;
  /* ref of Recorder to allow app recording Audio */
  private MediaRecorder mRecorder;
  /* ref of MediaPlayer to allow app playback the recorded notes */
  private MediaPlayer mMediaPlayer;


  /* Private ref to release the Resources once our MediaPlayBack is done */
  private final MediaPlayer.OnCompletionListener mOnCompletionListener =
      new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
          stopPlaying();
        }
      };

  /*Declare a global AudioManager to control the behavior of Playing Audio States */
  private final AudioManager mAudioManager;

  /* set OnAudioChange while recording */
  private final AudioManager.OnAudioFocusChangeListener mOnRecordingFocusChangeListener
      = new AudioManager.OnAudioFocusChangeListener() {
    @Override
    public void onAudioFocusChange(int focusChange) {
      switch (focusChange) {
        /* When get Audio Focus start recording*/
        case AudioManager.AUDIOFOCUS_GAIN:
          mRecorder.start();
          break;
        /* When we loss this audio focus we stop recording */
        case AudioManager.AUDIOFOCUS_LOSS:
          stopRecording();
          break;
      }
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
          pausePlaying();
          // pause the Audio and Start from beginning
          mMediaPlayer.seekTo(0);
          break;
      }
    }
  };
  // flags to track the current state.
  private boolean isRecording = false;
  private boolean isPlaying = false;
  private boolean isPausing = false;
  // Shared Pref
  private Context mContext;


  /**
   * public Constructor
   *
   * @param audioViewCallbacks passed View in our case the fragment that implemented the View
   * interface
   * @param context the Fragment holder Activity to register {@link AudioManager}
   */
  public AudioRecordingPresenter(AudioRecordingContract.View audioViewCallbacks,
      Context context) {
    mView = audioViewCallbacks;
    mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    this.mContext = context;

  }

  @Override
  public void startRecording() {
    // create a new path
    mFileName = GeneralUtils.getSavedPath();
    // setup MediaRecorder.
    mRecorder = new MediaRecorder();
    int audioResult = mAudioManager.requestAudioFocus(
        // instructions for different states of focus
        mOnRecordingFocusChangeListener,
        // the type of our app Audio
        AudioManager.STREAM_MUSIC,
        // we don't need the service for a long time
        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
    if (audioResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
      SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
      boolean pref = preferences.getBoolean(mContext.getString(R.string.settings_recording_key),
          mContext.getResources().getBoolean(R.bool.high_quality_recording));
      mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
      mRecorder.setOutputFile(mFileName);
      if (pref) {
        mRecorder.setOutputFormat(OutputFormat.MPEG_4);
        mRecorder.setAudioEncoder(AudioEncoder.AAC);
        mRecorder.setAudioEncodingBitRate(96000);
        mRecorder.setAudioSamplingRate(44100);
      } else {
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setAudioEncodingBitRate(12200);
        mRecorder.setAudioSamplingRate(44100);
      }
      try {
        mRecorder.prepare();
      } catch (IOException e) {
        e.printStackTrace();
      }
      isRecording = true;
      if (mView != null) {
        mView.setPlayButtonSrc();
      }
      mRecorder.start();

    }

  }

  @Override
  public void stopRecording() {
    if (mRecorder != null) {
      mRecorder.stop();
      mRecorder.release();
      mRecorder = null;
      mAudioManager.abandonAudioFocus(mOnRecordingFocusChangeListener);
      isRecording = false;
      if (mView != null) {
        mView.setPlayButtonSrc();
      }
    }
  }

  @Override
  public void playRecording() {
//    if the recorded is empty we return
    if (mFileName.isEmpty()) {
      Toast.makeText(mContext, mContext.getString(R.string.cannot_find_audio_file)
          , Toast.LENGTH_LONG).show();
      return;
    }
    // first we need to be sure that we released the prev resources before start a new play.
    if (isPausing) {
      playerSeekTo(getCurrentPosition());
    } else {
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
          isPausing = false;
          if (mView != null) {
            mView.setPlayButtonSrc();
          }
          mMediaPlayer.setDataSource(mFileName);
          mMediaPlayer.prepare();
          mMediaPlayer.start();
          mMediaPlayer.setOnCompletionListener(mOnCompletionListener);

        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

  }


  @Override
  public void stopPlaying() {
    isPlaying = false;
    isPausing = false;
    releaseMediaPlayer();
    if (mView != null) {
      mView.setPlayButtonSrc();
    }
  }

  @Override
  public void deleteAudioFile() {
    if (mFileName != null && mFileName.length() > 0) {
      File audioFile = new File(mFileName);
      if (audioFile.exists()) {
        audioFile.delete();
      }
    }

  }

  @Override
  public void releaseMediaPlayer() {
    if (mMediaPlayer != null) {
      mMediaPlayer.release();
      mMediaPlayer = null;
      mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
    }
  }

  @Override
  public void pausePlaying() {
    isPlaying = false;
    isPausing = true;
    mMediaPlayer.pause();
    mView.setPlayButtonSrc();
  }

  @Override
  public void playerSeekTo(int duration) {
    mMediaPlayer.seekTo(duration);
    mMediaPlayer.start();
    isPlaying = true;
    isPausing = false;
    if (mView != null) {
      mView.setPlayButtonSrc();
    }
  }

  public boolean isRecording() {
    return isRecording;
  }

  public boolean isPlaying() {
    return isPlaying;
  }

  public String getFileName() {
    return mFileName;
  }

  public void setFileName(String fileName) {
    mFileName = fileName;
  }

  public int getTrackDuration() {
    return mMediaPlayer.getDuration();
  }

  public int getCurrentPosition() {
    return mMediaPlayer.getCurrentPosition();
  }

}
