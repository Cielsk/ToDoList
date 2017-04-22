package com.example.ciels.todolist.data.task;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import com.example.ciels.todolist.di.Local;
import com.example.ciels.todolist.di.Remote;
import io.reactivex.Observable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 *
 */
@Singleton
public class TasksRepository implements ITasksRepository {

    private final ITasksRepository mLocalTasksRepository;

    private final ITasksRepository mRemoteTasksRepository;

    /**
     * This variable has package local visibility so it can be accessed from tests.
     */
    @VisibleForTesting @Nullable Map<String, Task> mCachedTasks;

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    @VisibleForTesting boolean mCacheIsDirty = false;

    @Inject
    public TasksRepository(@Local ITasksRepository localTasksRepository,
                           @Remote ITasksRepository remoteTasksRepository) {
        mLocalTasksRepository = localTasksRepository;
        mRemoteTasksRepository = remoteTasksRepository;
    }

    @Override
    public void add(@NonNull Task element) {
        mLocalTasksRepository.add(element);
        mRemoteTasksRepository.add(element);

        // Do in memory cache update to keep the app UI up to date
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(element.getId(), element);
    }

    @Override
    public void remove(@NonNull Task element) {
        mLocalTasksRepository.remove(element);
        mRemoteTasksRepository.remove(element);
        if (mCachedTasks != null) {
            mCachedTasks.remove(element.getId(), element);
        }
    }

    @Override
    public void remove(@NonNull String id) {
        mLocalTasksRepository.remove(id);
        mRemoteTasksRepository.remove(id);
        if (mCachedTasks != null) {
            mCachedTasks.remove(id);
        }
    }

    @Override
    public void clear() {
        mLocalTasksRepository.clear();
        mRemoteTasksRepository.clear();
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.clear();
    }

    @Override
    public void update(@NonNull Task element) {
        mLocalTasksRepository.update(element);
        mRemoteTasksRepository.update(element);
        // Do in memory cache update to keep the app UI up to date
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(element.getId(), element);
    }

    @Override
    public Observable<Task> getById(@NonNull final String id) {
        if (mCachedTasks != null) {
            return Observable.just(getTaskWithId(id));
        }

        mCachedTasks = new LinkedHashMap<>();

        Observable<Task> localTask = mLocalTasksRepository
            .getById(id).doOnNext(task -> mCachedTasks.put(id, task)).firstOrError().toObservable();

        Observable<Task> remoteTask = mRemoteTasksRepository.getById(id).doOnNext(task -> {
            mLocalTasksRepository.add(task);
            mCachedTasks.put(task.getId(), task);
        });

        return Observable.concat(localTask, remoteTask).firstElement().map(task -> {
            if (task == null) {
                throw new NoSuchElementException("No task found with taskId " + id);
            }
            return task;
        }).toObservable();
    }

    @Override
    public Observable<List<Task>> getAll() {
        if (mCachedTasks != null && !mCacheIsDirty) {
            return Observable.fromIterable(mCachedTasks.values()).toList().toObservable();
        }

        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }

        Observable<List<Task>> remoteTasks = mRemoteTasksRepository
            .getAll()
            .flatMap(tasks -> Observable.fromIterable(tasks).doOnNext(task -> {
                mCachedTasks.put(task.getId(), task);
                mLocalTasksRepository.add(task);
            }).toList().toObservable())
            .doOnComplete(() -> mCacheIsDirty = false);

        if (mCacheIsDirty) {
            return remoteTasks;
        } else {
            Observable<List<Task>> localTasks = mLocalTasksRepository
                .getAll()
                .flatMap(tasks -> Observable
                    .fromIterable(tasks)
                    .doOnNext(task -> mCachedTasks.put(task.getId(), task))
                    .toList()
                    .toObservable());

            return Observable
                .concat(localTasks, remoteTasks)
                .filter(tasks -> !tasks.isEmpty())
                .firstOrError()
                .toObservable();
        }
    }

    private Task getTaskWithId(@NonNull final String id) {
        if (mCachedTasks == null || mCachedTasks.isEmpty()) {
            return null;
        } else {
            return mCachedTasks.get(id);
        }
    }

    @Override
    public void completeTask(@NonNull Task task) {
        mLocalTasksRepository.completeTask(task);
        mRemoteTasksRepository.completeTask(task);

        Task completedTask = new Task(task.getTitle(), task.getDescription(), task.getId(), true);

        // Do in memory cache update to keep the app UI up to date
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(task.getId(), completedTask);
    }

    @Override
    public void completeTask(@NonNull String id) {
        Task taskWithId = getTaskWithId(id);
        if (taskWithId != null) {
            completeTask(taskWithId);
        }
    }

    @Override
    public void activateTask(@NonNull Task task) {
        mLocalTasksRepository.activateTask(task);
        mRemoteTasksRepository.activateTask(task);

        Task completedTask = new Task(task.getTitle(), task.getDescription(), task.getId(), false);

        // Do in memory cache update to keep the app UI up to date
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(task.getId(), completedTask);
    }

    @Override
    public void activateTask(@NonNull String id) {
        Task taskWithId = getTaskWithId(id);
        if (taskWithId != null) {
            activateTask(taskWithId);
        }
    }

    @Override
    public void clearCompletedTasks() {
        mLocalTasksRepository.clearCompletedTasks();
        mRemoteTasksRepository.clearCompletedTasks();

        // Do in memory cache update to keep the app UI up to date
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        Iterator<Map.Entry<String, Task>> it = mCachedTasks.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Task> entry = it.next();
            if (entry.getValue().isCompleted()) {
                it.remove();
            }
        }
    }

    @Override
    public void refreshTasks() {
        mCacheIsDirty = true;
    }
}
