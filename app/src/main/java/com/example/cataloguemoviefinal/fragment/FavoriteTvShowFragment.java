package com.example.cataloguemoviefinal.fragment;

import android.content.Intent;
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
import android.widget.ProgressBar;

import com.example.cataloguemoviefinal.DetailActivity;
import com.example.cataloguemoviefinal.LoadFavoriteTvShowCallback;
import com.example.cataloguemoviefinal.R;
import com.example.cataloguemoviefinal.adapter.TvShowAdapter;
import com.example.cataloguemoviefinal.async.LoadFavoriteTvShowAsync;
import com.example.cataloguemoviefinal.database.FavoriteItemsHelper;
import com.example.cataloguemoviefinal.entity.TvShowItem;
import com.example.cataloguemoviefinal.support.ItemClickSupport;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteTvShowFragment extends Fragment implements LoadFavoriteTvShowCallback {
	// Key untuk membawa data ke intent (data tidak d private untuk dapat diapplikasikan di berbagai Fragments dan diakses ke {@link DetailActivity})
	public static final String TV_SHOW_ID_DATA = "TV_SHOW_ID_DATA";
	public static final String TV_SHOW_NAME_DATA = "TV_SHOW_NAME_DATA";
	public static final String TV_SHOW_BOOLEAN_STATE_DATA = "TV_SHOW_BOOLEAN_STATE_DATA";
	// Constant untuk represent mode agar membuka data tertentu
	public static final String MODE_INTENT = "mode_intent";
	// Bikin constant (key) yang merepresent Parcelable object
	private static final String TV_SHOW_LIST_STATE = "tvShowListState";
	// Array list untuk menyimpan data bedasarkan Database
	static ArrayList<TvShowItem> favTvShowListData;
	@BindView(R.id.rv_tv_shows_item_list)
	RecyclerView recyclerView;
	@BindView(R.id.progress_bar)
	ProgressBar progressBar;
	private TvShowAdapter tvShowAdapter;
	// Helper untuk membuka koneksi ke DB
	private FavoriteItemsHelper favoriteItemsHelper;
	
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Buka koneksi terhadap database ketika Fragment dibuat
		if(getActivity().getApplicationContext() != null) {
			favoriteItemsHelper = FavoriteItemsHelper.getInstance(getActivity().getApplicationContext());
			favoriteItemsHelper.open();
		}
	}
	
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
		// Cek jika bundle savedInstanceState itu ada
		if(savedInstanceState != null) {
			// Retrieve array list parcelable untuk retrieve scroll position
			final ArrayList<TvShowItem> tvShowItemsList = savedInstanceState.getParcelableArrayList(TV_SHOW_LIST_STATE);
			
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
				}
			}
		} else {
			new LoadFavoriteTvShowAsync(favoriteItemsHelper, this).execute();
		}
	}
	
	// Callback method dari Interface LoadFavoriteTvShowCallback
	@Override
	public void preExecute() {
		// Set progress bar visibility into visible and recyclerview visibility into visible
		// to prepare loading data
		progressBar.setVisibility(View.VISIBLE);
		recyclerView.setVisibility(View.INVISIBLE);
	}
	
	@Override
	public void postExecute(final ArrayList<TvShowItem> tvShowItems) {
		// Bikin ArrayList global variable sama dengan hasil dari AsyncTask class
		favTvShowListData = tvShowItems;
		if(tvShowItems.size() > 0) {
			// Ketika data selesai di load, maka kita akan mendapatkan data dan menghilangkan progress bar
			// yang menandakan bahwa loadingnya sudah selesai
			progressBar.setVisibility(View.GONE);
			recyclerView.setVisibility(View.VISIBLE);
			// Set data into adapter
			tvShowAdapter.setTvShowData(tvShowItems);
			// Set item click listener di dalam recycler view
			ItemClickSupport.addSupportToView(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
				@Override
				public void onItemClicked(RecyclerView recyclerView, int position, View view) {
					showSelectedTvShowItems(tvShowItems.get(position));
				}
			});
		} else {
			// Ketika tidak ada data untuk display, set RecyclerView ke
			// invisible dan progress bar menjadi tidak ada
			progressBar.setVisibility(View.GONE);
			recyclerView.setVisibility(View.INVISIBLE);
			tvShowAdapter.setTvShowData(tvShowItems);
		}
	}
	
	// Method tsb berguna untuk membawa value dari Intent ke Activity tujuan serta memanggil Activity tujuan
	private void showSelectedTvShowItems(TvShowItem tvShowItem) {
		// Dapatkan id dan title bedasarkan item di ArrayList
		int tvShowIdItem = tvShowItem.getId();
		String tvShowNameItem = tvShowItem.getTvShowName();
		// Item position untuk mengakses arraylist specific position
		int itemPosition = 0;
		
		// Cek jika size dari ArrayList itu lebih dari 0
		if(favTvShowListData.size() > 0) {
			// Loop until getting exact position
			for(int i = 0 ; i < favTvShowListData.size() ; i++) {
				if(tvShowIdItem == favTvShowListData.get(i).getId()) {
					favTvShowListData.get(i).setFavoriteBooleanState(1);
					// Dapatin position dari arraylist jika idnya itu sama kyk id yg tersedia
					itemPosition = i;
					break;
				}
			}
		}
		// Tentukan bahwa kita ingin membuka data TV Show
		String modeItem = "open_tv_show_detail";
		
		// Initiate intent
		Intent intentWithTvShowIdData = new Intent(getActivity(), DetailActivity.class);
		// Bawa data untuk disampaikan ke {@link DetailActivity}
		intentWithTvShowIdData.putExtra(TV_SHOW_ID_DATA, tvShowIdItem);
		intentWithTvShowIdData.putExtra(TV_SHOW_NAME_DATA, tvShowNameItem);
		// Cek jika ArrayList ada data
		if(favTvShowListData.size() > 0) {
			intentWithTvShowIdData.putExtra(TV_SHOW_BOOLEAN_STATE_DATA, favTvShowListData.get(itemPosition).getFavoriteBooleanState());
		}
		intentWithTvShowIdData.putExtra(MODE_INTENT, modeItem);
		// Start activity tujuan bedasarkan intent object dan bawa request code
		// REQUEST_CHANGE untuk onActivityResult
		startActivityForResult(intentWithTvShowIdData, DetailActivity.REQUEST_CHANGE);
	}
	
	
	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		// Put ArrayList into Bundle for handling orientation change
		outState.putParcelableArrayList(TV_SHOW_LIST_STATE, tvShowAdapter.getTvShowData());
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
					boolean changedDataState = data.getBooleanExtra(DetailActivity.EXTRA_CHANGED_STATE, false);
					// Cek jika ada perubahan di tv show item data state
					if(changedDataState) {
						// Execute AsyncTask kembali
						new LoadFavoriteTvShowAsync(favoriteItemsHelper, this).execute();
						// Reset scroll position ke paling atas
						recyclerView.smoothScrollToPosition(0);
					}
				}
			}
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		// Menutup koneksi terhadap SQL
		favoriteItemsHelper.close();
	}
	
}
