package com.example.cataloguemoviefinal;

import android.database.Cursor;

import com.example.cataloguemoviefinal.entity.MovieItem;

import java.util.ArrayList;

public interface LoadFavoriteMoviesCallback{
	// Preexecute untuk prepare array list data
	void favoriteMoviePreExecute();
	// Postexecute untuk memberi hasil kepada array list
	void favoriteMoviePostExecute(Cursor movieItems);
}
