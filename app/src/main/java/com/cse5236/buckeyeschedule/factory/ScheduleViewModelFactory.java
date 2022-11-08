package com.cse5236.buckeyeschedule.factory;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.cse5236.buckeyeschedule.viewmodel.ScheduleViewModel;

public class ScheduleViewModelFactory implements ViewModelProvider.Factory {
    private Application mApplication;
    private String userId;


    public ScheduleViewModelFactory(Application application, String userId) {
        this.mApplication = application;
        this.userId = userId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new ScheduleViewModel(mApplication, userId);
    }
}