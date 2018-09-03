package com.nloops.ntasks.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.nloops.ntasks.R;
import com.nloops.ntasks.addedittasks.AddEditTasks;
import com.nloops.ntasks.data.Todo;
import com.nloops.ntasks.utils.GeneralUtils;
import com.nloops.ntasks.views.TaskTitleView;
import java.util.ArrayList;

public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.TodoListViewHolder> {


  private ArrayList<Todo> mTodoArraylist;
  private TodoListAdapter.onItemClickListener mOnClickListener;

  public TodoListAdapter(ArrayList<Todo> todoArrayList) {
    this.mTodoArraylist = todoArrayList;
  }

  public void setOnClickListener(TodoListAdapter.onItemClickListener clickListener) {
    this.mOnClickListener = clickListener;
  }

  private void completionToggled(TodoListViewHolder holder) {
    if (mOnClickListener != null) {
      mOnClickListener.onItemToggled(holder.mCheckBox.isChecked(),
          holder.getAdapterPosition());
    }
  }

  private void alarmClicked(TodoListViewHolder holder) {
    if (mOnClickListener != null) {
      mOnClickListener.onAlarmClick(holder.getAdapterPosition());
    }
  }

  private void deleteItem(TodoListViewHolder holder) {
    if (mOnClickListener != null) {
      mOnClickListener.onDeleteClick(holder.getAdapterPosition());
    }
  }

  public Todo getItem(int position) {
    return mTodoArraylist.get(position);
  }

  @Override
  public long getItemId(int position) {
    return getItem(position).getID();
  }

  @NonNull
  @Override
  public TodoListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    int layoutID = R.layout.item_todo_list;
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(layoutID, parent, false);
    return new TodoListViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull TodoListViewHolder holder, int position) {
    Todo currentTodo = mTodoArraylist.get(position);
    holder.mItemTitle.setText(currentTodo.getTodo());
    holder.mCheckBox.setChecked(currentTodo.isComplete());
    if (currentTodo.getDueDate() != Long.MAX_VALUE) {
      holder.mDueDateView.setText(GeneralUtils.formatDate(currentTodo.getDueDate()));
    }

    setTitleState(holder.mItemTitle, currentTodo);

  }

  private void setTitleState(TaskTitleView view, Todo item) {
    view.setState(TaskTitleView.NORMAL);
    if (item.getDueDate() < System.currentTimeMillis() && item.getDueDate() != Long.MAX_VALUE) {
      view.setState(TaskTitleView.OVERDUE);
    }
    if (item.isComplete()) {
      view.setState(TaskTitleView.DONE);
    }
  }

  @Override
  public int getItemCount() {
    return (mTodoArraylist != null) ? mTodoArraylist.size() : 0;
  }

  public void swapTodoList(ArrayList<Todo> todoArrayList) {
    this.mTodoArraylist = todoArrayList;
    notifyDataSetChanged();
  }

  public interface onItemClickListener {

    void onItemToggled(boolean active, int position);

    void onAlarmClick(int position);

    void onDeleteClick(int position);
  }

  class TodoListViewHolder extends RecyclerView.ViewHolder implements
      View.OnClickListener {

    @BindView(R.id.task_todo_checkbox)
    CheckBox mCheckBox;
    @BindView(R.id.task_todo_textview)
    TaskTitleView mItemTitle;
    @BindView(R.id.task_todo_due_date)
    TextView mDueDateView;
    @BindView(R.id.task_todo_alarm)
    ImageButton mListAlarm;
    @BindView(R.id.task_todo_delete)
    ImageButton mListDelete;


    TodoListViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      mCheckBox.setOnClickListener(this);
      mListAlarm.setOnClickListener(this);
      mListDelete.setOnClickListener(this);
      if (AddEditTasks.TASK_URI == null) {
        mCheckBox.setVisibility(View.GONE);
      } else {
        mCheckBox.setVisibility(View.VISIBLE);
      }

    }

    @Override
    public void onClick(View v) {
      if (v == mCheckBox) {
        completionToggled(this);
      } else if (v == mListAlarm) {
        alarmClicked(this);
      } else if (v == mListDelete) {
        deleteItem(this);
      }
    }
  }
}
