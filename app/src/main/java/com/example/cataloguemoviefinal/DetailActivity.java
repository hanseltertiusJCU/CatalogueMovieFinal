package com.example.cataloguemoviefinal;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.cataloguemoviefinal.entity.MovieItem;
import com.example.cataloguemoviefinal.entity.TvShowItem;
import com.example.cataloguemoviefinal.factory.DetailedMovieViewModelFactory;
import com.example.cataloguemoviefinal.factory.DetailedTvShowViewModelFactory;
import com.example.cataloguemoviefinal.model.DetailedMovieViewModel;
import com.example.cataloguemoviefinal.model.DetailedTvShowViewModel;
import com.example.cataloguemoviefinal.widget.FavoriteMovieItemWidget;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.provider.BaseColumns._ID;
import static com.example.cataloguemoviefinal.BuildConfig.EXTRA_CHANGED_STATE;
import static com.example.cataloguemoviefinal.BuildConfig.EXTRA_MOVIE_ITEM;
import static com.example.cataloguemoviefinal.BuildConfig.EXTRA_TV_SHOW_ITEM;
import static com.example.cataloguemoviefinal.BuildConfig.EXTRA_URI;
import static com.example.cataloguemoviefinal.BuildConfig.KEY_DRAWABLE_MARKED_AS_FAVORITE_STATE;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_DATE_ADDED_FAVORITE_COLUMN;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_FAVORITE_COLUMN;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_FAVORITE_CONTENT_URI;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_FILE_PATH_COLUMN;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_ORIGINAL_LANGUAGE_COLUMN;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_RATINGS_COLUMN;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_RELEASE_DATE_COLUMN;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_TITLE_COLUMN;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_DATE_ADDED_COLUMN;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_FAVORITE_COLUMN;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_FAVORITE_CONTENT_URI;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_FILE_PATH_COLUMN;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_FIRST_AIR_DATE_COLUMN;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_NAME_COLUMN;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_ORIGINAL_LANGUAGE_COLUMN;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_RATINGS_COLUMN;

/**
 * Kelas ini berguna untuk:
 * - Menampilkan informasi scr detail berupa selected movie item dari:
 * *){@link com.example.cataloguemoviefinal.fragment.MovieFragment}
 * *){@link com.example.cataloguemoviefinal.fragment.FavoriteMovieFragment}
 * *){@link com.example.cataloguemoviefinal.fragment.SearchMovieFragment}
 * maupun tv show item dari:
 * *){@link com.example.cataloguemoviefinal.fragment.TvShowFragment}
 * *){@link com.example.cataloguemoviefinal.fragment.FavoriteTvShowFragment}
 * *){@link com.example.cataloguemoviefinal.fragment.SearchTvShowFragment}
 * - Merubah data state ke favorite atau tidak melalui detailed boolean state value dari:
 * *){@link MovieItem}
 * *){@link TvShowItem}
 * Hal tersebut berlaku hanya ketika connected ke internet
 * - Memasukkan data {@link MovieItem} atau {@link TvShowItem} ke ContentProvider
 * - Mengetahui bahwa datanya itu dibuka dari {@link FavoriteMovieItemWidget} ataupun
 * {@link android.support.v4.app.Fragment} yang ada di {@link MainActivity}
 */
public class DetailActivity extends AppCompatActivity {
	// Setup views for informations in detailed movie/detailed tv show
	@BindView(R.id.detailed_poster_image)
	ImageView imageViewDetailedPosterImage;
	@BindView(R.id.detailed_first_info_text)
	TextView textViewDetailedFirstInfoText;
	@BindView(R.id.detailed_second_info_text)
	TextView textViewDetailedSecondInfoText;
	@BindView(R.id.detailed_third_info_text)
	TextView textViewDetailedThirdInfoText;
	@BindView(R.id.detailed_fourth_info_text)
	TextView textViewDetailedFourthInfoText;
	@BindView(R.id.detailed_fifth_info_text)
	TextView textViewDetailedFifthInfoText;
	@BindView(R.id.detailed_sixth_info_title)
	TextView textViewDetailedSixthInfoTitle;
	@BindView(R.id.detailed_sixth_info_text)
	TextView textViewDetailedSixthInfoText;
	@BindView(R.id.detailed_seventh_info_title)
	TextView textViewDetailedSeventhInfoTitle;
	@BindView(R.id.detailed_seventh_info_text)
	TextView textViewDetailedSeventhInfoText;
	@BindView(R.id.detailed_eighth_info_title)
	TextView textViewDetailedEighthInfoTitle;
	@BindView(R.id.detailed_eighth_info_text)
	TextView textViewDetailedEighthInfoText;
	@BindView(R.id.detailed_ninth_info_title)
	TextView textViewDetailedNinthInfoTitle;
	@BindView(R.id.detailed_ninth_info_text)
	TextView textViewDetailedNinthInfoText;

	// Set layout value untuk dapat menjalankan process loading data
	@BindView(R.id.detailed_progress_bar)
	ProgressBar detailedProgressBar;
	@BindView(R.id.detailed_content_info)
	ConstraintLayout detailedContentInfo;
	@BindView(R.id.detailed_app_bar)
	AppBarLayout detailedAppBarLayout;

	// Set empty text view
	@BindView(R.id.empty_detailed_info_text)
	TextView detailedEmptyTextView;

	// Set toolbar
	@BindView(R.id.detailed_toolbar)
	Toolbar detailedToolbar;

	// Setup coordinator layout for making snackbar
	@BindView(R.id.detailed_coordinator_layout)
	CoordinatorLayout detailedCoordinatorLayout;

	// Swipe to refresh layout untuk DetailActivity content
	@BindView(R.id.detailed_content_swipe_refresh_layout)
	SwipeRefreshLayout detailedContentSwipeRefreshLayout;

	// Layout for collapsing toolbar layout
	@BindView(R.id.detailed_toolbar_layout)
	CollapsingToolbarLayout detailedToolbarLayout;

	// Setup intent value untuk movie items
	private int detailedMovieId;
	private String detailedMovieTitle;
	private int detailedMovieFavoriteStateValue = 0;
	private int detailedMovieFavoriteStateValueComparison = 0;

	// Setup intent value untuk tv show items
	private int detailedTvShowId;
	private String detailedTvShowName;
	private int detailedTvShowFavoriteStateValue = 0;
	private int detailedTvShowFavoriteStateValueComparison = 0;

	// Setup boolean menu clickable state
	private boolean menuClickable = false;
	// Gunakan BuildConfig untuk menjaga credential
	private String baseImageUrl = BuildConfig.POSTER_IMAGE_ITEM_URL;
	// Drawable Global variable to handle orientation changes
	private int drawableMenuMarkedAsFavouriteResourceId;

	// Initiate MovieItem class untuk mengotak-atik value dr sebuah item di MovieItem class
	private MovieItem detailedMovieItem;
	// Initiate TvShowItem class untuk mengotak-atik value dr sebuah item di TvShowItem class
	private TvShowItem detailedTvShowItem;

	// String value untuk mengetahui mode data yg dibuka
	private String accessItemMode;
	// Boolean value untuk extra di Intent
	private boolean changedState;
	// Uri value untuk membaca data (jika data ada di favorite) ataupun insert data
	private Uri uri;

