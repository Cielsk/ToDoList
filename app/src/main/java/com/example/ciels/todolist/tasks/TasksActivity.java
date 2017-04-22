package com.example.ciels.todolist.tasks;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.example.ciels.todolist.R;
import com.example.ciels.todolist.base.ToDoApplication;
import com.example.ciels.todolist.statistics.StatisticsActivity;
import com.example.ciels.todolist.util.ActivityUtils;
import javax.inject.Inject;

public class TasksActivity extends AppCompatActivity {

    private static final String CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY";

    @Inject TasksPresenter mTasksPresenter;

    @BindView(R.id.toolbar) Toolbar mToolbar;

    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasks_act);
        ButterKnife.bind(this);

        // Set up the toolbar
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        // Set up the navigation drawer.
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        // Set up the fragment
        TasksFragment tasksFragment
            = (TasksFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (tasksFragment == null) {
            // Create the fragment
            tasksFragment = TasksFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                                                tasksFragment,
                                                R.id.contentFrame);
        }

        // Create the presenter
        DaggerTasksComponent
            .builder()
            .appComponent(ToDoApplication.getInstance(this).getAppComponent())
            .tasksPresenterModule(new TasksPresenterModule(tasksFragment))
            .build()
            .inject(this);

        // Load previously saved state, if available.
        if (savedInstanceState != null) {
            TasksFilterType currentFiltering = (TasksFilterType) savedInstanceState.getSerializable(
                CURRENT_FILTERING_KEY);
            mTasksPresenter.setFiltering(currentFiltering);
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.list_navigation_menu_item:
                    // Do nothing, we're already on that screen
                    break;
                case R.id.statistics_navigation_menu_item:
                    Intent intent = new Intent(TasksActivity.this, StatisticsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
            // Close the navigation drawer when an item is selected.
            menuItem.setChecked(true);
            mDrawerLayout.closeDrawers();
            return true;
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(CURRENT_FILTERING_KEY, mTasksPresenter.getFiltering());

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Open the navigation drawer when the home icon is selected from the toolbar.
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
