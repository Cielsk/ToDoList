package com.example.ciels.todolist.tasks;

import com.example.ciels.todolist.di.ActivityScope;
import com.example.ciels.todolist.di.AppComponent;
import dagger.Component;

/**
 *
 */
@ActivityScope
@Component(dependencies = AppComponent.class, modules = TasksPresenterModule.class)
public interface TasksComponent {

    void inject(TasksActivity activity);
}
