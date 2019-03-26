package com.example.cataloguemoviefinal.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.cataloguemoviefinal.DetailActivity;
import com.example.cataloguemoviefinal.LoadFavoriteMoviesCallback;
import com.example.cataloguemoviefinal.MainActivity;
import com.example.cataloguemoviefinal.R;
import com.example.cataloguemoviefinal.adapter.MovieAdapter;
import com.example.cataloguemoviefinal.async.LoadFavoriteMoviesAsync;
import com.example.cataloguemoviefinal.database.FavoriteItemsHelper;
import com.example.cataloguemoviefinal.entity.MovieItem;
import com.example.cataloguemoviefinal.model.MovieViewModel;
import com.example.cataloguemoviefinal.observer.FavoriteMovieDataObserver;
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
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_FAVORITE_CONTENT_URI;
import static com.example.cataloguemoviefinal.helper.FavoriteMovieMappingHelper.mapCursorToFavoriteMovieArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieFragment extends Fragment{
	
	// Bind Views
	@BindView(R.id.rv_movie_item_list)
	RecyclerView recyclerView;
	@BindView(R.id.progress_bar)
	ProgressBar progressBar;
	// TextView buat empty state text
	@BindView(R.id.movie_empty_state_text)
	TextView emptyTextView;
	// LinearLayout untuk atur visibility dari Search keyword
	@BindView(R.id.movie_search_keyword_result)
	LinearLayout movieSearchKeywordResult;
	private MovieAdapter movieAdapter;
	// Bikin parcelable yang berguna untuk menyimpan lalu merestore position
	private Parcelable mMovieListState = null;
	// Bikin linearlayout manager untuk dapat call onsaveinstancestate method
	private LinearLayoutManager movieLinearLayoutManager;
	// Initiate ViewModel dan Componentnya
	MovieViewModel movieViewModel;
	Observer<ArrayList<MovieItem>> movieObserver;
	// Initiate Swipe to refresh layout
	@BindView(R.id.fragment_movie_swipe_refresh_layout)
	SwipeRefreshLayout fragmentMovieSwipeRefreshLayout;
	
	
	public MovieFragment() {
		// Required empty public constructor
	}
	
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_movie, container, false);
		ButterKnife.bind(this, view);
		return view;
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		// Initiate movie adapter
		movieAdapter = new MovieAdapter(getContext());
		// Notify when data changed into adapter
		movieAdapter.notifyDataSetChanged();
		
		// Set LinearLayoutManager object value dengan memanggil LinearLayoutManager constructor
		movieLinearLayoutManager = new LinearLayoutManager(getContext());
		// Ukuran data recycler view sama
		recyclerView.setHasFixedSize(true);
		// Kita menggunakan LinearLayoutManager berorientasi vertical untuk RecyclerView
		recyclerView.setLayoutManager(movieLinearLayoutManager);
		
		// Set visibility dari LinearLayout jadi GONE supaya tidak memakan tempat + tidak ada keyword result
		movieSearchKeywordResult.setVisibility(View.GONE);
		
		// Set empty adapter agar dapat di rotate
		recyclerView.setAdapter(movieAdapter);
		
		// Set background color untuk RecyclerView
		recyclerView.setBackgroundColor(getResources().getColor(android.R.color.white));
		
		if(getContext() != null) {
			// Buat object DividerItemDecoration dan set drawable untuk DividerItemDecoration
			DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
			itemDecorator.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(getContext(), R.drawable.item_divider)));
			// Set divider untuk RecyclerView items
			recyclerView.addItemDecoration(itemDecorator);
		}
		

	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Cek jika Bundle exist, jika iya maka kita metretrieve list state as well as
		// list/item positions (scroll position)
		if(savedInstanceState != null) {
			mMovieListState = savedInstanceState.getParcelable(MOVIE_LIST_STATE);
		}

		// Cek jika activity exists
		if(getActivity() != null){
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
				// Dapatkan ViewModel yang tepat dari ViewModelProviders
				movieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
				// Panggil method createObserver untuk return Observer object
				movieObserver = createObserver();
				// Tempelkan Observer ke LiveData object
				movieViewModel.getMovies().observe(this, movieObserver);
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

		// Set refresh listener ke swipe to refresh layout
		fragmentMovieSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				// Cek jika activity exists
				if(getActivity() != null){
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
						// Cek ketika view model exist, berarti jika ada,
						// kita tidak perlu create view model lagi dan
						// ini berguna ketika kita kita ingin load data ketika
						// viewmodel pas pertama kali dijalankan tidak ada
						if(movieViewModel != null){
							// Panggil method createObserver untuk return Observer object
							movieObserver = createObserver();
							// Tempelkan Observer ke LiveData object
							movieViewModel.getMovies().observe(MovieFragment.this, movieObserver);
						} else {
							// Dapatkan ViewModel yang tepat dari ViewModelProviders
							movieViewModel = ViewModelProviders.of(MovieFragment.this).get(MovieViewModel.class);
							// Panggil method createObserver untuk return Observer object
							movieObserver = createObserver();
							// Tempelkan Observer ke LiveData object
							movieViewModel.getMovies().observe(MovieFragment.this, movieObserver);
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
				// Set refreshing into false
				fragmentMovieSwipeRefreshLayout.setRefreshing(false);
			}
		});
		
	}
	
	private void showSelectedMovieItems(MovieItem movieItem) {
		// Dapatkan id dan title bedasarkan ListView item
		int movieIdItem = movieItem.getId();
		String movieTitleItem = movieItem.getMovieTitle();
		int movieBooleanStateItem = 0;
		Uri movieUriItem = null;
		// Cek jika ArrayList dari MainActivity itu ada isinya
		if(MainActivity.favoriteMovieItemArrayList.size() > 0){
			for(int i = 0; i < MainActivity.favoriteMovieItemArrayList.size(); i ++){
				if(movieIdItem == MainActivity.favoriteMovieItemArrayList.get(i).getId()){
					movieBooleanStateItem = MainActivity.favoriteMovieItemArrayList.get(i).getFavoriteBooleanState();
					movieUriItem = Uri.parse(MOVIE_FAVORITE_CONTENT_URI + "/" + movieIdItem);
					break;
				}
			}
		}
		// Tentukan bahwa kita ingin membuka data Movie
		String modeItem = "open_movie_detail";
		// Create intent object agar ke DetailActivity yg merupakan activity tujuan
		Intent intentWithMovieIdData = new Intent(getContext(), DetailActivity.class);
		// Bawa data untuk disampaikan ke {@link DetailActivity}
		intentWithMovieIdData.putExtra(MOVIE_ID_DATA, movieIdItem);
		intentWithMovieIdData.putExtra(MOVIE_TITLE_DATA, movieTitleItem);
		intentWithMovieIdData.putExtra(MOVIE_BOOLEAN_STATE_DATA, movieBooleanStateItem);
		intentWithMovieIdData.putExtra(MODE_INTENT, modeItem);
		// Set Uri ke Intent
		intentWithMovieIdData.setData(movieUriItem);
		// Start activity ke DetailActivity
		startActivity(intentWithMovieIdData);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		// Cek jika Parcelable itu exist, jika iya, maka update layout manager dengan memasukkan
		// Parcelable sebagai input parameter
		if(mMovieListState != null) {
			movieLinearLayoutManager.onRestoreInstanceState(mMovieListState);
		}
	}
	
	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		// Cek jika movieLinearLayoutManager itu ada, jika tidak maka kita tidak akan ngapa2in
		// di onSaveInstanceState
		if(movieLinearLayoutManager != null) {
			// Save list state/ scroll position dari list
			mMovieListState = movieLinearLayoutManager.onSaveInstanceState();
			outState.putParcelable(MOVIE_LIST_STATE, mMovieListState);
		}
		
	}
	
	// Method tsb berguna untuk membuat observer
	public Observer<ArrayList<MovieItem>> createObserver() {
		// Buat Observer yang gunanya untuk update UI
		return new Observer<ArrayList<MovieItem>>() {
			@Override
			public void onChanged(@Nullable final ArrayList<MovieItem> movieItems) {
				// Cek jika activity exist
				if(getActivity() != null){
                    // Connectivity manager untuk mengecek state dari network connectivity
                    ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                    // Network Info object untuk melihat ada data network yang aktif
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                    // Cek jika ada network connection
                    if(networkInfo != null && networkInfo.isConnected()){
                        // Cek jika ada arraylist
                        if(movieItems != null) {
							// Cek jika ada data di dalam arraylist
							if (movieItems.size() > 0) {
								// Ketika data selesai di load, maka kita akan mendapatkan data dan menghilangkan progress bar
								// yang menandakan bahwa loadingnya sudah selesai
								recyclerView.setVisibility(View.VISIBLE);
								progressBar.setVisibility(View.GONE);
								// Set empty view visibility into gone : doesnt take space and no content displayed
								emptyTextView.setVisibility(View.GONE);
								// Set data ke adapter
								movieAdapter.setData(movieItems);
								// Set item click listener di dalam recycler view
								ItemClickSupport.addSupportToView(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
									// Implement interface method
									@Override
									public void onItemClicked(RecyclerView recyclerView, int position, View view) {
										// Panggil method showSelectedMovieItems untuk mengakses DetailActivity bedasarkan data yang ada
										showSelectedMovieItems(movieItems.get(position));
									}
								});
							} else { // kondisi jika tidak ada data
								// Set data into adapter
								movieAdapter.setData(movieItems);
								// Set progress bar visibility into gone, indicating that data finished on loading
								progressBar.setVisibility(View.GONE);
								// Set recycler view visibility into invisible: take space but doesnt display anything
								recyclerView.setVisibility(View.INVISIBLE);
								// Set empty view visibility into visible
								emptyTextView.setVisibility(View.VISIBLE);
								// Set empty view text
								emptyTextView.setText(getString(R.string.no_movie_data_shown));

							}
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
			}
		};
	}
	
}