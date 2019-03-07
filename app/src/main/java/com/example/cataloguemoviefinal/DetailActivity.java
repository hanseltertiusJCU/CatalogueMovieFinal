package com.example.cataloguemoviefinal;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.cataloguemoviefinal.database.FavoriteItemsHelper;
import com.example.cataloguemoviefinal.entity.MovieItem;
import com.example.cataloguemoviefinal.entity.TvShowItem;
import com.example.cataloguemoviefinal.factory.DetailedMovieViewModelFactory;
import com.example.cataloguemoviefinal.factory.DetailedTvShowViewModelFactory;
import com.example.cataloguemoviefinal.fragment.MovieFragment;
import com.example.cataloguemoviefinal.fragment.TvShowFragment;
import com.example.cataloguemoviefinal.model.DetailedMovieViewModel;
import com.example.cataloguemoviefinal.model.DetailedTvShowViewModel;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.provider.BaseColumns._ID;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteMovieItemColumns.DATE_ADDED_COLUMN;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteMovieItemColumns.FAVORITE_COLUMN;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteMovieItemColumns.FILE_PATH_COLUMN;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_FAVORITE_CONTENT_URI;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteMovieItemColumns.ORIGINAL_LANGUAGE_COLUMN;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteMovieItemColumns.RATINGS_COLUMN;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteMovieItemColumns.RELEASE_DATE_COLUMN;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteMovieItemColumns.TITLE_COLUMN;

public class DetailActivity extends AppCompatActivity {
	// Request code
	public static final int REQUEST_CHANGE = 100;
	// Result code
	public static final int RESULT_CHANGE = 200;
	// Constant untuk dibawa ke FavoriteMovieFragment ataupun FavoriteTvShowFragment
	public static final String EXTRA_CHANGED_STATE = "extra_changed_state";
	// Constant untuk key dri drawable dan boolean state, as well as its comparisons
	private static final String KEY_DRAWABLE_MARKED_AS_FAVORITE_STATE = "drawable_favorite_state";
	// Constant untuk key dari URI
	private static final String EXTRA_URI = "extra_uri";
	// Constant untuk key object Movie
	private static final String EXTRA_MOVIE_ITEM = "extra_movie_item";
	// Constant untuk key object TV Show
	// Setup views bedasarkan id yang ada di layout xml
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
	@BindView(R.id.detailed_fifth_info_title)
	TextView textViewDetailedFifthInfoTitle;
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
	// Set layout value untuk dapat menjalankan process loading data
	@BindView(R.id.detailed_progress_bar)
	ProgressBar detailedProgressBar;
	@BindView(R.id.detailed_content)
	LinearLayout detailedContent;
	@BindView(R.id.detailed_app_bar)
	AppBarLayout detailedAppBarLayout;
	@BindView(R.id.detailed_toolbar)
	Toolbar detailedToolbar;
	// Setup intent value untuk movie items
	private int detailedMovieId;
	private String detailedMovieTitle;
	private int detailedMovieFavoriteStateValue;
	private int detailedMovieFavoriteStateValueComparison;
	// Setup intent value untuk tv show items
	private int detailedTvShowId;
	private String detailedTvShowName;
	private int detailedTvShowFavoriteStateValue;
	private int detailedTvShowFavoriteStateValueComparison;
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
	// Initiate Item Helper (DML class)
	private FavoriteItemsHelper favoriteItemsHelper;
	// String value untuk mengetahui mode data yg dibuka
	private String accessItemMode;
	
	// Boolean value untuk extra di Intent
	private boolean changedState;
	
	// Uri value
	private Uri uri;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		
		ButterKnife.bind(this);
		
		// Create instance dari FavoriteMovieItemsHelper
		favoriteItemsHelper = FavoriteItemsHelper.getInstance(getApplicationContext());
		
		setSupportActionBar(detailedToolbar);
		
		accessItemMode = getIntent().getStringExtra(MovieFragment.MODE_INTENT);
		
