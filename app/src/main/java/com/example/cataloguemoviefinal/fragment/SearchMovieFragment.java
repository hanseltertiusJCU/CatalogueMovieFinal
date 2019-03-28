package com.example.cataloguemoviefinal.fragment;


import android.app.SearchManager;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.cataloguemoviefinal.DetailActivity;
import com.example.cataloguemoviefinal.MainActivity;
import com.example.cataloguemoviefinal.R;
import com.example.cataloguemoviefinal.adapter.MovieAdapter;
import com.example.cataloguemoviefinal.entity.MovieItem;
import com.example.cataloguemoviefinal.factory.SearchMovieViewModelFactory;
import com.example.cataloguemoviefinal.model.SearchMovieViewModel;
import com.example.cataloguemoviefinal.support.ItemClickSupport;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.cataloguemoviefinal.BuildConfig.MODE_INTENT;
import static com.example.cataloguemoviefinal.BuildConfig.MOVIE_BOOLEAN_STATE_DATA;
import static com.example.cataloguemoviefinal.BuildConfig.MOVIE_ID_DATA;
import static com.example.cataloguemoviefinal.BuildConfig.MOVIE_LIST_STATE;
import static com.example.cataloguemoviefinal.BuildConfig.MOVIE_TITLE_DATA;
import static com.example.cataloguemoviefinal.BuildConfig.OPEN_FROM_WIDGET;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_FAVORITE_CONTENT_URI;

/**
 * Class tersebut berguna untuk:
 * - menampilkan data berisi movie dari URL search movie ketika connected ke internet
 * - menghandle search keyword change dengan memanggil kembali {@link LiveData}
 * - membuat intent ke {@link DetailActivity} ketika view object dari {@link RecyclerView} di click
 */
public class SearchMovieFragment extends Fragment{

	// Bind Views
	@BindView(R.id.rv_movie_item_list)
	RecyclerView recyclerView;
	@BindView(R.id.progress_bar)
	ProgressBar progressBar;
	// TextView buat empty state text
	@BindView(R.id.movie_empty_state_text)
	TextView emptyTextView;
	@BindView(R.id.movie_search_keyword_content)
	TextView movieSearchKeyword;
	private MovieAdapter movieAdapter;
	// Bikin parcelable yang berguna untuk menyimpan lalu merestore position
	private Parcelable mMovieListState = null;
	// Bikin linearlayout manager untuk dapat call onsaveinstancestate method
	private LinearLayoutManager searchMovieLinearLayoutManager;
	// Constant untuk key untuk keyword search result di movie
	private static final String MOVIE_KEYWORD_RESULT = "movie_keyword_result";
	// Value untuk keyword di search movie
	private String moviekeywordResult;
	// Initiate Viewmodel dan componentnya
	SearchMovieViewModel searchMovieViewModel;
	Observer<ArrayList<MovieItem>> searchMovieObserver;
	// Initiate Swipe to refresh layout
	@BindView(R.id.fragment_movie_swipe_refresh_layout)
	SwipeRefreshLayout fragmentMovieSwipeRefreshLayout;
	
	public SearchMovieFragment() {
		// Required empty public constructor
	}

	/**
	 * Method ini di triggered pada saat {@link Fragment} pertama kali dibuat
	 * Method ini berguna untuk membuat View bedasarkan layout xml fragment_tv_show
	 * @param inflater LayoutInflater untuk inflate layout dari xml
	 * @param container ViewGroup yang menampung fragment (root view dari xml possibly)
	 * @param savedInstanceState Bundle object untuk dapat handle orientation changes
	 * @return View object untuk onViewCreated()
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_movie, container, false);
		setHasOptionsMenu(true);
		ButterKnife.bind(this, view);
		return view;
	}

	/**
	 * Method ini di triggered pada saat view dari {@link Fragment} dibuat
	 * Method ini berguna untuk:
	 * - Set recyclerView layout manager
	 * - Set adapter ke recyclerView
	 * - Set border ke setiap recyclerView item
	 * @param view View hasil dari onCreateView
	 * @param savedInstanceState bundle object untuk menghandle orientation change
	 */
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
		recyclerView.setBackgroundColor(getResources().getColor(android.R.color.white));
		
