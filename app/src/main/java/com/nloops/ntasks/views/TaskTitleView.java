package com.nloops.ntasks.views;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import com.nloops.ntasks.R;

public class TaskTitleView extends AppCompatTextView {

  public static final int NORMAL = 0;
  public static final int DONE = 1;
  public static final int OVERDUE = 2;
  private int mState;

  public TaskTitleView(Context context) {
    super(context);

  }

  public TaskTitleView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public TaskTitleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  /**
   * Return the current display state of this view.
   *
   * @return One of {@link #NORMAL}, {@link #DONE}, or {@link #OVERDUE}.
   */
  public int getState() {
    return mState;
  }

  /**
   * Update the text display state of this view. Normal status shows black text. Overdue displays in
   * red. Completed draws a strikethrough line on the text.
   *
   * @param state New state. One of {@link #NORMAL}, {@link #DONE}, or {@link #OVERDUE}.
   */
  public void setState(int state) {
    switch (state) {
      case DONE:
        setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        setTextColor(ContextCompat.getColor(getContext(), R.color.colorGray));
        break;
      case NORMAL:
        setPaintFlags(0);
        setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack));
        break;
      case OVERDUE:
        setPaintFlags(0);
        setTextColor(ContextCompat.getColor(getContext(), R.color.colorRed));
        break;
      default:
        return;
    }

    mState = state;
  }


  private Typeface setTextFont(@NonNull Context context) {
    return Typeface.createFromAsset(context.getAssets(), "fonts/font_bold.ttf");
  }


}
