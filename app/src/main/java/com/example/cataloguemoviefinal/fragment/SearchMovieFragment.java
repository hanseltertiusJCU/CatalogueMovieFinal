package com.example.cataloguemoviefinal.fragment;


import android.app.SearchManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.SearchView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.cataloguemoviefinal.DetailActivity;
import com.example.cataloguemoviefinal.LoadFavoriteMoviesCallback;
import com.example.cataloguemoviefinal.R;
import com.example.cataloguemoviefinal.adapter.MovieAdapter;
import com.example.cataloguemoviefinal.async.LoadFavoriteMoviesAsync;
import com.example.cataloguemoviefinal.database.FavoriteItemsHelper;
import com.example.cataloguemoviefinal.entity.MovieItem;
import com.example.cataloguemoviefinal.factory.SearchMovieViewModelFactory;
import com.example.cataloguemoviefinal.model.SearchMovieViewModel;
import com.example.cataloguemoviefinal.support.ItemClickSupport;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchMovieFragment extends Fragment implements LoadFavoriteMoviesCallback {
	
	// Key untuk membawa data ke intent (data tidak d private untuk dapat diapplikasikan di berbagai Fragments dan diakses ke {@link DetailActivity})
	public static final String MOVIE_ID_DATA = "MOVIE_ID_DATA";
	public static final String MOVIE_TITLE_DATA = "MOVIE_TITLE_DATA";
	public static final String MOVIE_BOOLEAN_STATE_DATA = "MOVIE_BOOLEAN_STATE_DATA";
	// Constant untuk represent mode agar membuka data tertentu
	public static final String MODE_INTENT = "mode_intent";
	// Bikin constant (key) yang merepresent Parcelable object
	private static final String MOVIE_LIST_STATE = "movieListState";
	@BindView(R.id.rv_movie_item_list)
	RecyclerView recyclerView;
	@BindView(R.id.progress_bar)
	ProgressBar progressBar;
	@BindView(R.id.movie_search_keyword_content)
	TextView movieSearchKeyword;
	private MovieAdapter movieAdapter;
	// Bikin parcelable yang berguna untuk menyimpan lalu merestore position
	private Parcelable mMovieListState = null;
	// Helper untuk membuka koneksi ke DB
	private FavoriteItemsHelper favoriteItemsHelper;
	// Bikin linearlayout manager untuk dapat call onsaveinstancestate method
	private LinearLayoutManager searchMovieLinearLayoutManager;
	// Constant untuk key untuk keyword search result di movie
	private static final String MOVIE_KEYWORD_RESULT = "movie_keyword_result";
	// Value untuk keyword di search movie
	private String moviekeywordResult;
	// Initiate Viewmodel dan componentnya
	SearchMovieViewModel searchMovieViewModel;
	Observer<ArrayList<MovieItem>> searchMovieObserver;
	
	public SearchMovieFragment() {
		// Required empty public constructor
	}
	
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Cek jika ada ApplicationContext, jika ada maka buka koneksi ke ItemHelper
		if(Objects.requireNonNull(getActivity()).getApplicationContext() != null){
			favoriteItemsHelper = FavoriteItemsHelper.getInstance(getActivity().getApplicationContext());
			favoriteItemsHelper.open();
		}
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
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		// Set LinearLayoutManager object value dengan memanggil LinearLayoutManager constructor
		searchMovieLinearLayoutManager = new LinearLayoutManager(getContext());
		// Ukuran data recycler view sama
		recyclerView.setHasFixedSize(true);
		// Kita menggunakan LinearLayoutManager berorientasi vertical untuk RecyclerView
		recyclerView.setLayoutManager(searchMovieLinearLayoutManager);
		
		// Initiate movie adapter
		movieAdapter = new MovieAdapter(getContext());
		// Notify when data changed into adapter
		movieAdapter.notifyDataSetChanged();
		
		// Set empty adapter agar dapat di rotate
		recyclerView.setAdapter(movieAdapter);
		
		// Set background color untuk RecyclerView
		recyclerView.setBackgroundColor(getResources().getColor(R.color.colorWhite));
		
		if(getContext() != null){
			// Buat object DividerItemDecoration dan set drawable untuk DividerItemDecoration
			DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
			itemDecorator.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(getContext(), R.drawable.item_divider)));
			// Set divider untuk RecyclerView items
			recyclerView.addItemDecoration(itemDecorator);
		}
		
		// Set visiblity of views ketika sedang dalam meretrieve data
		recyclerView.setVisibility(View.INVISIBLE);
		progressBar.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(savedInstanceState != null){
			moviekeywordResult = savedInstanceState.getString(MOVIE_KEYWORD_RESULT);
			movieSearchKeyword.setText(moviekeywordResult);
			mMovieListState = savedInstanceState.getParcelable(MOVIE_LIST_STATE);
		} else {
			moviekeywordResult = "avenger"; // Default value
			movieSearchKeyword.setText(moviekeywordResult);
			new LoadFavoriteMoviesAsync(favoriteItemsHelper, this).execute();
		}
		
		if(Objects.requireNonNull(getActivity()).getApplication() != null){
			searchMovieViewModel = ViewModelProviders.of(this, new SearchMovieViewModelFactory(getActivity().getApplication(), moviekeywordResult)).get(SearchMovieViewModel.class);
			
			searchMovieObserver = createObserver();
			
			searchMovieViewModel.getSearchMovies().observe(this, searchMovieObserver);
		}
		
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_search, menu);
		
		if(getActivity() != null){
			// Line ini berguna untuk memasang listener untuk SearchView
			SearchManager movieSearchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
			if(movieSearchManager != null){
				SearchView movieSearchView  = (SearchView) (menu.findItem(R.id.search)).getActionView();
				movieSearchView.setSearchableInfo(movieSearchManager.getSearchableInfo(getActivity().getComponentName()));
				movieSearchView.setQueryHint(getResources().getString(R.string.search));
				// Listener untuk text dari searchview
				movieSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
					@Override
					public boolean onQueryTextSubmit(String query) {
						moviekeywordResult = query;
						movieSearchKeyword.setText(moviekeywordResult);
						
						// Ketika kita submit query text, maka data akan melakukan loading kembali
						recyclerView.setVisibility(View.INVISIBLE);
						progressBar.setVisibility(View.VISIBLE);
						
						// Call setter method untuk merubah value parameter di ViewModel
						searchMovieViewModel.setMovieSearchKeyword(moviekeywordResult);
						
						// Recall live data, kesannya itu kyk merubah parameter dari ViewModelFactory
						searchMovieViewModel.searchMovieRecall();
						
						// Buat Observer object untuk dapat merespon changes dengan mengupdate UI
						searchMovieObserver = createObserver();
						
						// Replace sebuah observer ke observer yang baru untuk menampilkan LiveData yang baru
						searchMovieViewModel.getSearchMovies().observe(SearchMovieFragment.this, searchMovieObserver);
						
						// Reset recyclerview position ke awal setelah retrieve keyword baru
						recyclerView.smoothScrollToPosition(0);
						
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
	
	private void showSelectedMovieItems(MovieItem movieItem){
		// Dapatkan id dan title bedasarkan ListView item
		int movieIdItem = movieItem.getId();
		String movieTitleItem = movieItem.getMovieTitle();
		int movieBooleanStateItem = 0;
		if(FavoriteMovieFragment.favMovieListData.size() > 0){
			for(int i = 0; i < FavoriteMovieFragment.favMovieListData.size(); i ++){
				// Cek jika movieIdItem itu cocok dengan item id yg ada di arraylist
				if(movieIdItem == FavoriteMovieFragment.favMovieListData.get(i).getId()){
					// Get favorite boolean state value untuk transfer ke variable movieBooleanStateItem
					movieBooleanStateItem = FavoriteMovieFragment.favMovieListData.get(i).getFavoriteBooleanState();
					break;
				}
			}
		}
		// Tentukan bahwa kita ingin membuka data Movie
		String modeItem = "open_movie_detail";
		// Create intent object agar ke DetailActivity yg merupakan activity tujuan
		Intent intentWithMovieIdData = new Intent(getActivity(), DetailActivity.class);
		// Bawa data untuk disampaikan ke {@link DetailActivity}
		intentWithMovieIdData.putExtra(MOVIE_ID_DATA, movieIdItem);
		intentWithMovieIdData.putExtra(MOVIE_TITLE_DATA, movieTitleItem);
		intentWithMovieIdData.putExtra(MOVIE_BOOLEAN_STATE_DATA, movieBooleanStateItem);
		intentWithMovieIdData.putExtra(MODE_INTENT, modeItem);
		// Start activity tujuan bedasarkan intent object dan bawa request code
		// REQUEST_CHANGE untuk onActivityResult
		startActivityForResult(intentWithMovieIdData, DetailActivity.REQUEST_CHANGE);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		// Cek jika Parcelable itu exist, jika iya, maka update layout manager dengan memasukkan
		// Parcelable sebagai input parameter
		if(mMovieListState != null){
			searchMovieLinearLayoutManager.onRestoreInstanceState(mMovieListState);
		}
	}
	
	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		if(searchMovieLinearLayoutManager != null){
			// Save list state/ scroll position dari list
			mMovieListState = searchMovieLinearLayoutManager.onSaveInstanceState();
			outState.putParcelable(MOVIE_LIST_STATE, mMovieListState);
			// Save string keyword for retrieving string values in keyword tracker
			outState.putString(MOVIE_KEYWORD_RESULT, moviekeywordResult);
		}
		
	}
	
	@Override
	public void preExecute() {
	
	}
	
	@Override
	public void postExecute(ArrayList<MovieItem> movieItems) {
		// Bikin ArrayList global variable sama dengan hasil dari AsyncTask class
		FavoriteMovieFragment.favMovieListData = movieItems;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(data != null){
			// Check for correct request code
			if(requestCode == DetailActivity.REQUEST_CHANGE){
				// Check for result code
				if(resultCode == DetailActivity.RESULT_CHANGE){
					// Tambahkan item ke adapter dan reset scroll position ke paling atas
					boolean changedDataState = data.getBooleanExtra(DetailActivity.EXTRA_CHANGED_STATE, false);
					// Cek jika value dari changedDataState itu true
					if(changedDataState){
						if(Objects.requireNonNull(getActivity()).getSupportFragmentManager() != null){
							// Dapatin position fragment dari FavoriteMovieFragment di ViewPager since ViewPager menampung list dari Fragments
							FavoriteMovieFragment favoriteMovieFragment = (FavoriteMovieFragment) getActivity().getSupportFragmentManager().getFragments().get(2);
							// Cek jika favoriteMovieFragment itu ada
							if(favoriteMovieFragment != null) {
								// Komunikasi dengan FavoriteMovieFragment dengan memanggil onActivityResult method di FavoriteMovieFragment
								favoriteMovieFragment.onActivityResult(requestCode, resultCode, data);
							}
						}
					}
				}
			}
		}
	}
	
	public Observer<ArrayList<MovieItem>> createObserver(){
		// Buat observer yg gunanya untuk update UI
		return new Observer<ArrayList<MovieItem>>() {
			@Override
			public void onChanged(@Nullable final ArrayList<MovieItem> movieItems) {
				// Ketika data selesai di load, maka kita akan mendapatkan data dan menghilangkan progress bar
				// yang menandakan bahwa loadingnya sudah selesai
				recyclerView.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.GONE);
				// Set data ke adapter
				movieAdapter.setData(movieItems);
				// Set item click listener di dalam recycler view
				ItemClickSupport.addSupportToView(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
					// Implement interface method
					@Override
					public void onItemClicked(RecyclerView recyclerView, int position, View view) {
						// Panggil method showSelectedMovieItems untuk mengakses DetailActivity bedasarkan data yang ada
						if(movieItems != null) {
							showSelectedMovieItems(movieItems.get(position));
						}
					}
				});
			}
		};
	}
}
