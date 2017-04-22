package com.example.ciels.todolist.taskdetail;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.example.ciels.todolist.R;
import com.example.ciels.todolist.base.ToDoApplication;
import com.example.ciels.todolist.util.ActivityUtils;
import javax.inject.Inject;

public class TaskDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_ID = "TASK_ID";

    @BindView(R.id.toolbar) Toolbar mToolbar;

    @Inject TaskDetailPresenter mTaskDetailPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taskdetail_act);
        ButterKnife.bind(this);

        // Set up the toolbar.
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);

        // Get the requested task id
        String taskId = getIntent().getStringExtra(EXTRA_TASK_ID);

        TaskDetailFragment taskDetailFragment
            = (TaskDetailFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (taskDetailFragment == null) {
            taskDetailFragment = TaskDetailFragment.newInstance(taskId);

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                                                taskDetailFragment,
                                                R.id.contentFrame);
        }

        // Create the presenter
        DaggerTaskDetailComponent
            .builder()
            .appComponent(ToDoApplication.getInstance(this).getAppComponent())
            .taskDetailPresenterModule(new TaskDetailPresenterModule(taskDetailFragment, taskId))
            .build()
            .inject(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
