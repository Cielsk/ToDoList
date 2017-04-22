package com.example.ciels.todolist.addedittask;

import com.example.ciels.todolist.base.BasePresenter;
import com.example.ciels.todolist.base.BaseView;

/**
 *
 */
public interface AddEditTaskContract {

    interface View extends BaseView<Presenter> {

        void showEmptyTaskError();

        void showTasksList();

        void setTitle(String title);

        void setDescription(String description);

        boolean isActive();
    }

    interface Presenter extends BasePresenter {

        void saveTask(String title, String description);

        void populateTask();
    }
}
