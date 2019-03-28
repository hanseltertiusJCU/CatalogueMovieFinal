package com.example.cataloguemoviefinal.factory;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.cataloguemoviefinal.model.SearchTvShowViewModel;

/**
 * Class ini berguna untuk membuat {@link SearchTvShowViewModel} object dan membuat view model
 * yang menampung lebih dari 1 parameter karena ada tambahan informasi yang penting
 */
public class SearchTvShowViewModelFactory implements ViewModelProvider.Factory {

    // Initiate variable untuk dibawa
    private Application mApplication;
    private String mTvShowSearchKeyword;

    // Constructor untuk membawa value dari variable bedasarkan parameter lalu di bawa ke
    // {@link create()} method
    public SearchTvShowViewModelFactory(Application application, String tvShowSearchKeyword) {
        mApplication = application;
        mTvShowSearchKeyword = tvShowSearchKeyword;
    }

    // Buat ViewModel bedasarkan variables yang ada di ViewModelFactory
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new SearchTvShowViewModel(mApplication, mTvShowSearchKeyword);
    }
}
