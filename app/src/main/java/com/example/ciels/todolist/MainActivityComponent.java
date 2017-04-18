package com.example.ciels.todolist;

import android.app.Activity;
import android.content.Context;
import com.example.ciels.todolist.di.ActivityContext;
import com.example.ciels.todolist.di.ActivityModule;
import com.example.ciels.todolist.di.ActivityScope;
import com.example.ciels.todolist.di.AppComponent;
import dagger.Component;

/**
 *
 */
@ActivityScope
@Component(dependencies = AppComponent.class, modules = ActivityModule.class)
public interface MainActivityComponent {

    void inject(MainActivity activity);

    @ActivityContext
    Context getContext();

    Activity getActivity();
}
