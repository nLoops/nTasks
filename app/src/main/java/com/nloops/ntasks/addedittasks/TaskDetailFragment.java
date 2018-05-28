package com.nloops.ntasks.addedittasks;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.nloops.ntasks.R;

import com.nloops.ntasks.UI.DatePickerFragment;
import com.nloops.ntasks.UI.TimePickerFragment;
import com.nloops.ntasks.data.Task;
import com.nloops.ntasks.data.TasksDBContract;
import com.nloops.ntasks.utils.GeneralUtils;

import org.w3c.dom.Text;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    private long mDueDate = Long.MAX_VALUE;
    private int mYear;
    private int mMonth;
    private int mDay;


    /**
     * Empty constructor (Required)
     */
    public TaskDetailFragment() {
    }

    public static TaskDetailFragment newInstance() {
        return new TaskDetailFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // using to inflate fragment menu.
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.frag_task_detail, container, false);
        ButterKnife.bind(this, rootView);
        FloatingActionButton detailFAB = (FloatingActionButton) getActivity().findViewById(R.id.task_detail_fab);
        detailFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AddEditTasks.TASK_URI == null) {
                    mPresenter.saveTask(getTask());
                } else {
                    mPresenter.updateTask(getTask(), AddEditTasks.TASK_URI);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.loadTaskData();
    }

    @Override
    public void displayTaskData(Task task) {
        mTitle.setText(task.getTitle());
        mBody.setText(task.getBody());
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
    public void setPresenter(TaskDetailContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.task_detail_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_detail_delete:
                mPresenter.deleteTask(AddEditTasks.TASK_URI);
                break;
            case R.id.action_detail_reminder:
                mPresenter.launchDatePicker();
                break;
        }

        return true;
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
            CharSequence formatted = DateUtils.getRelativeTimeSpanString(getActivity(), mDueDate);
            mDateText.setText(formatted);
        }
    }

    private Task getTask() {
        Task task = new Task(mTitle.getText().toString(),
                mBody.getText().toString(),
                AddEditTasks.TASK_TYPE,
                GeneralUtils.getTaskPriority(mPrioritySwitch),
                mDueDate,
                TasksDBContract.TaskEntry.STATE_NOT_COMPLETED
                , "", null);
        return task;
    }

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


}
