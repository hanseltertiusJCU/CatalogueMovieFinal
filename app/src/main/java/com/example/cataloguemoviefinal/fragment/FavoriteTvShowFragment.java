package com.example.cataloguemoviefinal.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.TextView;

import com.example.cataloguemoviefinal.DetailActivity;
import com.example.cataloguemoviefinal.LoadFavoriteTvShowCallback;
import com.example.cataloguemoviefinal.MainActivity;
import com.example.cataloguemoviefinal.R;
import com.example.cataloguemoviefinal.adapter.TvShowAdapter;
import com.example.cataloguemoviefinal.async.LoadFavoriteTvShowAsync;
import com.example.cataloguemoviefinal.database.FavoriteItemsHelper;
import com.example.cataloguemoviefinal.entity.TvShowItem;
import com.example.cataloguemoviefinal.support.ItemClickSupport;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_FAVORITE_CONTENT_URI;

public class FavoriteTvShowFragment extends Fragment implements LoadFavoriteTvShowCallback {
	
	// Key untuk membawa data ke intent (data tidak d private untuk dapat diapplikasikan di berbagai Fragments dan diakses ke {@link DetailActivity})
	public static final String TV_SHOW_ID_DATA = "TV_SHOW_ID_DATA";
	public static final String TV_SHOW_NAME_DATA = "TV_SHOW_NAME_DATA";
	public static final String TV_SHOW_BOOLEAN_STATE_DATA = "TV_SHOW_BOOLEAN_STATE_DATA";
	// Constant untuk represent mode agar membuka data tertentu
	public static final String MODE_INTENT = "MODE_INTENT";
	// Bikin constant (key) yang merepresent Parcelable object
	private static final String TV_SHOW_LIST_STATE = "tvShowListState";
	@BindView(R.id.rv_tv_shows_item_list)
	RecyclerView recyclerView;
	@BindView(R.id.progress_bar)
	ProgressBar progressBar;
	// TextView buat empty state text
	@BindView(R.id.tv_show_empty_state_text)
	TextView emptyTextView;
	// LinearLayout untuk atur visibility dari Search keyword
	@BindView(R.id.tv_show_search_keyword_result)
	LinearLayout tvShowSearchKeywordResult;
	TvShowAdapter tvShowAdapter;
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_tv_show, container, false);
		// Bind components to View
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
		
		// Initialize tv show adapter
		tvShowAdapter = new TvShowAdapter(getContext());
		tvShowAdapter.notifyDataSetChanged();
		
		// Set visibility dari LinearLayout jadi GONE supaya tidak memakan tempat + tidak ada keyword result
		tvShowSearchKeywordResult.setVisibility(View.GONE);
		
		// Attach adapter ke RecyclerView agar bisa menghandle data untuk situasi orientation changes
		recyclerView.setAdapter(tvShowAdapter);
		
		// Set background color untuk RecyclerView
		recyclerView.setBackgroundColor(getResources().getColor(R.color.colorWhite));
		
		// Cek jika context itu ada
		if(getContext() != null) {
			// Buat object DividerItemDecoration dan set drawable untuk DividerItemDecoration
			DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
			itemDecorator.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.item_divider));
			// Set divider untuk RecyclerView items
			recyclerView.addItemDecoration(itemDecorator);
		}
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Set progress bar visibility into visible and recyclerview visibility into visible
		// to prepare loading data
		progressBar.setVisibility(View.VISIBLE);
		recyclerView.setVisibility(View.INVISIBLE);
		emptyTextView.setVisibility(View.GONE);
		// Cek jika bundle savedInstanceState itu ada
		if(savedInstanceState != null) {
			// Retrieve array list parcelable untuk retrieve scroll position
			final ArrayList<TvShowItem> tvShowItemsList = savedInstanceState.getParcelableArrayList(TV_SHOW_LIST_STATE);
			// Cek jika array list exist
			if(tvShowItemsList != null) {
				if(tvShowItemsList.size() > 0) {
					// Hilangkan progress bar agar tidak ada progress bar lagi setelah d rotate
					progressBar.setVisibility(View.GONE);
					recyclerView.setVisibility(View.VISIBLE);
					// Set data ke adapter
					tvShowAdapter.setTvShowData(tvShowItemsList);
					// Set item click listener di dalam recycler view agar item tsb dapat di click
					ItemClickSupport.addSupportToView(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
						@Override
						public void onItemClicked(RecyclerView recyclerView, int position, View view) {
							// Panggil method showSelectedTvShowItems untuk mengakses DetailActivity bedasarkan data yang ada
							showSelectedTvShowItems(tvShowItemsList.get(position));
						}
					});
				} else {
					// Ketika tidak ada data untuk display, set RecyclerView ke
					// invisible dan progress bar menjadi tidak ada
					tvShowAdapter.setTvShowData(tvShowItemsList);
					progressBar.setVisibility(View.GONE);
					recyclerView.setVisibility(View.INVISIBLE);
					// Set empty view visibility into visible
					emptyTextView.setVisibility(View.VISIBLE);
					// Set empty text
					emptyTextView.setText(getString(R.string.no_favorite_tv_show_data_shown));
				}
			}
		}

		// Cek jika activity exist
		if(getActivity() != null){
			// Connectivity manager untuk mengecek state dari network connectivity
			ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
			// Network Info object untuk melihat ada data network yang aktif
			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
			// Cek jika ada active network connection
			if(networkInfo != null && networkInfo.isConnected()){
				new LoadFavoriteTvShowAsync(getActivity(), this).execute();
			} else {
				// Progress bar into done and recycler view into invisible as the data finished on loading
				progressBar.setVisibility(View.GONE);
				recyclerView.setVisibility(View.INVISIBLE);
				// Set empty view visibility into visible
				emptyTextView.setVisibility(View.VISIBLE);
				// Empty text view yg menunjukkan bahwa tidak ada internet yang sedang terhubung
				emptyTextView.setText(getString(R.string.no_internet_connection));
			}
		}
	}
	
	// Callback method dari Interface LoadFavoriteTvShowCallback
	
	@Override
	public void favoriteTvShowPostExecute(Cursor tvShowItems) {
		// Cek jika array list favorite ada data
		if(MainActivity.favoriteTvShowItemArrayList.size() > 0) {
			// Ketika data selesai di load, maka kita akan mendapatkan data dan menghilangkan progress bar
			// yang menandakan bahwa loadingnya sudah selesai
			progressBar.setVisibility(View.GONE);
			recyclerView.setVisibility(View.VISIBLE);
			// Set empty view visibility into gone
			emptyTextView.setVisibility(View.GONE);
			// Set data into adapter
			tvShowAdapter.setTvShowData(MainActivity.favoriteTvShowItemArrayList);
			// Set item click listener di dalam recycler view
			ItemClickSupport.addSupportToView(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
				@Override
				public void onItemClicked(RecyclerView recyclerView, int position, View view) {
					showSelectedTvShowItems(MainActivity.favoriteTvShowItemArrayList.get(position));
				}
			});
		} else {
			// Ketika tidak ada data untuk display, set RecyclerView ke
			// invisible dan progress bar menjadi tidak ada
			tvShowAdapter.setTvShowData(MainActivity.favoriteTvShowItemArrayList);
			progressBar.setVisibility(View.GONE);
			recyclerView.setVisibility(View.INVISIBLE);
			// Set empty view visibility into visible
			emptyTextView.setVisibility(View.VISIBLE);
			// Set empty view text
			emptyTextView.setText(getString(R.string.no_favorite_tv_show_data_shown));
		}
	}
	
	// Method tsb berguna untuk membawa value dari Intent ke Activity tujuan serta memanggil Activity tujuan
	private void showSelectedTvShowItems(TvShowItem tvShowItem) {
		// Dapatkan id dan title bedasarkan item di ArrayList
		int tvShowIdItem = tvShowItem.getId();
		String tvShowNameItem = tvShowItem.getTvShowName();
		int tvBooleanStateItem = tvShowItem.getFavoriteBooleanState();
		// Tentukan bahwa kita ingin membuka data TV Show
		String modeItem = "open_tv_show_detail";
		// Create URI untuk bawa URI ke data di intent dengan row id value
		// content://com.example.cataloguemoviefinal/favorite_tv_shows/id
		Uri tvShowUriItem = Uri.parse(TV_SHOW_FAVORITE_CONTENT_URI + "/" + tvShowIdItem);
		// Initiate intent
		Intent intentWithTvShowIdData = new Intent(getActivity(), DetailActivity.class);
		// Bawa data untuk disampaikan ke {@link DetailActivity}
		intentWithTvShowIdData.putExtra(TV_SHOW_ID_DATA, tvShowIdItem);
		intentWithTvShowIdData.putExtra(TV_SHOW_NAME_DATA, tvShowNameItem);
		intentWithTvShowIdData.putExtra(TV_SHOW_BOOLEAN_STATE_DATA, tvBooleanStateItem);
		intentWithTvShowIdData.putExtra(MODE_INTENT, modeItem);
		// Bawa Uri ke Intent
		intentWithTvShowIdData.setData(tvShowUriItem);
		// Start activity ke activity tujuan
		startActivity(intentWithTvShowIdData);
	}
	
	
	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		// Put ArrayList into Bundle for handling orientation change
		outState.putParcelableArrayList(TV_SHOW_LIST_STATE, tvShowAdapter.getTvShowData());
	}
	
}
