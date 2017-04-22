package com.example.ciels.todolist.tasks;

import dagger.Module;
import dagger.Provides;

/**
 *
 */
@Module
public class TasksPresenterModule {

    private final TasksContract.View mView;

    public TasksPresenterModule(TasksContract.View view) {
        mView = view;
    }

    @Provides
    TasksContract.View provideTasksContractView() {
        return mView;
    }
}
