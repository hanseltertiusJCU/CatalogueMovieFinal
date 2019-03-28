package com.example.cataloguemoviefinal;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.cataloguemoviefinal.adapter.ItemSectionsFragmentPagerAdapter;
import com.example.cataloguemoviefinal.async.LoadFavoriteMoviesAsync;
import com.example.cataloguemoviefinal.async.LoadFavoriteTvShowAsync;
import com.example.cataloguemoviefinal.entity.MovieItem;
import com.example.cataloguemoviefinal.entity.TvShowItem;
import com.example.cataloguemoviefinal.fragment.FavoriteMovieFragment;
import com.example.cataloguemoviefinal.fragment.FavoriteTvShowFragment;
import com.example.cataloguemoviefinal.fragment.MovieFragment;
import com.example.cataloguemoviefinal.fragment.SearchMovieFragment;
import com.example.cataloguemoviefinal.fragment.SearchTvShowFragment;
import com.example.cataloguemoviefinal.fragment.TvShowFragment;
import com.example.cataloguemoviefinal.observer.FavoriteMovieDataObserver;
import com.example.cataloguemoviefinal.observer.FavoriteTvShowDataObserver;
import com.example.cataloguemoviefinal.widget.FavoriteMovieItemWidget;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_FAVORITE_CONTENT_URI;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_FAVORITE_CONTENT_URI;
import static com.example.cataloguemoviefinal.helper.FavoriteMovieMappingHelper.mapCursorToFavoriteMovieArrayList;
import static com.example.cataloguemoviefinal.helper.FavoriteTvShowMappingHelper.mapCursorToFavoriteTvShowArrayList;

/**
 * Class ini berguna untuk:
 * - Menampilkan layout activity_main.xml
 * - Load async task data untuk distribusikan ke fragment favorite movie dan favorite TV show
 * - Register data observer jika ada pergantian dari data favorite
 * - Memanggil fragment Favorite movie/TV Show dan memanggil ulang widget favorite movie jika ada
 * perubahan data
 * - Mengatur layout dari tab item di TabLayout
 * - Memberi akses untuk mengganti bahasa maupun setting alarm dengan memberi menu tsb.
 */
public class MainActivity extends AppCompatActivity implements LoadFavoriteMoviesCallback, LoadFavoriteTvShowCallback{

	// Create ViewPager untuk swipe Fragments
	@BindView(R.id.item_viewPager)
	ViewPager viewPager;
	// Assign TabLayout
	@BindView(R.id.menu_tabs)
	TabLayout tabLayout;
	@BindView(R.id.main_toolbar)
	Toolbar mainToolbar;
	// Initiate fragment adapter object
	private ItemSectionsFragmentPagerAdapter itemSectionsFragmentPagerAdapter;
	// Set textview untuk isi dari TabLayout
	private TextView tabMovie;
	private TextView tabTvShow;
	private TextView tabFavoriteMovie;
	private TextView tabFavoriteTvShow;
	private TextView tabSearchMovie;
	private TextView tabSearchTvShow;
	// Set drawable array beserta drawable untuk icon dr TabLayout
	private Drawable[] movieDrawables;
	private Drawable movieDrawable;
	private Drawable[] tvShowDrawables;
	private Drawable tvShowDrawable;
	private Drawable[] favoriteMovieDrawables;
	private Drawable favoriteMovieDrawable;
	private Drawable[] favoriteTvShowDrawables;
	private Drawable favoriteTvShowDrawable;
	private Drawable[] searchMovieDrawables;
	private Drawable searchMovieDrawable;
	private Drawable[] searchTvShowDrawables;
	private Drawable searchTvShowDrawable;
	// ArrayList object untuk MovieItem
	public static ArrayList<MovieItem> favoriteMovieItemArrayList;
	// ArrayList object untuk TvShowItem
	public static ArrayList<TvShowItem> favoriteTvShowItemArrayList;

