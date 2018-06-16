package com.nloops.ntasks.addedittasks;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.nloops.ntasks.R;
import com.nloops.ntasks.UI.DatePickerFragment;
import com.nloops.ntasks.UI.TimePickerFragment;
import com.nloops.ntasks.adapters.TodoListAdapter;
import com.nloops.ntasks.data.Task;
import com.nloops.ntasks.data.TaskLoader;
import com.nloops.ntasks.data.TasksDBContract;
import com.nloops.ntasks.data.Todo;
import com.nloops.ntasks.utils.GeneralUtils;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

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


    private TodoListAdapter mAdapter;
    private ArrayList<Todo> mTodoList;

    private long mDueDate = Long.MAX_VALUE;
    private int mYear;
    private int mMonth;
    private int mDay;
    private TaskLoader mTaskLoader;

    // flag to track TouchListener
    private boolean mElementsChanged = false;
    FloatingActionButton detailFAB;

    /**
     * Empty constructor (Required)
     */
    public TaskTodoFragment() {
    }

    public static TaskTodoFragment newInstance() {
        return new TaskTodoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.frag_todo_note, container, false);
        ButterKnife.bind(this, rootView);
        detailFAB = (FloatingActionButton) getActivity().findViewById(R.id.task_detail_fab);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mTodoRecycleView.setHasFixedSize(true);
        mTodoRecycleView.setLayoutManager(layoutManager);
        mTodoList = new ArrayList<>();
        mAdapter = new TodoListAdapter(mTodoList);
        mAdapter.setOnClickListener(onItemClickListener);
        mTodoRecycleView.setAdapter(mAdapter);

        detailFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mElementsChanged) {
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
            mTaskLoader = new TaskLoader(getContext());
            getActivity().getSupportLoaderManager().initLoader(0, null, this);
        }

        mDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.launchDatePicker();
            }
        });

        // Set OnTouch Listener.
        mTitle.setOnTouchListener(mTouchListener);
        mDateText.setOnTouchListener(mTouchListener);
        mItemEditText.setOnTouchListener(mTouchListener);
        mPrioritySwitch.setOnTouchListener(mTouchListener);
        mDateText.setOnTouchListener(mTouchListener);


        return rootView;
    }

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
        MenuItem menuItem = menu.findItem(R.id.action_detail_delete);
        if (AddEditTasks.TASK_URI != null) {
            menuItem.setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_detail_delete:
                long rawID = ContentUris.parseId(AddEditTasks.TASK_URI);
                mPresenter.completeTask(true, rawID);
                break;
            case R.id.action_detail_reminder:
                mPresenter.launchDatePicker();
                break;
            case android.R.id.home:
                if (!mElementsChanged) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                    break;
                }
                DialogInterface.OnClickListener discardButton =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NavUtils.navigateUpFromSameTask(getActivity());
                            }
                        };
                showUnSavedChangesDialog(discardButton);
                break;
        }

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.loadTaskData();
    }

    @Override
    public void displayTaskData(Task task) {
        mTitle.setText(task.getTitle());
        setDateSelection(task.getDate());
        if (task.isPriority()) {
            mPrioritySwitch.setChecked(true);
        }
    }

    @Override
    public void showTasksListUpdated() {
        getActivity().setResult(AddEditTasks.RESULT_UPDATE_TASK);
        getActivity().finish();
    }

    @Override
    public void showTasksListDelete() {
        getActivity().setResult(AddEditTasks.RESULT_DELETE_TASK);
        getActivity().finish();
    }

    @Override
    public void showTasksListAdded() {
        getActivity().setResult(AddEditTasks.RESULT_ADD_TASK);
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
    public void setPresenter(TaskDetailContract.Presenter presenter) {
        mPresenter = presenter;
    }

    private Task getTask() {
        return new Task(mTitle.getText().toString(),
                "",
                AddEditTasks.TASK_TYPE,
                GeneralUtils.getTaskPriority(mPrioritySwitch),
                mDueDate,
                TasksDBContract.TaskEntry.STATE_NOT_COMPLETED
                , "", mTodoList);
    }

    private void getDatePicker() {
        DatePickerFragment pickerFragment = new DatePickerFragment();
        pickerFragment.setDateListener(mSetDatePicker);
        pickerFragment.show(getActivity().getSupportFragmentManager(), "DateFragment");

    }

    public void setDateSelection(long selectedTimestamp) {
        mDueDate = selectedTimestamp;
        updateDateDisplay();

    }

    public long getDateSelection() {
        return mDueDate;
    }

    private void updateDateDisplay() {
        if (getDateSelection() == Long.MAX_VALUE) {
            mDateText.setText(getString(R.string.label_date_not_set));
        } else {
            mDateText.setText(GeneralUtils.formatDate(mDueDate));
        }
    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mElementsChanged = true;
            return false;
        }
    };

    /**
     * Helper method that shows a dialog to user if there's any changes will discard.
     *
     * @param discardButton
     */
    private void showUnSavedChangesDialog(DialogInterface.OnClickListener discardButton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        // set dialog message
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        // set positive button (passed click listener)
        builder.setPositiveButton(R.string.discard, discardButton);
        // set negative button to keep editing.
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // create the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    TodoListAdapter.onItemClickListener onItemClickListener = new TodoListAdapter.onItemClickListener() {
        @Override
        public void onItemToggled(boolean active, int position) {
            long rawID = mAdapter.getItemId(position);
            mPresenter.completeTODO(active, rawID);
        }
    };

    DatePickerDialog.OnDateSetListener mSetDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            mYear = year;
            mMonth = month;
            mDay = dayOfMonth;
            TimePickerFragment pickerFragment = new TimePickerFragment();
            pickerFragment.setOnTimeSetListenr(mSetTimePicker);
            pickerFragment.show(getActivity().getSupportFragmentManager(), "TimeFragment");
        }
    };

    TimePickerDialog.OnTimeSetListener mSetTimePicker = new TimePickerDialog.OnTimeSetListener() {
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

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String taskID = AddEditTasks.TASK_URI.getLastPathSegment();
        return mTaskLoader.createTODOLoader(taskID);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() > 0) {
            while (data.moveToNext()) {
                mTodoList.add(new Todo(data));
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.swapTodoList(null);
    }
}
