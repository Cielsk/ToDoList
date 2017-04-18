package com.example.ciels.todolist.di;

import android.content.Context;
import com.example.ciels.todolist.base.ToDoApplication;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/**
 *
 */
@Module
public class AppModule {

    private final Context mContext;

    public AppModule(Context context) {
        mContext = context;
    }

    @Singleton
    @Provides
    ToDoApplication provideApplication() {
        return (ToDoApplication) mContext.getApplicationContext();
    }

    @ApplicationContext
    @Singleton
    @Provides
    Context provideContext() {
        return mContext;
    }
}
