package com.example.cataloguemoviefinal.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cataloguemoviefinal.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchTvShowFragment extends Fragment {
	
	
	public SearchTvShowFragment() {
		// Required empty public constructor
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		TextView textView = new TextView(getActivity());
		textView.setText(R.string.hello_blank_fragment);
		return textView;
	}
	
}
