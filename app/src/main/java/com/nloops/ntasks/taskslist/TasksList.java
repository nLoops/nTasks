package com.nloops.ntasks.taskslist;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.View;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mindorks.placeholderview.PlaceHolderView;
import com.nloops.ntasks.R;
import com.nloops.ntasks.UI.DrawerHeader;
import com.nloops.ntasks.UI.DrawerMenuItem;
import com.nloops.ntasks.UI.DrawerMenuItem.DrawerCallBack;
import com.nloops.ntasks.addedittasks.AddEditTasks;
import com.nloops.ntasks.calenderview.CalendarView;
import com.nloops.ntasks.data.Task;
import com.nloops.ntasks.data.TasksDBContract;
import com.nloops.ntasks.data.TasksDBContract.TaskEntry;
import com.nloops.ntasks.data.Todo;
import com.nloops.ntasks.reminders.AlarmReceiver;
import com.nloops.ntasks.reminders.AlarmScheduler;
import com.nloops.ntasks.reports.TasksReports;
import com.nloops.ntasks.utils.CloudSyncTasks;
import com.nloops.ntasks.utils.Constants;
import com.nloops.ntasks.utils.DatabaseValues;
import com.nloops.ntasks.utils.SharedPreferenceHelper;
import com.nloops.ntasks.widgets.WidgetIntentService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import pub.devrel.easypermissions.EasyPermissions;

