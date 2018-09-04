package com.nloops.ntasks.taskslist;


import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.firebase.auth.FirebaseAuth;
import com.nloops.ntasks.R;
import com.nloops.ntasks.UI.SettingsActivity;
import com.nloops.ntasks.adapters.TaskListAdapter;
import com.nloops.ntasks.addedittasks.AddEditTasks;
import com.nloops.ntasks.data.Task;
import com.nloops.ntasks.data.TaskLoader;
import com.nloops.ntasks.data.TasksDBContract;
import com.nloops.ntasks.data.TasksDBContract.TaskEntry;
import com.nloops.ntasks.data.TasksLocalDataSource;
import com.nloops.ntasks.login.LoginActivity;
import com.nloops.ntasks.utils.GeneralUtils;
import com.nloops.ntasks.utils.SharedPreferenceHelper;
import com.nloops.ntasks.widgets.WidgetIntentService;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

public class TasksFragment extends Fragment implements TasksListContract.View {

  private TasksListContract.Presenter mPresenter;
  private final int NO_TASK_ID = -1;

  private TaskListAdapter mAdapter;
  @BindView(R.id.tasks_list_recycle)
  RecyclerView mRecyclerView;
  @BindView(R.id.tasks_list_progress)
  ProgressBar mProgressBar;
  private FabSpeedDial fabSpeedDial;
  private final TaskListAdapter.OnItemClickListener onItemClickListener = new TaskListAdapter.OnItemClickListener() {
    @Override
    public void onItemClick(View v, int position, int taskType) {
      long rawID = mAdapter.getItemId(position);
      mPresenter.loadAddEditActivity(rawID, taskType);
    }

    @Override
    public void onItemToggled(boolean active, int position) {
      Task task = mAdapter.getItem(position);
      if (task.getIsRepeated()) {
        long nextDate = task.getDate() + GeneralUtils.getRepeatedValue(task.getRepeated());
        task.setDate(nextDate);
        mPresenter.updateTask(task, TasksDBContract.getTaskUri(task));
      } else {
        long rawID = mAdapter.getItemId(position);
        mPresenter.updateComplete(active, rawID);
      }

//      'Show' FAB after check_complete
      fabSpeedDial.show();
    }

    @Override
    public void onSwipeDeleteClick(Uri taskUri) {
      mPresenter.deleteTask(taskUri);
    }
  };

  /**
   * Empty Constructor required by system.
   */
  public TasksFragment() {
  }

  public static TasksFragment newInstance() {
    return new TasksFragment();
  }

