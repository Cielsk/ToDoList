package com.example.ciels.todolist;

/**
 * @author Ciel
 */
public interface BaseView<T extends BasePresenter> {

    void setPresenter(T presenter);
}
