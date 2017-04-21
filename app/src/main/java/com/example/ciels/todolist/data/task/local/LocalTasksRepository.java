package com.example.ciels.todolist.data.task.local;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.example.ciels.todolist.config.TaskDbTableContract.TaskEntry;
import com.example.ciels.todolist.data.database.DbHelper;
import com.example.ciels.todolist.data.task.ITasksRepository;
import com.example.ciels.todolist.data.task.Task;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.Observable;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 *
 */
@Singleton
public class LocalTasksRepository implements ITasksRepository {

    @NonNull private final BriteDatabase mDatabase;

    @NonNull private final Func1<Cursor, Task> mTaskMapperFunction;

    private final String[] All_COLUMNS_PROJECTION = {
        TaskEntry.COLUMN_NAME_ENTRY_ID,
        TaskEntry.COLUMN_NAME_TITLE,
        TaskEntry.COLUMN_NAME_DESCRIPTION,
        TaskEntry.COLUMN_NAME_COMPLETED
    };
    private final String ID_SELECTION = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";

    @Inject
    public LocalTasksRepository(@NonNull DbHelper dbHelper) {
        SqlBrite sqlBrite = new SqlBrite.Builder().build();
        mDatabase = sqlBrite.wrapDatabaseHelper(dbHelper, Schedulers.io());
        mTaskMapperFunction = this::getByCursor;
    }

    private Task getByCursor(@NonNull Cursor cursor) {
        String itemId
            = cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_ENTRY_ID));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_TITLE));
        String description
            = cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_DESCRIPTION));
        boolean completed
            = cursor.getInt(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_COMPLETED)) == 1;
        return new Task(title, description, itemId, completed);
    }

    @Override
    public void add(@NonNull Task element) {
        ContentValues values = getContentValues(element);
        mDatabase.insert(TaskEntry.TABLE_NAME, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    @NonNull
    private ContentValues getContentValues(@NonNull Task element) {
        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_NAME_ENTRY_ID, element.getId());
        values.put(TaskEntry.COLUMN_NAME_TITLE, element.getTitle());
        values.put(TaskEntry.COLUMN_NAME_DESCRIPTION, element.getDescription());
        values.put(TaskEntry.COLUMN_NAME_COMPLETED, element.isCompleted());
        return values;
    }

    @Override
    public void remove(@NonNull Task element) {
        String selection = TaskEntry.COLUMN_NAME_ENTRY_ID
                           + " LIKE ? AND "
                           + TaskEntry.COLUMN_NAME_TITLE
                           + " LIKE ? AND "
                           + TaskEntry.COLUMN_NAME_DESCRIPTION
                           + " LIKE ? AND "
                           + TaskEntry.COLUMN_NAME_COMPLETED
                           + " LIKE ?";
        String[] selectionArgs = {
            element.getId(),
            element.getTitle(),
            element.getDescription(),
            element.isCompleted() ? "1" : "0"
        };

        mDatabase.delete(TaskEntry.TABLE_NAME, selection, selectionArgs);
    }

    @Override
    public void remove(@NonNull String id) {
        String[] selectionArgs = { id };
        mDatabase.delete(TaskEntry.TABLE_NAME, ID_SELECTION, selectionArgs);
    }

    @Override
    public void clear() {
        mDatabase.delete(TaskEntry.TABLE_NAME, null);
    }

    @Override
    public void update(@NonNull Task element) {
        ContentValues values = getContentValues(element);

        String[] selectionArgs = { element.getId() };
        mDatabase.update(TaskEntry.TABLE_NAME, values, ID_SELECTION, selectionArgs);
    }

    @Override
    public Observable<Task> getById(@NonNull String id) {
        String sql = String.format("SELECT %s FROM %s WHERE %s LIKE ?",
                                   TextUtils.join(",", All_COLUMNS_PROJECTION),
                                   TaskEntry.TABLE_NAME,
                                   TaskEntry.COLUMN_NAME_ENTRY_ID);

        return RxJavaInterop.toV2Observable(mDatabase
                                                .createQuery(TaskEntry.TABLE_NAME, sql, id)
                                                .mapToOneOrDefault(mTaskMapperFunction, null));
    }

    @Override
    public Observable<List<Task>> getAll() {
        String sql = String.format("SELECT %s FROM %s",
                                   TextUtils.join(",", All_COLUMNS_PROJECTION),
                                   TaskEntry.TABLE_NAME);

        return RxJavaInterop.toV2Observable(mDatabase
                                                .createQuery(TaskEntry.TABLE_NAME, sql)
                                                .mapToList(mTaskMapperFunction));
    }

    @Override
    public void completeTask(@NonNull Task task) {
        completeTask(task.getId());
    }

    @Override
    public void completeTask(@NonNull String id) {
        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_NAME_COMPLETED, true);

        String[] selectionArgs = { id };
        mDatabase.update(TaskEntry.TABLE_NAME, values, ID_SELECTION, selectionArgs);
    }

    @Override
    public void activateTask(@NonNull Task task) {
        activateTask(task.getId());
    }

    @Override
    public void activateTask(@NonNull String id) {
        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_NAME_COMPLETED, false);

        String[] selectionArgs = { id };
        mDatabase.update(TaskEntry.TABLE_NAME, values, ID_SELECTION, selectionArgs);
    }

    @Override
    public void clearCompletedTasks() {
        String selection = TaskEntry.COLUMN_NAME_COMPLETED + " LIKE ?";
        String[] selectionArgs = { "1" };
        mDatabase.delete(TaskEntry.TABLE_NAME, selection, selectionArgs);
    }

    @Override
    public void refreshTasks() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }
}
