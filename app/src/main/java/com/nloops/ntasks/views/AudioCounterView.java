package com.nloops.ntasks.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.nloops.ntasks.R;


public class AudioCounterView extends AppCompatTextView {

    public static final int IS_RECORDING = 0;
    public static final int IS_PLAYING = 1;
    private int mState;

    public AudioCounterView(Context context) {
        super(context);
    }

    public AudioCounterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AudioCounterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getState() {
        return mState;
    }

    public void setState(int state) {
        switch (state) {
            case IS_RECORDING:
                setTextColor(ContextCompat.getColor(getContext(), R.color.colorRed));
                break;
            case IS_PLAYING:
                setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack));
                break;
        }

        mState = state;
    }

}
