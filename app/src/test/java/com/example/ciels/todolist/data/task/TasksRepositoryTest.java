package com.example.ciels.todolist.data.task;

import android.content.Context;
import com.example.ciels.todolist.data.database.DbHelper;
import com.example.ciels.todolist.data.task.local.LocalTasksRepository;
import com.example.ciels.todolist.data.task.remote.RemoteTasksRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 */
public class TasksRepositoryTest {

    private final static String TASK_TITLE = "title";

    private final static String TASK_TITLE2 = "title2";

    private final static String TASK_TITLE3 = "title3";

    private static List<Task> TASKS = new ArrayList<>();

    static {
        TASKS.add(new Task("Title1", "Description1"));
        TASKS.add(new Task("Title2", "Description2"));
    }

    private TasksRepository mTasksRepository;

    private TestSubscriber<List<Task>> mTasksTestSubscriber;

    @Mock private RemoteTasksRepository mTasksRemoteDataSource;

    @Mock private LocalTasksRepository mTasksLocalDataSource;

    @Mock private Context mContext;

    @Mock private DbHelper mDbHelper;

    @Before
    public void setupTasksRepository() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test
        mTasksRepository = new TasksRepository(mTasksLocalDataSource, mTasksRemoteDataSource);

        mTasksTestSubscriber = new TestSubscriber<>();
    }

    @Test
    public void getAll_repositoryCachesAfterFirstSubscription_whenTasksAvailableInLocalStorage() {
        // Given that the local data source has data available
        setTasksAvailable(mTasksLocalDataSource, TASKS);
        // And the remote data source does not have any data available
        setTasksNotAvailable(mTasksRemoteDataSource);

        // When two subscriptions are set
        TestSubscriber<List<Task>> testSubscriber1 = new TestSubscriber<>();
        mTasksRepository.getAll().subscribe(testSubscriber1);

        TestSubscriber<List<Task>> testSubscriber2 = new TestSubscriber<>();
        mTasksRepository.getAll().subscribe(testSubscriber2);

        // Then tasks were only requested once from remote and local sources
        verify(mTasksRemoteDataSource).getAll();
        verify(mTasksLocalDataSource).getAll();

        assertFalse(mTasksRepository.mCacheIsDirty);
        testSubscriber1.assertValue(TASKS);
        testSubscriber2.assertValue(TASKS);
    }

    private void setTasksNotAvailable(ITasksRepository dataSource) {
        when(dataSource.getAll()).thenReturn(Observable.just(Collections.emptyList()));
    }

    private void setTasksAvailable(ITasksRepository dataSource, List<Task> tasks) {
        // don't allow the data sources to complete.
        when(dataSource.getAll()).thenReturn(Observable.just(tasks).concatWith(Observable.never()));
    }

    @Test
    public void getAll_repositoryCachesAfterFirstSubscription_whenTasksAvailableInRemoteStorage() {
        // Given that the local data source has data available
        setTasksAvailable(mTasksRemoteDataSource, TASKS);
        // And the remote data source does not have any data available
        setTasksNotAvailable(mTasksLocalDataSource);

        // When two subscriptions are set
        TestSubscriber<List<Task>> testSubscriber1 = new TestSubscriber<>();
        mTasksRepository.getAll().subscribe(testSubscriber1);

        TestSubscriber<List<Task>> testSubscriber2 = new TestSubscriber<>();
        mTasksRepository.getAll().subscribe(testSubscriber2);

        // Then tasks were only requested once from remote and local sources
        verify(mTasksRemoteDataSource).getAll();
        verify(mTasksLocalDataSource).getAll();
        assertFalse(mTasksRepository.mCacheIsDirty);
        testSubscriber1.assertValue(TASKS);
        testSubscriber2.assertValue(TASKS);
    }

    @Test
    public void getAll_requestsAllTasksFromLocalDataSource() {
        // Given that the local data source has data available
        setTasksAvailable(mTasksLocalDataSource, TASKS);
        // And the remote data source does not have any data available
        setTasksNotAvailable(mTasksRemoteDataSource);

        // When tasks are requested from the tasks repository
        mTasksRepository.getAll().subscribe(mTasksTestSubscriber);

        // Then tasks are loaded from the local data source
        verify(mTasksLocalDataSource).getAll();
        mTasksTestSubscriber.assertValue(TASKS);
    }

    @Test
    public void add_savesTaskToServiceAPI() {
        // Given a stub task with title and description
        Task newTask = new Task(TASK_TITLE, "Some Task Description");

        // When a task is saved to the tasks repository
        mTasksRepository.add(newTask);

        // Then the service API and persistent repository are called and the cache is updated
        verify(mTasksRemoteDataSource).add(newTask);
        verify(mTasksLocalDataSource).add(newTask);
        assertThat(mTasksRepository.mCachedTasks.size(), is(1));
    }

    @Test
    public void completeTask_completesTaskToServiceAPIUpdatesCache() {
        // Given a stub active task with title and description added in the repository
        Task newTask = new Task(TASK_TITLE, "Some Task Description");
        mTasksRepository.add(newTask);

        // When a task is completed to the tasks repository
        mTasksRepository.completeTask(newTask);

        // Then the service API and persistent repository are called and the cache is updated
        verify(mTasksRemoteDataSource).completeTask(newTask);
        verify(mTasksLocalDataSource).completeTask(newTask);
        assertThat(mTasksRepository.mCachedTasks.size(), is(1));
        assertThat(mTasksRepository.mCachedTasks.get(newTask.getId()).isActive(), is(false));
    }

    @Test
    public void completeTaskId_completesTaskToServiceAPIUpdatesCache() {
        // Given a stub active task with title and description added in the repository
        Task newTask = new Task(TASK_TITLE, "Some Task Description");
        mTasksRepository.add(newTask);

        // When a task is completed using its id to the tasks repository
        mTasksRepository.completeTask(newTask.getId());

        // Then the service API and persistent repository are called and the cache is updated
        verify(mTasksRemoteDataSource).completeTask(newTask);
        verify(mTasksLocalDataSource).completeTask(newTask);
        assertThat(mTasksRepository.mCachedTasks.size(), is(1));
        assertThat(mTasksRepository.mCachedTasks.get(newTask.getId()).isActive(), is(false));
    }

    @Test
    public void activateTask_activatesTaskToServiceAPIUpdatesCache() {
        // Given a stub completed task with title and description in the repository
        Task newTask = new Task(TASK_TITLE, "Some Task Description", true);
        mTasksRepository.add(newTask);

        // When a completed task is activated to the tasks repository
        mTasksRepository.activateTask(newTask);

        // Then the service API and persistent repository are called and the cache is updated
        verify(mTasksRemoteDataSource).activateTask(newTask);
        verify(mTasksLocalDataSource).activateTask(newTask);
        assertThat(mTasksRepository.mCachedTasks.size(), is(1));
        assertThat(mTasksRepository.mCachedTasks.get(newTask.getId()).isActive(), is(true));
    }

    @Test
    public void activateTaskId_activatesTaskToServiceAPIUpdatesCache() {
        // Given a stub completed task with title and description in the repository
        Task newTask = new Task(TASK_TITLE, "Some Task Description", true);
        mTasksRepository.add(newTask);

        // When a completed task is activated with its id to the tasks repository
        mTasksRepository.activateTask(newTask.getId());

        // Then the service API and persistent repository are called and the cache is updated
        verify(mTasksRemoteDataSource).activateTask(newTask);
        verify(mTasksLocalDataSource).activateTask(newTask);
        assertThat(mTasksRepository.mCachedTasks.size(), is(1));
        assertThat(mTasksRepository.mCachedTasks.get(newTask.getId()).isActive(), is(true));
    }

    @Test
    public void getById_requestsSingleTaskFromLocalDataSource() {
        // Given a stub completed task with title and description in the local repository
        Task task = new Task(TASK_TITLE, "Some Task Description", true);
        setTaskAvailable(mTasksLocalDataSource, task);
        // And the task not available in the remote repository
        setTaskNotAvailable(mTasksRemoteDataSource, task.getId());

        // When a task is requested from the tasks repository
        TestSubscriber<Task> testSubscriber = new TestSubscriber<>();
        mTasksRepository.getById(task.getId()).subscribe(testSubscriber);

        // Then the task is loaded from the database
        verify(mTasksLocalDataSource).getById(eq(task.getId()));
        testSubscriber.assertValue(task);
    }

    private void setTaskNotAvailable(ITasksRepository dataSource, String taskId) {
        when(dataSource.getById(eq(taskId))).thenReturn(Observable.<Task>just(null).concatWith(
            Observable.never()));
    }

    private void setTaskAvailable(ITasksRepository dataSource, Task task) {
        when(dataSource.getById(eq(task.getId()))).thenReturn(Observable
                                                                  .just(task)
                                                                  .concatWith(Observable.never()));
    }

    @Test
    public void getById_whenDataNotLocal_fails() {
        // Given a stub completed task with title and description in the remote repository
        Task task = new Task(TASK_TITLE, "Some Task Description", true);
        setTaskAvailable(mTasksRemoteDataSource, task);
        // And the task not available in the local repository
        setTaskNotAvailable(mTasksLocalDataSource, task.getId());

        // When a task is requested from the tasks repository
        TestSubscriber<Task> testSubscriber = new TestSubscriber<>();

        mTasksRepository.getById(task.getId()).subscribe(testSubscriber);

        // Verify no data is returned
        testSubscriber.assertNoValues();
        // Verify that error is returned
        testSubscriber.assertError(NoSuchElementException.class);
    }

    @Test
    public void deleteCompletedTasks_deleteCompletedTasksToServiceAPIUpdatesCache() {
        // Given 2 stub completed tasks and 1 stub active tasks in the repository
        Task newTask = new Task(TASK_TITLE, "Some Task Description", true);
        mTasksRepository.add(newTask);
        Task newTask2 = new Task(TASK_TITLE2, "Some Task Description");
        mTasksRepository.add(newTask2);
        Task newTask3 = new Task(TASK_TITLE3, "Some Task Description", true);
        mTasksRepository.add(newTask3);

        // When a completed tasks are cleared to the tasks repository
        mTasksRepository.clearCompletedTasks();

        // Then the service API and persistent repository are called and the cache is updated
        verify(mTasksRemoteDataSource).clearCompletedTasks();
        verify(mTasksLocalDataSource).clearCompletedTasks();

        assertThat(mTasksRepository.mCachedTasks.size(), is(1));
        assertTrue(mTasksRepository.mCachedTasks.get(newTask2.getId()).isActive());
        assertThat(mTasksRepository.mCachedTasks.get(newTask2.getId()).getTitle(), is(TASK_TITLE2));
    }

    @Test
    public void clear_deletesToServiceAPIUpdatesCache() {
        // Given 2 stub completed tasks and 1 stub active tasks in the repository
        Task newTask = new Task(TASK_TITLE, "Some Task Description", true);
        mTasksRepository.add(newTask);
        Task newTask2 = new Task(TASK_TITLE2, "Some Task Description");
        mTasksRepository.add(newTask2);
        Task newTask3 = new Task(TASK_TITLE3, "Some Task Description", true);
        mTasksRepository.add(newTask3);

        // When all tasks are deleted to the tasks repository
        mTasksRepository.clear();

        // Verify the data sources were called
        verify(mTasksRemoteDataSource).clear();
        verify(mTasksLocalDataSource).clear();

        assertThat(mTasksRepository.mCachedTasks.size(), is(0));
    }

    @Test
    public void delete_deleteToServiceAPIRemovedFromCache() {
        // Given a task in the repository
        Task newTask = new Task(TASK_TITLE, "Some Task Description", true);
        mTasksRepository.add(newTask);
        assertThat(mTasksRepository.mCachedTasks.containsKey(newTask.getId()), is(true));

        // When deleted
        mTasksRepository.remove(newTask.getId());

        // Verify the data sources were called
        verify(mTasksRemoteDataSource).remove(newTask.getId());
        verify(mTasksLocalDataSource).remove(newTask.getId());

        // Verify it's removed from repository
        assertThat(mTasksRepository.mCachedTasks.containsKey(newTask.getId()), is(false));
    }

    @Test
    public void getAllWithDirtyCache_tasksAreRetrievedFromRemote() {
        // Given that the remote data source has data available
        setTasksAvailable(mTasksRemoteDataSource, TASKS);

        // When calling getAll in the repository with dirty cache
        mTasksRepository.refreshTasks();
        mTasksRepository.getAll().subscribe(mTasksTestSubscriber);

        // Verify the tasks from the remote data source are returned, not the local
        verify(mTasksLocalDataSource, never()).getAll();
        verify(mTasksRemoteDataSource).getAll();
        mTasksTestSubscriber.assertValue(TASKS);
    }

    @Test
    public void getAllWithLocalDataSourceUnavailable_tasksAreRetrievedFromRemote() {
        // Given that the local data source has no data available
        setTasksNotAvailable(mTasksLocalDataSource);
        // And the remote data source has data available
        setTasksAvailable(mTasksRemoteDataSource, TASKS);

        // When calling getAll in the repository
        mTasksRepository.getAll().subscribe(mTasksTestSubscriber);

        // Verify the tasks from the remote data source are returned
        verify(mTasksRemoteDataSource).getAll();
        mTasksTestSubscriber.assertValue(TASKS);
    }

    @Test
    public void getAllWithBothDataSourcesUnavailable_firesOnDataUnavailable() {
        // Given that the local data source has no data available
        setTasksNotAvailable(mTasksLocalDataSource);
        // And the remote data source has no data available
        setTasksNotAvailable(mTasksRemoteDataSource);

        // When calling getAll in the repository
        mTasksRepository.getAll().subscribe(mTasksTestSubscriber);

        // Verify no data is returned
        mTasksTestSubscriber.assertNoValues();
        // Verify that error is returned
        mTasksTestSubscriber.assertError(NoSuchElementException.class);
    }

    @Test
    public void getByIdWithBothDataSourcesUnavailable_firesOnError() {
        // Given a task id
        final String taskId = "123";
        // And the local data source has no data available
        setTaskNotAvailable(mTasksLocalDataSource, taskId);
        // And the remote data source has no data available
        setTaskNotAvailable(mTasksRemoteDataSource, taskId);

        // When calling getById in the repository
        TestSubscriber<Task> testSubscriber = new TestSubscriber<>();
        mTasksRepository.getById(taskId).subscribe(testSubscriber);

        // Verify that error is returned
        testSubscriber.assertError(NoSuchElementException.class);
    }

    @Test
    public void getAll_refreshesLocalDataSource() {
        // Given that the remote data source has data available
        setTasksAvailable(mTasksRemoteDataSource, TASKS);

        // Mark cache as dirty to force a reload of data from remote data source.
        mTasksRepository.refreshTasks();

        // When calling getAll in the repository
        mTasksRepository.getAll().subscribe(mTasksTestSubscriber);

        // Verify that the data fetched from the remote data source was saved in local.
        verify(mTasksLocalDataSource, times(TASKS.size())).add(any(Task.class));
        mTasksTestSubscriber.assertValue(TASKS);
    }
}
