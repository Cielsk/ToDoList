package com.example.ciels.todolist.base;

import android.support.annotation.NonNull;

/**
 * @author Ciel
 */
public interface BaseView<T extends BasePresenter> {

    void setPresenter(@NonNull T presenter);
}
