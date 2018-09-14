package com.nloops.ntasks.utils;

public final class Constants {

  /*Strings of Year Months names.*/
  public static final String JANUARY = "January";
  public static final String FEBRUARY = "February";
  public static final String MARCH = "March";
  public static final String APRIL = "April";
  public static final String MAY = "May";
  public static final String JUNE = "June";
  public static final String JULY = "July";
  public static final String AUGUST = "August";
  public static final String SEPTEMBER = "September";
  public static final String OCTOBER = "October";
  public static final String NOVEMBER = "November";
  public static final String DECEMBER = "December";
  public static final String NOT_AVAILABLE = "N/A";

  /*Strings of week days names.*/
  public static final String SUNDAY = "Sunday";
  public static final String MONDAY = "Monday";
  public static final String TUESDAY = "Tuesday";
  public static final String WEDNESDAY = "Wednesday";
  public static final String THURSDAY = "Thursday";
  public static final String FRIDAY = "Friday";
  public static final String SATURDAY = "Saturday";

  /*Time in milliseconds*/
  public static final long DAY_IN_MILLIS = 86400000;
  public static final long WEEK_IN_MILLIS = 604800016L;
  public static final long MONTH_IN_MILLIS = 2629800000L;
  public static final long YEAR_IN_MILLIS = 31557600000L;

  public static final int ZERO_VALUE = 0;
  public static final String TASKS_DATABASE_REFERENCE = "tasks";
  public static final String USERS_DATABASE_REFERENCE = "users";

  /*Login and Register Constants*/
  public static int REQUEST_CODE_REGISTER = 2000;
  public static String STR_EXTRA_ACTION = "action";
  public static String STR_EXTRA_USERNAME = "username";
  public static String STR_EXTRA_PASSWORD = "password";
  public static String UID = "";
  public static String CURRENT_USERNAME = "";
  public static String CURRENT_USEREMAIL = "";

  //  Action to pass with intent to Alarm Receiver to handle TODOitems notification.
  public static final String ACTION_SCHUDELE_TODO = "com.nloops.ntasks.ACTION_TODO";


}