	// Boolean untuk mengetahui apakah kita membuka activity ini melalui Widget
	private boolean openDataFromWidget;

	// Viewmodel dan Observer untuk detailed movie
    DetailedMovieViewModel detailedMovieViewModel;
    Observer<ArrayList<MovieItem>> detailedMovieObserver;

    // Viewmodel dan Observer untuk detailed tv show
    DetailedTvShowViewModel detailedTvShowViewModel;
    Observer<ArrayList<TvShowItem>> detailedTvShowObserver;

    // Initiate snackbar
	Snackbar snackbarMessage;

    /**
     * Method ini di trigger ketika app membuat detail activity
     * @param savedInstanceState
     */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		// Bind View
		ButterKnife.bind(this);

		// Set action bar bedasarkan toolbar
		setSupportActionBar(detailedToolbar);

		// Buat typeface untuk sebagai font untuk CollapsingToolbarLayout
		Typeface typeface = ResourcesCompat.getFont(this, R.font.raleway_bold);

		// Set expanded and collapsed mode font in CollapsingToolbarLayout
		detailedToolbarLayout.setExpandedTitleTypeface(typeface);
		detailedToolbarLayout.setCollapsedTitleTypeface(typeface);

		accessItemMode = getIntent().getStringExtra(BuildConfig.MODE_INTENT);
		openDataFromWidget = getIntent().getBooleanExtra(BuildConfig.OPEN_FROM_WIDGET, false);

		// Cek untuk mode yg tepat
		if(accessItemMode.equals("open_movie_detail")) {
			// Get intent untuk mendapatkan id, title serta favorite movie state dari {@link MainActivity}
			detailedMovieId = getIntent().getIntExtra(BuildConfig.MOVIE_ID_DATA, 0);
			detailedMovieTitle = getIntent().getStringExtra(BuildConfig.MOVIE_TITLE_DATA);
			detailedMovieFavoriteStateValueComparison = getIntent().getIntExtra(BuildConfig.MOVIE_BOOLEAN_STATE_DATA, 0);
		} else if(accessItemMode.equals("open_tv_show_detail")) {
			// Get intent untuk mendapatkan id, title serta favorite tv show state dari {@link MainActivity}
			detailedTvShowId = getIntent().getIntExtra(BuildConfig.TV_SHOW_ID_DATA, 0);
			detailedTvShowName = getIntent().getStringExtra(BuildConfig.TV_SHOW_NAME_DATA);
			detailedTvShowFavoriteStateValueComparison = getIntent().getIntExtra(BuildConfig.TV_SHOW_BOOLEAN_STATE_DATA, 0);
		}

		// Get data dari intent di URI
		uri = getIntent().getData();

		// Jika ada URI yang dibawa, query bedasarkan URI dan buat MovieItem/TvShowItem object
		// yang membawa Cursor object
		if(uri != null){
			Cursor cursor = getContentResolver().query(uri, null, null, null, null);

			if(cursor != null){
				if(cursor.moveToFirst()){
					// If condition to accomodate which custom class object to create
					if(accessItemMode.equals("open_movie_detail")){
						detailedMovieItem = new MovieItem(cursor);
						cursor.close(); // Close connection ke Cursor untuk mencegah leak
					} else if(accessItemMode.equals("open_tv_show_detail")){
						detailedTvShowItem = new TvShowItem(cursor);
						cursor.close(); // Close connection ke Cursor untuk mencegah leak
					}
				}
			}
		}


		// Cek jika savedInstanceState itu ada, jika iya, restore drawable marked as favorite icon state
		if(savedInstanceState != null) {
			if(accessItemMode.equals("open_movie_detail")) {
				detailedMovieFavoriteStateValue = savedInstanceState.getInt(KEY_DRAWABLE_MARKED_AS_FAVORITE_STATE);
				changedState = savedInstanceState.getBoolean(EXTRA_CHANGED_STATE);
				// Bawa object Uri agar Uri tidak null pada saat rotate, sehingga mencegah run time error
				uri = savedInstanceState.getParcelable(EXTRA_URI);
				// Bawa object MovieItem agar MovieItem tidak null pada saat rotate, sehingga mencegah run time error (NullPointerException)
				detailedMovieItem = savedInstanceState.getParcelable(EXTRA_MOVIE_ITEM);
				// Tujuan dari line code ini adalah agar bs bawa ke result serta handle comparison value
				// dimana kedua hal tsb dapat menghandle situasi orientation changes
				if(changedState) { // Cek jika value dr changedState itu true
					if(detailedMovieFavoriteStateValue == 1){
						detailedMovieFavoriteStateValueComparison = 1; // Update comparison value
					} else {
						detailedMovieFavoriteStateValueComparison = 0;
					}
				}
			} else if(accessItemMode.equals("open_tv_show_detail")) {
				detailedTvShowFavoriteStateValue = savedInstanceState.getInt(KEY_DRAWABLE_MARKED_AS_FAVORITE_STATE);
				changedState = savedInstanceState.getBoolean(EXTRA_CHANGED_STATE);
				// Bawa object Uri agar Uri tidak null pada saat rotate, sehingga mencegah run time error
				uri = savedInstanceState.getParcelable(EXTRA_URI);
				// Bawa object TvShowItem agar TvShowItem tidak null pada saat rotate, sehingga mencegah run time error (NullPointerException)
				detailedTvShowItem = savedInstanceState.getParcelable(EXTRA_TV_SHOW_ITEM);
				// Tujuannya agar bs bawa ke result serta handle comparison value
				// dimana kedua hal tsb dapat menghandle situasi orientation changes
				if(changedState) { // Cek jika value dr changedState itu true
					if(detailedTvShowFavoriteStateValue == 1){
						detailedTvShowFavoriteStateValueComparison = 1; // Update comparison value
					} else {
						detailedTvShowFavoriteStateValueComparison = 0;
					}
				}
			}

		} else { // Jika tidak ada Bundle savedInstanceState
			if(accessItemMode.equals("open_movie_detail")) {
				// Valuenya dr MovieFavoriteState d samain sm comparison
				detailedMovieFavoriteStateValue = detailedMovieFavoriteStateValueComparison;
			} else if(accessItemMode.equals("open_tv_show_detail")) {
				// Valuenya dr TvShowFavoriteState d samain sm comparison
				detailedTvShowFavoriteStateValue = detailedTvShowFavoriteStateValueComparison;
			}
		}

		// Cek kalo ada action bar
		if(getSupportActionBar() != null) {
			// Set action bar title untuk DetailActivity
			if(accessItemMode.equals("open_movie_detail")) {
				getSupportActionBar().setTitle(detailedMovieTitle);
			} else if(accessItemMode.equals("open_tv_show_detail")) {
				getSupportActionBar().setTitle(detailedTvShowName);
			}
			// Set up button
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}