  private View mEmptyView;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Create new object of Cursor Loader
    assert getActivity() != null;
    assert getContext() != null;
    TaskLoader loader = new TaskLoader(getActivity());
    // Create new instance of LocalDataSource
    TasksLocalDataSource dataSource = TasksLocalDataSource
        .getInstance(getActivity().getApplicationContext().getContentResolver(),
            getActivity().getApplicationContext());
    // define Tasks.Presenter
    mPresenter = new TasksPresenter(loader, getActivity().getSupportLoaderManager(),
        this, dataSource);
  }

  @Override
  public void onResume() {
    super.onResume();
    mPresenter.loadTasks();
    setupRecyclerLayoutAnim();
  }

  /**
   * Helper Method to load Layout anim to recyclerView items.
   */
  private void setupRecyclerLayoutAnim() {
    //Setup Layout Animation for RecyclerView
    LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(
        getContext(), R.anim.layout_animation_from_right
    );
    mRecyclerView.setLayoutAnimation(animationController);
  }

  @Override
  public void onPause() {
    super.onPause();
    mPresenter.removeLoader();
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.tasks_list_menu, menu);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      Bundle savedInstanceState) {
    setHasOptionsMenu(true);
    View rootView = inflater.inflate(R.layout.frag_tasks_list, container, false);
    assert getContext() != null;
    assert getActivity() != null;
    assert container != null;
    Context context = container.getContext();
    //Bind fragment layout elements
    ButterKnife.bind(this, rootView);
    // get ref of empty view lives into Activity.
    mEmptyView = getActivity().findViewById(R.id.empty_view);
    mAdapter = new TaskListAdapter(null, getContext());
    mAdapter.setOnClickListener(onItemClickListener);

    // Set RecyclerView Adapter
    mRecyclerView.setAdapter(mAdapter);
    LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL,
        false);
    mRecyclerView.setLayoutManager(manager);
    mRecyclerView.setHasFixedSize(true);
    // ref of Activity menu_fab
    fabSpeedDial = getActivity()
        .findViewById(R.id.tasks_list_fab);
    fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
      @Override
      public boolean onMenuItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
          case R.id.action_note:
            mPresenter.loadAddEditActivity(NO_TASK_ID, TaskEntry.TYPE_NORMAL_NOTE);
            break;
          case R.id.action_todo:
            mPresenter.loadAddEditActivity(NO_TASK_ID, TaskEntry.TYPE_TODO_NOTE);
            break;
          case R.id.action_mic:
            mPresenter.loadAddEditActivity(NO_TASK_ID, TaskEntry.TYPE_AUDIO_NOTE);
            break;
        }
        return true;
      }
    });

    /*this part will hide FAB onScroll*/
    mRecyclerView.addOnScrollListener(new OnScrollListener() {
      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (dy > 0) {
          fabSpeedDial.hide();
        } else if (dy < 0) {
          fabSpeedDial.show();
        }
      }
    });

    return rootView;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_settings:
        showSettingsActivity();
        return true;
      case R.id.action_list_signout:
        prepareUserSignOut();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }

  }

  /**
   * This helper method will perform to make sure that user sign-out and the data backed up.
   */
  private void prepareUserSignOut() {
    assert getContext() != null;
    assert getActivity() != null;
    SharedPreferenceHelper.getInstance(getActivity().getApplicationContext()).updateUserUID();
    // Update Home Widget
    WidgetIntentService.startActionChangeList(getActivity().getApplicationContext());
    FirebaseAuth.getInstance().signOut();
    Intent loginIntent = new Intent(getContext(), LoginActivity.class);
    startActivity(loginIntent);
    getActivity().finish();
  }

  @Override
  public void showTasks(Cursor tasks) {
    if (mEmptyView.getVisibility() == View.VISIBLE) {
      mEmptyView.setVisibility(View.INVISIBLE);
    }
    mAdapter.swapCursor(tasks);
  }

  @Override
  public void showNoData() {
    mEmptyView.setVisibility(View.VISIBLE);
  }

  @Override
  public void showDataReset() {
    mAdapter.swapCursor(null);
  }

  @Override
  public void showAddEditUI(long taskID, int taskType) {
    Intent addEditIntent = new Intent(getContext(), AddEditTasks.class);
    // if we have TaskID available that means user clicked on Task and in this case
    // we need to pass uri to Detail Activity to load data.
    if (taskID != NO_TASK_ID) {
      Uri taskUri = ContentUris.withAppendedId(TasksDBContract.TaskEntry.CONTENT_TASK_URI, taskID);
      addEditIntent.putExtra(AddEditTasks.EXTRAS_TASK_TYPE, taskType);
      addEditIntent.setData(taskUri);
      startActivityForResult(addEditIntent, AddEditTasks.REQUEST_EDIT_TASK);
    } else {
      addEditIntent.putExtra(AddEditTasks.EXTRAS_TASK_TYPE, taskType);
      startActivityForResult(addEditIntent, AddEditTasks.REQUEST_ADD_TASK);
    }

  }

  @Override
  public void showDeletedMessage() {
    showMessage(getString(R.string.msg_task_deleted));
  }

  @Override
  public void showUpdatedMessage() {
    showMessage(getString(R.string.msg_task_updated));
  }

  @Override
  public void showAddedMessage() {
    showMessage(getString(R.string.msg_task_added));
  }

  @Override
  public void setLoadingIndicator(boolean state) {
    if (state) {
      mProgressBar.setVisibility(View.VISIBLE);
    } else {
      mProgressBar.setVisibility(View.INVISIBLE);
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    mPresenter.result(requestCode, resultCode);
  }

  @Override
  public void showSettingsActivity() {
    Intent intent = new Intent(getContext(), SettingsActivity.class);
    startActivity(intent);
    assert getActivity() != null;
    // set Navigation Animation
    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
  }

  @Override
  public void showDenyPermissionsMessage() {
    showMessage(getString(R.string.deny_permissions_message));
  }

  @Override
  public void setPresenter(TasksListContract.Presenter presenter) {
    /*to implement in the future*/
  }

  private void showMessage(String message) {
    assert getView() != null;
    Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
  }
}