public class TasksList extends AppCompatActivity implements EasyPermissions.PermissionCallbacks,
    SharedPreferences.OnSharedPreferenceChangeListener {

  private static final int PERMISSION_REQ_CODE = 225;
  private static final String SHORTCUT_INTENT_ACTION = "ACTION_TRUE";
  /**
   * CallBack to Listen to DrawerItem Click
   */
  private final DrawerCallBack mDrawerCallback = new DrawerCallBack() {
    @Override
    public void onCalendarViewSelected() {
      Intent intent = new Intent(TasksList.this, CalendarView.class);
      startActivity(intent);
      overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onReportViewSelected() {
      Intent intent = new Intent(TasksList.this, TasksReports.class);
      startActivity(intent);
      overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
  };
  /* Ref of Shared Preferences */
  private SharedPreferences preferences;
  /*Ref of DrawerLayout*/
  private DrawerLayout mDrawer;
  /*Ref of PlaceHolder that contains the Drawer views.*/
  private PlaceHolderView mDrawerView;
  /*Ref of Activity toolbar*/
  private Toolbar mToolbar;
  /*Ref of SpinKitView*/
  private SpinKitView mSpinKitView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_tasks_list);
    /*Get Views ref and Setup Menus*/
    mSpinKitView = findViewById(R.id.task_list_spinkit_indicator);
    mDrawer = findViewById(R.id.task_list_drawer_layout);
    mDrawerView = findViewById(R.id.drawerView);
    mToolbar = findViewById(R.id.tasks_list_toolbar);
    mToolbar.inflateMenu(R.menu.tasks_list_menu);
    setSupportActionBar(mToolbar);
    assert getSupportActionBar() != null;
    getSupportActionBar().setDisplayShowTitleEnabled(false);

    TasksFragment tasksFragment =
        (TasksFragment) getSupportFragmentManager().findFragmentById(R.id.tasks_list_container);
    if (tasksFragment == null) {
      // Create a new instance.
      tasksFragment = TasksFragment.newInstance();
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      transaction.add(R.id.tasks_list_container, tasksFragment);
      transaction.commit();
    }

    // update Widget List with data.
    WidgetIntentService.startActionChangeList(this);

    // get Preferences Ref
    preferences = PreferenceManager.getDefaultSharedPreferences(this);
    preferences.registerOnSharedPreferenceChangeListener(this);

    // Launch first Run Tutorial
    setupFirstRunGuide(mToolbar);
    // setupDrawer
    setupDrawer();

    if (VERSION.SDK_INT >= VERSION_CODES.N_MR1) {
      setupAppShortcuts();
    }

  }


  @Override
  protected void onResume() {
    super.onResume();
    // user signed in
    if (isSyncEnabled() && !isScheduled()) {
      CloudSyncTasks.initialize(TasksList.this);
    }
  }

  /**
   * @return if cloud back enabled/disabled.
   */
  private boolean isSyncEnabled() {
    return preferences.getBoolean(getString(R.string.settings_sync_key),
        getResources().getBoolean(R.bool.sync_data));
  }

  /**
   * @return if already backup set scheduled or no.
   */
  private boolean isScheduled() {
    return preferences.getBoolean(getString(R.string.backup_schedule),
        getResources().getBoolean(R.bool.backup_scheduled));
  }

  /**
   * @return if this is first app run.
   */
  private boolean isFirstTimeRun() {
    return preferences.getBoolean(getString(R.string.str_is_first_run),
        getResources().getBoolean(R.bool.first_run));
  }

  /**
   * This Method will check if we have the required permissions to RECORD and SAVE files, if not we
   * will alert USER to get the permissions.
   */
  @TargetApi(23)
  private void getPermissions() {
    String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO};
    if (!EasyPermissions.hasPermissions(TasksList.this, permissions)) {
      EasyPermissions.requestPermissions(this,
          getString(R.string.permissions_required),
          PERMISSION_REQ_CODE, permissions);
    }
  }

  @TargetApi(23)
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
  }

  @Override
  public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
    // will implemented soon
  }

  @Override
  public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
    // will implemented soon
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    String currentValue = sharedPreferences.getString(getString(R.string.settings_sync_time_key),
        getString(R.string.settings_sync_time_default));
    if (key.equals(getString(R.string.settings_sync_time_key))
        && !key.equals(currentValue)) {
      CloudSyncTasks.initialize(this);
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    preferences.unregisterOnSharedPreferenceChangeListener(this);

  }

  /**
   * This method helps to get Backup from server onFirstRun if it's available.
   */
  private void getDataFromServer() {
    // get ref of whole database
    final FirebaseDatabase mFireDataBase = FirebaseDatabase.getInstance();
    // get ref of tasks node in the database.
    DatabaseReference mFireDatabaseReference =
        mFireDataBase.getReference().child(Constants.TASKS_DATABASE_REFERENCE)
            .child(SharedPreferenceHelper.getInstance(getApplicationContext()).getUID());
    mFireDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
          /*Declare List of Task to hold the data.*/
          List<Task> serverData = new ArrayList<>();
          for (DataSnapshot child : dataSnapshot.getChildren()) {
            /* get CurrentTask*/
            Task task = child.getValue(Task.class);
            /* Add Current Task to the Array*/
            serverData.add(task);
          }
          /*Check if the list has data included*/
          if (!serverData.isEmpty()) {
            showAvailableBackupMessage(serverData);
          }
        } else {
          setupSpinKitAnimation(false);
          /* we need to get required permissions to allow app to RECORD AUDIO_NOTES and SAVE FILES*/
          getPermissions();
        }

      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {
        /*implemented later*/
      }
    });
  }


  /**
   * This method will pop-up to user to get data from server.
   */
  private void showAvailableBackupMessage(final List<Task> taskList) {
    AlertDialog.Builder builder = new AlertDialog.Builder(TasksList.this)
        /*Set AlertDialog Message*/
        .setMessage(getString(R.string.backup_message))
        /* Set AlertDialog Positive button which restoreData from Server*/
        .setPositiveButton(getString(R.string.backup_restore),
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                confirmBackup(taskList);
              }
            })
        /*set Negative Button which dismiss the dialog*/
        .setNegativeButton(getString(R.string.backup_cancel),
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                  dialog.dismiss();
                  getPermissions();
                }
              }
            });

    /*Build the Dialog*/
    AlertDialog alertDialog = builder.create();
    /*Make Indicator GONE*/
    setupSpinKitAnimation(false);
    /*Show the Dialog*/
    alertDialog.show();


  }

  /**
   * This Method will handle Inserting data into DB and Schedule future tasks alarm.
   *
   * @param taskList list of {@link Task}
   */
  private void confirmBackup(List<Task> taskList) {
    ContentValues[] values = new ContentValues[taskList.size()];
    for (int i = 0; i < taskList.size(); i++) {
      ContentValues value = DatabaseValues.from(taskList.get(i));
      values[i] = value;
      /*Schedule tasks from Server*/
      Task currentTask = taskList.get(i);
      /*Check if this task has TODOS items*/
      if (currentTask.getTodos() != null) {
        List<Todo> todos = currentTask.getTodos();
        for (int x = 0; x < todos.size(); x++) {
          ContentValues todoValue = DatabaseValues.from(todos.get(x));
          getContentResolver().insert(TasksDBContract.TodoEntry.CONTENT_TODO_URI,
              todoValue);
        }
      }
      /*if the task date is not null and also bigger than now*/
      if (currentTask.getDate() != Long.MAX_VALUE
          && currentTask.getDate() > System.currentTimeMillis()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
          AlarmScheduler.scheduleAlarm(TasksList.this,
              currentTask.getDate(),
              TasksDBContract.getTaskUri(currentTask),
              AlarmReceiver.class, currentTask.getType());
        }
      }
    }
    /*Insert data to DB*/
    getContentResolver().bulkInsert(TasksDBContract.TaskEntry.CONTENT_TASK_URI,
        values);

    /*then get permissions*/
    getPermissions();
  }

  private void setupSpinKitAnimation(boolean state) {
    if (state) {
      mSpinKitView.setVisibility(View.VISIBLE);
      Wave wave = new Wave();
      mSpinKitView.setIndeterminateDrawable(wave);
    } else {
      mSpinKitView.setVisibility(View.GONE);
    }
  }

  /**
   * if this is first run we launch a tutorial helping user to know the app interface
   *
   * @param toolbar {@link TasksList} mToolbar.
   */
  private void setupFirstRunGuide(Toolbar toolbar) {

    if (isFirstTimeRun()) {
      // get Display Size to Draw Rectangle in the Center of Screen
      // with app Drawable
      final Display display = getWindowManager().getDefaultDisplay();
      final Drawable appDrawable = ContextCompat.getDrawable(this, R.drawable.ic_auth_calendar);
      assert appDrawable != null;
      final Rect drawableRect = new Rect(0, 0, appDrawable.getIntrinsicWidth() * 2,
          appDrawable.getIntrinsicHeight() * 2);
      drawableRect.offset(display.getWidth() / 2, display.getHeight() / 2);

      // Setup TapTargetView Guide.
      // first we setup Sequence that will our Tutorial will go throw.
      final TapTargetSequence sequence = new TapTargetSequence(this)
          .targets(
              TapTarget.forView(findViewById(R.id.tasks_list_fab),
                  getString(R.string.task_list_fab_title),
                  getString(R.string.task_list_fab_description))
                  .cancelable(false)
                  .outerCircleColor(R.color.colorDarkGreen)
                  .outerCircleAlpha(0.96f)
                  .drawShadow(true)
                  .titleTextDimen(R.dimen.title_text_size)
                  .tintTarget(false)
                  .id(1),
              TapTarget.forToolbarMenuItem(
                  toolbar, R.id.action_settings,
                  getString(R.string.task_list_settings_title),
                  getString(R.string.task_list_settings_desc))
                  .cancelable(false)
                  .outerCircleColor(R.color.colorDarkGreen)
                  .outerCircleAlpha(0.96f)
                  .id(2),
              TapTarget.forToolbarOverflow(toolbar, getString(R.string.task_list_overflow_title),
                  getString(R.string.task_list_overflow_desc))
                  .cancelable(false)
                  .outerCircleColor(R.color.colorDarkGreen)
                  .outerCircleAlpha(0.96f)
                  .id(3)
          ).listener(new TapTargetSequence.Listener() {
            @Override
            public void onSequenceFinish() {
              Snackbar.make(findViewById(R.id.task_list_coordinator),
                  getString(R.string.sequence_finish_message), Snackbar.LENGTH_LONG).show();
              setupSpinKitAnimation(true);
              /*Call backup check to get data from server*/
              getDataFromServer();
            }

            @Override
            public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
              // will implemented soon
            }

            @Override
            public void onSequenceCanceled(TapTarget lastTarget) {
              // will implemented soon
            }
          });

      // Second we fire our Guide.
      TapTargetView.showFor(this, TapTarget.forBounds(drawableRect,
          getString(R.string.task_list_app_title),
          getString(R.string.task_list_app_desc))
          .cancelable(false)
          .outerCircleColor(R.color.colorDarkGreen)
          .outerCircleAlpha(0.96f)
          .icon(appDrawable), new TapTargetView.Listener() {
        @Override
        public void onTargetClick(TapTargetView view) {
          super.onTargetClick(view);
          sequence.start();
        }
      });
      preferences.edit()
          .putBoolean(getString(R.string.str_is_first_run), false)
          .apply();
    }
  }

  /**
   * This helper method will setup {@link #mDrawer} and add {@link #mDrawerView} which holds the
   * views to the Drawer and add listener for Open/close.
   */
  private void setupDrawer() {
    DrawerMenuItem calendarItem = new DrawerMenuItem(this.getApplicationContext(),
        DrawerMenuItem.DRAWER_MENU_ITEM_CALENDER_VIEW);
    calendarItem.setDrawerCallBack(mDrawerCallback);
    DrawerMenuItem reportItem = new DrawerMenuItem(this.getApplicationContext(),
        DrawerMenuItem.DRAWER_MENU_ITEM_REPORT_VIEW);
    reportItem.setDrawerCallBack(mDrawerCallback);
    mDrawerView
        .addView(new DrawerHeader(this.getApplicationContext()))
        .addView(calendarItem)
        .addView(reportItem);
    ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
        this,
        mDrawer,
        mToolbar,
        R.string.open_drawer, R.string.close_drawer) {

    };
    mDrawer.addDrawerListener(drawerToggle);
    drawerToggle.syncState();
  }

  @RequiresApi(api = VERSION_CODES.N_MR1)
  private void setupAppShortcuts() {

//    ref of Shortcut manager
    final ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
// build shortcut of Normal Task
    ShortcutInfo normalTask = new ShortcutInfo.Builder(this, getString(R.string.normal_task_id))
        .setShortLabel(getString(R.string.normal_task_short_label))
        .setLongLabel(getString(R.string.normal_task_long_label))
        .setDisabledMessage(getString(R.string.shortcut_disable_message))
        .setIcon(Icon.createWithResource(this, R.drawable.ic_add_shortcut))
        .setIntent(getTaskFragmentIntent(TaskEntry.TYPE_NORMAL_NOTE))
        .setRank(0)
        .build();
// build shortcut of TODOList Task
    ShortcutInfo listTask = new ShortcutInfo.Builder(this, getString(R.string.list_task_id))
        .setShortLabel(getString(R.string.list_task_short_label))
        .setLongLabel(getString(R.string.list_task_long_label))
        .setDisabledMessage(getString(R.string.shortcut_disable_message))
        .setIcon(Icon.createWithResource(this, R.drawable.ic_list_shortcut))
        .setIntent(getTaskFragmentIntent(TaskEntry.TYPE_TODO_NOTE))
        .setRank(1)
        .build();
// build shortcut of Audio Note Task
    ShortcutInfo audioTask = new ShortcutInfo.Builder(this, getString(R.string.audio_task_id))
        .setShortLabel(getString(R.string.audio_task_short_label))
        .setLongLabel(getString(R.string.audio_task_long_label))
        .setDisabledMessage(getString(R.string.shortcut_disable_message))
        .setIcon(Icon.createWithResource(this, R.drawable.ic_mic_shortcut))
        .setIntent(getTaskFragmentIntent(TaskEntry.TYPE_AUDIO_NOTE))
        .setRank(2)
        .build();

    assert shortcutManager != null;
    shortcutManager.addDynamicShortcuts(Arrays.asList(normalTask, listTask, audioTask));

  }

  @RequiresApi(api = VERSION_CODES.N_MR1)
  private Intent getTaskFragmentIntent(int taskType) {
    Intent addEditIntent = new Intent(TasksList.this, AddEditTasks.class);
    addEditIntent.putExtra(AddEditTasks.EXTRAS_TASK_TYPE, taskType);
    addEditIntent.setAction(SHORTCUT_SERVICE);
    return addEditIntent;
  }
}
