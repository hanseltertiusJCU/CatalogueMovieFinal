package com.example.cataloguemoviefinal.factory;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.cataloguemoviefinal.model.SearchTvShowViewModel;

public class SearchTvShowViewModelFactory implements ViewModelProvider.Factory {
	
	private Application mApplication;
	private String mTvShowSearchKeyword;
	
	public SearchTvShowViewModelFactory(Application application, String tvShowSearchKeyword){
		mApplication = application;
		mTvShowSearchKeyword = tvShowSearchKeyword;
	}
	
	@NonNull
	@Override
	public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
		return (T) new SearchTvShowViewModel(mApplication, mTvShowSearchKeyword);
	}
}
