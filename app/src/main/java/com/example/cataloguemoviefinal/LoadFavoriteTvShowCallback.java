package com.example.cataloguemoviefinal;


import android.database.Cursor;

import com.example.cataloguemoviefinal.entity.TvShowItem;

import java.util.ArrayList;

public interface LoadFavoriteTvShowCallback{
	void favoriteTvShowPostExecute(Cursor tvShowItems);
}
