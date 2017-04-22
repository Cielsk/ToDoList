package com.example.ciels.todolist.addedittask;

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

public class AddEditTaskActivity extends AppCompatActivity {

    public static final int REQUEST_ADD_TASK = 1;

    public static final String SHOULD_LOAD_DATA_FROM_REPO_KEY = "SHOULD_LOAD_DATA_FROM_REPO_KEY";

    @BindView(R.id.toolbar) Toolbar mToolbar;

    @Inject AddEditTaskPresenter mAddEditTaskPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);
        ButterKnife.bind(this);

        // set up the toolbar
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // set up the fragment
        AddEditTaskFragment addEditTaskFragment
            = (AddEditTaskFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        String taskId = getIntent().getStringExtra(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID);

        if (addEditTaskFragment == null) {
            addEditTaskFragment = AddEditTaskFragment.newInstance();

            if (getIntent().hasExtra(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID)) {
                actionBar.setTitle(R.string.edit_task);
                Bundle bundle = new Bundle();
                bundle.putString(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID, taskId);
                addEditTaskFragment.setArguments(bundle);
            } else {
                actionBar.setTitle(R.string.add_task);
            }

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                                                addEditTaskFragment,
                                                R.id.contentFrame);
        }

        // Create the presenter
        DaggerAddEditTaskComponent
            .builder()
            .addEditTaskPresenterModule(new AddEditTaskPresenterModule(addEditTaskFragment, taskId))
            .appComponent((ToDoApplication.getInstance(this).getAppComponent()))
            .build()
            .inject(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
