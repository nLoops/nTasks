package com.nloops.ntasks.audiorecording;

public interface AudioRecordingContract {

    interface View {

        void setPlayButtonSrc();

    }

    interface Presenter {

        void startRecording();

        void stopRecording();

        void playRecording();

        void stopPlaying();

        void deleteAudioFile();

        void releaseMediaPlayer();

        void pausePlaying();

        void playerSeekTo(int duration);


    }
}
