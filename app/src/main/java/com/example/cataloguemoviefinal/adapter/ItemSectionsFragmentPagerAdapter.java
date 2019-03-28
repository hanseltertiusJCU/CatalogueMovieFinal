package com.example.cataloguemoviefinal.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Kelas ini berguna untuk memuat Fragment list dan title
 * ({@link android.support.v7.app.ActionBar} title) yang dipakai untuk {@link ViewPager}
 */
public class ItemSectionsFragmentPagerAdapter extends FragmentPagerAdapter {
	// Create ArrayList untuk menampung Fragment beserta Title
	private final List<Fragment> mFragmentList = new ArrayList<>();
	private final List<String> mFragmentTitleList = new ArrayList<>();
	// Create Context object
	private Context mContext;

	/**
	 * Constructor untuk membuat object {@link ItemSectionsFragmentPagerAdapter} yang membawa
	 * {@link FragmentManager} object dari getSupportFragmentManager() method di
	 * {@link com.example.cataloguemoviefinal.MainActivity}. Dalam fungsional,
	 * ia memanggil {@link FragmentPagerAdapter} yang merupakan
	 * @param context
	 * @param fragmentManager
	 */
	public ItemSectionsFragmentPagerAdapter(Context context, FragmentManager fragmentManager) {
		super(fragmentManager);
		mContext = context;
	}

	/**
	 * Method ini mereturn Fragment object dari {@link ViewPager} dan
	 * {@link android.support.design.widget.TabLayout} position karena
	 * TabLayout merupakan bagian dari ViewPager
	 * @param position dari fragment pager adapter
	 * @return Fragment object
	 */
	@Override
	public Fragment getItem(int position) {
		return mFragmentList.get(position);
	}

	/**
	 * Method ini berguna untuk mereturn berapa Fragment yang ada di FragmentPagerAdapter
	 * @return ukuran dari ArrayList yang membawa Fragment
	 */
	@Override
	public int getCount() {
		return mFragmentList.size();
	}

	/**
	 * Method tsb berguna untuk memasukkan fragment dan title ke ArrayList
	 * masing-masing bedasarkan input parameter yang ada
	 * @param fragment Fragment object bawaan dari {@link com.example.cataloguemoviefinal.MainActivity}
	 * @param title String object bawaan dari {@link com.example.cataloguemoviefinal.MainActivity}
	 */
	public void addMovieSectionFragment(Fragment fragment, String title) {
		mFragmentList.add(fragment);
		mFragmentTitleList.add(title);
	}

	/**
	 * Method ini berguna untuk return page title bedasarkan position dari
	 * {@link FragmentPagerAdapter}
	 * @param position position dari FragmentPagerAdapter
	 * @return page title
	 */
	@Nullable
	@Override
	public CharSequence getPageTitle(int position) {
		return mFragmentTitleList.get(position);
	}
}
