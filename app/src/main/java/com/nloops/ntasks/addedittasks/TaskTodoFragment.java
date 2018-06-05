package com.nloops.ntasks.addedittasks;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nloops.ntasks.R;
import com.nloops.ntasks.adapters.TodoListAdapter;
import com.nloops.ntasks.data.Task;
import com.nloops.ntasks.data.Todo;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskTodoFragment extends Fragment implements TaskDetailContract.View {

    private TaskDetailContract.Presenter mPresenter;

    @BindView(R.id.task_todo_title)
    EditText mTitle;
    @BindView(R.id.task_todo_recycle)
    RecyclerView mTodoRecycleView;
    @BindView(R.id.task_todo_item_edittext)
    TextInputEditText mItemEditText;


    private TodoListAdapter mAdapter;
    private ArrayList<Todo> mTodoList;

    private long mDueDate = Long.MAX_VALUE;
    private int mYear;
    private int mMonth;
    private int mDay;

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
        FloatingActionButton detailFAB = (FloatingActionButton) getActivity().findViewById(R.id.task_detail_fab);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mTodoRecycleView.setHasFixedSize(true);
        mTodoRecycleView.setLayoutManager(layoutManager);
        mTodoList = new ArrayList<>();
        mAdapter = new TodoListAdapter(mTodoList);
        mTodoRecycleView.setAdapter(mAdapter);

        detailFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mItemEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (mItemEditText.getText().length() > 0) {
                        mTodoList.add(new Todo(mItemEditText.getText().toString()));
                        mAdapter.swapTodoList(mTodoList);
                        mItemEditText.setText("");
                        return true;
                    }
                }
                return false;
            }
        });
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.task_detail_menu, menu);
    }


    @Override
    public void displayTaskData(Task task) {

    }

    @Override
    public void showTasksListUpdated() {

    }

    @Override
    public void showTasksListDelete() {

    }

    @Override
    public void showTasksListAdded() {

    }

    @Override
    public void showDateTimePicker() {

    }

    @Override
    public void setPresenter(TaskDetailContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
