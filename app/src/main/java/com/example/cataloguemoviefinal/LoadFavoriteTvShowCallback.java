package com.example.cataloguemoviefinal;


import com.example.cataloguemoviefinal.entity.TvShowItem;

import java.util.ArrayList;

public interface LoadFavoriteTvShowCallback{
	void preExecute();
	
	void postExecute(ArrayList<TvShowItem> tvShowItems);
}
