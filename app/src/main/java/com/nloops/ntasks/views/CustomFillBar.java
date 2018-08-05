package com.nloops.ntasks.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.nloops.ntasks.R;


public class CustomFillBar extends FrameLayout {

    private final Solid mSolid;
    private long mProgress;
    private double mMaxValue = 1.0;


    public CustomFillBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        /* Load styled attributes */
        final TypedArray attributes = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.FillSeekBar, R.attr.fillseekbarViewStyle, 0);
        int mFillColor = R.color.colorPrimaryDark;
        mProgress = attributes.getInt(R.styleable.FillSeekBar_progress, 0);
        attributes.recycle();
        mSolid = new Solid(context, null);
        mSolid.initPaint(mFillColor);
        addView(mSolid, 0, LayoutParams.MATCH_PARENT);

    }

    public void setMaxValue(double maxValue) {
        this.mMaxValue = maxValue;
    }

    public void setProgress(long progress) {
        mProgress = progress > mMaxValue ? (long) mMaxValue : progress;
        computeProgressRight();
    }

    private void computeProgressRight() {

        int mSolidRight = (int) (getWidth() * (1f - mProgress / mMaxValue));
        ViewGroup.LayoutParams params = mSolid.getLayoutParams();
        if (params != null) {
            ((LayoutParams) params).width = getWidth() - mSolidRight;
        }
        mSolid.setLayoutParams(params);
    }

    private static class Solid extends View {

        private Paint progressPaint;

        public Solid(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public Solid(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            params.weight = 1;
            setLayoutParams(params);
        }

        void initPaint(int mFillColor) {
            progressPaint = new Paint();
            progressPaint.setColor(mFillColor);
            progressPaint.setAlpha(125);
            progressPaint.setStyle(Paint.Style.FILL);
            progressPaint.setAntiAlias(true);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawRect(getLeft(), 0, getWidth(), getBottom(), progressPaint);
        }
    }
}
