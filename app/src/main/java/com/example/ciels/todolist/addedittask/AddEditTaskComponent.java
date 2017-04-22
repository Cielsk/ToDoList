package com.example.ciels.todolist.addedittask;

import com.example.ciels.todolist.di.ActivityScope;
import com.example.ciels.todolist.di.AppComponent;
import dagger.Component;

/**
 *
 */
@ActivityScope
@Component(dependencies = AppComponent.class, modules = AddEditTaskPresenterModule.class)
public interface AddEditTaskComponent {

    void inject(AddEditTaskActivity activity);
}
