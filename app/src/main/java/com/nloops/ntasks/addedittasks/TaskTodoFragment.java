package com.nloops.ntasks.addedittasks;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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
import com.nloops.ntasks.adapters.TodoListAdapter;
import com.nloops.ntasks.data.Task;
import com.nloops.ntasks.data.TaskLoader;
import com.nloops.ntasks.data.TasksDBContract;
import com.nloops.ntasks.data.TasksDBContract.TaskEntry;
import com.nloops.ntasks.data.TasksDBContract.TodoEntry;
import com.nloops.ntasks.data.Todo;
import com.nloops.ntasks.utils.GeneralUtils;
import com.nloops.ntasks.utils.SharedPreferenceHelper;
import java.util.ArrayList;
import java.util.Calendar;

public class TaskTodoFragment extends Fragment implements TaskDetailContract.View,
    LoaderManager.LoaderCallbacks<Cursor> {

  private TaskDetailContract.Presenter mPresenter;

  @BindView(R.id.task_todo_title)
  EditText mTitle;
  @BindView(R.id.task_todo_recycle)
  RecyclerView mTodoRecycleView;
  @BindView(R.id.task_todo_item_edittext)
  TextInputEditText mItemEditText;
  @BindView(R.id.task_todo_priority_switch)
  SwitchCompat mPrioritySwitch;
  @BindView(R.id.task_todo_date)
  TextView mDateText;
  @BindView(R.id.ib_repeat_list)
  ImageButton mRepeatButton;
  @BindView(R.id.repeats_container_list)
  LinearLayout mRepeatsContainer;
  @BindView(R.id.list_repeat_group)
  RadioGroup mRepeatRadioContainer;
  private static final int LOADER_ID = 910;
  private FloatingActionButton detailFAB;

  private TodoListAdapter mAdapter;
  private ArrayList<Todo> mTodoList;

  private int mTaskRepeatType;
  private long mDueDate = Long.MAX_VALUE;
  private final DatePickerDialog.OnDateSetListener mListDatePicker = new DatePickerDialog.OnDateSetListener() {
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
      mYear = year;
      mMonth = month;
      mDay = dayOfMonth;
      TimePickerFragment pickerFragment = new TimePickerFragment();
      pickerFragment.setOnTimeSetListener(mListTimePicker);
      assert getActivity() != null;
      pickerFragment.show(getActivity().getSupportFragmentManager(), "ListTimeFragment");

    }
  };
  private int mYear;
  private int mMonth;
  private int mDay;
  private TaskLoader mTaskLoader;

  private static final String SAVED_TASK = "task_values";

  /**
   * Empty constructor (Required)
   */
  public TaskTodoFragment() {
  }

  public static TaskTodoFragment newInstance() {
    return new TaskTodoFragment();
  }

  private int currentItemPos = -1;

  /**
   * Helper method that takes EditText String and add to {@link #mTodoList}
   */
  private void addOneTodo() {
    mTodoList.add(new Todo(mItemEditText.getText().toString(),
        TasksDBContract.TodoEntry.STATE_NOT_COMPLETED));
    mAdapter.swapTodoList(mTodoList);
    mItemEditText.setText("");
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
  public void onResume() {
    super.onResume();
    mPresenter.loadTaskData();
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
  private final TodoListAdapter.onItemClickListener onItemClickListener = new TodoListAdapter.onItemClickListener() {
    @Override
    public void onItemToggled(boolean active, int position) {
      mTodoList.get(position).setIsCompleted(getCompleteState(active));
      mTodoList.get(position).setDueDate(Long.MAX_VALUE);
      mAdapter.swapTodoList(mTodoList);
      detailFAB.show();

    }

    @Override
    public void onAlarmClick(int position) {
      currentItemPos = position;
      getListPicker();
    }

    @Override
    public void onDeleteClick(int position) {
      mTodoList.remove(position);
      mAdapter.swapTodoList(mTodoList);
      detailFAB.show();
    }
  };

  private void setRadioGroupCheck(RadioGroup groupContainer) {
    switch (mTaskRepeatType) {
      case TaskEntry.REPEAT_DAILY:
        groupContainer.check(R.id.rb_daily_repeat_list);
        break;
      case TaskEntry.REPEAT_WEEKLY:
        groupContainer.check(R.id.rb_weekly_repeat_list);
        break;
      case TaskEntry.REPEAT_MONTHLY:
        groupContainer.check(R.id.rb_monthly_repeat_list);
        break;
      case TaskEntry.REPEAT_YEARLY:
        groupContainer.check(R.id.rb_yearly_repeat_list);
        break;
      case TaskEntry.REPEAT_NONE:
      default:
        groupContainer.check(R.id.rb_none_repeat_list);
    }
  }

  @Override
  public void displayTaskData(Task task) {
    mTitle.setText(task.getTitle());
    setDateSelection(task.getDate());
    mTaskRepeatType = task.getRepeated();
    if (task.getIsPriority()) {
      mPrioritySwitch.setChecked(true);
    }
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
// implement in the future.
  }

  @Override
  public void setPresenter(TaskDetailContract.Presenter presenter) {
    mPresenter = presenter;
  }

  private Task getTask() {
    if (mDueDate == Long.MAX_VALUE) {
      mTaskRepeatType = TaskEntry.REPEAT_NONE;
    }
    String userUID = SharedPreferenceHelper.getInstance(getActivity().getApplicationContext())
        .getUID();
    String currentUser = userUID.length() > 0 ? userUID : "UnKnown";
    return new Task(mTitle.getText().toString(),
        "",
        AddEditTasks.TASK_TYPE,
        GeneralUtils.getTaskPriority(mPrioritySwitch),
        mDueDate,
        TasksDBContract.TaskEntry.STATE_NOT_COMPLETED,
        mTaskRepeatType
        , "", currentUser, mTodoList, "");
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
  public void showTasksListAdded() {
    assert getActivity() != null;
    getActivity().setResult(AddEditTasks.RESULT_ADD_TASK);
    getActivity().finish();
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

  private void getDatePicker() {
    DatePickerFragment pickerFragment = new DatePickerFragment();
    pickerFragment.setDateListener(mSetDatePicker);
    assert getActivity() != null;
    pickerFragment.show(getActivity().getSupportFragmentManager(), "DateFragment");

  }

  private long getDateSelection() {
    return mDueDate;
  }

  private void setDateSelection(long selectedTimestamp) {
    mDueDate = selectedTimestamp;
    updateDateDisplay();

  }

  @Override
  public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
    super.onViewStateRestored(savedInstanceState);
    if (savedInstanceState != null) {
      Task task = savedInstanceState.getParcelable(SAVED_TASK);
      assert task != null;
      mTitle.setText(task.getTitle());
      setDateSelection(task.getDate());
      if (task.getIsPriority()) {
        mPrioritySwitch.setChecked(true);
      }
      mTodoList = (ArrayList<Todo>) task.getTodos();
      mAdapter.swapTodoList(mTodoList);
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (getActivity() != null) {
      getActivity().finish();
    }
  }

  @NonNull
  @Override
  public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
    String taskID = AddEditTasks.TASK_URI.getLastPathSegment();
    return mTaskLoader.createTODOLoader(taskID);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    setHasOptionsMenu(true);
    View rootView = inflater.inflate(R.layout.frag_todo_note, container, false);
    ButterKnife.bind(this, rootView);
    assert getActivity() != null;
    detailFAB = getActivity().findViewById(R.id.task_detail_fab);

    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
        LinearLayoutManager.VERTICAL, false);
    mTodoRecycleView.setHasFixedSize(true);
    mTodoRecycleView.setLayoutManager(layoutManager);
    mTodoList = new ArrayList<>();
    mAdapter = new TodoListAdapter(mTodoList);
    mAdapter.setOnClickListener(onItemClickListener);
    mTodoRecycleView.setAdapter(mAdapter);

    detailFAB.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mTitle.length() > 0) {
          if (mItemEditText.getText().length() > 0) {
            addOneTodo();
          } else {
            if (AddEditTasks.TASK_URI == null) {
              mPresenter.saveTask(getTask());
            } else {
              mPresenter.updateTask(getTask(), AddEditTasks.TASK_URI);
            }
          }
        } else {
          showSaveEmptyError();
        }
      }
    });

    mItemEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
          if (mItemEditText.getText().length() > 0) {
            addOneTodo();
            return true;
          }
        }
        return false;
      }
    });

    if (AddEditTasks.TASK_URI != null) {
      assert getContext() != null;
      mTaskLoader = new TaskLoader(getContext());
      getActivity().getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

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
          case R.id.rb_daily_repeat_list:
            mTaskRepeatType = TaskEntry.REPEAT_DAILY;
            break;
          case R.id.rb_weekly_repeat_list:
            mTaskRepeatType = TaskEntry.REPEAT_WEEKLY;

            break;
          case R.id.rb_monthly_repeat_list:
            mTaskRepeatType = TaskEntry.REPEAT_MONTHLY;

            break;
          case R.id.rb_yearly_repeat_list:
            mTaskRepeatType = TaskEntry.REPEAT_YEARLY;

            break;
          case R.id.rb_none_repeat_list:
            mTaskRepeatType = TaskEntry.REPEAT_NONE;
            break;
        }
      }
    });

    mItemEditText.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//later
      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.length() > 0) {
          detailFAB.hide();
        } else {
          detailFAB.show();
        }
        String currentString = charSequence.toString();
        int lineCounts = currentString.split("[\n]").length;
        if (lineCounts > 1) {
          String[] lines = currentString.split("[\n]");
          for (String line : lines) {
            mTodoList.add(new Todo(line,
                TasksDBContract.TodoEntry.STATE_NOT_COMPLETED));
          }

          mAdapter.swapTodoList(mTodoList);
          mItemEditText.setText("");
        }
      }

      @Override
      public void afterTextChanged(Editable editable) {
//later
      }
    });

    mTodoRecycleView.addOnScrollListener(new OnScrollListener() {
      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (dy > 0) {
          detailFAB.hide();
        } else if (dy < 0) {
          detailFAB.show();
        }
      }
    });

    return rootView;
  }

  @Override
  public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    mAdapter.swapTodoList(null);
  }

  @Override
  public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
    if (data != null && data.getCount() > 0) {
      while (data.moveToNext()) {
        mTodoList.add(new Todo(data));
      }
      mAdapter.swapTodoList(mTodoList);
    }
  }

  private int getCompleteState(boolean state) {
    if (state) {
      return TodoEntry.STATE_COMPLETED;
    } else {
      return TodoEntry.STATE_NOT_COMPLETED;
    }
  }

  private final TimePickerDialog.OnTimeSetListener mListTimePicker = new TimePickerDialog.OnTimeSetListener() {
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
      Calendar c = Calendar.getInstance();
      c.set(Calendar.YEAR, mYear);
      c.set(Calendar.MONTH, mMonth);
      c.set(Calendar.DAY_OF_MONTH, mDay);
      c.set(Calendar.HOUR_OF_DAY, hourOfDay);
      c.set(Calendar.MINUTE, minute);

      long mListDueDate = c.getTimeInMillis();
      if (currentItemPos > -1) {
        mTodoList.get(currentItemPos).setDueDate(mListDueDate);
        mAdapter.swapTodoList(mTodoList);
      }
    }
  };

  private void getListPicker() {
    DatePickerFragment pickerFragment = new DatePickerFragment();
    pickerFragment.setDateListener(mListDatePicker);
    assert getActivity() != null;
    pickerFragment.show(getActivity().getSupportFragmentManager(), "ListDateFragment");

  }

}
