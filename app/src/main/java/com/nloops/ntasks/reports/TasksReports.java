package com.nloops.ntasks.reports;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import com.nloops.ntasks.R;

public class TasksReports extends AppCompatActivity {

  BottomNavigationView bottomNavigationView;
  WeeklyTasksReport weeklyTasksReport;

  //Fragments
  ActiveDaysReport activeDaysReport;
  MenuItem prevMenuItem;
  //This is our viewPager
  private ViewPager viewPager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_tasks_reports);
    //Initializing viewPager
    viewPager = findViewById(R.id.viewpager);

    //Initializing the bottomNavigationView
    bottomNavigationView = findViewById(R.id.bottom_navigation);

    bottomNavigationView.setOnNavigationItemSelectedListener(
        new BottomNavigationView.OnNavigationItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
              case R.id.action_call:
                viewPager.setCurrentItem(0);
                break;
              case R.id.action_chat:
                viewPager.setCurrentItem(1);
                break;
            }
            return false;
          }
        });

    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      @Override
      public void onPageSelected(int position) {
        if (prevMenuItem != null) {
          prevMenuItem.setChecked(false);
        } else {
          bottomNavigationView.getMenu().getItem(0).setChecked(false);
        }
        Log.d("page", "onPageSelected: " + position);
        bottomNavigationView.getMenu().getItem(position).setChecked(true);
        prevMenuItem = bottomNavigationView.getMenu().getItem(position);

      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });

    setupViewPager(viewPager);

  }


  private void setupViewPager(ViewPager viewPager) {
    TasksTabsAdapter adapter = new TasksTabsAdapter(getSupportFragmentManager());
    weeklyTasksReport = new WeeklyTasksReport();
    activeDaysReport = new ActiveDaysReport();
    adapter.addFragment(weeklyTasksReport);
    adapter.addFragment(activeDaysReport);
    viewPager.setAdapter(adapter);
  }
}