package com.example.cataloguemoviefinal.factory;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.cataloguemoviefinal.model.DetailedTvShowViewModel;

/**
 * Class ini berguna untuk membuat {@link DetailedTvShowViewModel} object dan membuat view model
 * yang menampung lebih dari 1 parameter karena ada tambahan informasi yang penting
 */
public class DetailedTvShowViewModelFactory implements ViewModelProvider.Factory {

    // Initiate variable untuk dibawa
    private Application mApplication;
    private int mTvShowId;

    // Constructor untuk membawa value dari variable bedasarkan parameter lalu di bawa ke
    // {@link create()} method
    public DetailedTvShowViewModelFactory(Application application, int tvShowId) {
        mApplication = application;
        mTvShowId = tvShowId;
    }

    // Buat ViewModel bedasarkan variables yang ada di ViewModelFactory
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new DetailedTvShowViewModel(mApplication, mTvShowId);
    }
}
