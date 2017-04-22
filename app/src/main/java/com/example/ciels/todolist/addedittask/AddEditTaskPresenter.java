package com.example.ciels.todolist.addedittask;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.example.ciels.todolist.data.task.Task;
import com.example.ciels.todolist.data.task.TasksRepository;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

/**
 *
 */
public final class AddEditTaskPresenter implements AddEditTaskContract.Presenter {

    @NonNull private final TasksRepository mTasksRepository;

    @NonNull private final AddEditTaskContract.View mAddEditTaskView;

    @Nullable private final String mTaskId;

    @NonNull private CompositeDisposable mDisposable;

    @Inject
    public AddEditTaskPresenter(@NonNull TasksRepository tasksRepository,
                                @NonNull AddEditTaskContract.View addEditTaskView,
                                @Nullable String taskId) {
        mTasksRepository = tasksRepository;
        mAddEditTaskView = addEditTaskView;
        this.mTaskId = taskId;
        mDisposable = new CompositeDisposable();
        mAddEditTaskView.setPresenter(this);
    }

    @Inject
    void setupListeners() {
        mAddEditTaskView.setPresenter(this);
    }

    @Override
    public void saveTask(String title, String description) {
        if (isNewTask()) {
            createTask(title, description);
        } else {
            updateTask(title, description);
        }
    }

    private boolean isNewTask() {
        return mTaskId == null;
    }

    private void createTask(String title, String description) {
        Task newTask = new Task(title, description);
        if (newTask.isEmpty()) {
            mAddEditTaskView.showEmptyTaskError();
        } else {
            mTasksRepository.add(newTask);
            mAddEditTaskView.showTasksList();
        }
    }

    private void updateTask(String title, String description) {
        if (isNewTask()) {
            throw new RuntimeException("updateTask() was called but task is new.");
        }
        mTasksRepository.add(new Task(title, description, mTaskId));
        mAddEditTaskView.showTasksList(); // After an edit, go back to the list.
    }

    @Override
    public void populateTask() {
        if (isNewTask()) {
            throw new RuntimeException("populateTask() was called but task is new.");
        }

        mDisposable.add(mTasksRepository
                            .getById(mTaskId)
                            .subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(task -> {
                                if (mAddEditTaskView.isActive()) {
                                    mAddEditTaskView.setTitle(task.getTitle());
                                    mAddEditTaskView.setDescription(task.getDescription());
                                }
                            }, __ -> {
                                if (mAddEditTaskView.isActive()) {
                                    mAddEditTaskView.showEmptyTaskError();
                                }
                            }));
    }

    @Override
    public void subscribe() {
        if (!isNewTask()) {
            populateTask();
        }
    }

    @Override
    public void unsubscribe() {
        mDisposable.clear();
    }
}
