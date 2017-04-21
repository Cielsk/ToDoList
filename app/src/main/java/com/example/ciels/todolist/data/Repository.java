package com.example.ciels.todolist.data;

import android.support.annotation.NonNull;
import io.reactivex.Observable;
import java.util.List;

/**
 *
 */

public interface Repository<T> {

    void add(@NonNull T element);

    void remove(@NonNull T element);

    void remove(@NonNull String id);

    void clear();

    void update(@NonNull T element);

    Observable<T> getById(@NonNull String id);

    Observable<List<T>> getAll();
}
