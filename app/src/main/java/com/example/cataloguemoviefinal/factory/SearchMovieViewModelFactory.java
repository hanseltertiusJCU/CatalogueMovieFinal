package com.example.cataloguemoviefinal.factory;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.cataloguemoviefinal.model.SearchMovieViewModel;

public class SearchMovieViewModelFactory implements ViewModelProvider.Factory {
	
	private Application mApplication;
	private String mMovieSearchKeyword;
	
	public SearchMovieViewModelFactory(Application application, String movieSearchKeyword){
		mApplication = application;
		mMovieSearchKeyword = movieSearchKeyword;
	}
	
	@NonNull
	@Override
	public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
		return (T) new SearchMovieViewModel(mApplication, mMovieSearchKeyword);
	}
}
