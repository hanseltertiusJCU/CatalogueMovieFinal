package com.example.cataloguemoviefinal.fragment;


import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cataloguemoviefinal.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchTvShowFragment extends Fragment {
	
	@BindView(R.id.tv_show_search_keyword_content)
	TextView tvShowSearchKeyword;
	
	private static final String TV_KEYWORD_RESULT = "tv_keyword_result";
	
	private String tvKeywordResult;
	
	public SearchTvShowFragment() {
		// Required empty public constructor
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_tv_show, container, false);
		setHasOptionsMenu(true);
		ButterKnife.bind(this, view);
		return view;
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(savedInstanceState != null){
			tvKeywordResult = savedInstanceState.getString(TV_KEYWORD_RESULT);
			tvShowSearchKeyword.setText(tvKeywordResult);
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		
		inflater.inflate(R.menu.menu_search, menu);
		
		if(getActivity() != null){
			// Line ini berguna untuk memasang listener untuk SearchView
			SearchManager tvShowSearchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
			if(tvShowSearchManager != null){
				SearchView tvShowSearchView = (SearchView) (menu.findItem(R.id.search)).getActionView();
				tvShowSearchView.setSearchableInfo(tvShowSearchManager.getSearchableInfo(getActivity().getComponentName()));
				tvShowSearchView.setQueryHint(getResources().getString(R.string.search));
				// Listener untuk text dari searchview
				tvShowSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
					@Override
					public boolean onQueryTextSubmit(String query) {
						tvKeywordResult = query;
						tvShowSearchKeyword.setText(tvKeywordResult);
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
	
	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(TV_KEYWORD_RESULT, tvKeywordResult);
	}
}
