package com.example.ciels.todolist.di;

import com.example.ciels.todolist.data.database.DbHelper;
import com.example.ciels.todolist.data.task.ITasksRepository;
import com.example.ciels.todolist.data.task.local.LocalTasksRepository;
import com.example.ciels.todolist.data.task.remote.RemoteTasksRepository;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/**
 *
 */
@Module
public class TasksRepositoryModule {

    @Provides
    @Singleton
    @Local
    ITasksRepository provideLocalTasksRepository(DbHelper dbHelper) {
        return new LocalTasksRepository(dbHelper);
    }

    @Provides
    @Singleton
    @Remote
    ITasksRepository provideRemoteTasksRepository() {
        return new RemoteTasksRepository();
    }
}
