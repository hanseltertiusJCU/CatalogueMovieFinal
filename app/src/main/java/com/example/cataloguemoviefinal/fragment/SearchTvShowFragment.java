package com.example.cataloguemoviefinal.fragment;


import android.app.SearchManager;
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
import com.example.cataloguemoviefinal.adapter.TvShowAdapter;
import com.example.cataloguemoviefinal.entity.TvShowItem;
import com.example.cataloguemoviefinal.factory.SearchTvShowViewModelFactory;
import com.example.cataloguemoviefinal.model.SearchTvShowViewModel;
import com.example.cataloguemoviefinal.support.ItemClickSupport;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.cataloguemoviefinal.BuildConfig.MODE_INTENT;
import static com.example.cataloguemoviefinal.BuildConfig.OPEN_FROM_WIDGET;
import static com.example.cataloguemoviefinal.BuildConfig.TV_SHOW_BOOLEAN_STATE_DATA;
import static com.example.cataloguemoviefinal.BuildConfig.TV_SHOW_ID_DATA;
import static com.example.cataloguemoviefinal.BuildConfig.TV_SHOW_LIST_STATE;
import static com.example.cataloguemoviefinal.BuildConfig.TV_SHOW_NAME_DATA;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_FAVORITE_CONTENT_URI;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchTvShowFragment extends Fragment{
	
	// Bind Views
	@BindView(R.id.rv_tv_shows_item_list)
	RecyclerView recyclerView;
	@BindView(R.id.progress_bar)
	ProgressBar progressBar;
	// TextView buat empty state text
	@BindView(R.id.tv_show_empty_state_text)
	TextView emptyTextView;
	@BindView(R.id.tv_show_search_keyword_content)
	TextView tvShowSearchKeyword;
	private TvShowAdapter tvShowAdapter;
	// Bikin parcelable yang berguna untuk menyimpan lalu merestore position
	private Parcelable mTvShowListState = null;
	// Bikin linearlayout manager untuk dapat call onsaveinstancestate method
	private LinearLayoutManager searchTvShowLinearLayoutManager;
	// Constant untuk key untuk keyword search result di tv show
	private static final String TV_KEYWORD_RESULT = "tv_keyword_result";
	// Value untuk keyword di search tv show
	private String tvKeywordResult;
	// Initiate Viewmodel dan componentnya
	SearchTvShowViewModel searchTvShowViewModel;
	Observer<ArrayList<TvShowItem>> searchTvShowObserver;
	// Initiate Swipe to refresh layout
	@BindView(R.id.fragment_tv_show_swipe_refresh_layout)
	SwipeRefreshLayout fragmentTvShowSwipeRefreshLayout;

	public SearchTvShowFragment() {
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
		View view = inflater.inflate(R.layout.fragment_tv_show, container, false);
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
		searchTvShowLinearLayoutManager = new LinearLayoutManager(getContext());
		// Set ukuran data dari setiap recycler view sama
		recyclerView.setHasFixedSize(true);
		// Kita menggunakan LinearLayoutManager berorientasi vertical untuk RecyclerView
		recyclerView.setLayoutManager(searchTvShowLinearLayoutManager);
		
		// Initiate tv show adapter
		tvShowAdapter = new TvShowAdapter(getContext());
		// Notify when data changed into adapter
		tvShowAdapter.notifyDataSetChanged();
		
		// Set empty adapter agar dapat di rotate
		recyclerView.setAdapter(tvShowAdapter);
		
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
		// Cek jika ada bundle savedinstancestate
		if(savedInstanceState != null){
			// Retrieve search keyword
			tvKeywordResult = savedInstanceState.getString(TV_KEYWORD_RESULT);
			tvShowSearchKeyword.setText(tvKeywordResult);
			// Retrieve parcelable to restore scroll position
			mTvShowListState = savedInstanceState.getParcelable(TV_SHOW_LIST_STATE);

		} else {
			tvKeywordResult = "flash"; // Default value
			tvShowSearchKeyword.setText(tvKeywordResult);
		}

		// Cek jika activity dan application exist
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
				searchTvShowViewModel = ViewModelProviders.of(this, new SearchTvShowViewModelFactory(getActivity().getApplication(), tvKeywordResult)).get(SearchTvShowViewModel.class);
				// Create observer that return ArrayList that takes ArrayList<TvShowItem>
				searchTvShowObserver = createObserver();
				// Calling LiveData from ViewModel (since LiveData is a part of ViewModel)
				searchTvShowViewModel.getSearchTvShows().observe(this, searchTvShowObserver);
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

		// Set refresh listener
		fragmentTvShowSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				// Cek jika activity dan application exist
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
						// Cek jika view model dari tv show itu exist
						if(searchTvShowViewModel != null){
							// Create observer that return ArrayList that takes ArrayList<TvShowItem>
							searchTvShowObserver = createObserver();
							// Calling LiveData from ViewModel (since LiveData is a part of ViewModel)
							searchTvShowViewModel.getSearchTvShows().observe(SearchTvShowFragment.this, searchTvShowObserver);
						} else {
							// Create Viewmodel object
							searchTvShowViewModel = ViewModelProviders.of(SearchTvShowFragment.this, new SearchTvShowViewModelFactory(getActivity().getApplication(), tvKeywordResult)).get(SearchTvShowViewModel.class);
							// Create observer that return ArrayList that takes ArrayList<TvShowItem>
							searchTvShowObserver = createObserver();
							// Calling LiveData from ViewModel (since LiveData is a part of ViewModel)
							searchTvShowViewModel.getSearchTvShows().observe(SearchTvShowFragment.this, searchTvShowObserver);
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
				// Set refresh into false, menandakan bahwa proses refresh sudah selesai
				fragmentTvShowSwipeRefreshLayout.setRefreshing(false);
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
		// Inflate menu search tv show
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
								if(searchTvShowViewModel != null){
									// Call setter method untuk merubah value parameter di ViewModel
									searchTvShowViewModel.setTvShowSearchKeyword(tvKeywordResult);
									// Recall live data, kesannya itu kyk merubah parameter dari ViewModelFactory
									searchTvShowViewModel.searchTvShowRecall();
									// Buat Observer object untuk dapat merespon changes dengan mengupdate UI
									searchTvShowObserver = createObserver();
									// Replace sebuah observer ke observer yang baru untuk menampilkan LiveData yang baru
									searchTvShowViewModel.getSearchTvShows().observe(SearchTvShowFragment.this, searchTvShowObserver);
									// Reset recyclerview position ke awal setelah retrieve keyword baru
									recyclerView.smoothScrollToPosition(0);
								} else {
									// Create Viewmodel object
									searchTvShowViewModel = ViewModelProviders.of(SearchTvShowFragment.this, new SearchTvShowViewModelFactory(getActivity().getApplication(), tvKeywordResult)).get(SearchTvShowViewModel.class);
									// Create observer that return ArrayList that takes ArrayList<TvShowItem>
									searchTvShowObserver = createObserver();
									// Calling LiveData from ViewModel (since LiveData is a part of ViewModel)
									searchTvShowViewModel.getSearchTvShows().observe(SearchTvShowFragment.this, searchTvShowObserver);
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
	 * @param tvShowItem {@link TvShowItem} dari {@link android.support.v7.widget.RecyclerView item}
	 * bedasarkan {@link TvShowItem}
	 */
	private void showSelectedTvShowItems(TvShowItem tvShowItem){
		// Dapatkan id dan title bedasarkan ListView item
		int tvShowIdItem = tvShowItem.getId();
		String tvShowNameItem = tvShowItem.getTvShowName();
		int tvShowBooleanStateItem = 0;
		Uri tvShowUriItem = null;
		if(MainActivity.favoriteTvShowItemArrayList.size() > 0){
			for(int i = 0; i < MainActivity.favoriteTvShowItemArrayList.size(); i++){
				// Cek jika tvShowIdItem itu cocok dengan item id yg ada di ArrayList
				if(tvShowIdItem == MainActivity.favoriteTvShowItemArrayList.get(i).getId()){
					// Set boolean state dan URI
					tvShowBooleanStateItem = MainActivity.favoriteTvShowItemArrayList.get(i).getFavoriteBooleanState();
					tvShowUriItem = Uri.parse(TV_SHOW_FAVORITE_CONTENT_URI + "/" + tvShowIdItem);
					break;
				}
			}
		}
		// Tentukan bahwa kita ingin membuka data TV Show
		String modeItem = "open_tv_show_detail";
		// Boolean variable untuk mengetahui apakah kita membuka data dari widget
		boolean openFromWidget = false;
		// Create intent object agar ke DetailActivity yg merupakan activity tujuan
		Intent intentWithTvShowIdData = new Intent(getActivity(), DetailActivity.class);
		// Bawa data untuk disampaikan ke {@link DetailActivity}
		intentWithTvShowIdData.putExtra(TV_SHOW_ID_DATA, tvShowIdItem);
		intentWithTvShowIdData.putExtra(TV_SHOW_NAME_DATA, tvShowNameItem);
		intentWithTvShowIdData.putExtra(TV_SHOW_BOOLEAN_STATE_DATA, tvShowBooleanStateItem);
		intentWithTvShowIdData.putExtra(MODE_INTENT, modeItem);
		intentWithTvShowIdData.putExtra(OPEN_FROM_WIDGET, openFromWidget);
		// Bawa Uri ke Intent
		intentWithTvShowIdData.setData(tvShowUriItem);
		// Start activity ke activity tujuan
		startActivity(intentWithTvShowIdData);
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
		if(mTvShowListState != null){
			searchTvShowLinearLayoutManager.onRestoreInstanceState(mTvShowListState);
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
		if(searchTvShowLinearLayoutManager != null){
			// Save list state/ scroll position dari list
			mTvShowListState = searchTvShowLinearLayoutManager.onSaveInstanceState();
			outState.putParcelable(TV_SHOW_LIST_STATE, mTvShowListState);
			// Save string keyword for retrieving string values in keyword tracker
			outState.putString(TV_KEYWORD_RESULT, tvKeywordResult);
		}
		
	}

	/**
	 * Method tsb berguna untuk membuat observer yang berhubungan dengan
	 * {@link android.arch.lifecycle.LiveData} dan handle empty data
	 * @return Observer yang menampung {@link ArrayList<TvShowItem>}
	 * (data dari {@link android.arch.lifecycle.LiveData})
	 */
	public Observer<ArrayList<TvShowItem>> createObserver(){
		// Buat observer yg gunanya untuk update UI
		return new Observer<ArrayList<TvShowItem>>() {
			@Override
			public void onChanged(@Nullable final ArrayList<TvShowItem> tvShowItems) {

				if(tvShowItems != null){
					if(tvShowItems.size() > 0){
						// Ketika data selesai di load, maka kita akan mendapatkan data dan menghilangkan progress bar
						// yang menandakan bahwa loadingnya sudah selesai
						recyclerView.setVisibility(View.VISIBLE);
						progressBar.setVisibility(View.GONE);
						emptyTextView.setVisibility(View.GONE);
						// Set data ke adapter
						tvShowAdapter.setTvShowData(tvShowItems);
						// Set item click listener di dalam recycler view
						ItemClickSupport.addSupportToView(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
							@Override
							public void onItemClicked(RecyclerView recyclerView, int position, View view) {
								// Click bwt show selected tv show items
								showSelectedTvShowItems(tvShowItems.get(position));
							}
						});
					} else {
						// Set data into adapter
						tvShowAdapter.setTvShowData(tvShowItems);
						// Set progress bar visibility into gone, indicating that data finished on loading
						progressBar.setVisibility(View.GONE);
						// Set recycler view visibility into invisible: take space but doesnt display anything
						recyclerView.setVisibility(View.INVISIBLE);
						// Set empty view visibility into visible
						emptyTextView.setVisibility(View.VISIBLE);
						// Set empty view text
						emptyTextView.setText(getString(R.string.no_tv_show_data_shown));
					}
				}
			}
		};
	}
}
