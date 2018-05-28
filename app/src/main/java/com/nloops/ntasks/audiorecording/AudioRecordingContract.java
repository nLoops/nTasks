package com.nloops.ntasks.audiorecording;

public interface AudioRecordingContract {

    interface View {

        void setPlayButtonSrc();

    }

    interface Presenter {

        void startRecording();

        void stopRecording();

        void playRecording();

        void pausePlaying();

        void stopPlaying();

    }
}