	/**
	 * Method ini trigger ketika activity created
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set content activity to use layout xml file activity_main.xml
		setContentView(R.layout.activity_main);

		// Bind views
		ButterKnife.bind(this);

		// Set action bar into the activity
		setSupportActionBar(mainToolbar);

		// Cek kalo ada action bar
		if(getSupportActionBar() != null) {
			// Set default action bar title, yaitu "Movie", alias item yg ada di posisi 0
			getSupportActionBar().setTitle(getString(R.string.movie));
		}

		// Initiate handler thread operation in Movie
		HandlerThread movieHandlerThread = new HandlerThread("FavoriteMovieDataObserver"); // Initiate HandlerThread
		movieHandlerThread.start();
		Handler movieHandler = new Handler(movieHandlerThread.getLooper()); // Initiate Handler
		FavoriteMovieDataObserver myFavoriteMovieObserver = new FavoriteMovieDataObserver(movieHandler, this); // Initiate ContentObserver
		getContentResolver().registerContentObserver(MOVIE_FAVORITE_CONTENT_URI, true, myFavoriteMovieObserver);

		// Initiate handler thread operation in TV Show
		HandlerThread tvShowHandlerThread = new HandlerThread("FavoriteTvShowDataObserver"); // Initiate HandlerThread
		tvShowHandlerThread.start();
		Handler tvShowHandler = new Handler(tvShowHandlerThread.getLooper()); // Initiate Handler
		FavoriteTvShowDataObserver myFavoriteTvShowObserver = new FavoriteTvShowDataObserver(tvShowHandler, this); // Initiate ContentObserver
		getContentResolver().registerContentObserver(TV_SHOW_FAVORITE_CONTENT_URI, true, myFavoriteTvShowObserver);

		// Load async task for getting the cursor in Movies and TV Show favorite
		new LoadFavoriteMoviesAsync(this, this).execute();
		new LoadFavoriteTvShowAsync(this, this).execute();

		// Panggil method ini untuk saving Fragment state di ViewPager, kesannya kyk simpen
		// fragment ketika sebuah fragment sedang tidak di display.
		// Kita menggunakan value 5 sebagai parameter karena kita punya 6 fragments, dan kita
		// hanya butuh simpan 5 fragments (1 lg untuk display).
		viewPager.setOffscreenPageLimit(5);

		// Panggil method tsb untuk membuat fragment list yang akan disimpan ke ViewPager
		createViewPagerContent(viewPager);

		// Beri ViewPager ke TabLayout
		tabLayout.setupWithViewPager(viewPager);

		// Panggil method tsb untuk membuat isi dari setiap tab
		createTabIcons();

		// Set listener untuk tab layout, hal ini berguna untuk merespon terhadap perubahan selected
		// tab item
		tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

			/**
			 * Method tsb berguna untuk set action bar title dan juga merubah warna dari tab item
			 * ketika selected
			 * @param tab Tab dari Tablayout
			 */
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				int position = tab.getPosition();
				// Cast getPageTitle return ke String dari CharSequence (return type yang semula)
				setActionBarTitle((String) itemSectionsFragmentPagerAdapter.getPageTitle(position));
				// Ubah text color dan drawable tint menjadi colorAccent, yang menandakan bahwa itemnya
				// sedang dipilih
				switch(position) {
					case 0:
						movieDrawables = tabMovie.getCompoundDrawables();
						movieDrawable = movieDrawables[1];
						movieDrawable.setTint(getResources().getColor(android.R.color.white));
						tabMovie.setTextColor(getResources().getColor(android.R.color.white));
						break;
					case 1:
						tvShowDrawables = tabTvShow.getCompoundDrawables();
						tvShowDrawable = tvShowDrawables[1];
						tvShowDrawable.setTint(getResources().getColor(android.R.color.white));
						tabTvShow.setTextColor(getResources().getColor(android.R.color.white));
						break;
					case 2:
						favoriteMovieDrawables = tabFavoriteMovie.getCompoundDrawables();
						favoriteMovieDrawable = favoriteMovieDrawables[1];
						favoriteMovieDrawable.setTint(getResources().getColor(android.R.color.white));
						tabFavoriteMovie.setTextColor(getResources().getColor(android.R.color.white));
						break;
					case 3:
						favoriteTvShowDrawables = tabFavoriteTvShow.getCompoundDrawables();
						favoriteTvShowDrawable = favoriteTvShowDrawables[1];
						favoriteTvShowDrawable.setTint(getResources().getColor(android.R.color.white));
						tabFavoriteTvShow.setTextColor(getResources().getColor(android.R.color.white));
						break;
					case 4:
						searchMovieDrawables = tabSearchMovie.getCompoundDrawables();
						searchMovieDrawable = searchMovieDrawables[1];
						searchMovieDrawable.setTint(getResources().getColor(android.R.color.white));
						tabSearchMovie.setTextColor(getResources().getColor(android.R.color.white));
						break;
					case 5:
						searchTvShowDrawables = tabSearchTvShow.getCompoundDrawables();
						searchTvShowDrawable = searchTvShowDrawables[1];
						searchTvShowDrawable.setTint(getResources().getColor(android.R.color.white));
						tabSearchTvShow.setTextColor(getResources().getColor(android.R.color.white));
						break;
					default:
						break;
				}

			}

			/**
			 * Method tsb berguna untuk merubah warna dari tab item ketika unselected
			 * @param tab Tab dari Tablayout
			 */
			@Override
			public void onTabUnselected(TabLayout.Tab tab) {
				int position = tab.getPosition();
				// Ubah text color dan drawable tint menjadi hitam, yang menandakan bahwa itemnya
				// sedang tidak dipilih
				switch(position) {
					case 0:
						movieDrawables = tabMovie.getCompoundDrawables();
						movieDrawable = movieDrawables[1];
						movieDrawable.setTint(getResources().getColor(R.color.colorAccentLight));
						tabMovie.setTextColor(getResources().getColor(R.color.colorAccentLight));
						break;
					case 1:
						tvShowDrawables = tabTvShow.getCompoundDrawables();
						tvShowDrawable = tvShowDrawables[1];
						tvShowDrawable.setTint(getResources().getColor(R.color.colorAccentLight));
						tabTvShow.setTextColor(getResources().getColor(R.color.colorAccentLight));
						break;
					case 2:
						favoriteMovieDrawables = tabFavoriteMovie.getCompoundDrawables();
						favoriteMovieDrawable = favoriteMovieDrawables[1];
						favoriteMovieDrawable.setTint(getResources().getColor(R.color.colorAccentLight));
						tabFavoriteMovie.setTextColor(getResources().getColor(R.color.colorAccentLight));
					case 3:
						favoriteTvShowDrawables = tabFavoriteTvShow.getCompoundDrawables();
						favoriteTvShowDrawable = favoriteTvShowDrawables[1];
						favoriteTvShowDrawable.setTint(getResources().getColor(R.color.colorAccentLight));
						tabFavoriteTvShow.setTextColor(getResources().getColor(R.color.colorAccentLight));
						break;
					case 4:
						searchMovieDrawables = tabSearchMovie.getCompoundDrawables();
						searchMovieDrawable = searchMovieDrawables[1];
						searchMovieDrawable.setTint(getResources().getColor(R.color.colorAccentLight));
						tabSearchMovie.setTextColor(getResources().getColor(R.color.colorAccentLight));
						break;
					case 5:
						searchTvShowDrawables = tabSearchTvShow.getCompoundDrawables();
						searchTvShowDrawable = searchTvShowDrawables[1];
						searchTvShowDrawable.setTint(getResources().getColor(R.color.colorAccentLight));
						tabSearchTvShow.setTextColor(getResources().getColor(R.color.colorAccentLight));
						break;
					default:
						break;
				}

			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {
				// Method ini tidak melakukan apa2
			}
		});

	}

	/**
	 * Method tsb berguna untuk membuat isi dari tab, terdiri dari icons dan text.
	 * Selain itu, method tsb juga menentukan warna dari icons dan text
	 */
	private void createTabIcons() {
		// Inflate TextView object untuk tablayout item
		tabMovie = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
		// Set isi dari text di sebuah tab
		tabMovie.setText(getString(R.string.movie));
		// Set icon di atas text
		tabMovie.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_movie, 0, 0);
		// Dapatkan getCompoundDrawable dari setCompoundDrawablesWithIntrinsicBounds
		movieDrawables = tabMovie.getCompoundDrawables();
		// Akses drawableTop, which is in this case kita mengakses element ke 2 (index value: 1)
		// dari Drawable[]
		movieDrawable = movieDrawables[1];
		// Set default tint untuk drawable yang menandakan bahwa tabnya itu sedang d select
		movieDrawable.setTint(getResources().getColor(android.R.color.white));
		// Set default text color yang menandakan bahwa tabnya itu sedang d select
		tabMovie.setTextColor(getResources().getColor(android.R.color.white));

		// Inflate custom_tab.xml ke dalam TabLayout
		Objects.requireNonNull(tabLayout.getTabAt(0)).setCustomView(tabMovie);

		tabTvShow = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
		tabTvShow.setText(getString(R.string.tv_show));
		tabTvShow.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_tv_show, 0, 0);
		// Dapatkan getCompoundDrawable dari setCompoundDrawablesWithIntrinsicBounds
		tvShowDrawables = tabTvShow.getCompoundDrawables();
		// Akses drawableTop, which is in this case kita mengakses element ke 2 (index value: 1)
		// dari Drawable[]
		tvShowDrawable = tvShowDrawables[1];
		// Set default tint untuk drawable ketika viewnya itu di buat
		tvShowDrawable.setTint(getResources().getColor(R.color.colorAccentLight));
		// Set default text color untuk text view ketika viewnya itu dibuat
		tabTvShow.setTextColor(getResources().getColor(R.color.colorAccentLight));
		Objects.requireNonNull(tabLayout.getTabAt(1)).setCustomView(tabTvShow);

		tabFavoriteMovie = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
		tabFavoriteMovie.setText(getString(R.string.favorite_movie));
		tabFavoriteMovie.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_movie_favorite, 0, 0);
		favoriteMovieDrawables = tabFavoriteMovie.getCompoundDrawables();
		favoriteMovieDrawable = favoriteMovieDrawables[1];
		favoriteMovieDrawable.setTint(getResources().getColor(R.color.colorAccentLight));
		tabFavoriteMovie.setTextColor(getResources().getColor(R.color.colorAccentLight));
		Objects.requireNonNull(tabLayout.getTabAt(2)).setCustomView(tabFavoriteMovie);

		tabFavoriteTvShow = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
		tabFavoriteTvShow.setText(getString(R.string.favorite_tv_show));
		tabFavoriteTvShow.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_tv_show_favorite, 0, 0);
		favoriteTvShowDrawables = tabFavoriteTvShow.getCompoundDrawables();
		favoriteTvShowDrawable = favoriteTvShowDrawables[1];
		favoriteTvShowDrawable.setTint(getResources().getColor(R.color.colorAccentLight));
		tabFavoriteTvShow.setTextColor(getResources().getColor(R.color.colorAccentLight));
		Objects.requireNonNull(tabLayout.getTabAt(3)).setCustomView(tabFavoriteTvShow);

		tabSearchMovie = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
		tabSearchMovie.setText(getString(R.string.search_movie));
		tabSearchMovie.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_search_movie, 0,0);
		searchMovieDrawables = tabSearchMovie.getCompoundDrawables();
		searchMovieDrawable = searchMovieDrawables[1];
		searchMovieDrawable.setTint(getResources().getColor(R.color.colorAccentLight));
		tabSearchMovie.setTextColor(getResources().getColor(R.color.colorAccentLight));
		Objects.requireNonNull(tabLayout.getTabAt(4)).setCustomView(tabSearchMovie);

		tabSearchTvShow = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
		tabSearchTvShow.setText(getString(R.string.search_tv_show));
		tabSearchTvShow.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_search_tv_show, 0, 0);
		searchTvShowDrawables = tabSearchTvShow.getCompoundDrawables();
		searchTvShowDrawable = searchTvShowDrawables[1];
		searchTvShowDrawable.setTint(getResources().getColor(R.color.colorAccentLight));
		tabSearchTvShow.setTextColor(getResources().getColor(R.color.colorAccentLight));
		Objects.requireNonNull(tabLayout.getTabAt(5)).setCustomView(tabSearchTvShow);
	}

	/**
	 * Method tsb berguna untuk membuat isi dari ViewPager
	 * @param viewPager viewpager object
	 */
	// Method tsb berguna untuk membuat isi dari ViewPager
	private void createViewPagerContent(ViewPager viewPager) {

		// Create FragmentPagerAdapter untuk mengetahui fragment mana yg di show
		itemSectionsFragmentPagerAdapter = new ItemSectionsFragmentPagerAdapter(this, getSupportFragmentManager());

		// Tambahkan fragment beserta title ke FragmentPagerAdapter
		itemSectionsFragmentPagerAdapter.addMovieSectionFragment(new MovieFragment(), getString(R.string.movie));
		itemSectionsFragmentPagerAdapter.addMovieSectionFragment(new TvShowFragment(), getString(R.string.tv_show));
		itemSectionsFragmentPagerAdapter.addMovieSectionFragment(new FavoriteMovieFragment(), getString(R.string.favorite_movie));
		itemSectionsFragmentPagerAdapter.addMovieSectionFragment(new FavoriteTvShowFragment(), getString(R.string.favorite_tv_show));
		itemSectionsFragmentPagerAdapter.addMovieSectionFragment(new SearchMovieFragment(), getString(R.string.search_movie));
		itemSectionsFragmentPagerAdapter.addMovieSectionFragment(new SearchTvShowFragment(), getString(R.string.search_tv_show));

		// Set FragmentPagerAdapter ke ViewPager
		viewPager.setAdapter(itemSectionsFragmentPagerAdapter);
	}

	/**
	 * Method ini berguna untuk menginflate menu layout xml ke layar
	 * @param menu Menu object
	 * @return boolean variable yg menandakan bahwa option menu telah dibuat
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_settings, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Method ini berguna untuk melakukan sesuatu ketika menu item dipilih
	 * @param item menu item
	 * @return boolean value bahwa menu item selected
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.action_change_language_settings) { // Open language settings
			Intent mIntent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
			startActivity(mIntent);
		} else if(item.getItemId() == R.id.action_reminder_preference_settings){ // Open preference settings for triggering alarm manager
			Intent mIntent = new Intent(this, SettingsActivity.class);
			startActivity(mIntent);
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Method ini berguna untuk set action bar title
	 * @param title
	 */
	public void setActionBarTitle(String title) {
		if(getSupportActionBar() != null) {
			// Gunakan getSupportActionBar untuk backward compatibility
			getSupportActionBar().setTitle(title);
		}
	}

	// Method dari LoadFavoriteMoviesCallback interface dan kita coba implement dari method tsb

	/**
	 * Method ini berguna untuk menyiapkan data Favorite movie array list
	 */
	@Override
	public void favoriteMoviePreExecute() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				favoriteMovieItemArrayList = new ArrayList<>(); // create object while preparing
			}
		});
	}

	/**
	 * Method ini berguna untuk:
	 * - menampilkan data Favorite movie array list
	 * - Detatch reattatch existing fragment (fav movie) yang ada di ViewPager
	 * - Mengupdate isi widget ketika data berganti karena onChanged dari
	 * {@link FavoriteMovieDataObserver} load {@link LoadFavoriteMoviesAsync}
	 */
	@Override
	public void favoriteMoviePostExecute(Cursor movieItems) {
		// Change cursor to ArrayList that contains MovieItem
		favoriteMovieItemArrayList = mapCursorToFavoriteMovieArrayList(movieItems);
		// Line code tsb bertujuan untuk refresh fragment favorite movie ketika ada perubahan data di database
		FavoriteMovieFragment favoriteMovieFragment = (FavoriteMovieFragment) itemSectionsFragmentPagerAdapter.getItem(2); // Panggil Fragment favorite movie item
		// Initiate fragment transaction untuk melakukan fragment operation
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		// Cek jika fragmentnya itu ada
		if(favoriteMovieFragment != null){
			// Cek jika fragment di attach ke activity
			if(favoriteMovieFragment.isAdded()){
				fragmentTransaction.detach(favoriteMovieFragment);
				fragmentTransaction.attach(favoriteMovieFragment);
				fragmentTransaction.commitAllowingStateLoss();
			}

		}

		// Line ini berguna untuk update isi widget ketika ada pergantian data dari movie favorite
        // (baik dari CatalogueMovieFinal maupun FavoriteFilmApp)

        // Panggil AppWidgetManager class
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
		// Get App widget ids dari FavoriteMovieItemWidget class
		int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(getApplicationContext(), FavoriteMovieItemWidget.class));
		// Notify R.id.favorite_movie_stack_view {@link StackView di favorite_movie_item_widget.xml} agar dpt memanggil onDataSetChanged method
		appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.favorite_movie_stack_view);

	}

	// Method dari LoadFavoriteTvShowCallback interface dan kita coba implement dari method tsb

	/**
	 * Method ini berguna untuk menyiapkan data Favorite tv show array list
	 */
	@Override
	public void favoriteTvShowPreExecute() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				favoriteTvShowItemArrayList = new ArrayList<>(); // create object while preparing
			}
		});
	}

	/**
	 * Method ini berguna untuk menampilkan data Favorite TV Show array list
	 * beserta detatch reattatch existing fragment (fav TV Show) yang ada di ViewPager
	 */
	@Override
	public void favoriteTvShowPostExecute(Cursor tvShowItems) {
		// Change cursor to ArrayList that contains TvShowItem
		favoriteTvShowItemArrayList = mapCursorToFavoriteTvShowArrayList(tvShowItems);

		// Line code tsb bertujuan untuk refresh fragment favorite tv show ketika ada perubahan data di database
		FavoriteTvShowFragment favoriteTvShowFragment = (FavoriteTvShowFragment) itemSectionsFragmentPagerAdapter.getItem(3);
		// Initiate fragment transaction untuk melakukan fragment operation
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		// Cek jika fragmentnya itu ada
		if(favoriteTvShowFragment != null){
			// Cek jika fragment di attach ke activity
			if(favoriteTvShowFragment.isAdded()){
				fragmentTransaction.detach(favoriteTvShowFragment);
				fragmentTransaction.attach(favoriteTvShowFragment);
				fragmentTransaction.commitAllowingStateLoss();
			}
		}
	}
}
