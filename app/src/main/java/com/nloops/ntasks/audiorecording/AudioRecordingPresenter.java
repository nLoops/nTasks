package com.nloops.ntasks.audiorecording;

import android.media.MediaPlayer;
import android.media.MediaRecorder;

import com.nloops.ntasks.utils.GeneralUtils;

import java.io.IOException;

public class AudioRecordingPresenter implements AudioRecordingContract.Presenter {

    private AudioRecordingContract.View mView;
    private String mFileName;
    private MediaRecorder mRecorder;
    private MediaPlayer mMediaPlayer;

    private boolean isRecording = false;
    private boolean isPlaying = false;

    public AudioRecordingPresenter(AudioRecordingContract.View audioViewCallbacks) {
        mView = audioViewCallbacks;
    }


    @Override
    public void startRecording() {
        mFileName = GeneralUtils.getSavedPath();
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
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        isRecording = false;
        mView.setPlayButtonSrc();

    }

    @Override
    public void playRecording() {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(mFileName);
            mMediaPlayer.prepare();
            isPlaying = true;
            mView.setPlayButtonSrc();
            mMediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pausePlaying() {
        mMediaPlayer.pause();
        isPlaying = false;
        mView.setPlayButtonSrc();
    }

    @Override
    public void stopPlaying() {
        mMediaPlayer.release();
        mMediaPlayer = null;
        isPlaying = false;
        mView.setPlayButtonSrc();
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
}
