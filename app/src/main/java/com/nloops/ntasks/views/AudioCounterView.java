package com.nloops.ntasks.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import com.nloops.ntasks.R;
import com.nloops.ntasks.data.Task;

/**
 * This Class extends from {@link AppCompatTextView} to control {@link Task} behavior, if the date
 * Overdue textColor goes RED, if Normal goes BLACK, if Done draws a line on the text.
 */
public class AudioCounterView extends AppCompatTextView {

  public static final int IS_RECORDING = 0;
  public static final int IS_PLAYING = 1;

  public AudioCounterView(Context context) {
    super(context);
  }

  public AudioCounterView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public AudioCounterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
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

  }

}
