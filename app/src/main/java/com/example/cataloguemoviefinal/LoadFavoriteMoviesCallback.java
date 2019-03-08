package com.example.cataloguemoviefinal;

import android.database.Cursor;

import com.example.cataloguemoviefinal.entity.MovieItem;

import java.util.ArrayList;

public interface LoadFavoriteMoviesCallback{
	void favoriteMoviePreExecute();
	
	void favoriteMoviePostExecute(Cursor movieItems);
}
