package com.example.cataloguemoviefinal.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.example.cataloguemoviefinal.entity.MovieItem;
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

public class FavoriteMovieFragment extends Fragment implements LoadFavoriteMoviesCallback {

	// Bind Views
	@BindView(R.id.rv_movie_item_list)
	RecyclerView recyclerView;
	MovieAdapter movieAdapter;
	@BindView(R.id.progress_bar)
	ProgressBar progressBar;
	// TextView buat empty state text
	@BindView(R.id.movie_empty_state_text)
	TextView emptyTextView;
	// LinearLayout untuk atur visibility dari Search keyword
	@BindView(R.id.movie_search_keyword_result)
	LinearLayout movieSearchKeywordResult;
	// Initiate Swipe to refresh layout
	@BindView(R.id.fragment_movie_swipe_refresh_layout)
	SwipeRefreshLayout fragmentMovieSwipeRefreshLayout;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_movie, container, false);
		ButterKnife.bind(this, view);
		return view;
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		// Set Layout Manager into RecyclerView
		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		// Buat ukuran dr masing-masing item di RecyclerView menjadi sama
		recyclerView.setHasFixedSize(true);
		
		// Initialize movie adapter
		movieAdapter = new MovieAdapter(getContext());
		movieAdapter.notifyDataSetChanged();
		
		// Set visibility dari LinearLayout jadi GONE supaya tidak memakan tempat + tidak ada keyword result
		movieSearchKeywordResult.setVisibility(View.GONE);
		
		// Attach adapter ke RecyclerView agar bisa menghandle data untuk situasi orientation changes
		recyclerView.setAdapter(movieAdapter);
		
		// Set background color untuk RecyclerView
		recyclerView.setBackgroundColor(getResources().getColor(R.color.colorWhite));
		
		// Cek jika context itu ada
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
		// Cek jika bundle savedInstanceState itu ada
		if(savedInstanceState != null) {
			// Retrieve array list parcelable
			final ArrayList<MovieItem> movieItemList = savedInstanceState.getParcelableArrayList(MOVIE_LIST_STATE);
			// Cek jika array list itu ada
			if (movieItemList != null) {
				// Cek jika array list itu ada datanya
				if (movieItemList.size() > 0) {
					// Hilangkan progress bar agar tidak ada progress bar lagi setelah d rotate
					progressBar.setVisibility(View.GONE);
					recyclerView.setVisibility(View.VISIBLE);
					// Set empty view visibility into gone
					emptyTextView.setVisibility(View.GONE);
					// Set data ke adapter
					movieAdapter.setData(movieItemList);
					// Set item click listener di dalam recycler view agar item tsb dapat di click
					ItemClickSupport.addSupportToView(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
						@Override
						public void onItemClicked(RecyclerView recyclerView, int position, View view) {
							// Panggil method showSelectedMovieItems untuk mengakses DetailActivity bedasarkan data yang ada
							showSelectedMovieItems(movieItemList.get(position));
						}
					});
				} else {
					// Ketika tidak ada data untuk display, set RecyclerView ke
					// invisible dan progress bar menjadi tidak ada
					movieAdapter.setData(movieItemList);
					progressBar.setVisibility(View.GONE);
					recyclerView.setVisibility(View.INVISIBLE);
					// Set empty view visibility into visible
					emptyTextView.setVisibility(View.VISIBLE);
					// Set empty view text
					emptyTextView.setText(getString(R.string.no_favorite_movie_data_shown));
				}
			}
		}

		// Cek jika activity exist, line ini berguna untuk pertama kali activity dijalankan
		if(getActivity() != null){
			// Connectivity manager untuk mengecek state dari network connectivity
			ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
			// Network Info object untuk melihat ada data network yang aktif
			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
			// Cek jika ada network connection
			if(networkInfo != null && networkInfo.isConnected()){
				// Lakukan AsyncTask utk meretrieve ArrayList yg isinya data dari database
				new LoadFavoriteMoviesAsync(getActivity(), this).execute();
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

		// Set refresh listener untuk menghandle refresh event
		fragmentMovieSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			// Line ini berguna ketika fragment sedang di refresh
			@Override
			public void onRefresh() {
				// Cek jika activity exist, line ini berguna untuk ketika data ingin di refresh kembali
				if(getActivity() != null){
					// Connectivity manager untuk mengecek state dari network connectivity
					ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
					// Network Info object untuk melihat ada data network yang aktif
					NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
					// Cek jika ada network connection
					if(networkInfo != null && networkInfo.isConnected()){
						// Lakukan AsyncTask utk meretrieve ArrayList yg isinya data dari database
						new LoadFavoriteMoviesAsync(getActivity(), FavoriteMovieFragment.this).execute();
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
				// Set refresh jadi false menandakan bahwa datanya sudah di load
				fragmentMovieSwipeRefreshLayout.setRefreshing(false);
			}
		});
	}
	
	// Method tsb berguna untuk membawa value dari Intent ke Activity tujuan serta memanggil Activity tujuan
	private void showSelectedMovieItems(MovieItem movieItem) {
		// Dapatkan id dan title bedasarkan item di ArrayList
		int movieIdItem = movieItem.getId();
		String movieTitleItem = movieItem.getMovieTitle();
		int movieBooleanStateItem = movieItem.getFavoriteBooleanState();
		// Tentukan bahwa kita ingin membuka data Movie
		String modeItem = "open_movie_detail";
		// Create URI untuk bawa URI ke data di intent dengan row id value
		// content://com.example.cataloguemoviefinal/favorite_movies/id
		Uri movieUriItem = Uri.parse(MOVIE_FAVORITE_CONTENT_URI + "/" + movieIdItem);
		// Initiate intent
		Intent intentWithMovieIdData = new Intent(getContext(), DetailActivity.class);
		// Bawa data untuk disampaikan ke {@link DetailActivity}
		intentWithMovieIdData.putExtra(MOVIE_ID_DATA, movieIdItem);
		intentWithMovieIdData.putExtra(MOVIE_TITLE_DATA, movieTitleItem);
		intentWithMovieIdData.putExtra(MOVIE_BOOLEAN_STATE_DATA, movieBooleanStateItem);
		intentWithMovieIdData.putExtra(MODE_INTENT, modeItem);
		intentWithMovieIdData.setData(movieUriItem);
		// Start activity ke detail activity
		startActivity(intentWithMovieIdData);
	}

	// Callback method dari Interface LoadFavoriteMoviesCallback

	@Override
	public void favoriteMoviePreExecute() {
		if(getActivity() != null){
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// Set visiblity of views ketika sedang dalam meretrieve data, kesannya seperti data sedang loading
					recyclerView.setVisibility(View.INVISIBLE);
					progressBar.setVisibility(View.VISIBLE);
					emptyTextView.setVisibility(View.GONE);
				}
			});
		}
	}

	@Override
	public void favoriteMoviePostExecute(Cursor movieItems) {
		// cek jika array list favorite ada data
		if(MainActivity.favoriteMovieItemArrayList.size() > 0){
			// Ketika data selesai di load, maka kita akan mendapatkan data dan menghilangkan progress bar
			// yang menandakan bahwa loadingnya sudah selesai
			progressBar.setVisibility(View.GONE);
			recyclerView.setVisibility(View.VISIBLE);
			// Set recycler view scroll position ke 0, alias balik ke awal
			// (berguna ketika data berubah = load asynctask kembali)
			recyclerView.smoothScrollToPosition(0);
			// Set empty view visibility into gone
			emptyTextView.setVisibility(View.GONE);
			// Set data into adapter
			movieAdapter.setData(MainActivity.favoriteMovieItemArrayList);
			// Set item click listener di dalam recycler view
			ItemClickSupport.addSupportToView(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
				@Override
				public void onItemClicked(RecyclerView recyclerView, int position, View view) {
					showSelectedMovieItems(MainActivity.favoriteMovieItemArrayList.get(position));
				}
			});
		} else {
			// Ketika tidak ada data untuk display, set RecyclerView ke
			// invisible dan progress bar menjadi tidak ada
			movieAdapter.setData(MainActivity.favoriteMovieItemArrayList);
			progressBar.setVisibility(View.GONE);
			recyclerView.setVisibility(View.INVISIBLE);
			// Set empty view visibility into visible
			emptyTextView.setVisibility(View.VISIBLE);
			// Set empty view text
			emptyTextView.setText(getString(R.string.no_favorite_movie_data_shown));
		}
	}
	
	
	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		// Put ArrayList into Bundle for handling orientation change
		outState.putParcelableArrayList(MOVIE_LIST_STATE, movieAdapter.getmMovieData());
	}

}
