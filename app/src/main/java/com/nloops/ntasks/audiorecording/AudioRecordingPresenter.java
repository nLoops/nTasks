package com.nloops.ntasks.audiorecording;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;

import com.nloops.ntasks.utils.GeneralUtils;

import java.io.File;
import java.io.IOException;

/**
 * This class will implemented {@link AudioRecordingContract.Presenter} interface
 * to support Audio Recording and Playback method related to {@link com.nloops.ntasks.addedittasks.AudioNoteFragment}
 */
public class AudioRecordingPresenter implements AudioRecordingContract.Presenter {

    // ref of View to trigger actions related to fragment views.
    private final AudioRecordingContract.View mView;
    // this variable will hold the path of recorded item
    // onRecord will create a new path, onPlaying will pass it from SqlDB
    private String mFileName;
    // ref of Recorder to allow app recording Audio
    private MediaRecorder mRecorder;
    // ref of MediaPlayer to allow app playback the recorded notes.
    private MediaPlayer mMediaPlayer;

    // Private ref to release the Resources once our MediaPlayBack is done.
    private final MediaPlayer.OnCompletionListener mOnCompletionListener =
            new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlaying();
                }
            };

    // Declare a global AudioManager to control the behavior of Playing Audio States
    private final AudioManager mAudioManager;

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
    // flags to track the current state.
    private boolean isRecording = false;
    private boolean isPlaying = false;


    /**
     * public Constructor
     *
     * @param audioViewCallbacks passed View in our case the fragment that implemented the View interface
     * @param activity           the Fragment holder Activity to register {@link AudioManager}
     */
    public AudioRecordingPresenter(AudioRecordingContract.View audioViewCallbacks,
                                   Activity activity) {
        mView = audioViewCallbacks;
        mAudioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void startRecording() {
        // create a new path
        mFileName = GeneralUtils.getSavedPath();
        // setup MediaRecorder.
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isRecording = true;
        mView.setPlayButtonSrc();
        mRecorder.start();


    }

    @Override
    public void stopRecording() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            isRecording = false;
            mView.setPlayButtonSrc();
        }
    }

    @Override
    public void playRecording() {
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
                mView.setPlayButtonSrc();
                mMediaPlayer.setDataSource(mFileName);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
                mMediaPlayer.setOnCompletionListener(mOnCompletionListener);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stopPlaying() {
        isPlaying = false;
        releaseMediaPlayer();
        mView.setPlayButtonSrc();
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