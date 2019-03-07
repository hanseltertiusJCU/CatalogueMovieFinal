package com.example.cataloguemoviefinal.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.cataloguemoviefinal.DetailActivity;
import com.example.cataloguemoviefinal.LoadFavoriteMoviesCallback;
import com.example.cataloguemoviefinal.MainActivity;
import com.example.cataloguemoviefinal.R;
import com.example.cataloguemoviefinal.adapter.MovieAdapter;
import com.example.cataloguemoviefinal.async.LoadFavoriteMoviesAsync;
import com.example.cataloguemoviefinal.entity.MovieItem;
import com.example.cataloguemoviefinal.observer.FavoriteMovieDataObserver;
import com.example.cataloguemoviefinal.support.ItemClickSupport;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_FAVORITE_CONTENT_URI;
import static com.example.cataloguemoviefinal.helper.FavoriteMovieMappingHelper.mapCursorToFavoriteMovieArrayList;

public class FavoriteMovieFragment extends Fragment implements LoadFavoriteMoviesCallback {
	
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
	MovieAdapter movieAdapter;
	@BindView(R.id.progress_bar)
	ProgressBar progressBar;
	// LinearLayout untuk atur visibility dari Search keyword
	@BindView(R.id.movie_search_keyword_result)
	LinearLayout movieSearchKeywordResult;
	
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
			
			if(movieItemList != null) {
				if(movieItemList.size() > 0) {
					// Hilangkan progress bar agar tidak ada progress bar lagi setelah d rotate
					progressBar.setVisibility(View.GONE);
					recyclerView.setVisibility(View.VISIBLE);
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
				}
			}
		} else {
			// Lakukan AsyncTask utk meretrieve ArrayList yg isinya data dari database
			new LoadFavoriteMoviesAsync(getActivity(), this).execute();
		}
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
		// Start activity tujuan bedasarkan intent object
		startActivityForResult(intentWithMovieIdData, DetailActivity.REQUEST_CHANGE);
	}
	
	
	// Callback method dari Interface LoadFavoriteMoviesCallback
	@Override
	public void preExecute() {
		// Set progress bar visibility into visible and recyclerview visibility into visible to prepare loading data
		progressBar.setVisibility(View.VISIBLE);
		recyclerView.setVisibility(View.INVISIBLE);
	}
	
	@Override
	public void postExecute(Cursor movieItems) {
		
		if(MainActivity.favoriteMovieItemArrayList.size() > 0){
			// Ketika data selesai di load, maka kita akan mendapatkan data dan menghilangkan progress bar
			// yang menandakan bahwa loadingnya sudah selesai
			progressBar.setVisibility(View.GONE);
			recyclerView.setVisibility(View.VISIBLE);
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
		}
	}
	
	
	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		// Put ArrayList into Bundle for handling orientation change
		outState.putParcelableArrayList(MOVIE_LIST_STATE, movieAdapter.getmMovieData());
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// Cek jika Intent itu ada
		if(data != null) {
			// Check for correct request code
			if(requestCode == DetailActivity.REQUEST_CHANGE) {
				// Check for result code
				if(resultCode == DetailActivity.RESULT_CHANGE) {
					// Retrieve value dari boolean changedState {@link DetailActivity}
					boolean changedDataState = data.getBooleanExtra(DetailActivity.EXTRA_CHANGED_STATE, false);
					// Cek jika ada perubahan di movie item data state
					if(changedDataState) {
						// Execute AsyncTask kembali dengan getActivity() method sbg parameter karena
						// getActivity() return Activity (MainActivity) dan memanggil AsyncTask
						// ke Activity; Fragment ini berkomunikasi dgn MainActivity. Plus,
						// Activity extends Context yg merupakan parameter dari AsyncTask sehingga
						// Activity represent Context
						new LoadFavoriteMoviesAsync(getActivity(), this).execute();
						// Reset scroll position ke paling atas
						recyclerView.smoothScrollToPosition(0);
					}
				}
			}
		}
	}
}
