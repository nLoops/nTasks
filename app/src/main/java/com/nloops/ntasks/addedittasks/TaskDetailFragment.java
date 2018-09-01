package com.nloops.ntasks.addedittasks;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.nloops.ntasks.data.Task;
import com.nloops.ntasks.data.TasksDBContract;
import com.nloops.ntasks.data.TasksDBContract.TaskEntry;
import com.nloops.ntasks.utils.GeneralUtils;
import com.nloops.ntasks.utils.SharedPreferenceHelper;
import java.util.Calendar;

public class TaskDetailFragment extends Fragment implements TaskDetailContract.View {

  private TaskDetailContract.Presenter mPresenter;

  @BindView(R.id.task_detail_title)
  EditText mTitle;
  @BindView(R.id.task_detail_body)
  EditText mBody;
  @BindView(R.id.task_detail_priority_switch)
  SwitchCompat mPrioritySwitch;
  @BindView(R.id.task_detail_date)
  TextView mDateText;
  @BindView(R.id.ib_repeat)
  ImageButton mRepeatButton;
  @BindView(R.id.repeats_container)
  LinearLayout mRepeatsContainer;
  @BindView(R.id.detail_repeat_group)
  RadioGroup mRepeatRadioContainer;

  private int mTaskRepeatType;
  private long mDueDate = Long.MAX_VALUE;
  private int mYear;
  private int mMonth;
  private int mDay;

  private static final String SAVED_TASK = "task_values";


  /**
   * Empty constructor (Required)
   */
  public TaskDetailFragment() {
  }

  public static TaskDetailFragment newInstance() {
    return new TaskDetailFragment();
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
  public void onResume() {
    super.onResume();
    mPresenter.loadTaskData();
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

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    // using to inflate fragment menu.
    setHasOptionsMenu(true);
    View rootView = inflater.inflate(R.layout.frag_task_detail, container, false);
    ButterKnife.bind(this, rootView);
    assert getActivity() != null;
    FloatingActionButton detailFAB = getActivity().findViewById(R.id.task_detail_fab);

    detailFAB.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mTitle.length() > 0) {
          if (AddEditTasks.TASK_URI == null) {
            mPresenter.saveTask(getTask());
          } else {
            mPresenter.updateTask(getTask(), AddEditTasks.TASK_URI);
          }
        } else {
          showSaveEmptyError();
        }
      }
    });

    mDateText.setOnClickListener(new View.OnClickListener() {
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
          case R.id.rb_daily_repeat_detail:
            mTaskRepeatType = TaskEntry.REPEAT_DAILY;
            break;
          case R.id.rb_weekly_repeat_detail:
            mTaskRepeatType = TaskEntry.REPEAT_WEEKLY;

            break;
          case R.id.rb_monthly_repeat_detail:
            mTaskRepeatType = TaskEntry.REPEAT_MONTHLY;

            break;
          case R.id.rb_yearly_repeat_detail:
            mTaskRepeatType = TaskEntry.REPEAT_YEARLY;

            break;
          case R.id.rb_none_repeat_detail:
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
        groupContainer.check(R.id.rb_daily_repeat_detail);
        break;
      case TaskEntry.REPEAT_WEEKLY:
        groupContainer.check(R.id.rb_weekly_repeat_detail);
        break;
      case TaskEntry.REPEAT_MONTHLY:
        groupContainer.check(R.id.rb_monthly_repeat_detail);
        break;
      case TaskEntry.REPEAT_YEARLY:
        groupContainer.check(R.id.rb_yearly_repeat_detail);
        break;
      case TaskEntry.REPEAT_NONE:
      default:
        groupContainer.check(R.id.rb_none_repeat_detail);
    }
  }

  @Override
  public void displayTaskData(Task task) {
    mTitle.setText(task.getTitle());
    mBody.setText(task.getBody());
    setDateSelection(task.getDate());
    mTaskRepeatType = task.getRepeated();
    if (task.getIsPriority()) {
      mPrioritySwitch.setChecked(true);
    }

  }

  @Override
  public void showTasksListUpdated() {
    assert getActivity() != null;
    getActivity().setResult(AddEditTasks.RESULT_UPDATE_TASK);
    getActivity().finish();
  }

  @Override
  public void showDateTimePicker() {
    getDatePicker();
  }

  @Override
  public void showSaveEmptyError() {
    Snackbar.make(mTitle, getString(R.string.cannot_save_empty), Snackbar.LENGTH_LONG).show();
  }

  @Override
  public void showDenyPermissions() {

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
  public void showTasksListDelete() {
    assert getActivity() != null;
    getActivity().setResult(AddEditTasks.RESULT_DELETE_TASK);
    getActivity().finish();
  }

  @Override
  public void showTasksListAdded() {
    assert getActivity() != null;
    getActivity().setResult(AddEditTasks.RESULT_ADD_TASK);
    getActivity().finish();
  }

  private void getDatePicker() {
    DatePickerFragment pickerFragment = new DatePickerFragment();
    pickerFragment.setDateListener(mSetDatePicker);
    assert getActivity() != null;
    pickerFragment.show(getActivity().getSupportFragmentManager(), "DateFragment");

  }

  private void updateDateDisplay() {
    if (getDateSelection() == Long.MAX_VALUE) {
      mDateText.setText(getString(R.string.label_date_not_set));
      mDateText.setTextColor(getResources().getColor(R.color.colorAccent));
    } else {
      mDateText.setText(GeneralUtils.formatDate(mDueDate));
      mDateText.setTextColor(getResources().getColor(R.color.colorDarkGreen));
    }
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelable(SAVED_TASK, getTask());
  }

  private long getDateSelection() {
    return mDueDate;
  }

  private void setDateSelection(long selectedTimestamp) {
    mDueDate = selectedTimestamp;
    updateDateDisplay();

  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (getActivity() != null) {
      getActivity().finish();
    }
  }

  @Override
  public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
    super.onViewStateRestored(savedInstanceState);
    if (savedInstanceState != null) {
      Task task = savedInstanceState.getParcelable(SAVED_TASK);
      assert task != null;
      mTitle.setText(task.getTitle());
      mBody.setText(task.getBody());
      setDateSelection(task.getDate());
      if (task.getIsPriority()) {
        mPrioritySwitch.setChecked(true);
      }

    }
  }

  private Task getTask() {
    if (mDueDate == Long.MAX_VALUE) {
      mTaskRepeatType = TaskEntry.REPEAT_NONE;
    }
    String userUID = SharedPreferenceHelper.getInstance(getActivity().getApplicationContext())
        .getUID();
    String currentUser = userUID.length() > 0 ? userUID : "UnKnown";
    return new Task(mTitle.getText().toString(),
        mBody.getText().toString(),
        AddEditTasks.TASK_TYPE,
        GeneralUtils.getTaskPriority(mPrioritySwitch),
        mDueDate,
        TasksDBContract.TaskEntry.STATE_NOT_COMPLETED,
        mTaskRepeatType
        , "",
        currentUser,
        null, "");

  }


}
