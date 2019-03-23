package com.example.cataloguemoviefinal;


import android.database.Cursor;

import com.example.cataloguemoviefinal.entity.TvShowItem;

import java.util.ArrayList;

public interface LoadFavoriteTvShowCallback{
	// Preexecute untuk prepare array list data
	void favoriteTvShowPreExecute();
	// Postexecute untuk memberi hasil kepada array list
	void favoriteTvShowPostExecute(Cursor tvShowItems);
}
