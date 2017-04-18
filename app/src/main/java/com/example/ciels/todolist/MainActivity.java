package com.example.ciels.todolist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.example.ciels.todolist.base.ToDoApplication;
import com.example.ciels.todolist.data.task.TasksRepository;
import com.example.ciels.todolist.di.ActivityModule;
import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.text) TextView mText;

    @Inject TasksRepository mTasksRepository;
    @Inject TasksRepository mTasksRepository1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        DaggerMainActivityComponent.builder()
                                   .appComponent(ToDoApplication.getInstance(this)
                                                                .getAppComponent())
                                   .activityModule(new ActivityModule(this))
                                   .build()
                                   .inject(this);
        if (mTasksRepository == mTasksRepository1) {
            mText.setText("Singleton test pass.");
        }
    }
}
