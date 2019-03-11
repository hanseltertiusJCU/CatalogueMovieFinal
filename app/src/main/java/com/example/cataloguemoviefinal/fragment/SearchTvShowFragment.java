package com.example.cataloguemoviefinal.fragment;


import android.app.SearchManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.cataloguemoviefinal.factory.SearchTvShowViewModelFactory;
import com.example.cataloguemoviefinal.model.SearchTvShowViewModel;
import com.example.cataloguemoviefinal.support.ItemClickSupport;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_FAVORITE_CONTENT_URI;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchTvShowFragment extends Fragment{
	
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
			tvKeywordResult = savedInstanceState.getString(TV_KEYWORD_RESULT);
			tvShowSearchKeyword.setText(tvKeywordResult);
			mTvShowListState = savedInstanceState.getParcelable(TV_SHOW_LIST_STATE);
		} else {
			tvKeywordResult = "flash"; // Default value
			tvShowSearchKeyword.setText(tvKeywordResult);
		}
		
		if(Objects.requireNonNull(getActivity()).getApplication() != null){
			searchTvShowViewModel = ViewModelProviders.of(this, new SearchTvShowViewModelFactory(getActivity().getApplication(), tvKeywordResult)).get(SearchTvShowViewModel.class);
			
			searchTvShowObserver = createObserver();
			
			searchTvShowViewModel.getSearchTvShows().observe(this, searchTvShowObserver);
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
						
						// Ketika kita submit query text, maka data akan melakukan loading kembali
						recyclerView.setVisibility(View.INVISIBLE);
						progressBar.setVisibility(View.VISIBLE);
						
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
		// Bawa Uri ke Intent
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
		if(mTvShowListState != null){
			searchTvShowLinearLayoutManager.onRestoreInstanceState(mTvShowListState);
		}
	}
	
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
							FavoriteTvShowFragment favoriteTvShowFragment = (FavoriteTvShowFragment) getActivity().getSupportFragmentManager().getFragments().get(3);
							// Cek jika favoriteMovieFragment itu ada
							if(favoriteTvShowFragment != null) {
								// Komunikasi dengan FavoriteMovieFragment dengan memanggil onActivityResult method di FavoriteMovieFragment
								favoriteTvShowFragment.onActivityResult(requestCode, resultCode, data);
							}
						}
					}
				}
			}
		}
	}
	
	public Observer<ArrayList<TvShowItem>> createObserver(){
		// Buat observer yg gunanya untuk update UI
		return new Observer<ArrayList<TvShowItem>>() {
			@Override
			public void onChanged(@Nullable final ArrayList<TvShowItem> tvShowItems) {
				// Ketika data selesai di load, maka kita akan mendapatkan data dan menghilangkan progress bar
				// yang menandakan bahwa loadingnya sudah selesai
				recyclerView.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.GONE);
				// Set data ke adapter
				tvShowAdapter.setTvShowData(tvShowItems);
				// Set item click listener di dalam recycler view
				ItemClickSupport.addSupportToView(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
					@Override
					public void onItemClicked(RecyclerView recyclerView, int position, View view) {
						if(tvShowItems != null){
							showSelectedTvShowItems(tvShowItems.get(position));
						}
					}
				});
				
			}
		};
	}
}
