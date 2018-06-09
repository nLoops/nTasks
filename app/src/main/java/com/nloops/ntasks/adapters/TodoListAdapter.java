package com.nloops.ntasks.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.nloops.ntasks.R;
import com.nloops.ntasks.data.Todo;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.TodoListViewHolder> {


    private ArrayList<Todo> mTodoArraylist;
    private TodoListAdapter.onItemClickListener mOnClickListener;

    public interface onItemClickListener {
        void onItemToggled(boolean active, int position);
    }

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

    private Todo getItem(int position) {
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
    }

    @Override
    public int getItemCount() {
        return (mTodoArraylist != null) ? mTodoArraylist.size() : 0;
    }

    public void swapTodoList(ArrayList<Todo> todoArrayList) {
        this.mTodoArraylist = todoArrayList;
        notifyDataSetChanged();
    }

    class TodoListViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {

        @BindView(R.id.task_todo_checkbox)
        CheckBox mCheckBox;
        @BindView(R.id.task_todo_textview)
        TextView mItemTitle;

        TodoListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mCheckBox.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            completionToggled(this);
        }
    }
}