		// Mode untuk menangani ViewModel yg berbeda
		if(accessItemMode.equals("open_movie_detail")) {
			// Set visiblity of views ketika sedang dalam meretrieve data
			detailedContentInfo.setVisibility(View.INVISIBLE);
			detailedProgressBar.setVisibility(View.VISIBLE);
			detailedEmptyTextView.setVisibility(View.GONE);
			// Connectivity manager untuk mengecek state dari network connectivity
			ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
			// Network Info object untuk melihat ada data network yang aktif
			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
			if(networkInfo != null && networkInfo.isConnected()){
			    // Panggil MovieViewModel dengan menggunakan ViewModelFactory sebagai parameter tambahan (dan satu-satunya pilihan) selain activity
				// Buat ViewModel untuk detailedMovieInfo
				detailedMovieViewModel = ViewModelProviders.of(this, new DetailedMovieViewModelFactory(this.getApplication(), detailedMovieId)).get(DetailedMovieViewModel.class);
				// Buat observer object untuk mendisplay data ke UI
				// Buat Observer untuk detailedMovieInfo
				detailedMovieObserver = createDetailedMovieObserver();
				// Tempelkan Observer ke LiveData object
				detailedMovieViewModel.getDetailedMovie().observe(this, detailedMovieObserver);
			} else {
				// Progress bar into gone and recycler view into invisible as the data finished on loading
				detailedProgressBar.setVisibility(View.GONE);
				detailedContentInfo.setVisibility(View.INVISIBLE);
				// Set empty view visibility into visible
				detailedEmptyTextView.setVisibility(View.VISIBLE);
				// Empty text view yang menunjukkan bahwa tidak ada internet yang sedang terhubung
				detailedEmptyTextView.setText(getString(R.string.no_internet_connection));
			}
		} else if(accessItemMode.equals("open_tv_show_detail")) {
			// Set visiblity of views ketika sedang dalam meretrieve data
			detailedContentInfo.setVisibility(View.INVISIBLE);
			detailedProgressBar.setVisibility(View.VISIBLE);
			detailedEmptyTextView.setVisibility(View.GONE);
			// Connectivity manager untuk mengecek state dari network connectivity
			ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
			// Network Info object untuk melihat ada data network yang aktif
			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
			// Cek jika ada network connection
			if(networkInfo != null && networkInfo.isConnected()){
				// Panggil MovieViewModel dengan menggunakan ViewModelFactory sebagai parameter tambahan (dan satu-satunya pilihan) selain activity
				// Buat ViewModel untuk detailedTvShowInfo
				detailedTvShowViewModel = ViewModelProviders.of(this, new DetailedTvShowViewModelFactory(this.getApplication(), detailedTvShowId)).get(DetailedTvShowViewModel.class);
				// Buat observer object untuk mendisplay data ke UI
				// Buat Observer untuk detailedTvShowInfo
				detailedTvShowObserver = createDetailedTvShowObserver();
				// Tempelkan Observer ke LiveData object
				detailedTvShowViewModel.getDetailedTvShow().observe(this, detailedTvShowObserver);
			} else { // Kondisi jika tidak connected ke network
				// Progress bar into gone and recycler view into invisible as the data finished on loading
				detailedProgressBar.setVisibility(View.GONE);
				detailedContentInfo.setVisibility(View.INVISIBLE);
				// Set empty view visibility into visible
				detailedEmptyTextView.setVisibility(View.VISIBLE);
				// Empty text view yang menunjukkan bahwa tidak ada internet yang sedang terhubung
				detailedEmptyTextView.setText(getString(R.string.no_internet_connection));
			}
		}

