package com.example.ciels.todolist.base;

/**
 * @author Ciel
 */
public interface BaseView<T extends BasePresenter> {

    void setPresenter(T presenter);
}