		// Cek untuk mode yg tepat
		if(accessItemMode.equals("open_movie_detail")) {
			// Get intent untuk mendapatkan id, title serta favorite movie state dari {@link MainActivity}
			detailedMovieId = getIntent().getIntExtra(MovieFragment.MOVIE_ID_DATA, 0);
			detailedMovieTitle = getIntent().getStringExtra(MovieFragment.MOVIE_TITLE_DATA);
			detailedMovieFavoriteStateValueComparison = getIntent().getIntExtra(MovieFragment.MOVIE_BOOLEAN_STATE_DATA, 0);
		} else if(accessItemMode.equals("open_tv_show_detail")) {
			// Get intent untuk mendapatkan id, title serta favorite tv show state dari {@link MainActivity}
			detailedTvShowId = getIntent().getIntExtra(TvShowFragment.TV_SHOW_ID_DATA, 0);
			detailedTvShowName = getIntent().getStringExtra(TvShowFragment.TV_SHOW_NAME_DATA);
			detailedTvShowFavoriteStateValueComparison = getIntent().getIntExtra(TvShowFragment.TV_SHOW_BOOLEAN_STATE_DATA, 0);
		}
		
		uri = getIntent().getData();
		
		if(uri != null){
			Cursor cursor = getContentResolver().query(uri, null, null, null, null);
			
			if(cursor != null){
				if(cursor.moveToFirst()){
					// todo: if condition to accomodate object creation
					if(accessItemMode.equals("open_movie_detail")){
						detailedMovieItem = new MovieItem(cursor);
						cursor.close();
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
				// Tujuannya agar bs bawa ke result serta handle comparison value
				// dimana kedua hal tsb dapat menghandle situasi orientation changes
				if(changedState) { // Cek jika value dr changedState itu true
					if(detailedMovieFavoriteStateValue == 1){
						detailedMovieFavoriteStateValueComparison = 1; // Update comparison value
					} else {
						detailedMovieFavoriteStateValueComparison = 0;
					}
					Intent resultIntent = new Intent();
					resultIntent.putExtra(EXTRA_CHANGED_STATE, changedState);
					setResult(RESULT_CHANGE, resultIntent);
				}
			} else if(accessItemMode.equals("open_tv_show_detail")) {
				detailedTvShowFavoriteStateValue = savedInstanceState.getInt(KEY_DRAWABLE_MARKED_AS_FAVORITE_STATE);
				changedState = savedInstanceState.getBoolean(EXTRA_CHANGED_STATE);
				// Tujuannya agar bs bawa ke result serta handle comparison value
				// dimana kedua hal tsb dapat menghandle situasi orientation changes
				if(changedState) { // Cek jika value dr changedState itu true
					if(detailedTvShowFavoriteStateValue == 1){
						detailedTvShowFavoriteStateValueComparison = 1; // Update comparison value
					} else {
						detailedTvShowFavoriteStateValueComparison = 0;
					}
					Intent resultIntent = new Intent();
					resultIntent.putExtra(EXTRA_CHANGED_STATE, changedState);
					setResult(RESULT_CHANGE, resultIntent);
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
		
		// Set visiblity of views ketika sedang dalam meretrieve data
		detailedContent.setVisibility(View.INVISIBLE);
		detailedProgressBar.setVisibility(View.VISIBLE);
		
		// Mode untuk menangani ViewModel yg berbeda
		if(accessItemMode.equals("open_movie_detail")) {
			// Panggil MovieViewModel dengan menggunakan ViewModelFactory sebagai parameter tambahan (dan satu-satunya pilihan) selain activity
			// Buat ViewModel untuk detailedMovieInfo
			DetailedMovieViewModel detailedMovieViewModel = ViewModelProviders.of(this, new DetailedMovieViewModelFactory(this.getApplication(), detailedMovieId)).get(DetailedMovieViewModel.class);
			// Buat observer object untuk mendisplay data ke UI
			// Buat Observer untuk detailedMovieInfo
			Observer<ArrayList<MovieItem>> detailedMovieObserver = createDetailedMovieObserver();
			// Tempelkan Observer ke LiveData object
			detailedMovieViewModel.getDetailedMovie().observe(this, detailedMovieObserver);
		} else if(accessItemMode.equals("open_tv_show_detail")) {
			// Panggil MovieViewModel dengan menggunakan ViewModelFactory sebagai parameter tambahan (dan satu-satunya pilihan) selain activity
			// Buat ViewModel untuk detailedTvShowInfo
			DetailedTvShowViewModel detailedTvShowViewModel = ViewModelProviders.of(this, new DetailedTvShowViewModelFactory(this.getApplication(), detailedTvShowId)).get(DetailedTvShowViewModel.class);
			// Buat observer object untuk mendisplay data ke UI
			// Buat Observer untuk detailedTvShowInfo
			Observer<ArrayList<TvShowItem>> detailedTvShowObserver = createDetailedTvShowObserver();
			// Tempelkan Observer ke LiveData object
			detailedTvShowViewModel.getDetailedTvShow().observe(this, detailedTvShowObserver);
			
		}
		
		
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
	
	private Observer<ArrayList<MovieItem>> createDetailedMovieObserver() {
		return new Observer<ArrayList<MovieItem>>() {
			@Override
			public void onChanged(@Nullable ArrayList<MovieItem> detailedMovieItems) {
				// Ketika data selesai di load, maka kita akan mendapatkan data dan menghilangkan progress bar
				// yang menandakan bahwa loadingnya sudah selesai
				detailedContent.setVisibility(View.VISIBLE);
				detailedProgressBar.setVisibility(View.GONE);
				
				if(detailedMovieItems != null) {
					// Set semua data ke dalam detail activity
					// Load image jika ada poster path
					Picasso.get().load(baseImageUrl + detailedMovieItems.get(0).getMoviePosterPath()).into(imageViewDetailedPosterImage);
					
					textViewDetailedFirstInfoText.setText(detailedMovieItems.get(0).getMovieTitle());
					
					textViewDetailedSecondInfoText.setText("\"" + detailedMovieItems.get(0).getMovieTagline() + "\"");
					
					// Set textview content in detailed movie runtime to contain a variety of different colors
					Spannable statusWord = new SpannableString(getString(R.string.span_movie_detail_status) + " ");
					statusWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, statusWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					textViewDetailedThirdInfoText.setText(statusWord);
					Spannable statusDetailedMovie = new SpannableString(detailedMovieItems.get(0).getMovieStatus());
					statusDetailedMovie.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, statusDetailedMovie.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					textViewDetailedThirdInfoText.append(statusDetailedMovie);
					
					// Set textview content in detailed movie rating to contain a variety of different colors
					Spannable ratingWord = new SpannableString(getString(R.string.span_movie_detail_rating) + " ");
					ratingWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ratingWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					textViewDetailedFourthInfoText.setText(ratingWord);
					Spannable ratingDetailedMovie = new SpannableString(detailedMovieItems.get(0).getMovieRatings());
					ratingDetailedMovie.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, ratingDetailedMovie.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					textViewDetailedFourthInfoText.append(ratingDetailedMovie);
					
					Spannable ratingFromWord = new SpannableString(" " + getString(R.string.span_movie_detail_from) + " ");
					ratingFromWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ratingFromWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					textViewDetailedFourthInfoText.append(ratingFromWord);
					
					Spannable ratingDetailedMovieVotes = new SpannableString(detailedMovieItems.get(0).getMovieRatingsVote());
					ratingDetailedMovieVotes.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, ratingDetailedMovieVotes.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					textViewDetailedFourthInfoText.append(ratingDetailedMovieVotes);
					
					Spannable ratingVotesWord = new SpannableString(" " + getString(R.string.span_movie_detail_votes));
					ratingVotesWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ratingVotesWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					textViewDetailedFourthInfoText.append(ratingVotesWord);
					
					textViewDetailedFifthInfoTitle.setText(getString(R.string.detailed_movie_languages_title));
					
					textViewDetailedFifthInfoText.setText(detailedMovieItems.get(0).getMovieLanguages());
					
					textViewDetailedSixthInfoTitle.setText(getString(R.string.detailed_movie_genres_title));
					
					textViewDetailedSixthInfoText.setText(detailedMovieItems.get(0).getMovieGenres());
					
					textViewDetailedSeventhInfoTitle.setText(getString(R.string.detailed_movie_release_date_title));
					
					textViewDetailedSeventhInfoText.setText(detailedMovieItems.get(0).getMovieReleaseDate());
					
					textViewDetailedEighthInfoTitle.setText(getString(R.string.detailed_movie_overview_title));
					
					textViewDetailedEighthInfoText.setText(detailedMovieItems.get(0).getMovieOverview());
					
					if(uri == null){
						// Set value dari Item bedasarkan parameter lalu akses object pertama
						detailedMovieItem = detailedMovieItems.get(0);
					}
					
					// Set menu clickable into true, literally setelah asynctask kelar,
					// maka menu bs d click
					menuClickable = true;
					// Update option menu to searchMovieRecall onPrepareOptionMenu method
					invalidateOptionsMenu();
				}
			}
		};
	}
	
	private Observer<ArrayList<TvShowItem>> createDetailedTvShowObserver() {
		
		return new Observer<ArrayList<TvShowItem>>() {
			@Override
			public void onChanged(@Nullable ArrayList<TvShowItem> detailedTvShowItems) {
				// Ketika data selesai di load, maka kita akan mendapatkan data dan menghilangkan progress bar
				// yang menandakan bahwa loadingnya sudah selesai
				detailedContent.setVisibility(View.VISIBLE);
				detailedProgressBar.setVisibility(View.GONE);
				
				if(detailedTvShowItems != null) {
					// Set semua data ke dalam detail activity
					// Load image jika ada poster path
					Picasso.get().load(baseImageUrl + detailedTvShowItems.get(0).getTvShowPosterPath()).into(imageViewDetailedPosterImage);
					
					textViewDetailedFirstInfoText.setText(detailedTvShowItems.get(0).getTvShowName());
					
					Spannable seasonsWord = new SpannableString(getString(R.string.span_tv_show_detail_number_of_seasons) + " ");
					seasonsWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, seasonsWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					textViewDetailedSecondInfoText.setText(seasonsWord);
					Spannable seasonsDetailedTvShow = new SpannableString(detailedTvShowItems.get(0).getTvShowSeasons());
					seasonsDetailedTvShow.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, seasonsDetailedTvShow.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					textViewDetailedSecondInfoText.append(seasonsDetailedTvShow);
					
					// Set textview content in detailed movie runtime to contain a variety of different colors
					Spannable episodesWord = new SpannableString(getString(R.string.span_tv_show_detail_number_of_episodes) + " ");
					episodesWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, episodesWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					textViewDetailedThirdInfoText.setText(episodesWord);
					Spannable episodesDetailedMovie = new SpannableString(detailedTvShowItems.get(0).getTvShowEpisodes());
					episodesDetailedMovie.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, episodesDetailedMovie.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					textViewDetailedThirdInfoText.append(episodesDetailedMovie);
					
					// Set textview content in detailed movie rating to contain a variety of different colors
					Spannable ratingWord = new SpannableString(getString(R.string.span_tv_show_detail_rating) + " ");
					ratingWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ratingWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					textViewDetailedFourthInfoText.setText(ratingWord);
					Spannable tvShowDetailedMovie = new SpannableString(detailedTvShowItems.get(0).getTvShowRatings());
					tvShowDetailedMovie.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, tvShowDetailedMovie.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					textViewDetailedFourthInfoText.append(tvShowDetailedMovie);
					Spannable ratingFromWord = new SpannableString(" " + getString(R.string.span_tv_show_detail_from) + " ");
					ratingFromWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ratingFromWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					textViewDetailedFourthInfoText.append(ratingFromWord);
					Spannable ratingDetailedTvShowVotes = new SpannableString(detailedTvShowItems.get(0).getTvShowRatingsVote());
					ratingDetailedTvShowVotes.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, ratingDetailedTvShowVotes.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					textViewDetailedFourthInfoText.append(ratingDetailedTvShowVotes);
					Spannable ratingVotesWord = new SpannableString(" " + getString(R.string.span_tv_show_detail_votes));
					ratingVotesWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ratingVotesWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					textViewDetailedFourthInfoText.append(ratingVotesWord);
					
					textViewDetailedFifthInfoTitle.setText(getString(R.string.detailed_tv_show_networks_title));
					textViewDetailedFifthInfoText.setText(detailedTvShowItems.get(0).getTvShowNetworks());
					
					textViewDetailedSixthInfoTitle.setText(getString(R.string.detailed_tv_show_genres_title));
					textViewDetailedSixthInfoText.setText(detailedTvShowItems.get(0).getTvShowGenres());
					
					textViewDetailedSeventhInfoTitle.setText(getString(R.string.detailed_tv_show_first_air_date_title));
					textViewDetailedSeventhInfoText.setText(detailedTvShowItems.get(0).getTvShowFirstAirDate());
					
					textViewDetailedEighthInfoTitle.setText(getString(R.string.detailed_tv_show_overview_title));
					textViewDetailedEighthInfoText.setText(detailedTvShowItems.get(0).getTvShowOverview());
					
					// Set value dari Item bedasarkan parameter lalu akses object pertama
					detailedTvShowItem = detailedTvShowItems.get(0);
					// Set menu clickable into true, literally setelah asynctask kelar,
					// maka menu bs d click
					menuClickable = true;
					
					// Update option menu to searchMovieRecall onPrepareOptionMenu method
					invalidateOptionsMenu();
				}
			}
		};
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if(menuClickable) {
			menu.findItem(R.id.action_marked_as_favorite).setEnabled(true);
		} else {
			menu.findItem(R.id.action_marked_as_favorite).setEnabled(false);
		}
		
		return super.onPrepareOptionsMenu(menu);
	}
	
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
		DrawableCompat.setTint(menuDrawable, ContextCompat.getColor(this, R.color.colorWhite));
		menu.findItem(R.id.action_marked_as_favorite).setIcon(menuDrawable);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Create new Intent object
		Intent resultIntent = new Intent();
		
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
						
						ContentValues values = new ContentValues();
						// Put columns values in content values
						values.put(_ID, detailedMovieItem.getId());
						values.put(TITLE_COLUMN, detailedMovieItem.getMovieTitle());
						values.put(RATINGS_COLUMN, detailedMovieItem.getMovieRatings());
						values.put(ORIGINAL_LANGUAGE_COLUMN, detailedMovieItem.getMovieOriginalLanguage());
						values.put(RELEASE_DATE_COLUMN, detailedMovieItem.getMovieReleaseDate());
						values.put(FILE_PATH_COLUMN, detailedMovieItem.getMoviePosterPath());
						values.put(DATE_ADDED_COLUMN, detailedMovieItem.getDateAddedFavorite());
						values.put(FAVORITE_COLUMN, detailedMovieItem.getFavoriteBooleanState());
						
						
						// Cek jika ada pergantian state dari sebuah data
						if(changedState) {
							uri = getContentResolver().insert(MOVIE_FAVORITE_CONTENT_URI, values);
							if(uri != null){
								// Bawa nilai ke intent
								resultIntent.putExtra(EXTRA_CHANGED_STATE, changedState);
								setResult(RESULT_CHANGE, resultIntent); // Set result that brings result code and intent
							}
							detailedMovieFavoriteStateValueComparison = 1; // Ganti value untuk mengupdate comparison
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
							int deletedIdItem = getContentResolver().delete(uri, null, null);
							detailedMovieFavoriteStateValueComparison = 0; // Ganti value untuk mengupdate comparison
							if(deletedIdItem > 0) {
								// Bawa nilai ke intent
								resultIntent.putExtra(EXTRA_CHANGED_STATE, changedState);
								setResult(RESULT_CHANGE, resultIntent); // Set result that brings result code and intent
							}
						}
						
						// Update option menu
						invalidateOptionsMenu();
					}
				} else if(accessItemMode.equals("open_tv_show_detail")) { // Cek jika mode yg dibuka itu berada di TV Show
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
						// Cek jika ada pergantian state dari sebuah data
						if(changedState) {
							// Insert based on data
							long newIdItem = favoriteItemsHelper.insertFavoriteTvShowItem(detailedTvShowItem);
							detailedTvShowFavoriteStateValueComparison = 1; // Ganti value untuk mengupdate comparison
							if(newIdItem > 0) {
								// Bawa nilai ke intent
								resultIntent.putExtra(EXTRA_CHANGED_STATE, changedState);
								setResult(RESULT_CHANGE, resultIntent); // Set result that brings result code and intent
							}
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
							// Remove from database
							long deletedIdItem = favoriteItemsHelper.deleteFavoriteTvShowItem(detailedTvShowItem.getId());
							detailedTvShowFavoriteStateValueComparison = 0; // Ganti value untuk mengupdate comparison
							if(deletedIdItem > 0) {
								// Bawa nilai ke intent
								resultIntent.putExtra(EXTRA_CHANGED_STATE, changedState);
								setResult(RESULT_CHANGE, resultIntent); // Set result that brings result code and intent
							}
						}
						
						// Update option menu
						invalidateOptionsMenu();
					}
				}
				break;
			case R.id.home:
				// Finish method untuk membawa Intent ke MainActivity
				finish();
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		// Finish method untuk membawa Intent ke MainActivity
		finish();
	}
	
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
		}
		super.onSaveInstanceState(outState);
	}
	
	// Method tsb berguna untuk mendapatkan waktu dimana sebuah item di tambahkan
	private String getCurrentDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
		Date date = new Date();
		
		return dateFormat.format(date);
	}
}

