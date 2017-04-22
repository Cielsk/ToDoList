package com.example.ciels.todolist.statistics;

import dagger.Module;
import dagger.Provides;

/**
 *
 */
@Module
public class StatisticsPresenterModule {

    private final StatisticsContract.View mView;

    public StatisticsPresenterModule(StatisticsContract.View view) {
        mView = view;
    }

    @Provides
    StatisticsContract.View provideStatisticsContractView() {
        return mView;
    }
}
