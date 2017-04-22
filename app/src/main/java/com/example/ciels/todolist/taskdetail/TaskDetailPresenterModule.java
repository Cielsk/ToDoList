package com.example.ciels.todolist.taskdetail;

import dagger.Module;
import dagger.Provides;

/**
 *
 */
@Module
public class TaskDetailPresenterModule {

    private final TaskDetailContract.View mView;

    private final String mTaskId;

    public TaskDetailPresenterModule(TaskDetailContract.View view, String taskId) {
        mView = view;
        mTaskId = taskId;
    }

    @Provides
    TaskDetailContract.View provideTaskDetailContractView() {
        return mView;
    }

    @Provides
    String provideTaskId() {
        return mTaskId;
    }
}
