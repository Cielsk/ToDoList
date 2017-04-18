package com.example.ciels.todolist.util;

import java.util.Arrays;

/**
 *
 */
public final class ObjectsUtils {

    private ObjectsUtils() {

    }

    public static boolean equal(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }

    public static int hash(Object... objects) {
        return Arrays.hashCode(objects);
    }
}
