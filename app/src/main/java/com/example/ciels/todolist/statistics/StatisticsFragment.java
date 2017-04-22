package com.example.ciels.todolist.statistics;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.example.ciels.todolist.R;

/**
 *
 */
public class StatisticsFragment extends Fragment implements StatisticsContract.View {

    @BindView(R.id.statistics) TextView mStatistics;
    Unbinder unbinder;
    private StatisticsContract.Presenter mPresenter;

    public StatisticsFragment() {
    }

    public static StatisticsFragment newInstance() {
        return new StatisticsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.statistics_frag, container, false);

        unbinder = ButterKnife.bind(this, view);
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
    public void setPresenter(@NonNull StatisticsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void setProgressIndicator(boolean active) {
        if (active) {
            mStatistics.setText(getString(R.string.loading));
        }
    }

    @Override
    public void showStatistics(int numberOfIncompleteTasks, int numberOfCompletedTasks) {
        if (numberOfCompletedTasks == 0 && numberOfIncompleteTasks == 0) {
            mStatistics.setText(getResources().getString(R.string.statistics_no_tasks));
        } else {
            String displayString = getResources().getString(R.string.statistics_active_tasks)
                                   + " "
                                   + numberOfIncompleteTasks
                                   + "\n"
                                   + getResources().getString(R.string.statistics_completed_tasks)
                                   + " "
                                   + numberOfCompletedTasks;
            mStatistics.setText(displayString);
        }
    }

    @Override
    public void showLoadingStatisticsError() {
        mStatistics.setText(getResources().getString(R.string.statistics_error));
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }
}
