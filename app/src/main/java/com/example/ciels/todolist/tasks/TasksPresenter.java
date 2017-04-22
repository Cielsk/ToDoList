package com.example.ciels.todolist.tasks;

import android.app.Activity;
import android.support.annotation.NonNull;
import com.example.ciels.todolist.addedittask.AddEditTaskActivity;
import com.example.ciels.todolist.data.task.Task;
import com.example.ciels.todolist.data.task.TasksRepository;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import javax.inject.Inject;

/**
 *
 */
public class TasksPresenter implements TasksContract.Presenter {

    @NonNull private final TasksRepository mTasksRepository;

    @NonNull private final TasksContract.View mTasksView;

    @NonNull private CompositeDisposable mDisposable;

    @NonNull private TasksFilterType mCurrentFiltering = TasksFilterType.ALL_TASKS;

    private boolean mFirstLoad = true;

    @Inject
    public TasksPresenter(@NonNull TasksRepository tasksRepository,
                          @NonNull TasksContract.View tasksView) {
        mTasksRepository = tasksRepository;
        mTasksView = tasksView;

        mDisposable = new CompositeDisposable();
    }

    /**
     * Method injection is used here to safely reference {@code this} after the object is created.
     * For more information, see Java Concurrency in Practice.
     */
    @Inject
    void setupListeners() {
        mTasksView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        loadTasks(false);
    }

    @Override
    public void unsubscribe() {
        mDisposable.clear();
    }

    @Override
    public void result(int requestCode, int resultCode) {
        // If a task was successfully added, show snackbar
        if (AddEditTaskActivity.REQUEST_ADD_TASK == requestCode
            && Activity.RESULT_OK == resultCode) {
            mTasksView.showSuccessfullySavedMessage();
        }
    }

    @Override
    public void loadTasks(boolean forceUpdate) {
        // Simplification for sample: a network reload will be forced on first load.
        loadTasks(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
    }

    private void loadTasks(final boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            mTasksView.setLoadingIndicator(true);
        }
        if (forceUpdate) {
            mTasksRepository.refreshTasks();
        }

        mDisposable.clear();
        Disposable disposable = mTasksRepository
            .getAll()
            .flatMap(tasks -> Observable.fromIterable(tasks))
            .filter(task -> {
                switch (mCurrentFiltering) {
                    case ACTIVE_TASKS:
                        return task.isActive();
                    case COMPLETED_TASKS:
                        return task.isCompleted();
                    case ALL_TASKS:
                    default:
                        return true;
                }
            })
            .toList()
            .toObservable()
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                // onNext
                this::processTasks,
                // onError
                throwable -> mTasksView.showLoadingTasksError(),
                // onCompleted
                () -> mTasksView.setLoadingIndicator(false));
        mDisposable.add(disposable);
    }

    @Override
    public void addNewTask() {
        mTasksView.showAddTask();
    }

    @Override
    public void openTaskDetails(@NonNull Task requestedTask) {
        mTasksView.showTaskDetailsUi(requestedTask.getId());
    }

    @Override
    public void completeTask(@NonNull Task completedTask) {
        mTasksRepository.completeTask(completedTask);
        mTasksView.showTaskMarkedComplete();
        loadTasks(false, false);
    }

    @Override
    public void activateTask(@NonNull Task activeTask) {
        mTasksRepository.activateTask(activeTask);
        mTasksView.showTaskMarkedActive();
        loadTasks(false, false);
    }

    @Override
    public void clearCompletedTasks() {
        mTasksRepository.clearCompletedTasks();
        mTasksView.showCompletedTasksCleared();
        loadTasks(false, false);
    }

    @Override
    public TasksFilterType getFiltering() {
        return mCurrentFiltering;
    }

    @Override
    public void setFiltering(TasksFilterType requestType) {
        mCurrentFiltering = requestType;
    }

    private void processTasks(@NonNull List<Task> tasks) {
        if (tasks.isEmpty()) {
            // Show a message indicating there are no tasks for that filter type.
            processEmptyTasks();
        } else {
            // Show the list of tasks
            mTasksView.showTasks(tasks);
            // Set the filter label's text.
            showFilterLabel();
        }
    }

    private void processEmptyTasks() {
        switch (mCurrentFiltering) {
            case ACTIVE_TASKS:
                mTasksView.showNoActiveTasks();
                break;
            case COMPLETED_TASKS:
                mTasksView.showNoCompletedTasks();
                break;
            default:
                mTasksView.showNoTasks();
                break;
        }
    }

    private void showFilterLabel() {
        switch (mCurrentFiltering) {
            case ACTIVE_TASKS:
                mTasksView.showActiveFilterLabel();
                break;
            case COMPLETED_TASKS:
                mTasksView.showCompletedFilterLabel();
                break;
            default:
                mTasksView.showAllFilterLabel();
                break;
        }
    }
}
