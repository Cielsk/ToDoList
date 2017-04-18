package com.example.ciels.todolist.base;

import android.app.Application;
import android.content.Context;
import com.example.ciels.todolist.di.AppComponent;
import com.example.ciels.todolist.di.AppModule;
import com.example.ciels.todolist.di.DaggerAppComponent;

/**
 *
 */

public class ToDoApplication extends Application {

    protected AppComponent mAppComponent;

    public static ToDoApplication getInstance(Context context) {
        return (ToDoApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mAppComponent = DaggerAppComponent.builder()
                                          .appModule(new AppModule(this))
                                          .build();
        mAppComponent.inject(this);
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}
