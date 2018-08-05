package com.nloops.ntasks.reports;

public class DayModel {

  private final String mDayName;
  private int mTotalTasks;

  public DayModel(String dayName, int totalTasks) {
    this.mDayName = dayName;
    this.mTotalTasks = totalTasks;
  }

  public String getDayName() {
    return mDayName;
  }

  public int getTotalTasks() {
    return mTotalTasks;
  }

  public void addToTotal() {
    mTotalTasks++;
  }
}
