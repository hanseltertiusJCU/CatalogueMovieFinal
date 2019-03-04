package com.example.cataloguemoviefinal;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


import com.example.cataloguemoviefinal.adapter.ItemSectionsFragmentPagerAdapter;
import com.example.cataloguemoviefinal.fragment.FavoriteMovieFragment;
import com.example.cataloguemoviefinal.fragment.FavoriteTvShowFragment;
import com.example.cataloguemoviefinal.fragment.MovieFragment;
import com.example.cataloguemoviefinal.fragment.TvShowFragment;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
	
	// Create ViewPager untuk swipe Fragments
	@BindView(R.id.item_viewPager)
	ViewPager viewPager;
	// Assign TabLayout
	@BindView(R.id.menu_tabs)
	TabLayout tabLayout;
	@BindView(R.id.main_toolbar)
	Toolbar mainToolbar;
	private ItemSectionsFragmentPagerAdapter itemSectionsFragmentPagerAdapter;
	// Set textview untuk isi dari TabLayout
	private TextView tabMovie;
	private TextView tabTvShow;
	private TextView tabFavoriteMovie;
	private TextView tabFavoriteTvShow;
	// Set drawable array beserta drawable untuk icon dr TabLayout
	private Drawable[] movieDrawables;
	private Drawable movieDrawable;
	private Drawable[] tvShowDrawables;
	private Drawable tvShowDrawable;
	private Drawable[] favoriteMovieDrawables;
	private Drawable favoriteMovieDrawable;
	private Drawable[] favoriteTvShowDrawables;
	private Drawable favoriteTvShowDrawable;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set content activity to use layout xml file activity_main.xml
		setContentView(R.layout.activity_main); // penyebab errornya
		
		ButterKnife.bind(this);
		
		setSupportActionBar(mainToolbar);
		
		// Cek kalo ada action bar
		if(getSupportActionBar() != null) {
			// Set default action bar title, yaitu "Movie"
			getSupportActionBar().setTitle(getString(R.string.movie));
		}
		
		// Panggil method ini untuk saving Fragment state di ViewPager, kesannya kyk simpen
		// fragment ketika sebuah fragment sedang tidak di display.
		// Kita menggunakan value 3 sebagai parameter karena kita punya 4 fragments, dan kita
		// hanya butuh simpan 3 fragments (1 lg untuk display).
		viewPager.setOffscreenPageLimit(3);
		
		// Panggil method tsb untuk membuat fragment yang akan disimpan ke ViewPager
		createViewPagerContent(viewPager);
		
		// Beri ViewPager ke TabLayout
		tabLayout.setupWithViewPager(viewPager);
		
		// Panggil method tsb untuk membuat isi dari setiap tab
		createTabIcons();
		
		// Set listener untuk tab layout
		tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			// Set action bar title ketika sebuah tab dipilih
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
						movieDrawable.setTint(getResources().getColor(R.color.colorAccent));
						tabMovie.setTextColor(getResources().getColor(R.color.colorAccent));
						break;
					case 1:
						tvShowDrawables = tabTvShow.getCompoundDrawables();
						tvShowDrawable = tvShowDrawables[1];
						tvShowDrawable.setTint(getResources().getColor(R.color.colorAccent));
						tabTvShow.setTextColor(getResources().getColor(R.color.colorAccent));
						break;
					case 2:
						favoriteMovieDrawables = tabFavoriteMovie.getCompoundDrawables();
						favoriteMovieDrawable = favoriteMovieDrawables[1];
						favoriteMovieDrawable.setTint(getResources().getColor(R.color.colorAccent));
						tabFavoriteMovie.setTextColor(getResources().getColor(R.color.colorAccent));
						break;
					case 3:
						favoriteTvShowDrawables = tabFavoriteTvShow.getCompoundDrawables();
						favoriteTvShowDrawable = favoriteTvShowDrawables[1];
						favoriteTvShowDrawable.setTint(getResources().getColor(R.color.colorAccent));
						tabFavoriteTvShow.setTextColor(getResources().getColor(R.color.colorAccent));
						break;
					default:
						break;
				}
				
			}
			
			@Override
			public void onTabUnselected(TabLayout.Tab tab) {
				int position = tab.getPosition();
				// Ubah text color dan drawable tint menjadi hitam, yang menandakan bahwa itemnya
				// sedang tidak dipilih
				switch(position) {
					case 0:
						movieDrawables = tabMovie.getCompoundDrawables();
						movieDrawable = movieDrawables[1];
						movieDrawable.setTint(getResources().getColor(R.color.colorBlack));
						tabMovie.setTextColor(getResources().getColor(R.color.colorBlack));
						break;
					case 1:
						tvShowDrawables = tabTvShow.getCompoundDrawables();
						tvShowDrawable = tvShowDrawables[1];
						tvShowDrawable.setTint(getResources().getColor(R.color.colorBlack));
						tabTvShow.setTextColor(getResources().getColor(R.color.colorBlack));
						break;
					case 2:
						favoriteMovieDrawables = tabFavoriteMovie.getCompoundDrawables();
						favoriteMovieDrawable = favoriteMovieDrawables[1];
						favoriteMovieDrawable.setTint(getResources().getColor(R.color.colorBlack));
						tabFavoriteMovie.setTextColor(getResources().getColor(R.color.colorBlack));
					case 3:
						favoriteTvShowDrawables = tabFavoriteTvShow.getCompoundDrawables();
						favoriteTvShowDrawable = favoriteTvShowDrawables[1];
						favoriteTvShowDrawable.setTint(getResources().getColor(R.color.colorBlack));
						tabFavoriteTvShow.setTextColor(getResources().getColor(R.color.colorBlack));
						break;
					default:
						break;
				}
				
			}
			
			@Override
			public void onTabReselected(TabLayout.Tab tab) {
			
			}
		});
		
	}
	
	// Method tsb berguna untuk membuat icons beserta isinya di Tab
	private void createTabIcons() {
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
		movieDrawable.setTint(getResources().getColor(R.color.colorAccent));
		// Set default text color yang menandakan bahwa tabnya itu sedang d select
		tabMovie.setTextColor(getResources().getColor(R.color.colorAccent));
		
		// Inflate custom_tab.xml ke dalam TabLayout
		Objects.requireNonNull(tabLayout.getTabAt(0)).setCustomView(tabMovie);
		
		tabTvShow = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
		tabTvShow.setText(getString(R.string.tv_show));
		tabTvShow.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_tv_show, 0, 0);
		Objects.requireNonNull(tabLayout.getTabAt(1)).setCustomView(tabTvShow);
		
		tabFavoriteMovie = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
		tabFavoriteMovie.setText(getString(R.string.favorite));
		tabFavoriteMovie.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_movie_favorite, 0, 0);
		Objects.requireNonNull(tabLayout.getTabAt(2)).setCustomView(tabFavoriteMovie);
		
		tabFavoriteTvShow = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
		tabFavoriteTvShow.setText(getString(R.string.favorite));
		tabFavoriteTvShow.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_tv_show_favorite, 0, 0);
		Objects.requireNonNull(tabLayout.getTabAt(3)).setCustomView(tabFavoriteTvShow);
	}
	
	// Method tsb berguna untuk membuat isi dari ViewPager
	private void createViewPagerContent(ViewPager viewPager) {
		
		// Create FragmentPagerAdapter untuk mengetahui fragment mana yg di show
		itemSectionsFragmentPagerAdapter = new ItemSectionsFragmentPagerAdapter(this, getSupportFragmentManager());
		
		// Tambahkan fragment beserta title ke FragmentPagerAdapter
		itemSectionsFragmentPagerAdapter.addMovieSectionFragment(new MovieFragment(), getString(R.string.movie));
		itemSectionsFragmentPagerAdapter.addMovieSectionFragment(new TvShowFragment(), getString(R.string.tv_show));
		itemSectionsFragmentPagerAdapter.addMovieSectionFragment(new FavoriteMovieFragment(), getString(R.string.favorite_movie));
		itemSectionsFragmentPagerAdapter.addMovieSectionFragment(new FavoriteTvShowFragment(), getString(R.string.favorite_tv_show));
		
		// Set FragmentPagerAdapter ke ViewPager
		viewPager.setAdapter(itemSectionsFragmentPagerAdapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_language_settings, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.action_change_language_settings) {
			Intent mIntent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
			startActivity(mIntent);
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
	}
	
	public void setActionBarTitle(String title) {
		if(getSupportActionBar() != null) {
			// Gunakan getSupportActionBar untuk backward compatibility
			getSupportActionBar().setTitle(title);
		}
	}
}
