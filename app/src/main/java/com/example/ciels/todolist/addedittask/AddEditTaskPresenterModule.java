package com.example.ciels.todolist.addedittask;

import android.support.annotation.Nullable;
import dagger.Module;
import dagger.Provides;

/**
 *
 */
@Module
public class AddEditTaskPresenterModule {

    private final AddEditTaskContract.View mView;

    private String mTaskId;

    public AddEditTaskPresenterModule(AddEditTaskContract.View view, @Nullable String taskId) {
        mView = view;
        mTaskId = taskId;
    }

    @Provides
    AddEditTaskContract.View provideAddEditTaskContractView() {
        return mView;
    }

    @Provides
    @Nullable
    String provideTaskId() {
        return mTaskId;
    }
}
