package com.example.cataloguemoviefinal;

import com.example.cataloguemoviefinal.entity.MovieItem;

import java.util.ArrayList;

public interface LoadFavoriteMoviesCallback{
	void preExecute();
	
	void postExecute(ArrayList<MovieItem> movieItems);
}
