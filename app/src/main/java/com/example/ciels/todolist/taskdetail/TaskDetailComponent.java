package com.example.ciels.todolist.taskdetail;

import com.example.ciels.todolist.di.ActivityScope;
import com.example.ciels.todolist.di.AppComponent;
import dagger.Component;

/**
 *
 */
@ActivityScope
@Component(dependencies = AppComponent.class, modules = TaskDetailPresenterModule.class)
public interface TaskDetailComponent {

    void inject(TaskDetailActivity activity);
}