		// Set listener ke swipe to refresh layout
        detailedContentSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            // Method ini trigger ketika sedang refresh
            @Override
            public void onRefresh() {
                // Mode untuk menangani ViewModel yg berbeda
                if(accessItemMode.equals("open_movie_detail")) {
					// Set visiblity of views ketika sedang dalam meretrieve data
					detailedContentInfo.setVisibility(View.INVISIBLE);
					detailedProgressBar.setVisibility(View.VISIBLE);
					detailedEmptyTextView.setVisibility(View.GONE);
                    // Connectivity manager untuk mengecek state dari network connectivity
                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                    // Network Info object untuk melihat ada data network yang aktif
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                    if(networkInfo != null && networkInfo.isConnected()){
                        // Cek ketika view model dari detailed movie itu ada, berarti ga perlu initiate viewmodel lagi
                        if(detailedMovieViewModel != null){
                            // Buat observer object untuk mendisplay data ke UI
                            detailedMovieObserver = createDetailedMovieObserver();
                            // Tempelkan Observer ke LiveData object
                            detailedMovieViewModel.getDetailedMovie().observe(DetailActivity.this, detailedMovieObserver);
                        } else {
                            // Panggil MovieViewModel dengan menggunakan ViewModelFactory sebagai parameter tambahan (dan satu-satunya pilihan) selain activity
                            detailedMovieViewModel = ViewModelProviders.of(DetailActivity.this, new DetailedMovieViewModelFactory(getApplication(), detailedMovieId)).get(DetailedMovieViewModel.class);
                            // Buat observer object untuk mendisplay data ke UI
                            detailedMovieObserver = createDetailedMovieObserver();
                            // Tempelkan Observer ke LiveData object
                            detailedMovieViewModel.getDetailedMovie().observe(DetailActivity.this, detailedMovieObserver);
                        }

                    } else {
                        // Progress bar into gone and recycler view into invisible as the data finished on loading
                        detailedProgressBar.setVisibility(View.GONE);
                        detailedContentInfo.setVisibility(View.INVISIBLE);
                        // Set empty view visibility into visible
                        detailedEmptyTextView.setVisibility(View.VISIBLE);
                        // Empty text view yang menunjukkan bahwa tidak ada internet yang sedang terhubung
                        detailedEmptyTextView.setText(getString(R.string.no_internet_connection));

                        // Line ini berguna untuk membuat clickable icon menjadi false,
						// alias ketika tidak ada internet
                        // Set clickable into false
                        menuClickable = false;
                        // Invalidate option menu for call on prepare option menu
                        invalidateOptionsMenu();
                    }
                } else if(accessItemMode.equals("open_tv_show_detail")) {
					// Set visiblity of views ketika sedang dalam meretrieve data
					detailedContentInfo.setVisibility(View.INVISIBLE);
					detailedProgressBar.setVisibility(View.VISIBLE);
					detailedEmptyTextView.setVisibility(View.GONE);
                    // Connectivity manager untuk mengecek state dari network connectivity
                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                    // Network Info object untuk melihat ada data network yang aktif
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                    // Cek jika ada network connection
                    if(networkInfo != null && networkInfo.isConnected()){
                        // Cek ketika view model dari detailed movie itu ada, berarti ga perlu initiate viewmodel lagi
                        if(detailedTvShowViewModel != null){
                            // Buat observer object untuk mendisplay data ke UI
                            detailedTvShowObserver = createDetailedTvShowObserver();
                            // Tempelkan Observer ke LiveData object
                            detailedTvShowViewModel.getDetailedTvShow().observe(DetailActivity.this, detailedTvShowObserver);
                        } else {
                            // Panggil MovieViewModel dengan menggunakan ViewModelFactory sebagai parameter tambahan (dan satu-satunya pilihan) selain activity
                            detailedTvShowViewModel = ViewModelProviders.of(DetailActivity.this, new DetailedTvShowViewModelFactory(getApplication(), detailedTvShowId)).get(DetailedTvShowViewModel.class);
                            // Buat observer object untuk mendisplay data ke UI
                            detailedTvShowObserver = createDetailedTvShowObserver();
                            // Tempelkan Observer ke LiveData object
                            detailedTvShowViewModel.getDetailedTvShow().observe(DetailActivity.this, detailedTvShowObserver);
                        }
                    } else { // Kondisi jika tidak connected ke network
                        // Progress bar into gone and recycler view into invisible as the data finished on loading
                        detailedProgressBar.setVisibility(View.GONE);
                        detailedContentInfo.setVisibility(View.INVISIBLE);
                        // Set empty view visibility into visible
                        detailedEmptyTextView.setVisibility(View.VISIBLE);
                        // Empty text view yang menunjukkan bahwa tidak ada internet yang sedang terhubung
                        detailedEmptyTextView.setText(getString(R.string.no_internet_connection));

						// Line ini berguna untuk membuat clickable icon menjadi false,
						// alias ketika tidak ada internet
                        // Set clickable into false
						menuClickable = false;
						// Invalidate option menu for call on prepare option menu
						invalidateOptionsMenu();
                    }
                }
                // Set refresh into false, meaning that datanya sudah tidak di load lagi
                detailedContentSwipeRefreshLayout.setRefreshing(false);
            }
        });


		// Add on offset changed listener ke AppBarLayout untuk mengatur
		// ketika app barnya itu gede/collapse
		detailedAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
			boolean isAppBarLayoutShow = false;
			int scrollRange = - 1;

			@Override
			public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
				// Jika scrollRange berada di posisi default atau -1, maka set value untuk scrollRange
				if(scrollRange == - 1) {
					scrollRange = appBarLayout.getTotalScrollRange();
				}

				// Jika scroll range dengan vertical offset (parameter) berjumlah 0, maka gedein
				// app bar layout
				if(scrollRange + verticalOffset == 0) {
					isAppBarLayoutShow = true;
				} else if(isAppBarLayoutShow) {
					// Collapse app bar layout jika booleannya true
					isAppBarLayoutShow = false;
				}

			}
		});
	}

	/**
	 * Method ini berguna untuk menampilkan data yang berisi detailed Movie observer
	 * @return Observer yang membawa ArrayList<MovieItem>
	 */
	private Observer<ArrayList<MovieItem>> createDetailedMovieObserver() {
		return new Observer<ArrayList<MovieItem>>() {
			@Override
			public void onChanged(@Nullable ArrayList<MovieItem> detailedMovieItems) {
				// Ketika data selesai di load, maka kita akan mendapatkan data dan menghilangkan progress bar
				// yang menandakan bahwa loadingnya sudah selesai
				detailedContentInfo.setVisibility(View.VISIBLE);
				detailedProgressBar.setVisibility(View.GONE);
				detailedEmptyTextView.setVisibility(View.GONE);

				// Cek jika ArrayList<MovieItem> ada
				if(detailedMovieItems != null) {
					// Cek jika ArrayList<MovieItem> ada datanya
					if(detailedMovieItems.size() > 0){
						// Line ini berguna untuk set semua data ke dalam detail activity

                        // Load image jika ada poster path
						Picasso.get().load(baseImageUrl + detailedMovieItems.get(0).getMoviePosterPath()).into(imageViewDetailedPosterImage);

						// Cek jika ada value dari variable
						if(detailedMovieItems.get(0).getMovieTitle() != null && !detailedMovieItems.get(0).getMovieTitle().isEmpty()){
							textViewDetailedFirstInfoText.setText(detailedMovieItems.get(0).getMovieTitle());
						} else {
							textViewDetailedFirstInfoText.setText(getString(R.string.detailed_movie_unknown_title)); // Set unknown placeholder value jika tidak ada value dari variable
						}

						// Cek jika ada value dari variable
						if(detailedMovieItems.get(0).getMovieTagline() != null && ! detailedMovieItems.get(0).getMovieTagline().isEmpty()){
							textViewDetailedSecondInfoText.setText(String.format("\"%s\"", detailedMovieItems.get(0).getMovieTagline()));
						} else {
							textViewDetailedSecondInfoText.setText(String.format("\"%s\"", getString(R.string.detailed_movie_unknown_tagline)));
						}

						// Set textview content in detailed movie runtime to contain a variety of different colors
						Spannable runtimeWord = new SpannableString(getString(R.string.span_movie_detail_runtime) + " ");
						runtimeWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, runtimeWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						textViewDetailedThirdInfoText.setText(runtimeWord);

						// Cek jika ada value dari variable
						if(detailedMovieItems.get(0).getMovieRuntime() != null && !detailedMovieItems.get(0).getMovieRuntime().isEmpty()){
							Spannable runtimeDetailedMovie = new SpannableString(detailedMovieItems.get(0).getMovieRuntime() + " ");
							runtimeDetailedMovie.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, runtimeDetailedMovie.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							textViewDetailedThirdInfoText.append(runtimeDetailedMovie);

							Spannable runtimeDetailedMovieMinutes = new SpannableString(getString(R.string.span_movie_detail_runtime_minutes));
							runtimeDetailedMovieMinutes.setSpan(new ForegroundColorSpan(Color.BLACK), 0, runtimeDetailedMovieMinutes.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							textViewDetailedThirdInfoText.append(runtimeDetailedMovieMinutes);
						} else {
							Spannable runtimeDetailedMovie = new SpannableString(getString(R.string.detailed_movie_unknown_runtime));
							runtimeDetailedMovie.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, runtimeDetailedMovie.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							textViewDetailedThirdInfoText.append(runtimeDetailedMovie);
						}

						// Set textview content in detailed movie status to contain a variety of different colors
						Spannable statusWord = new SpannableString(getString(R.string.span_movie_detail_status) + " ");
						statusWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, statusWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						textViewDetailedFourthInfoText.setText(statusWord);

						// Cek jika ada value dari variable
						if(detailedMovieItems.get(0).getMovieStatus() != null && !detailedMovieItems.get(0).getMovieStatus().isEmpty()){
							Spannable statusDetailedMovie = new SpannableString(detailedMovieItems.get(0).getMovieStatus());
							statusDetailedMovie.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, statusDetailedMovie.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							textViewDetailedFourthInfoText.append(statusDetailedMovie);
						} else {
							Spannable statusDetailedMovie = new SpannableString(getString(R.string.detailed_movie_unknown_status));
							statusDetailedMovie.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, statusDetailedMovie.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							textViewDetailedFourthInfoText.append(statusDetailedMovie);
						}


						// Set textview content in detailed movie rating to contain a variety of different colors
						Spannable ratingWord = new SpannableString(getString(R.string.span_movie_detail_rating) + " ");
						ratingWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ratingWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						textViewDetailedFifthInfoText.setText(ratingWord);

						// Cek jika ada value dari variable
						if(detailedMovieItems.get(0).getMovieRatings() != null && !detailedMovieItems.get(0).getMovieRatings().isEmpty()){
							Spannable ratingDetailedMovie = new SpannableString(detailedMovieItems.get(0).getMovieRatings());
							ratingDetailedMovie.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, ratingDetailedMovie.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							textViewDetailedFifthInfoText.append(ratingDetailedMovie);
						} else {
							Spannable ratingDetailedMovie = new SpannableString(getString(R.string.detailed_movie_default_value_ratings)); // Set default value menjadi 0
							ratingDetailedMovie.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, ratingDetailedMovie.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							textViewDetailedFifthInfoText.append(ratingDetailedMovie);
						}


						Spannable ratingFromWord = new SpannableString(" " + getString(R.string.span_movie_detail_from) + " ");
						ratingFromWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ratingFromWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						textViewDetailedFifthInfoText.append(ratingFromWord);

						// Cek jika ada value dari variable
						if(detailedMovieItems.get(0).getMovieRatingsVote() != null && !detailedMovieItems.get(0).getMovieRatingsVote().isEmpty()){
							Spannable ratingDetailedMovieVotes = new SpannableString(detailedMovieItems.get(0).getMovieRatingsVote());
							ratingDetailedMovieVotes.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, ratingDetailedMovieVotes.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							textViewDetailedFifthInfoText.append(ratingDetailedMovieVotes);
						} else {
							Spannable ratingDetailedMovieVotes = new SpannableString(getString(R.string.detailed_movie_default_value_ratings_vote));
							ratingDetailedMovieVotes.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, ratingDetailedMovieVotes.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							textViewDetailedFifthInfoText.append(ratingDetailedMovieVotes);
						}


						Spannable ratingVotesWord = new SpannableString(" " + getString(R.string.span_movie_detail_votes));
						ratingVotesWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ratingVotesWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						textViewDetailedFifthInfoText.append(ratingVotesWord);

						textViewDetailedSixthInfoTitle.setText(getString(R.string.detailed_movie_languages_title));

						// Cek jika ada value dari variable
						if(detailedMovieItems.get(0).getMovieLanguages() != null && !detailedMovieItems.get(0).getMovieLanguages().isEmpty()){
							textViewDetailedSixthInfoText.setText(detailedMovieItems.get(0).getMovieLanguages());
						} else {
							textViewDetailedSixthInfoText.setText(getString(R.string.detailed_movie_unknown_language));
						}

						textViewDetailedSeventhInfoTitle.setText(getString(R.string.detailed_movie_genres_title));

						// Cek jika ada value dari variable
						if(detailedMovieItems.get(0).getMovieGenres() != null && !detailedMovieItems.get(0).getMovieGenres().isEmpty()){
							textViewDetailedSeventhInfoText.setText(detailedMovieItems.get(0).getMovieGenres());
						} else {
							textViewDetailedSeventhInfoText.setText(getString(R.string.detailed_movie_unknown_genres));
						}

						textViewDetailedEighthInfoTitle.setText(getString(R.string.detailed_movie_release_date_title));

						// Cek jika ada value dari variable
						if(detailedMovieItems.get(0).getMovieReleaseDate() != null && !detailedMovieItems.get(0).getMovieReleaseDate().isEmpty()){
							textViewDetailedEighthInfoText.setText(detailedMovieItems.get(0).getMovieReleaseDate());
						} else {
							textViewDetailedEighthInfoText.setText(getString(R.string.detailed_movie_unknown_release_date));
						}

						textViewDetailedNinthInfoTitle.setText(getString(R.string.detailed_movie_overview_title));

						// Cek jika ada value dari variable
						if(detailedMovieItems.get(0).getMovieOverview() != null && !detailedMovieItems.get(0).getMovieOverview().isEmpty()){
							textViewDetailedNinthInfoText.setText(detailedMovieItems.get(0).getMovieOverview());
						} else {
							textViewDetailedNinthInfoText.setText(getString(R.string.detailed_movie_unknown_overview));
						}


						// Cek jika Uri tidak ada alias null
						if(uri == null){
							// Set value dari Item bedasarkan parameter lalu akses object pertama
							detailedMovieItem = detailedMovieItems.get(0);
						}

						// Set menu clickable into true, literally setelah asynctask kelar,
						// maka menu bs d click
						menuClickable = true;
						// Update option menu to searchMovieRecall onPrepareOptionMenu method
						invalidateOptionsMenu();
					} else {
						// Progress bar into gone and recycler view into invisible as the data finished on loading
						detailedProgressBar.setVisibility(View.GONE);
						detailedContentInfo.setVisibility(View.INVISIBLE);
						// Set empty view visibility into visible
						detailedEmptyTextView.setVisibility(View.VISIBLE);
						// Empty text view yang menunjukkan bahwa tidak ada data
						detailedEmptyTextView.setText(getString(R.string.no_movie_data_shown));
					}
				}
			}
		};
	}

	/**
	 * Method ini berguna untuk menampilkan data yang berisi detailed TV Show observer
	 * @return Observer yang membawa ArrayList<TvShowItem>
	 */
	private Observer<ArrayList<TvShowItem>> createDetailedTvShowObserver() {

		return new Observer<ArrayList<TvShowItem>>() {
			@Override
			public void onChanged(@Nullable ArrayList<TvShowItem> detailedTvShowItems) {
				// Ketika data selesai di load, maka kita akan mendapatkan data dan menghilangkan progress bar
				// yang menandakan bahwa loadingnya sudah selesai
				detailedContentInfo.setVisibility(View.VISIBLE);
				detailedProgressBar.setVisibility(View.GONE);
				detailedEmptyTextView.setVisibility(View.GONE);

				// Cek jika ada ArrayList<TvShowItem>
				if(detailedTvShowItems != null) {

					// Cek jika ada data di ArrayList<TvShowItem>
					if(detailedTvShowItems.size() > 0){
                        // Line ini berguna untuk set semua data ke dalam detail activity

						// Load image jika ada poster path
						Picasso.get().load(baseImageUrl + detailedTvShowItems.get(0).getTvShowPosterPath()).into(imageViewDetailedPosterImage);

						// Cek jika ada value dari variable
						if(detailedTvShowItems.get(0).getTvShowName() != null && !detailedTvShowItems.get(0).getTvShowName().isEmpty()){
							textViewDetailedFirstInfoText.setText(detailedTvShowItems.get(0).getTvShowName());
						} else {
							textViewDetailedFirstInfoText.setText(getString(R.string.detailed_tv_show_unknown_name));
						}

						Spannable seasonsWord = new SpannableString(getString(R.string.span_tv_show_detail_number_of_seasons) + " ");
						seasonsWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, seasonsWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						textViewDetailedSecondInfoText.setText(seasonsWord);
						// Cek jika ada value dari variable
						if(detailedTvShowItems.get(0).getTvShowSeasons() != null && !detailedTvShowItems.get(0).getTvShowSeasons().isEmpty()){
							Spannable seasonsDetailedTvShow = new SpannableString(detailedTvShowItems.get(0).getTvShowSeasons());
							seasonsDetailedTvShow.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, seasonsDetailedTvShow.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							textViewDetailedSecondInfoText.append(seasonsDetailedTvShow);
						} else {
							Spannable seasonsDetailedTvShow = new SpannableString(getString(R.string.detailed_tv_show_unknown_seasons));
							seasonsDetailedTvShow.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, seasonsDetailedTvShow.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							textViewDetailedSecondInfoText.append(seasonsDetailedTvShow);
						}

						// Set textview content in detailed movie runtime to contain a variety of different colors
						Spannable episodesWord = new SpannableString(getString(R.string.span_tv_show_detail_number_of_episodes) + " ");
						episodesWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, episodesWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						textViewDetailedThirdInfoText.setText(episodesWord);
						// Cek jika ada value dari variable
						if(detailedTvShowItems.get(0).getTvShowEpisodes() != null && !detailedTvShowItems.get(0).getTvShowEpisodes().isEmpty()){
							Spannable episodesDetailedMovie = new SpannableString(detailedTvShowItems.get(0).getTvShowEpisodes());
							episodesDetailedMovie.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, episodesDetailedMovie.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							textViewDetailedThirdInfoText.append(episodesDetailedMovie);
						} else {
							Spannable episodesDetailedMovie = new SpannableString(getString(R.string.detailed_tv_show_unknown_episodes));
							episodesDetailedMovie.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, episodesDetailedMovie.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							textViewDetailedThirdInfoText.append(episodesDetailedMovie);
						}

						Spannable episodesRuntimeWord = new SpannableString(getString(R.string.span_tv_show_detail_runtime_episodes) + " ");
						episodesRuntimeWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, episodesRuntimeWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						textViewDetailedFourthInfoText.setText(episodesRuntimeWord);

						// Cek jika ada value dari variable
						if(detailedTvShowItems.get(0).getTvShowRuntimeEpisodes() != null && !detailedTvShowItems.get(0).getTvShowRuntimeEpisodes().isEmpty()){
							Spannable episodesRuntimeDetailTvShow = new SpannableString(detailedTvShowItems.get(0).getTvShowRuntimeEpisodes() + " ");
							episodesRuntimeDetailTvShow.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, episodesRuntimeDetailTvShow.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							textViewDetailedFourthInfoText.append(episodesRuntimeDetailTvShow);

							Spannable episodesRuntimeDetailTvShowMinutes = new SpannableString(getString(R.string.span_tv_show_detail_runtime_episodes_minutes));
							episodesRuntimeDetailTvShowMinutes.setSpan(new ForegroundColorSpan(Color.BLACK), 0, episodesRuntimeDetailTvShowMinutes.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							textViewDetailedFourthInfoText.append(episodesRuntimeDetailTvShowMinutes);
						}

						// Set textview content in detailed movie rating to contain a variety of different colors
						Spannable ratingWord = new SpannableString(getString(R.string.span_tv_show_detail_rating) + " ");
						ratingWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ratingWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						textViewDetailedFifthInfoText.setText(ratingWord);
						// Cek jika ada value dari variable
						if(detailedTvShowItems.get(0).getTvShowRatings() != null && !detailedTvShowItems.get(0).getTvShowRatings().isEmpty()){
							Spannable tvShowDetailedMovie = new SpannableString(detailedTvShowItems.get(0).getTvShowRatings());
							tvShowDetailedMovie.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, tvShowDetailedMovie.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							textViewDetailedFifthInfoText.append(tvShowDetailedMovie);
						} else {
							Spannable tvShowDetailedMovie = new SpannableString(getString(R.string.detailed_movie_default_value_ratings));
							tvShowDetailedMovie.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, tvShowDetailedMovie.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							textViewDetailedFifthInfoText.append(tvShowDetailedMovie);
						}


						Spannable ratingFromWord = new SpannableString(" " + getString(R.string.span_tv_show_detail_from) + " ");
						ratingFromWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ratingFromWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						textViewDetailedFifthInfoText.append(ratingFromWord);
						// Cek jika ada value dari variable
						if(detailedTvShowItems.get(0).getTvShowRatingsVote() != null && !detailedTvShowItems.get(0).getTvShowRatingsVote().isEmpty()){
							Spannable ratingDetailedTvShowVotes = new SpannableString(detailedTvShowItems.get(0).getTvShowRatingsVote());
							ratingDetailedTvShowVotes.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, ratingDetailedTvShowVotes.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							textViewDetailedFifthInfoText.append(ratingDetailedTvShowVotes);
						} else {
							Spannable ratingDetailedTvShowVotes = new SpannableString(getString(R.string.detailed_tv_show_default_value_ratings_vote));
							ratingDetailedTvShowVotes.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, ratingDetailedTvShowVotes.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							textViewDetailedFifthInfoText.append(ratingDetailedTvShowVotes);
						}


						Spannable ratingVotesWord = new SpannableString(" " + getString(R.string.span_tv_show_detail_votes));
						ratingVotesWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ratingVotesWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						textViewDetailedFifthInfoText.append(ratingVotesWord);

						textViewDetailedSixthInfoTitle.setText(getString(R.string.detailed_tv_show_networks_title));
						// Cek jika ada value dari variable
						if(detailedTvShowItems.get(0).getTvShowNetworks() != null && !detailedTvShowItems.get(0).getTvShowNetworks().isEmpty()){
							textViewDetailedSixthInfoText.setText(detailedTvShowItems.get(0).getTvShowNetworks());
						} else {
							textViewDetailedSixthInfoText.setText(getString(R.string.detailed_tv_show_unknown_networks));
						}

						textViewDetailedSeventhInfoTitle.setText(getString(R.string.detailed_tv_show_genres_title));
						// Cek jika ada value dari variable
						if(detailedTvShowItems.get(0).getTvShowGenres() != null && !detailedTvShowItems.get(0).getTvShowGenres().isEmpty()){
							textViewDetailedSeventhInfoText.setText(detailedTvShowItems.get(0).getTvShowGenres());
						} else {
							textViewDetailedSeventhInfoText.setText(getString(R.string.detailed_tv_show_unknown_genres));
						}


						textViewDetailedEighthInfoTitle.setText(getString(R.string.detailed_tv_show_first_air_date_title));
						// Cek jika ada value dari variable
						if(detailedTvShowItems.get(0).getTvShowFirstAirDate() != null && !detailedTvShowItems.get(0).getTvShowFirstAirDate().isEmpty()){
							textViewDetailedEighthInfoText.setText(detailedTvShowItems.get(0).getTvShowFirstAirDate());
						} else {
							textViewDetailedEighthInfoText.setText(getString(R.string.detailed_tv_show_unknown_first_air_date));
						}

						textViewDetailedNinthInfoTitle.setText(getString(R.string.detailed_tv_show_overview_title));
						// Cek jika ada value dari variable
						if(detailedTvShowItems.get(0).getTvShowOverview() != null && !detailedTvShowItems.get(0).getTvShowOverview().isEmpty()){
							textViewDetailedNinthInfoText.setText(detailedTvShowItems.get(0).getTvShowOverview());
						} else {
							textViewDetailedNinthInfoText.setText(getString(R.string.detailed_tv_show_unknown_overview));
						}


						if(uri == null){
							// Set value dari Item bedasarkan parameter lalu akses object pertama, kesannya kyk Uri null atau tidak, object dari custom class tetap ada
							detailedTvShowItem = detailedTvShowItems.get(0);
						}

						// Set menu clickable into true, literally setelah asynctask kelar,
						// maka menu bs d click
						menuClickable = true;

						// Update option menu to recall onPrepareOptionMenu method
						invalidateOptionsMenu();
					} else {
						// Progress bar into gone and recycler view into invisible as the data finished on loading
						detailedProgressBar.setVisibility(View.GONE);
						detailedContentInfo.setVisibility(View.INVISIBLE);
						// Set empty view visibility into visible
						detailedEmptyTextView.setVisibility(View.VISIBLE);
						// Empty text view yang menunjukkan bahwa tidak ada tv show data
						detailedEmptyTextView.setText(getString(R.string.no_tv_show_data_shown));
					}

				}
			}
		};
	}

    /**
     * Method ini berguna untuk menyiapkan option menu,
     * specifically untuk mengetahui bahwa itemnya itu di click dan di triggered melalui
     * invalidateOptionMenu() method
     * @param menu Menu object
     * @return menu option prepared
     */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// Create menu item object dengan id action_marked_as_favorite
		MenuItem menuItem = menu.findItem(R.id.action_marked_as_favorite);
		// Check jika MovieItem ataupun TvShowItem object itu exists
		if(detailedMovieItem != null || detailedTvShowItem != null){
			menuItem.setVisible(true); // Set menu item into visible
		} else {
			menuItem.setVisible(false); // Set menu item into invisible
		}

		// Cek jika menu item itu visible, alias ada
		if(menuItem.isVisible()){
			// Cek jika menu item itu clickable alias value tsb adalah true
			if(menuClickable) {
				menuItem.setEnabled(true); // Make menu item clickable
			} else {
				menuItem.setEnabled(false); // Make menu item unclickable
			}
		}


		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Method ini berguna untuk membuat menu icon bedasarkan value dari boolean state value favorite
	 * @param menu
	 * @return boolean value yg represent bahwa option menu itu dibuat
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate menu
		getMenuInflater().inflate(R.menu.menu_favorite, menu);
		if(accessItemMode.equals("open_movie_detail")) {
			// Cek jika value boolean nya itu adalah true, yang berarti menandakan movie favorite
			if(detailedMovieFavoriteStateValue == 1) {
				// Set drawable resource
				drawableMenuMarkedAsFavouriteResourceId = R.drawable.ic_favourite_on;
			} else {
				drawableMenuMarkedAsFavouriteResourceId = R.drawable.ic_favourite_off;
			}
		} else if(accessItemMode.equals("open_tv_show_detail")) {
			// Cek jika value boolean nya itu adalah true, yang berarti menandakan tv show favorite
			if(detailedTvShowFavoriteStateValue == 1) {
				// Set drawable resource
				drawableMenuMarkedAsFavouriteResourceId = R.drawable.ic_favourite_on;
			} else {
				drawableMenuMarkedAsFavouriteResourceId = R.drawable.ic_favourite_off;
			}
		}

		// Set inflated menu icon
		menu.findItem(R.id.action_marked_as_favorite).setIcon(drawableMenuMarkedAsFavouriteResourceId);
		// Get icon from drawable
		Drawable menuDrawable = menu.findItem(R.id.action_marked_as_favorite).getIcon();
		menuDrawable = DrawableCompat.wrap(menuDrawable);
		// Set color of menu icon to white, because the default was black
		DrawableCompat.setTint(menuDrawable, ContextCompat.getColor(this, android.R.color.white));
		menu.findItem(R.id.action_marked_as_favorite).setIcon(menuDrawable);
		return true;
	}

	/**
	 * Method tersebut triggered ketika menu item selected, sehingga
	 * merubah data ke favorite/unfavorite
	 * @param item MenuItem dari layout XML
	 * @return boolean value apakah item itu di select atau tidak
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Boolean untuk mengetahui apakah state dari movie item itu berganti atau tidak
		switch(item.getItemId()) {
			case R.id.action_marked_as_favorite:
				if(accessItemMode.equals("open_movie_detail")) { // Cek jika mode yg dibuka itu berada di Movie
					// Check for current state of drawable menu icon
					if(detailedMovieFavoriteStateValue != 1) {
						// Change icon into marked as favourite
						drawableMenuMarkedAsFavouriteResourceId = R.drawable.ic_favourite_on;
						detailedMovieFavoriteStateValue = 1;
						// Set current date value into MovieItem, where MovieItem added into Favorite
						detailedMovieItem.setDateAddedFavorite(getCurrentDate());
						// Set boolean state value into MovieItem
						detailedMovieItem.setFavoriteBooleanState(detailedMovieFavoriteStateValue);

						// Cek jika value dari detailedMovieFavoriteStateValue sama dengan value bawaan intent dengan key MOVIE_BOOLEAN_STATE_EXTRA
						changedState = detailedMovieFavoriteStateValue != detailedMovieFavoriteStateValueComparison;

						ContentValues movieColumnValues = new ContentValues();
						// Put columns values in content values
						movieColumnValues.put(_ID, detailedMovieItem.getId());
						movieColumnValues.put(MOVIE_TITLE_COLUMN, detailedMovieItem.getMovieTitle());
						movieColumnValues.put(MOVIE_RATINGS_COLUMN, detailedMovieItem.getMovieRatings());
						movieColumnValues.put(MOVIE_ORIGINAL_LANGUAGE_COLUMN, detailedMovieItem.getMovieOriginalLanguage());
						movieColumnValues.put(MOVIE_RELEASE_DATE_COLUMN, detailedMovieItem.getMovieReleaseDate());
						movieColumnValues.put(MOVIE_FILE_PATH_COLUMN, detailedMovieItem.getMoviePosterPath());
						movieColumnValues.put(MOVIE_DATE_ADDED_FAVORITE_COLUMN, detailedMovieItem.getDateAddedFavorite());
						movieColumnValues.put(MOVIE_FAVORITE_COLUMN, detailedMovieItem.getFavoriteBooleanState());


						// Cek jika ada pergantian state dari sebuah data
						if(changedState) {
							uri = getContentResolver().insert(MOVIE_FAVORITE_CONTENT_URI, movieColumnValues); // Call insert method from content resolver, which is then passed into content provider
							detailedMovieFavoriteStateValueComparison = 1; // Ganti value untuk mengupdate comparison
							// Buat sebuah snackbar yang menandakan bahwa movie item inserted ke database
							snackbarMessage = Snackbar.make(detailedCoordinatorLayout, getString(R.string.insert_movie_favorite_snackbar), Snackbar.LENGTH_SHORT);
							snackbarMessage.show();
							// Buat kondisi ketika data di buka dari widget
							if(openDataFromWidget){
								// Panggil AppWidgetManager class dengan memanggil application
								// context (1 applikasi yaitu CatalogueMovieFinal)
								AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
								// Get App widget ids dari FavoriteMovieItemWidget class
								int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(getApplicationContext(), FavoriteMovieItemWidget.class));
								// Notify R.id.favorite_movie_stack_view {@link StackView di favorite_movie_item_widget.xml} agar dpt memanggil onDataSetChanged method
								appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.favorite_movie_stack_view);
							}
						}

						// Update option menu
						invalidateOptionsMenu();
					} else {
						// Change icon into unmarked as favourite
						drawableMenuMarkedAsFavouriteResourceId = R.drawable.ic_favourite_off;
						detailedMovieFavoriteStateValue = 0;
						// Set boolean state value into MovieItem
						detailedMovieItem.setFavoriteBooleanState(detailedMovieFavoriteStateValue);
						// Cek jika value dari detailedMovieFavoriteStateValue sama dengan value bawaan intent dengan key MOVIE_BOOLEAN_STATE_EXTRA
						changedState = detailedMovieFavoriteStateValue != detailedMovieFavoriteStateValueComparison;
						// Cek jika ada pergantian state dari sebuah data
						if(changedState) {
							// Cek jika ada Uri
							if(uri != null){
								getContentResolver().delete(uri, null, null); // Call delete method from content resolver, which is then passed into content provider
								detailedMovieFavoriteStateValueComparison = 0; // Ganti value untuk mengupdate comparison
								// Buat sebuah snackbar yang menandakan bahwa movie item removed dari database
								snackbarMessage = Snackbar.make(detailedCoordinatorLayout, getString(R.string.remove_movie_favorite_snackbar), Snackbar.LENGTH_SHORT);
								snackbarMessage.show();
								// Buat kondisi ketika data di buka dari widget
								if(openDataFromWidget){
									// Panggil AppWidgetManager class dengan memanggil application
									// context (1 applikasi yaitu CatalogueMovieFinal)
									AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
									// Get App widget ids dari FavoriteMovieItemWidget class
									int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(getApplicationContext(), FavoriteMovieItemWidget.class));
									// Notify R.id.favorite_movie_stack_view {@link StackView di favorite_movie_item_widget.xml} agar dpt memanggil onDataSetChanged method
									appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.favorite_movie_stack_view);
								}
							}
						}

						// Update option menu
						invalidateOptionsMenu();
					}

				} else if(accessItemMode.equals("open_tv_show_detail")) { // Cek jika mode yg dibuka itu berada di TV Show
					// Cek jika boolean value masih di dalam state unmarked as favorite
					if(detailedTvShowFavoriteStateValue != 1) {
						// Change icon into marked as favourite
						drawableMenuMarkedAsFavouriteResourceId = R.drawable.ic_favourite_on;
						detailedTvShowFavoriteStateValue = 1;
						// Set current date value into TV Show item, where MovieItem added into Favorite
						detailedTvShowItem.setDateAddedFavorite(getCurrentDate());
						// Set boolean state value into TV Show item
						detailedTvShowItem.setFavoriteBooleanState(detailedTvShowFavoriteStateValue);
						// Cek jika value dari detailedTvShowFavoriteStateValue sama dengan value
						// bawaan intent dengan key TV_SHOW_BOOLEAN_STATE_EXTRA
						changedState = detailedTvShowFavoriteStateValue != detailedTvShowFavoriteStateValueComparison;

						ContentValues tvShowColumnValues = new ContentValues();
						// Tambahkan value ke content values
						tvShowColumnValues.put(_ID, detailedTvShowItem.getId());
						tvShowColumnValues.put(TV_SHOW_NAME_COLUMN, detailedTvShowItem.getTvShowName());
						tvShowColumnValues.put(TV_SHOW_RATINGS_COLUMN, detailedTvShowItem.getTvShowRatings());
						tvShowColumnValues.put(TV_SHOW_ORIGINAL_LANGUAGE_COLUMN, detailedTvShowItem.getTvShowOriginalLanguage());
						tvShowColumnValues.put(TV_SHOW_FIRST_AIR_DATE_COLUMN, detailedTvShowItem.getTvShowFirstAirDate());
						tvShowColumnValues.put(TV_SHOW_FILE_PATH_COLUMN, detailedTvShowItem.getTvShowPosterPath());
						tvShowColumnValues.put(TV_SHOW_DATE_ADDED_COLUMN, detailedTvShowItem.getDateAddedFavorite());
						tvShowColumnValues.put(TV_SHOW_FAVORITE_COLUMN, detailedTvShowItem.getFavoriteBooleanState());

						// Cek jika ada pergantian state dari sebuah data
						if(changedState) {
							uri = getContentResolver().insert(TV_SHOW_FAVORITE_CONTENT_URI, tvShowColumnValues); // Call insert method from content resolver, which is then passed into content provider
							detailedTvShowFavoriteStateValueComparison = 1; // Ganti value untuk mengupdate comparison
							// Buat sebuah snackbar yang menandakan bahwa tv show item inserted ke database
							snackbarMessage = Snackbar.make(detailedCoordinatorLayout, getString(R.string.insert_tv_show_favorite_snackbar), Snackbar.LENGTH_SHORT);
							snackbarMessage.show();
						}

						// Update option menu
						invalidateOptionsMenu();
					} else {
						// Change icon into unmarked as favourite
						drawableMenuMarkedAsFavouriteResourceId = R.drawable.ic_favourite_off;
						detailedTvShowFavoriteStateValue = 0;
						// Set boolean state value into MovieItem
						detailedTvShowItem.setFavoriteBooleanState(detailedTvShowFavoriteStateValue);
						// Cek jika value dari detailedTvShowFavoriteStateValue sama dengan value
						// bawaan intent dengan key TV_SHOW_BOOLEAN_STATE_EXTRA
						changedState = detailedTvShowFavoriteStateValue != detailedTvShowFavoriteStateValueComparison;
						// Cek jika ada pergantian state dari sebuah data
						if(changedState) {
							// Cek jika uri tidak null
							if(uri != null){
								// Remove from database
								getContentResolver().delete(uri, null, null); // Call delete method from content resolver, which is then passed into content provider
								detailedTvShowFavoriteStateValueComparison = 0; // Ganti value untuk mengupdate comparison
								// Buat sebuah snackbar yang menandakan bahwa tv show item removed dari database
								snackbarMessage = Snackbar.make(detailedCoordinatorLayout, getString(R.string.remove_tv_show_favorite_snackbar), Snackbar.LENGTH_SHORT);
								snackbarMessage.show();
							}
						}

						// Update option menu
						invalidateOptionsMenu();
					}
				}
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Method tsb berguna untuk membawa data ketika orientation change.
	 * Hal tersebut berguna untuk memaintain boolean state (berpengaruh kpd drawable) serta
	 * {@link Uri} dan {@link MovieItem} atau {@link TvShowItem} tergantung mode intentnya apa
	 * @param outState
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if(accessItemMode.equals("open_movie_detail")) {
			// Save drawable marked as favorite state for movie as well as boolean changed state + comparisons
			outState.putInt(KEY_DRAWABLE_MARKED_AS_FAVORITE_STATE, detailedMovieFavoriteStateValue);
			outState.putBoolean(EXTRA_CHANGED_STATE, changedState);
			outState.putParcelable(EXTRA_URI, uri);
			outState.putParcelable(EXTRA_MOVIE_ITEM, detailedMovieItem);
		} else if(accessItemMode.equals("open_tv_show_detail")) {
			// Save drawable marked as favorite state for tv show as well as boolean changed state
			outState.putInt(KEY_DRAWABLE_MARKED_AS_FAVORITE_STATE, detailedTvShowFavoriteStateValue);
			outState.putBoolean(EXTRA_CHANGED_STATE, changedState);
			outState.putParcelable(EXTRA_URI, uri);
			outState.putParcelable(EXTRA_TV_SHOW_ITEM, detailedTvShowItem);
		}
		super.onSaveInstanceState(outState);
	}

	/**
	 * Method tsb berguna untuk mendapatkan waktu dimana sebuah item di tambahkan
	 * @return String object yg represent waktu dimana item di tambahkan
	 */
	private String getCurrentDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
		Date date = new Date();

		return dateFormat.format(date);
	}
}

