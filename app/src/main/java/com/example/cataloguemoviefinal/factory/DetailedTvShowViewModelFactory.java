package com.example.cataloguemoviefinal.factory;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.cataloguemoviefinal.model.DetailedTvShowViewModel;


// Class ini berguna untuk membuat ViewModel yang menampung lebih dari 1 parameter
public class DetailedTvShowViewModelFactory implements ViewModelProvider.Factory {
	
	private Application mApplication;
	private int mTvShowId;
	
	public DetailedTvShowViewModelFactory(Application application, int tvShowId) {
		mApplication = application;
		mTvShowId = tvShowId;
	}
	
	@NonNull
	@Override
	public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
		return (T) new DetailedTvShowViewModel(mApplication, mTvShowId);
	}
}
