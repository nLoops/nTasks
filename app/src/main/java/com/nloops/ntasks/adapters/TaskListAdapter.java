package com.nloops.ntasks.adapters;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.nloops.ntasks.R;
import com.nloops.ntasks.data.Task;
import com.nloops.ntasks.data.TasksDBContract;
import com.nloops.ntasks.data.TasksDataSource;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskListViewHolder> {


    /*Callbacks for List Click Listener*/
    public interface OnItemClickListener {
        void onItemClick(View v, int position, int taskType);

        void onItemToggled(boolean active, int position);
    }

    private Cursor mCursor;
    private OnItemClickListener mOnClickListener;

    public TaskListAdapter(Cursor cursor) {
        this.mCursor = cursor;
    }

    public void setOnClickListener(OnItemClickListener clickListener) {
        this.mOnClickListener = clickListener;
    }

    private void completionToggled(TaskListViewHolder holder) {
        if (mOnClickListener != null) {
            mOnClickListener.onItemToggled(holder.mCheckBox.isChecked(), holder.getAdapterPosition());
        }
    }

    private void posItemClick(TaskListViewHolder holder) {
        if (mOnClickListener != null) {
            int taskType = TasksDBContract.TaskEntry.NO_TASK_TYPE;
            if (mCursor.moveToPosition(holder.getAdapterPosition())) {
                taskType = TasksDBContract.getColumnInt(mCursor, TasksDBContract.TaskEntry.COLUMN_NAME_TYPE);
            }
            mOnClickListener.onItemClick(holder.fullView, holder.getAdapterPosition(), taskType);
        }
    }

    @NonNull
    @Override
    public TaskListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutID = R.layout.item_tasks_list;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(layoutID, parent, false);
        return new TaskListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskListViewHolder holder, int position) {

        Task currentTask = getItem(position);
        holder.mTitleView.setText(currentTask.getTitle());
        if (currentTask.isComplete()) {
            holder.mCheckBox.setChecked(true);
        } else {
            holder.mCheckBox.setChecked(false);
        }

    }

    @Override
    public int getItemCount() {
        return (mCursor != null) ? mCursor.getCount() : 0;
    }

    /**
     * Helper Method that retrive a task from Cursor using Position.
     *
     * @param position
     * @return
     */
    public Task getItem(int position) {
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("Invalid item position requested");
        }
        return new Task(mCursor);
    }

    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getID();
    }

    class TaskListViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {

        @BindView(R.id.tasks_list_title)
        TextView mTitleView;

        @BindView(R.id.tasks_list_check)
        CheckBox mCheckBox;
        // help to listen to OnClickListener for full view
        View fullView;

        public TaskListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            fullView = itemView;
            mCheckBox.setOnClickListener(this);
            fullView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v == mCheckBox) {
                completionToggled(this);
            } else {
                posItemClick(this);
            }
        }
    }
}
