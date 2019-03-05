package com.example.cataloguemoviefinal.fragment;


import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cataloguemoviefinal.MainActivity;
import com.example.cataloguemoviefinal.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchMovieFragment extends Fragment {
	
	@BindView(R.id.movie_search_keyword_content)
	TextView movieSearchKeyword;
	
	public SearchMovieFragment() {
		// Required empty public constructor
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_movie, container, false);
		setHasOptionsMenu(true);
		ButterKnife.bind(this, view);
		return view;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_search, menu);
		
		if(getActivity() != null){
			// Line ini berguna untuk memasang listener untuk SearchView
			SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
			if(searchManager != null){
				SearchView movieSearchView  = (SearchView) (menu.findItem(R.id.search)).getActionView();
				movieSearchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
				movieSearchView.setQueryHint(getResources().getString(R.string.search));
				// Listener untuk text dari searchview
				movieSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
					@Override
					public boolean onQueryTextSubmit(String query) {
						// todo: querynya itu dibikin keyword search d horizontal linear layout
						movieSearchKeyword.setText(query);
						return true;
					}
					
					@Override
					public boolean onQueryTextChange(String newText) {
						return false;
					}
				});
			}
		}
		
		
		super.onCreateOptionsMenu(menu, inflater);
	}
}
