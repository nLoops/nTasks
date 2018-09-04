package com.nloops.ntasks.addedittasks;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.nloops.ntasks.R;
import com.nloops.ntasks.UI.DatePickerFragment;
import com.nloops.ntasks.UI.TimePickerFragment;
import com.nloops.ntasks.audiorecording.AudioRecordingContract;
import com.nloops.ntasks.audiorecording.AudioRecordingPresenter;
import com.nloops.ntasks.data.Task;
import com.nloops.ntasks.data.TasksDBContract;
import com.nloops.ntasks.data.TasksDBContract.TaskEntry;
import com.nloops.ntasks.utils.GeneralUtils;
import com.nloops.ntasks.utils.SharedPreferenceHelper;
import com.nloops.ntasks.views.AudioCounterView;
import com.nloops.ntasks.views.CustomFillBar;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import pub.devrel.easypermissions.EasyPermissions;

public class AudioNoteFragment extends Fragment implements TaskDetailContract.View,
    AudioRecordingContract.View, EasyPermissions.PermissionCallbacks {

  private TaskDetailContract.Presenter mPresenter;
  private AudioRecordingPresenter mAudioPresenter;

  @BindView(R.id.task_audio_title)
  TextView mTitleView;
  @BindView(R.id.audio_note_switch_priority)
  SwitchCompat mPrioritySwitch;
  @BindView(R.id.audionote_detail_date)
  TextView mDueDateTV;
  @BindView(R.id.play_back_btn)
  ImageButton mPlayBackBtn;
  @BindView(R.id.playback_txt_counter)
  AudioCounterView mPlayTimer;
  @BindView(R.id.custom_fill_seekbar)
  CustomFillBar mSeekBar;
  @BindView(R.id.ib_repeat_audio)
  ImageButton mRepeatButton;
  @BindView(R.id.repeats_container_audio)
  LinearLayout mRepeatsContainer;
  @BindView(R.id.audio_repeat_group)
  RadioGroup mRepeatRadioContainer;

  private int mTaskRepeatType;
  private long mDueDate = Long.MAX_VALUE;
  private int mYear;
  private int mMonth;
  private int mDay;

  private final Handler customHandler = new Handler();
  private long startHTime = 0L;
  private long timeInMilliseconds = 0L;
  private long timeSwapBuff = 0L;

  private static final int PERMISSION_REQ_CODE = 125;

  /**
   * Empty Constructor required by Platform
   */
  public AudioNoteFragment() {
    /*Empty Constructor*/
  }

  public static AudioNoteFragment newInstance() {
    return new AudioNoteFragment();
  }

  private final DatePickerDialog.OnDateSetListener mSetDatePicker = new DatePickerDialog.OnDateSetListener() {
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
      mYear = year;
      mMonth = month;
      mDay = dayOfMonth;
      TimePickerFragment pickerFragment = new TimePickerFragment();
      pickerFragment.setOnTimeSetListener(mSetTimePicker);
      assert getActivity() != null;
      pickerFragment.show(getActivity().getSupportFragmentManager(), "TimeFragment");
    }
  };

  @Override
  public void onStop() {
    super.onStop();
    if (AddEditTasks.TASK_URI == null) {
      if (mAudioPresenter.isRecording()) {
        mAudioPresenter.stopRecording();
        customHandler.removeCallbacks(updateTimerThread);
      }
    } else {
      if (mAudioPresenter.isPlaying()) {
        mAudioPresenter.stopPlaying();
        customHandler.removeCallbacks(refreshPlayingTimer);
      }
    }
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    assert getActivity() != null;
    mAudioPresenter = new AudioRecordingPresenter(this,
        getActivity().getApplicationContext());
    getPermissions();

  }

  /**
   * Helper Method that checks and operate the right action (Record, Play, Pause, Stop Recording)
   */
  private void handlePlayButtonClick() {
    if (AddEditTasks.TASK_URI != null) {
      handlePlayAudio();
    } else {
      handleRecordingAudio();
      Log.d("TESA", "handlePlayButtonClick: " + mAudioPresenter.isRecording());
    }
  }

  private void handlePlayAudio() {
    if (mAudioPresenter.isPlaying()) {
      mAudioPresenter.pausePlaying();
      customHandler.removeCallbacks(refreshPlayingTimer);
    } else {
      mAudioPresenter.playRecording();
      mPlayTimer.setState(AudioCounterView.IS_PLAYING);
      mSeekBar.setMaxValue(mAudioPresenter.getTrackDuration());
      updateSeekBar();

    }
  }

  private void handleRecordingAudio() {
    if (mAudioPresenter.isRecording()) {
      mAudioPresenter.stopRecording();
      timeSwapBuff += timeInMilliseconds;
      customHandler.removeCallbacks(updateTimerThread);
    } else if (!mAudioPresenter.isRecording() && mAudioPresenter.getFileName() != null) {
      handlePlayAudio();
    } else {
      mAudioPresenter.startRecording();
      mPlayTimer.setState(AudioCounterView.IS_RECORDING);
      startHTime = SystemClock.uptimeMillis();
      customHandler.postDelayed(updateTimerThread, 0);
    }
  }

  /**
   * This method will help to save new or update {@link Task}
   */
  private void actionFabClick() {
    if (mTitleView.length() > 0) {
      if (AddEditTasks.TASK_URI == null) {
        if (mAudioPresenter.isRecording()) {
          mAudioPresenter.stopRecording();
          customHandler.removeCallbacks(updateTimerThread);
        }
        mPresenter.saveTask(getTask());
      } else {
        if (mAudioPresenter.isPlaying()) {
          mAudioPresenter.stopPlaying();
        }
        mPresenter.updateTask(getTask(), AddEditTasks.TASK_URI);
      }
    } else {
      showSaveEmptyError();
    }
  }


  @Override
  public void onResume() {
    super.onResume();
    mPresenter.loadTaskData();
    if (getView() == null) {
      return;
    }

    getView().setFocusableInTouchMode(true);
    getView().requestFocus();
    getView().setOnKeyListener(new View.OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
          // handle back button's click listener
          if (mAudioPresenter.getFileName() != null) {
            mAudioPresenter.deleteAudioFile();
            assert getActivity() != null;
            getActivity().finish();
          }
          return true;
        }
        return false;
      }
    });
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    setHasOptionsMenu(true);
    View rootView = inflater.inflate(R.layout.frag_task_audio_note, container, false);
    ButterKnife.bind(this, rootView);
    if (AddEditTasks.TASK_URI != null) {
      mPlayBackBtn.setImageResource(R.drawable.ic_play_btn);
    }
    assert getActivity() != null;
    FloatingActionButton mActivityFab = getActivity().findViewById(R.id.task_detail_fab);
    mActivityFab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        actionFabClick();
      }
    });

    mPlayBackBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        handlePlayButtonClick();
      }
    });

    mDueDateTV.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mPresenter.launchDatePicker();
      }
    });

    mRepeatButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mRepeatsContainer.getVisibility() == View.GONE) {
          mRepeatsContainer.setVisibility(View.VISIBLE);
          setRadioGroupCheck(mRepeatRadioContainer);
        } else if (mRepeatsContainer.getVisibility() == View.VISIBLE) {
          mRepeatsContainer.setVisibility(View.GONE);
        }
      }
    });

    mRepeatRadioContainer.setOnCheckedChangeListener(new OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
          case R.id.rb_daily_repeat_audio:
            mTaskRepeatType = TaskEntry.REPEAT_DAILY;
            break;
          case R.id.rb_weekly_repeat_audio:
            mTaskRepeatType = TaskEntry.REPEAT_WEEKLY;

            break;
          case R.id.rb_monthly_repeat_audio:
            mTaskRepeatType = TaskEntry.REPEAT_MONTHLY;

            break;
          case R.id.rb_yearly_repeat_audio:
            mTaskRepeatType = TaskEntry.REPEAT_YEARLY;

            break;
          case R.id.rb_none_repeat_audio:
            mTaskRepeatType = TaskEntry.REPEAT_NONE;
            break;
        }
      }
    });
    return rootView;
  }

  private void setRadioGroupCheck(RadioGroup groupContainer) {
    switch (mTaskRepeatType) {
      case TaskEntry.REPEAT_DAILY:
        groupContainer.check(R.id.rb_daily_repeat_audio);
        break;
      case TaskEntry.REPEAT_WEEKLY:
        groupContainer.check(R.id.rb_weekly_repeat_audio);
        break;
      case TaskEntry.REPEAT_MONTHLY:
        groupContainer.check(R.id.rb_monthly_repeat_audio);
        break;
      case TaskEntry.REPEAT_YEARLY:
        groupContainer.check(R.id.rb_yearly_repeat_audio);
        break;
      case TaskEntry.REPEAT_NONE:
      default:
        groupContainer.check(R.id.rb_none_repeat_audio);
    }
  }

  @Override
  public void displayTaskData(Task task) {
    mTitleView.setText(task.getTitle());
    setDateSelection(task.getDate());
    mTaskRepeatType = task.getRepeated();
    if (task.getIsPriority()) {
      mPrioritySwitch.setChecked(true);
    }
    mAudioPresenter.setFileName(task.getPath());
  }

  @Override
  public void showTasksListUpdated() {
    assert getActivity() != null;
    getActivity().setResult(AddEditTasks.RESULT_UPDATE_TASK);
    getActivity().finish();
  }

  @Override
  public void showTasksListDelete() {
    assert getActivity() != null;
    getActivity().setResult(AddEditTasks.RESULT_DELETE_TASK);
    getActivity().finish();
  }

  @Override
  public void showDateTimePicker() {
    getDatePicker();
  }

  @Override
  public void showSaveEmptyError() {
    Snackbar.make(mTitleView, getString(R.string.cannot_save_empty), Snackbar.LENGTH_LONG).show();
  }

  @Override
  public void showDenyPermissions() {
    assert getActivity() != null;
    getActivity().setResult(AddEditTasks.RESULT_DENY_PERMISSIONS);
    getActivity().finish();
  }

  @Override
  public void setPresenter(TaskDetailContract.Presenter presenter) {
    mPresenter = presenter;
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.task_detail_menu, menu);
    // if we are in EditMode will make Done & Delete menuButton visible.
    MenuItem itemDelete = menu.findItem(R.id.action_detail_delete);
    MenuItem itemDone = menu.findItem(R.id.action_detail_done);
    if (AddEditTasks.TASK_URI != null) {
      itemDelete.setVisible(true);
      itemDone.setVisible(true);
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_detail_delete:
        mPresenter.deleteTask(AddEditTasks.TASK_URI);
        break;
      case R.id.action_detail_done:
        if (mTaskRepeatType != TaskEntry.REPEAT_NONE) {
          mDueDate = mDueDate + GeneralUtils.getRepeatedValue(mTaskRepeatType);
          setDateSelection(mDueDate);
          mPresenter.updateTask(getTask(), AddEditTasks.TASK_URI);
        } else {
          long rawID = ContentUris.parseId(AddEditTasks.TASK_URI);
          mPresenter.completeTask(true, rawID);
        }
        break;
      case R.id.action_detail_reminder:
        mPresenter.launchDatePicker();
        break;
    }

    return true;
  }

  @Override
  public void showTasksListAdded() {
    assert getActivity() != null;
    getActivity().setResult(AddEditTasks.RESULT_ADD_TASK);
    getActivity().finish();
  }

  private void setDateSelection(long selectedTimestamp) {
    mDueDate = selectedTimestamp;
    updateDateDisplay();

  }

  private long getDateSelection() {
    return mDueDate;
  }

  private void updateDateDisplay() {
    if (getDateSelection() == Long.MAX_VALUE) {
      mDueDateTV.setText(getString(R.string.label_date_not_set));
    } else {
      mDueDateTV.setText(GeneralUtils.formatDate(mDueDate));
    }
  }

  private Task getTask() {
    if (mDueDate == Long.MAX_VALUE) {
      mTaskRepeatType = TaskEntry.REPEAT_NONE;
    }
    String userUID = SharedPreferenceHelper.getInstance(getActivity().getApplicationContext())
        .getUID();
    String currentUser = userUID.length() > 0 ? userUID : "UnKnown";
    return new Task(mTitleView.getText().toString(),
        "",
        AddEditTasks.TASK_TYPE,
        GeneralUtils.getTaskPriority(mPrioritySwitch),
        mDueDate,
        TasksDBContract.TaskEntry.STATE_NOT_COMPLETED,
        mTaskRepeatType
        , mAudioPresenter.getFileName(), currentUser, null, "");
  }

  private void getDatePicker() {
    DatePickerFragment pickerFragment = new DatePickerFragment();
    pickerFragment.setDateListener(mSetDatePicker);
    assert getActivity() != null;
    pickerFragment.show(getActivity().getSupportFragmentManager(), "DateFragment");

  }

  private final TimePickerDialog.OnTimeSetListener mSetTimePicker = new TimePickerDialog.OnTimeSetListener() {
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
      Calendar c = Calendar.getInstance();
      c.set(Calendar.YEAR, mYear);
      c.set(Calendar.MONTH, mMonth);
      c.set(Calendar.DAY_OF_MONTH, mDay);
      c.set(Calendar.HOUR_OF_DAY, hourOfDay);
      c.set(Calendar.MINUTE, minute);

      setDateSelection(c.getTimeInMillis());
    }
  };

  @Override
  public void setPlayButtonSrc() {
    if (AddEditTasks.TASK_URI != null) {
      mPlayBackBtn.setImageResource(
          mAudioPresenter.isPlaying() ? R.drawable.ic_pause_btn : R.drawable.ic_play_btn);
    } else {
      if (mAudioPresenter.getFileName() == null) {
        mPlayBackBtn.setImageResource(
            mAudioPresenter.isRecording() ? R.drawable.ic_stop_btn : R.drawable.ic_red_mic);
      } else {
        if (mAudioPresenter.isPlaying()) {
          mPlayBackBtn.setImageResource(
              mAudioPresenter.isPlaying() ? R.drawable.ic_pause_btn : R.drawable.ic_play_btn);
        } else {
          mPlayBackBtn.setImageResource(
              mAudioPresenter.isRecording() ? R.drawable.ic_stop_btn : R.drawable.ic_play_btn);
        }
      }
    }
  }

  // this will handle track recording timer.
  private final Runnable updateTimerThread = new Runnable() {

    public void run() {

      timeInMilliseconds = SystemClock.uptimeMillis() - startHTime;

      long updatedTime = timeSwapBuff + timeInMilliseconds;

      int seconds = (int) (updatedTime / 1000);
      int minutes = seconds / 60;
      seconds = seconds % 60;
      mSeekBar.setMaxValue(60);
      mSeekBar.setProgress(seconds);
      if (mPlayTimer != null) {
        mPlayTimer.setText(getString(R.string.audio_counter_format, minutes, seconds));
      }
      customHandler.postDelayed(this, 0);
    }
  };


  private final Runnable refreshPlayingTimer = new Runnable() {
    @Override
    public void run() {
      if (mAudioPresenter.isPlaying()) {
        int mCurrentPosition = mAudioPresenter.getCurrentPosition();
        mSeekBar.setProgress(mCurrentPosition);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(mCurrentPosition);
        long seconds =
            TimeUnit.MILLISECONDS.toSeconds
                (mCurrentPosition) - TimeUnit.MINUTES.toSeconds(minutes);
        mPlayTimer.setText(
            getString(R.string.audio_counter_format, minutes, seconds));

        updateSeekBar();
      }
    }
  };

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (getActivity() != null) {
      getActivity().finish();
    }
  }

  private void updateSeekBar() {
    customHandler.postDelayed(refreshPlayingTimer, 100);
  }

  /**
   * This Method will check if we have the required permissions to RECORD and SAVE files, if not we
   * will alert USER to get the permissions.
   */
  @TargetApi(23)
  private void getPermissions() {
    assert getContext() != null;
    assert getActivity() != null;
    String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO};
    if (!EasyPermissions.hasPermissions(getContext(), permissions)) {
      EasyPermissions.requestPermissions(this,
          getString(R.string.permissions_required),
          PERMISSION_REQ_CODE, permissions);
    }
  }

  @TargetApi(23)
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    EasyPermissions.onRequestPermissionsResult
        (requestCode, permissions, grantResults, this);
  }

  @Override
  public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
    // will implemented soon
  }

  @Override
  public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
    showDenyPermissions();
  }

}
