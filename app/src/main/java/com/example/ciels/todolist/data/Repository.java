package com.example.ciels.todolist.data;

/**
 *
 */

public interface Repository<T> {

    boolean add(T element);

    int addAll(Iterable<T> elements);

    boolean remove(T element);

    boolean update(T element);
}
