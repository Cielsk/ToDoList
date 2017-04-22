package com.example.ciels.todolist.statistics;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import com.example.ciels.todolist.data.task.Task;
import com.example.ciels.todolist.data.task.TasksRepository;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

/**
 *
 */
public class StatisticsPresenter implements StatisticsContract.Presenter {

    @NonNull private final TasksRepository mTasksRepository;

    @NonNull private final StatisticsContract.View mStatisticsView;

    @NonNull private CompositeDisposable mDisposable;

    @Inject
    public StatisticsPresenter(@NonNull TasksRepository tasksRepository,
                               @NonNull StatisticsContract.View statisticsView) {
        mTasksRepository = tasksRepository;
        mStatisticsView = statisticsView;

        mDisposable = new CompositeDisposable();
    }

    /**
     * Method injection is used here to safely reference {@code this} after the object is created.
     * For more information, see Java Concurrency in Practice.
     */
    @Inject
    void setupListeners() {
        mStatisticsView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        loadStatistics();
    }

    private void loadStatistics() {
        mStatisticsView.setProgressIndicator(true);

        Observable<Task> tasks = mTasksRepository.getAll().flatMap(Observable::fromIterable);

        Observable<Integer> completedTasks = tasks
            .filter(Task::isCompleted)
            .count()
            .map(Long::intValue)
            .toObservable();

        Observable<Integer> activeTasks = tasks
            .filter(Task::isActive)
            .count()
            .map(Long::intValue)
            .toObservable();

        Disposable disposable = Observable
            .zip(completedTasks, activeTasks, (completed, active) -> Pair.create(active, completed))
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                // onNext
                stats -> mStatisticsView.showStatistics(stats.first, stats.second),
                // onError
                throwable -> mStatisticsView.showLoadingStatisticsError(),
                // onCompleted
                () -> mStatisticsView.setProgressIndicator(false));
        mDisposable.add(disposable);
    }

    @Override
    public void unsubscribe() {
        mDisposable.clear();
    }
}
