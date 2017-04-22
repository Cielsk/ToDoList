package com.example.ciels.todolist.statistics;

import com.example.ciels.todolist.di.ActivityScope;
import com.example.ciels.todolist.di.AppComponent;
import dagger.Component;

/**
 *
 */
@ActivityScope
@Component(dependencies = AppComponent.class, modules = StatisticsPresenterModule.class)
public interface StatisticsComponent {

    void inject(StatisticsActivity activity);
}
