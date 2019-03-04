package com.example.cataloguemoviefinal.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ItemSectionsFragmentPagerAdapter extends FragmentPagerAdapter {
	// Create ArrayList untuk menampung Fragment beserta Title
	private final List<Fragment> mFragmentList = new ArrayList<>();
	private final List<String> mFragmentTitleList = new ArrayList<>();
	private Context mContext;
	
	public ItemSectionsFragmentPagerAdapter(Context context, FragmentManager fragmentManager) {
		super(fragmentManager);
		mContext = context;
	}
	
	// Method ini menentukan posisi fragment untuk setiap tab (mungkin bwt pemanggilan fragment)
	@Override
	public Fragment getItem(int position) {
		return mFragmentList.get(position);
	}
	
	// Method ini menentukan berapa banyak tabs yang ada
	@Override
	public int getCount() {
		return mFragmentList.size();
	}
	
	// Method tsb berguna untuk memasukkan fragment dan title ke ArrayList masing-masing bedasarkan
	// input parameter yang ada
	public void addMovieSectionFragment(Fragment fragment, String title) {
		mFragmentList.add(fragment);
		mFragmentTitleList.add(title);
	}
	
	// Method ini berguna untuk mereturn tab title
	@Nullable
	@Override
	public CharSequence getPageTitle(int position) {
		return mFragmentTitleList.get(position);
	}
}
