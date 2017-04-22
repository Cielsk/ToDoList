package com.example.ciels.todolist.taskdetail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.example.ciels.todolist.R;
import com.example.ciels.todolist.addedittask.AddEditTaskActivity;
import com.example.ciels.todolist.addedittask.AddEditTaskFragment;
import javax.inject.Inject;

/**
 *
 */
public class TaskDetailFragment extends Fragment implements TaskDetailContract.View {

    @NonNull private static final String ARGUMENT_TASK_ID = "TASK_ID";

    @NonNull private static final int REQUEST_EDIT_TASK = 1;
    @BindView(R.id.task_detail_complete) CheckBox mTaskDetailComplete;
    @BindView(R.id.task_detail_title) TextView mTaskDetailTitle;
    @BindView(R.id.task_detail_description) TextView mTaskDetailDescription;
    Unbinder unbinder;

    @Inject TaskDetailContract.Presenter mPresenter;

    public static TaskDetailFragment newInstance(@Nullable String taskId) {
        Bundle arguments = new Bundle();
        arguments.putString(ARGUMENT_TASK_ID, taskId);
        TaskDetailFragment fragment = new TaskDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void setPresenter(@NonNull TaskDetailContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        if (active) {
            mTaskDetailTitle.setText("");
            mTaskDetailDescription.setText(getString(R.string.loading));
        }
    }

    @Override
    public void showMissingTask() {
        mTaskDetailTitle.setText("");
        mTaskDetailDescription.setText(getString(R.string.no_data));
    }

    @Override
    public void hideTitle() {
        mTaskDetailTitle.setVisibility(View.GONE);
    }

    @Override
    public void showTitle(@NonNull String title) {
        mTaskDetailTitle.setVisibility(View.VISIBLE);
        mTaskDetailTitle.setText(title);
    }

    @Override
    public void hideDescription() {
        mTaskDetailDescription.setVisibility(View.GONE);
    }

    @Override
    public void showDescription(@NonNull String description) {
        mTaskDetailDescription.setVisibility(View.VISIBLE);
        mTaskDetailDescription.setText(description);
    }

    @Override
    public void showCompletionStatus(boolean complete) {
        mTaskDetailComplete.setChecked(complete);
        mTaskDetailComplete.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mPresenter.completeTask();
            } else {
                mPresenter.activateTask();
            }
        });
    }

    @Override
    public void showEditTask(String taskId) {
        Intent intent = new Intent(getContext(), AddEditTaskActivity.class);
        intent.putExtra(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID, taskId);
        startActivityForResult(intent, REQUEST_EDIT_TASK);
    }

    @Override
    public void showTaskDeleted() {
        getActivity().finish();
    }

    @Override
    public void showTaskMarkedComplete() {
        Snackbar
            .make(getView(), getString(R.string.task_marked_complete), Snackbar.LENGTH_LONG)
            .show();
    }

    @Override
    public void showTaskMarkedActive() {
        Snackbar
            .make(getView(), getString(R.string.task_marked_active), Snackbar.LENGTH_LONG)
            .show();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_EDIT_TASK) {
            // If the task was edited successfully, go back to the list.
            if (resultCode == Activity.RESULT_OK) {
                getActivity().finish();
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.taskdetail_frag, container);

        unbinder = ButterKnife.bind(this, view);

        // Set up floating action button
        FloatingActionButton fab
            = (FloatingActionButton) getActivity().findViewById(R.id.fab_edit_task);

        fab.setOnClickListener(__ -> mPresenter.editTask());

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.taskdetail_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                mPresenter.deleteTask();
                return true;
        }
        return false;
    }
}
