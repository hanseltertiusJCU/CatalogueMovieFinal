package com.example.cataloguemoviefinal.factory;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.cataloguemoviefinal.model.DetailedMovieViewModel;

// Class ini berguna untuk membuat ViewModel yang menampung lebih dari 1 parameter
public class DetailedMovieViewModelFactory implements ViewModelProvider.Factory {
	
	private Application mApplication;
	private int mMovieId;
	
	public DetailedMovieViewModelFactory(Application application, int movieId) {
		mApplication = application;
		mMovieId = movieId;
	}
	
	// Buat ViewModel bedasarkan parameter yang ada di ViewModelFactory
	@NonNull
	@Override
	public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
		return (T) new DetailedMovieViewModel(mApplication, mMovieId);
	}
}
