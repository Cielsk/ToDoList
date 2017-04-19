package com.example.ciels.todolist.data.task.remote;

import android.support.annotation.NonNull;
import com.example.ciels.todolist.data.task.ITasksRepository;
import com.example.ciels.todolist.data.task.Task;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

/**
 *
 */
@Singleton
public class RemoteTasksRepository implements ITasksRepository {

    private static final int SERVICE_LATENCY_IN_MILLIS = 5000;

    private final static Map<String, Task> TASKS_SERVICE_DATA;

    static {
        TASKS_SERVICE_DATA = new LinkedHashMap<>(2);
        addTask("Build tower in Pisa", "Ground looks good, no foundation work required.");
        addTask("Finish bridge in Tacoma", "Found awesome girders at half the cost!");
    }

    @Inject
    public RemoteTasksRepository() {
    }

    private static void addTask(String title, String description) {
        Task newTask = new Task(title, description);
        TASKS_SERVICE_DATA.put(newTask.getId(), newTask);
    }

    @Override
    public void add(@NonNull Task element) {
        TASKS_SERVICE_DATA.put(element.getId(), element);
    }

    @Override
    public void remove(@NonNull Task element) {
        TASKS_SERVICE_DATA.remove(element.getId(), element);
    }

    @Override
    public void remove(@NonNull String id) {
        TASKS_SERVICE_DATA.remove(id);
    }

    @Override
    public void clear() {
        TASKS_SERVICE_DATA.clear();
    }

    @Override
    public void update(@NonNull Task element) {
        TASKS_SERVICE_DATA.put(element.getId(), element);
    }

    @Override
    public Observable<Task> getById(@NonNull String id) {
        final Task task = TASKS_SERVICE_DATA.get(id);
        if (task != null) {
            return Observable.just(task).delay(SERVICE_LATENCY_IN_MILLIS, TimeUnit.MILLISECONDS);
        } else {
            return Observable.empty();
        }
    }

    @Override
    public Observable<List<Task>> getAll() {
        return Observable
            .from(TASKS_SERVICE_DATA.values())
            .delay(SERVICE_LATENCY_IN_MILLIS, TimeUnit.MILLISECONDS)
            .toList();
    }

    @Override
    public void completeTask(@NonNull Task task) {
        Task completedTask = new Task(task.getTitle(), task.getDescription(), task.getId(), true);
        TASKS_SERVICE_DATA.put(task.getId(), completedTask);
    }

    @Override
    public void completeTask(@NonNull String id) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    @Override
    public void activateTask(@NonNull Task task) {
        Task activeTask = new Task(task.getTitle(), task.getDescription(), task.getId(), false);
        TASKS_SERVICE_DATA.put(task.getId(), activeTask);
    }

    @Override
    public void activateTask(@NonNull String id) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    @Override
    public void clearCompletedTasks() {
        Iterator<Map.Entry<String, Task>> it = TASKS_SERVICE_DATA.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Task> entry = it.next();
            if (entry.getValue().isCompleted()) {
                it.remove();
            }
        }
    }

    @Override
    public void refreshTasks() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }
}
