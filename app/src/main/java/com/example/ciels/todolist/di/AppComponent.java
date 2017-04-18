package com.example.ciels.todolist.di;

import android.content.Context;
import com.example.ciels.todolist.base.ToDoApplication;
import dagger.Component;
import javax.inject.Singleton;

/**
 *
 */
@Singleton
@Component(modules = { AppModule.class })
public interface AppComponent {

    void inject(ToDoApplication application);

    ToDoApplication getApplication();

    @ApplicationContext
    Context getContext();
}
