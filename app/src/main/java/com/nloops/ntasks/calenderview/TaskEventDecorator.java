package com.nloops.ntasks.calenderview;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;
import java.util.Collection;
import java.util.HashSet;

public class TaskEventDecorator implements DayViewDecorator {

  private final int mColor;
  private final HashSet<CalendarDay> mDates;

  public TaskEventDecorator(int color, Collection<CalendarDay> dates) {
    this.mColor = color;
    this.mDates = new HashSet<>(dates);
  }

  @Override
  public boolean shouldDecorate(CalendarDay calendarDay) {
    return mDates.contains(calendarDay);
  }

  @Override
  public void decorate(DayViewFacade dayViewFacade) {
    dayViewFacade.addSpan(new DotSpan(5, mColor));
  }


}