		if(getContext() != null){
			// Buat object DividerItemDecoration dan set drawable untuk DividerItemDecoration
			DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
			itemDecorator.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(getContext(), R.drawable.item_divider)));
			// Set divider untuk RecyclerView items
			recyclerView.addItemDecoration(itemDecorator);
		}
	}

	/**
	 * Method ini di triggered ketika activity dibuat, method ini berguna untuk:
	 * - Save scroll position dari items dari object {@link Bundle}
	 * - load data for first time while checking for Internet Connectivity
	 * - Swipe to refresh for reload data or make it connected
	 * @param savedInstanceState bundle object untuk menghandle orientation change
	 */
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Check if there is savedinstancestate (for rotation purposes), if not (when activity started), create view model (including live data) as well as putting observer on livedata
		if(savedInstanceState != null){
			moviekeywordResult = savedInstanceState.getString(MOVIE_KEYWORD_RESULT);
			movieSearchKeyword.setText(moviekeywordResult);
			mMovieListState = savedInstanceState.getParcelable(MOVIE_LIST_STATE);
		} else {
			moviekeywordResult = "avenger"; // Default value
			movieSearchKeyword.setText(moviekeywordResult);
		}

		// Check if activity + application exists
		if(Objects.requireNonNull(getActivity()).getApplication() != null){
			// Set visiblity of views ketika sedang dalam meretrieve data
			recyclerView.setVisibility(View.INVISIBLE);
			progressBar.setVisibility(View.VISIBLE);
			emptyTextView.setVisibility(View.GONE);
			// Connectivity manager untuk mengecek state dari network connectivity
			ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
			// Network Info object untuk melihat ada data network yang aktif
			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
			// Cek jika ada network connection
			if(networkInfo != null && networkInfo.isConnected()){
				// Create Viewmodel object
				searchMovieViewModel = ViewModelProviders.of(this, new SearchMovieViewModelFactory(getActivity().getApplication(), moviekeywordResult)).get(SearchMovieViewModel.class);
				// Create observer that return ArrayList that takes ArrayList<MovieItem>
				searchMovieObserver = createObserver();
				// Calling LiveData from ViewModel (since LiveData is a part of ViewModel) then observe the LiveData by putting observer
				searchMovieViewModel.getSearchMovies().observe(this, searchMovieObserver);
			} else {
				// Progress bar into gone and recycler view into invisible as the data finished on loading
				progressBar.setVisibility(View.GONE);
				recyclerView.setVisibility(View.INVISIBLE);
				// Set empty view visibility into visible
				emptyTextView.setVisibility(View.VISIBLE);
				// Empty text view yg menunjukkan bahwa tidak ada internet yang sedang terhubung
				emptyTextView.setText(getString(R.string.no_internet_connection));
			}
		}

		// Set refresh listener ke swipe to refresh layout fragment movie
		fragmentMovieSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				// Check if activity + application exists
				if(Objects.requireNonNull(getActivity()).getApplication() != null){
					// Set visiblity of views ketika sedang dalam meretrieve data
					recyclerView.setVisibility(View.INVISIBLE);
					progressBar.setVisibility(View.VISIBLE);
					emptyTextView.setVisibility(View.GONE);
					// Connectivity manager untuk mengecek state dari network connectivity
					ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
					// Network Info object untuk melihat ada data network yang aktif
					NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
					// Cek jika ada network connection
					if(networkInfo != null && networkInfo.isConnected()){
						if(searchMovieViewModel != null){
							// Create observer that return ArrayList that takes ArrayList<MovieItem>
							searchMovieObserver = createObserver();
							// Calling LiveData from ViewModel (since LiveData is a part of ViewModel) then observe the LiveData by putting observer
							searchMovieViewModel.getSearchMovies().observe(SearchMovieFragment.this, searchMovieObserver);
						} else {
							// Create Viewmodel object
							searchMovieViewModel = ViewModelProviders.of(SearchMovieFragment.this, new SearchMovieViewModelFactory(getActivity().getApplication(), moviekeywordResult)).get(SearchMovieViewModel.class);
							// Create observer that return ArrayList that takes ArrayList<MovieItem>
							searchMovieObserver = createObserver();
							// Calling LiveData from ViewModel (since LiveData is a part of ViewModel) then observe the LiveData by putting observer
							searchMovieViewModel.getSearchMovies().observe(SearchMovieFragment.this, searchMovieObserver);
						}
					} else {
						// Progress bar into gone and recycler view into invisible as the data finished on loading
						progressBar.setVisibility(View.GONE);
						recyclerView.setVisibility(View.INVISIBLE);
						// Set empty view visibility into visible
						emptyTextView.setVisibility(View.VISIBLE);
						// Empty text view yg menunjukkan bahwa tidak ada internet yang sedang terhubung
						emptyTextView.setText(getString(R.string.no_internet_connection));
					}
				}
				// Set refresh into false, menandakan bahwa swipe to refresh layout
				// sudah tidak dalam mode refreshing lagi
				fragmentMovieSwipeRefreshLayout.setRefreshing(false);
			}
		});
		
	}

	/**
	 * Method ini berguna untuk:
	 * - membuat option menu di {@link SearchMovieFragment}
	 * - submit keyword di {@link SearchView} untuk load data bedasarkan keyword yg disubmit
	 * @param menu Menu object
	 * @param inflater Menu inflater untuk inflate Layout xml ke Menu object
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate menu search movie
		inflater.inflate(R.menu.menu_search, menu);
		// Check if activity exists
		if(getActivity() != null){
			// Line ini berguna untuk memasang listener untuk SearchView
			SearchManager movieSearchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
			// Cek jika search manager exists
			if(movieSearchManager != null){
				SearchView movieSearchView  = (SearchView) (menu.findItem(R.id.search)).getActionView();
				movieSearchView.setSearchableInfo(movieSearchManager.getSearchableInfo(getActivity().getComponentName()));
				movieSearchView.setQueryHint(getResources().getString(R.string.search));
				// Listener untuk text dari searchview
				movieSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
					// Method ini melakukan aksi ketika tekan submit di keyboard
					@Override
					public boolean onQueryTextSubmit(String query) {
						moviekeywordResult = query;
						movieSearchKeyword.setText(moviekeywordResult);
						
						// Ketika kita submit query text, maka data akan melakukan loading kembali
						recyclerView.setVisibility(View.INVISIBLE);
						progressBar.setVisibility(View.VISIBLE);
						emptyTextView.setVisibility(View.GONE);

						// Cek jika activity exists
						if(getActivity() != null){
							// Connectivity manager untuk mengecek state dari network connectivity
							ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
							// Network Info object untuk melihat ada data network yang aktif
							NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
							// Cek jika ada network yg connected
							if(networkInfo != null && networkInfo.isConnected()){
								// Cek jika search movie view model itu exist, jika tidak, maka kita buat baru
								if(searchMovieViewModel != null){
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
								} else {
									// Create Viewmodel object (berlaku jika object Viewmodel masih baru)
									searchMovieViewModel = ViewModelProviders.of(SearchMovieFragment.this, new SearchMovieViewModelFactory(getActivity().getApplication(), moviekeywordResult)).get(SearchMovieViewModel.class);
									// Buat Observer object untuk dapat merespon changes dengan mengupdate UI
									searchMovieObserver = createObserver();
									// Replace sebuah observer ke observer yang baru untuk menampilkan LiveData yang baru
									searchMovieViewModel.getSearchMovies().observe(SearchMovieFragment.this, searchMovieObserver);
								}

							} else {
								// Progress bar into gone and recycler view into invisible as the data finished on loading
								progressBar.setVisibility(View.GONE);
								recyclerView.setVisibility(View.INVISIBLE);
								// Set empty view visibility into visible
								emptyTextView.setVisibility(View.VISIBLE);
								// Empty text view yg menunjukkan bahwa tidak ada internet yang sedang terhubung
								emptyTextView.setText(getString(R.string.no_internet_connection));
							}
						}
						
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

	/**
	 * Method tsb berguna untuk membawa value dari Intent ke {@link DetailActivity}
	 * @param movieItem {@link MovieItem} dari {@link android.support.v7.widget.RecyclerView item}
	 * bedasarkan {@link MovieAdapter}
	 */
	private void showSelectedMovieItems(MovieItem movieItem){
		// Dapatkan id dan title bedasarkan ListView item
		int movieIdItem = movieItem.getId();
		String movieTitleItem = movieItem.getMovieTitle();
		int movieBooleanStateItem = 0;
		Uri movieUriItem = null;
		if(MainActivity.favoriteMovieItemArrayList.size() > 0){
			for(int i = 0; i < MainActivity.favoriteMovieItemArrayList.size(); i ++){
				// Cek jika movieIdItem itu cocok dengan item id yg ada di arraylist
				if(movieIdItem == MainActivity.favoriteMovieItemArrayList.get(i).getId()){
					// Get favorite boolean state value untuk transfer ke variable movieBooleanStateItem
					movieBooleanStateItem = MainActivity.favoriteMovieItemArrayList.get(i).getFavoriteBooleanState();
					movieUriItem = Uri.parse(MOVIE_FAVORITE_CONTENT_URI + "/"  + movieIdItem);
					break;
				}
			}
		}
		// Tentukan bahwa kita ingin membuka data Movie
		String modeItem = "open_movie_detail";
		// Boolean variable untuk mengetahui apakah kita membuka data dari widget
		boolean openFromWidget = false;
		// Create intent object agar ke DetailActivity yg merupakan activity tujuan
		Intent intentWithMovieIdData = new Intent(getContext(), DetailActivity.class);
		// Bawa data untuk disampaikan ke {@link DetailActivity}
		intentWithMovieIdData.putExtra(MOVIE_ID_DATA, movieIdItem);
		intentWithMovieIdData.putExtra(MOVIE_TITLE_DATA, movieTitleItem);
		intentWithMovieIdData.putExtra(MOVIE_BOOLEAN_STATE_DATA, movieBooleanStateItem);
		intentWithMovieIdData.putExtra(MODE_INTENT, modeItem);
		intentWithMovieIdData.putExtra(OPEN_FROM_WIDGET, openFromWidget);
		// Bawa Uri ke Intent
		intentWithMovieIdData.setData(movieUriItem);
		// Start activity ke DetailActivity
		startActivity(intentWithMovieIdData);
	}

	/**
	 * Method tsb di triggered ketika activity melakukan orientation changes/activity dimulai lagi
	 * Method tsb berguna untuk merestore state dari {@link LinearLayoutManager} dari
	 * onSaveInstanceState() method
	 */
	@Override
	public void onResume() {
		super.onResume();
		// Cek jika Parcelable itu exist, jika iya, maka update layout manager dengan memasukkan
		// Parcelable sebagai input parameter
		if(mMovieListState != null){
			searchMovieLinearLayoutManager.onRestoreInstanceState(mMovieListState);
		}
	}

	/**
	 * Method ini berguna untuk menyimpan scroll position dengan membawa state dari
	 * {@link LinearLayoutManager} yang berguna saat orientation change
	 * @param outState Bundle object untuk di bawa ke onActivityCreated (tempat untuk restore state)
	 */
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

	/**
	 * Method tsb berguna untuk membuat observer yang berhubungan dengan
	 * {@link android.arch.lifecycle.LiveData} dan handle empty data
	 * @return Observer yang menampung {@link ArrayList<MovieItem>}
	 * (data dari {@link android.arch.lifecycle.LiveData})
	 */
	public Observer<ArrayList<MovieItem>> createObserver(){
		// Buat observer yg gunanya untuk update UI
		return new Observer<ArrayList<MovieItem>>() {
			@Override
			public void onChanged(@Nullable final ArrayList<MovieItem> movieItems) {
				// Cek jika ada arraylist
				if(movieItems != null){
					// Cek jika arraylist ada data
					if(movieItems.size() > 0){
						// Ketika data selesai di load, maka kita akan mendapatkan data dan menghilangkan progress bar
						// yang menandakan bahwa loadingnya sudah selesai
						recyclerView.setVisibility(View.VISIBLE);
						progressBar.setVisibility(View.GONE);
						// Set empty view visibility into gone : doesnt take space and no content displayed
						emptyTextView.setVisibility(View.GONE);
						// Set data ke adapter
						movieAdapter.setMovieData(movieItems);
						// Set item click listener di dalam recycler view
						ItemClickSupport.addSupportToView(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
							// Implement interface method
							@Override
							public void onItemClicked(RecyclerView recyclerView, int position, View view) {
								// Panggil method showSelectedMovieItems untuk mengakses DetailActivity bedasarkan data yang ada
								showSelectedMovieItems(movieItems.get(position));
							}
						});
					} else { // kondisi jika data tidak ada
						// Set data into adapter
						movieAdapter.setMovieData(movieItems);
						// Set progress bar visibility into gone, indicating that data finished on loading
						progressBar.setVisibility(View.GONE);
						// Set recycler view visibility into visible: take space but doesnt display anything
						recyclerView.setVisibility(View.INVISIBLE);
						// Set empty view visibility into visible
						emptyTextView.setVisibility(View.VISIBLE);
						// Set empty view text
						emptyTextView.setText(getString(R.string.no_movie_data_shown));
					}
				}

			}
		};
	}
}
