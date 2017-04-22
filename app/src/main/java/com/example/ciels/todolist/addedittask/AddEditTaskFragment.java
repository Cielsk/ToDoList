package com.example.ciels.todolist.addedittask;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.example.ciels.todolist.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddEditTaskFragment extends Fragment implements AddEditTaskContract.View {

    public static final String ARGUMENT_EDIT_TASK_ID = "EDIT_TASK_ID";

    @BindView(R.id.add_task_title) EditText mAddTaskTitle;

    @BindView(R.id.add_task_description) EditText mAddTaskDescription;

    Unbinder unbinder;

    private AddEditTaskContract.Presenter mPresenter;

    public AddEditTaskFragment() {
        // Required empty public constructor
    }

    public static AddEditTaskFragment newInstance() {
        return new AddEditTaskFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_edit_task, container, false);
        unbinder = ButterKnife.bind(this, view);

        setHasOptionsMenu(true);
        setRetainInstance(true);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FloatingActionButton fab
            = (FloatingActionButton) getActivity().findViewById(R.id.fab_edit_task_done);
        fab.setImageResource(R.drawable.ic_done);
        fab.setOnClickListener(__ -> mPresenter.saveTask(mAddTaskTitle.getText().toString(),
                                                         mAddTaskDescription.getText().toString()));
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
    public void setPresenter(@NonNull AddEditTaskContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showEmptyTaskError() {
        Snackbar
            .make(mAddTaskTitle, getString(R.string.empty_task_message), Snackbar.LENGTH_LONG)
            .show();
    }

    @Override
    public void showTasksList() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    @Override
    public void setTitle(String title) {
        mAddTaskTitle.setText(title);
    }

    @Override
    public void setDescription(String description) {
        mAddTaskDescription.setText(description);
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }
}
