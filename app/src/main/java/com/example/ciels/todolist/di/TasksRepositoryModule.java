package com.example.ciels.todolist.di;

import com.example.ciels.todolist.data.database.DbHelper;
import com.example.ciels.todolist.data.task.ITasksRepository;
import com.example.ciels.todolist.data.task.local.LocalITasksReposotory;
import com.example.ciels.todolist.data.task.remote.RemoteITasksRepository;
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
        return new LocalITasksReposotory(dbHelper);
    }

    @Provides
    @Singleton
    @Remote
    ITasksRepository provideRemoteTasksRepository() {
        return new RemoteITasksRepository();
    }
}
