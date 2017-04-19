package com.example.ciels.todolist.data.task;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.example.ciels.todolist.util.ObjectsUtils;
import java.util.UUID;

/**
 *
 */
public final class Task {

    @NonNull private final String mId;

    @Nullable private final String mTitle;

    @Nullable private final String mDescription;

    private final boolean mCompleted;

    /**
     * Use this constructor to create a new active Task.
     *
     * @param title title of the task
     * @param description description of the task
     */
    public Task(@Nullable String title, @Nullable String description) {
        this(title, description, UUID.randomUUID().toString(), false);
    }

    /**
     * Use this constructor to specify a completed Task if the Task already has an id (copy of
     * another Task).
     *
     * @param title title of the task
     * @param description description of the task
     * @param id id of the task
     * @param completed true if the task is completed, false if it's active
     */
    public Task(@Nullable String title, @Nullable String description, @NonNull String id,
                boolean completed) {
        mId = id;
        mTitle = title;
        mDescription = description;
        mCompleted = completed;
    }

    /**
     * Use this constructor to create an active Task if the Task already has an id (copy of another
     * Task).
     *
     * @param title title of the task
     * @param description description of the task
     * @param id id of the task
     */
    public Task(@Nullable String title, @Nullable String description, @NonNull String id) {
        this(title, description, id, false);
    }

    /**
     * Use this constructor to create a new completed Task.
     *
     * @param title title of the task
     * @param description description of the task
     * @param completed true if the task is completed, false if it's active
     */
    public Task(@Nullable String title, @Nullable String description, boolean completed) {
        this(title, description, UUID.randomUUID().toString(), completed);
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @Nullable
    public String getTitle() {
        return mTitle;
    }

    @Nullable
    public String getTitleForList() {
        if (!TextUtils.isEmpty(mTitle)) {
            return mTitle;
        } else {
            return mDescription;
        }
    }

    @Nullable
    public String getDescription() {
        return mDescription;
    }

    public boolean isCompleted() {
        return mCompleted;
    }

    public boolean isActive() {
        return !mCompleted;
    }

    public boolean isEmpty() {
        return TextUtils.isEmpty(mTitle) && TextUtils.isEmpty(mDescription);
    }

    @Override
    public int hashCode() {
        return ObjectsUtils.hash(mId, mTitle, mDescription);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return ObjectsUtils.equal(mId, task.mId)
               && ObjectsUtils.equal(mTitle, task.mTitle)
               && ObjectsUtils.equal(mDescription, task.mDescription);
    }

    @Override
    public String toString() {
        return "Task with title " + mTitle;
    }
}
