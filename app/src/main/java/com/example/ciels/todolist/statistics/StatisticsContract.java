package com.example.ciels.todolist.statistics;

import com.example.ciels.todolist.base.BasePresenter;
import com.example.ciels.todolist.base.BaseView;

/**
 *
 */
public interface StatisticsContract {

    interface View extends BaseView<Presenter> {

        void setProgressIndicator(boolean active);

        void showStatistics(int numberOfIncompleteTasks, int numberOfCompletedTasks);

        void showLoadingStatisticsError();

        boolean isActive();
    }

    interface Presenter extends BasePresenter {

    }
}
