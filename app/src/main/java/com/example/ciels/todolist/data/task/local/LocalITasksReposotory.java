package com.example.ciels.todolist.data.task.local;

import android.support.annotation.NonNull;
import com.example.ciels.todolist.data.database.DbHelper;
import com.example.ciels.todolist.data.task.Task;
import com.example.ciels.todolist.data.task.ITasksRepository;
import io.reactivex.Observable;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 *
 */
@Singleton
public final class LocalITasksReposotory implements ITasksRepository {

    private final DbHelper mDbHelper;

    @Inject
    public LocalITasksReposotory(DbHelper dbHelper) {
        mDbHelper = dbHelper;
    }

    @Override
    public void add(@NonNull Task element) {

    }

    @Override
    public void remove(@NonNull Task element) {

    }

    @Override
    public void remove(@NonNull String id) {

    }

    @Override
    public void clear() {

    }

    @Override
    public void update(@NonNull Task element) {

    }

    @Override
    public Observable<Task> getById(@NonNull String id) {
        return null;
    }

    @Override
    public Observable<List<Task>> getAll() {
        return null;
    }

    @Override
    public void completeTask(@NonNull Task task) {

    }

    @Override
    public void completeTask(@NonNull String id) {

    }

    @Override
    public void activateTask(@NonNull Task task) {

    }

    @Override
    public void activateTask(@NonNull String id) {

    }

    @Override
    public void clearCompletedTasks() {

    }

    @Override
    public void refreshTasks() {

    }
}
