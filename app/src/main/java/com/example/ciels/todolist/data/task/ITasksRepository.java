package com.example.ciels.todolist.data.task;

import android.support.annotation.NonNull;
import com.example.ciels.todolist.data.Repository;

/**
 *
 */
public interface ITasksRepository extends Repository<Task> {

    void completeTask(@NonNull Task task);

    void completeTask(@NonNull String id);

    void activateTask(@NonNull Task task);

    void activateTask(@NonNull String id);

    void clearCompletedTasks();

    void refreshTasks();
}
