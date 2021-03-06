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
import com.example.cataloguemoviefinal.LoadFavoriteTvShowCallback;
import com.example.cataloguemoviefinal.MainActivity;
import com.example.cataloguemoviefinal.R;
import com.example.cataloguemoviefinal.adapter.TvShowAdapter;
import com.example.cataloguemoviefinal.async.LoadFavoriteTvShowAsync;
import com.example.cataloguemoviefinal.entity.TvShowItem;
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
 * Class tersebut berguna untuk:
 * - menampilkan data berisi favorite tv show ketika connected ke internet bedasarkan data dari
 * {@link LoadFavoriteTvShowAsync}
 * - membuat intent ke {@link DetailActivity} ketika view object dari {@link RecyclerView} di click
 */
public class FavoriteTvShowFragment extends Fragment implements LoadFavoriteTvShowCallback {

    // Bind views
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
    // Initiate Swipe to refresh layout
    @BindView(R.id.fragment_tv_show_swipe_refresh_layout)
    SwipeRefreshLayout fragmentTvShowSwipeRefreshLayout;

    /**
     * Method ini di triggered pada saat {@link Fragment} pertama kali dibuat
     * Method ini berguna untuk membuat View bedasarkan layout xml fragment_tv_show
     *
     * @param inflater           LayoutInflater untuk inflate layout dari xml
     * @param container          ViewGroup yang menampung fragment (root view dari xml possibly)
     * @param savedInstanceState Bundle object untuk dapat handle orientation changes
     * @return View object untuk onViewCreated()
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tv_show, container, false);
        // Bind components to View
        ButterKnife.bind(this, view);
        return view;
    }

    /**
     * Method ini di triggered pada saat view dari {@link Fragment} dibuat
     * Method ini berguna untuk:
     * - Set recyclerView layout manager
     * - Set adapter ke recyclerView
     * - Set border ke setiap recyclerView item
     *
     * @param view               View hasil dari onCreateView
     * @param savedInstanceState bundle object untuk menghandle orientation change
     */
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
        recyclerView.setBackgroundColor(getResources().getColor(android.R.color.white));

        // Cek jika context itu ada
        if (getContext() != null) {
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
     *
     * @param savedInstanceState bundle object untuk menghandle orientation change
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Cek jika bundle savedInstanceState itu ada
        if (savedInstanceState != null) {
            // Retrieve array list parcelable untuk retrieve scroll position
            final ArrayList<TvShowItem> tvShowItemsList = savedInstanceState.getParcelableArrayList(TV_SHOW_LIST_STATE);
            // Cek jika array list exist
            if (tvShowItemsList != null) {
                if (tvShowItemsList.size() > 0) {
                    // Hilangkan progress bar agar tidak ada progress bar lagi setelah d rotate
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    // Set empty view visibility into gone
                    emptyTextView.setVisibility(View.GONE);
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
        if (getActivity() != null) {
            // Connectivity manager untuk mengecek state dari network connectivity
            ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            // Network Info object untuk melihat ada data network yang aktif
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            // Cek jika ada active network connection
            if (networkInfo != null && networkInfo.isConnected()) {
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

        // Set on refresh listener on fragment tv show
        fragmentTvShowSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            // Code ini di execute ketika sedang refresh
            @Override
            public void onRefresh() {
                // Cek jika activity exist
                if (getActivity() != null) {
                    // Connectivity manager untuk mengecek state dari network connectivity
                    ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                    // Network Info object untuk melihat ada data network yang aktif
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                    // Cek jika ada active network connection
                    if (networkInfo != null && networkInfo.isConnected()) {
                        new LoadFavoriteTvShowAsync(getActivity(), FavoriteTvShowFragment.this).execute();
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
                // Set refresh jadi false menandakan bahwa datanya sudah di load
                fragmentTvShowSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    // Callback method dari Interface LoadFavoriteTvShowCallback

    /**
     * Method ini berguna untuk menyiapkan data Favorite tv show array list
     */
    @Override
    public void favoriteTvShowPreExecute() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Set progress bar visibility into visible and recyclerview visibility into visible
                    // to prepare loading data
                    progressBar.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                    emptyTextView.setVisibility(View.GONE);
                }
            });
        }
    }

    /**
     * Method ini berguna utk:
     * - mempersiapkan data yang ada di {@link ArrayList<TvShowItem>}
     * yang membuat setiap data dari ArrayList tsb menjadi {@link RecyclerView} item
     * - Ketika di click, panggil showSelectedTvShowItems() agar dapat dibawa ke
     * {@link DetailActivity}
     * - Menghandle empty data di Favorite TV Show
     *
     * @param tvShowItems Cursor hasil dari doInBackground() method
     */
    @Override
    public void favoriteTvShowPostExecute(Cursor tvShowItems) {
        // Cek jika array list favorite ada data
        if (MainActivity.favoriteTvShowItemArrayList.size() > 0) {
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

    /**
     * Method tsb berguna untuk membawa value dari Intent ke {@link DetailActivity}
     *
     * @param tvShowItem {@link TvShowItem} dari {@link android.support.v7.widget.RecyclerView item}
     *                   bedasarkan {@link TvShowAdapter}
     */
    private void showSelectedTvShowItems(TvShowItem tvShowItem) {
        // Dapatkan id dan title bedasarkan item di ArrayList
        int tvShowIdItem = tvShowItem.getId();
        String tvShowNameItem = tvShowItem.getTvShowName();
        int tvBooleanStateItem = tvShowItem.getFavoriteBooleanState();
        // Tentukan bahwa kita ingin membuka data TV Show
        String modeItem = "open_tv_show_detail";
        // Boolean variable untuk mengetahui apakah kita membuka data dari widget
        boolean openFromWidget = false;
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
        intentWithTvShowIdData.putExtra(OPEN_FROM_WIDGET, openFromWidget);
        // Bawa Uri ke Intent
        intentWithTvShowIdData.setData(tvShowUriItem);
        // Start activity ke activity tujuan
        startActivity(intentWithTvShowIdData);
    }


    /**
     * Method ini berguna untuk menyimpan scroll position dengan membawa ArrayList parcelable
     * (kebetulan {@link TvShowItem} adalah {@link android.os.Parcelable} object) yang berguna pada
     * saat orientation change
     *
     * @param outState Bundle object untuk di bawa ke onActivityCreated (tempat untuk restore state)
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Put ArrayList into Bundle for handling orientation change
        outState.putParcelableArrayList(TV_SHOW_LIST_STATE, tvShowAdapter.getTvShowData());
    }

}
