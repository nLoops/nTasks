package com.nloops.ntasks.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.RetryStrategy;

public class TasksFirebaseJobService extends JobService {

  private AsyncTask<Void, Void, Void> mTasksData;

  /**
   * The entry point to your Job. Implementations should offload work to another thread of execution
   * as soon as possible. This is called by the Job Dispatcher to tell us we should start our job.
   * Keep in mind this method is run on the application's main thread, so we need to offload work to
   * a background thread.
   */
  @SuppressLint("StaticFieldLeak")
  @Override
  public boolean onStartJob(final JobParameters job) {
    mTasksData = new AsyncTask<Void, Void, Void>() {
      @Override
      protected Void doInBackground(Void... voids) {
        Context context = getApplicationContext();
        CloudSyncTasks.syncData(GeneralUtils.getData(context), context);
        return null;
      }

      @Override
      protected void onPostExecute(Void aVoid) {
        jobFinished(job, false);
      }
    };
    mTasksData.execute();
    return true;
  }


  /**
   * Called when the scheduling engine has decided to interrupt the execution of a running job, most
   * likely because the runtime constraints associated with the job are no longer satisfied.
   *
   * @return whether the job should be retried
   * @see Job.Builder#setRetryStrategy(RetryStrategy)
   * @see RetryStrategy
   */
  @Override
  public boolean onStopJob(JobParameters job) {
    if (mTasksData != null) {
      mTasksData.cancel(true);
    }
    return true;
  }
}
