package com.example.cataloguemoviefinal.fragment;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
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
import com.example.cataloguemoviefinal.model.TvShowViewModel;
import com.example.cataloguemoviefinal.support.ItemClickSupport;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_FAVORITE_CONTENT_URI;

/**
 * A simple {@link Fragment} subclass.
 */
public class TvShowFragment extends Fragment{
	
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
	private TvShowAdapter tvShowAdapter;
	// Bikin parcelable yang berguna untuk menyimpan lalu merestore position
	private Parcelable mTvShowListState = null;
	// Bikin linearlayout manager untuk dapat call onsaveinstancestate dan onrestoreinstancestate method
	private LinearLayoutManager tvShowLinearLayoutManager;
	
	public TvShowFragment() {
		// Required empty public constructor
	}
	
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_tv_show, container, false);
		ButterKnife.bind(this, view);
		return view;
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		tvShowAdapter = new TvShowAdapter(getContext());
		tvShowAdapter.notifyDataSetChanged();
		
		// Set LinearLayoutManager object value dengan memanggil LinearLayoutManager constructor
		tvShowLinearLayoutManager = new LinearLayoutManager(getContext());
		// Ukuran data recycler view sama
		recyclerView.setHasFixedSize(true);
		// Kita menggunakan LinearLayoutManager berorientasi vertical untuk RecyclerView
		recyclerView.setLayoutManager(tvShowLinearLayoutManager);
		
		// Set visibility dari LinearLayout jadi GONE supaya tidak memakan tempat + tidak ada keyword result
		tvShowSearchKeywordResult.setVisibility(View.GONE);
		
		// Set empty adapter agar dapat di rotate
		recyclerView.setAdapter(tvShowAdapter);
		
		// Set background color untuk RecyclerView
		recyclerView.setBackgroundColor(getResources().getColor(R.color.colorWhite));
		
		if(getContext() != null) {
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
		
		// Cek jika Bundle exist, jika iya maka kita metretrieve list state as well as
		// list/item positions (scroll position)
		if(savedInstanceState != null) {
			mTvShowListState = savedInstanceState.getParcelable(TV_SHOW_LIST_STATE);
		}

		// todo: check for network connection
		// Dapatkan ViewModel yang tepat dari ViewModelProviders
		TvShowViewModel tvShowViewModel = ViewModelProviders.of(this).get(TvShowViewModel.class);
		
		// Panggil method createObserver untuk return Observer object
		Observer<ArrayList<TvShowItem>> tvShowObserver = createObserver();
		
		// Tempelkan Observer ke LiveData object
		tvShowViewModel.getTvShows().observe(this, tvShowObserver);
		
	}
	
	private void showSelectedTvShowItems(TvShowItem tvShowItem) {
		// Dapatkan id dan title bedasarkan ListView item
		int tvShowIdItem = tvShowItem.getId();
		String tvShowNameItem = tvShowItem.getTvShowName();
		int tvShowBooleanStateItem = 0;
		Uri tvShowUriItem = null;
		if(MainActivity.favoriteTvShowItemArrayList.size() > 0){
			for(int i = 0; i < MainActivity.favoriteTvShowItemArrayList.size(); i++){
				// Cek jika tvShowIdItem itu cocok dengan item id yg ada di ArrayList
				if(tvShowIdItem == MainActivity.favoriteTvShowItemArrayList.get(i).getId()){
					tvShowBooleanStateItem = MainActivity.favoriteTvShowItemArrayList.get(i).getFavoriteBooleanState();
					tvShowUriItem = Uri.parse(TV_SHOW_FAVORITE_CONTENT_URI + "/" + tvShowIdItem);
					break;
				}
			}
		}
		// Tentukan bahwa kita ingin membuka data TV Show
		String modeItem = "open_tv_show_detail";
		// Create intent object agar ke DetailActivity yg merupakan activity tujuan
		Intent intentWithTvShowIdData = new Intent(getActivity(), DetailActivity.class);
		// Bawa data untuk disampaikan ke {@link DetailActivity}
		intentWithTvShowIdData.putExtra(TV_SHOW_ID_DATA, tvShowIdItem);
		intentWithTvShowIdData.putExtra(TV_SHOW_NAME_DATA, tvShowNameItem);
		intentWithTvShowIdData.putExtra(TV_SHOW_BOOLEAN_STATE_DATA, tvShowBooleanStateItem);
		intentWithTvShowIdData.putExtra(MODE_INTENT, modeItem);
		// Set Uri ke Intent
		intentWithTvShowIdData.setData(tvShowUriItem);
		// Start activity tujuan bedasarkan intent object dan bawa request code
		// REQUEST_CHANGE untuk onActivityResult
		startActivityForResult(intentWithTvShowIdData, DetailActivity.REQUEST_CHANGE);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		// Cek jika Parcelable itu exist, jika iya, maka update layout manager dengan memasukkan
		// Parcelable sebagai input parameter
		if(mTvShowListState != null) {
			tvShowLinearLayoutManager.onRestoreInstanceState(mTvShowListState);
		}
	}
	
	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		// Cek jika tvShowLinearLayoutManager itu ada, jika tidak maka tidak akan ngapa2in
		// di onSaveInstanceState
		if(tvShowLinearLayoutManager != null) {
			// Save list state/ scroll position dari list
			mTvShowListState = tvShowLinearLayoutManager.onSaveInstanceState();
			outState.putParcelable(TV_SHOW_LIST_STATE, mTvShowListState);
		}
	}
	
	// Method tsb berguna untuk membuat observer
	public Observer<ArrayList<TvShowItem>> createObserver() {
		// Buat Observer yang gunanya untuk update UI
		return new Observer<ArrayList<TvShowItem>>() {
			@Override
			public void onChanged(@Nullable final ArrayList<TvShowItem> tvShowItems) {
				// Cek jika array tv show item exist
				if(tvShowItems != null){
					// Cek jika array tv show item ada data
					if(tvShowItems.size() > 0){
						// Ketika data selesai di load, maka kita akan mendapatkan data dan menghilangkan progress bar
						// yang menandakan bahwa loadingnya sudah selesai
						recyclerView.setVisibility(View.VISIBLE);
						progressBar.setVisibility(View.GONE);
						// Set empty view visibility into gone : doesnt take space and no content displayed
						emptyTextView.setVisibility(View.GONE);
						// Set data ke Adapter
						tvShowAdapter.setTvShowData(tvShowItems);
						// Set item click listener di dalam recycler view
						ItemClickSupport.addSupportToView(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
							@Override
							public void onItemClicked(RecyclerView recyclerView, int position, View view) {
								// Panggil method showSelectedMovieItems untuk mengakses DetailActivity bedasarkan data yang ada
								showSelectedTvShowItems(tvShowItems.get(position));
							}
						});
					} else { // kondisi jika tidak ada data
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
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(data != null) {
			// Check for correct request code
			if(requestCode == DetailActivity.REQUEST_CHANGE) {
				// Check for result code
				if(resultCode == DetailActivity.RESULT_CHANGE) {
					// Tambahkan item ke adapter dan reset scroll position ke paling atas
					boolean changedDataState = data.getBooleanExtra(DetailActivity.EXTRA_CHANGED_STATE, false);
					// Cek jika value dari changedDataState itu true
					if(changedDataState) {
						if(Objects.requireNonNull(getActivity()).getSupportFragmentManager() != null) {
							// Dapatin position fragment dari FavoriteTvShowFragment di ViewPager since ViewPager menampung list dari Fragments
							FavoriteTvShowFragment favoriteTvShowFragment = (FavoriteTvShowFragment) getActivity().getSupportFragmentManager().getFragments().get(3);
							// Cek jika favoriteTvShowFragment itu ada
							if(favoriteTvShowFragment != null) {
								// Komunikasi dengan FavoriteTvShowFragment dengan memanggil onActivityResult method di FavoriteTvShowFragment
								favoriteTvShowFragment.onActivityResult(requestCode, resultCode, data);
							}
						}
					}
				}
			}
		}
	}
	
}
