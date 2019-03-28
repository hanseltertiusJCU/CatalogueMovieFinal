package com.example.cataloguemoviefinal.support;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.cataloguemoviefinal.R;

/**
 * Kelas ini berguna untuk memasang Click support ke {@link RecyclerView} item
 */
public class ItemClickSupport {
	private final RecyclerView mRecyclerView;
	private OnItemClickListener mOnItemClickListener;

	/**
	 * Create View.OnClickListener object untuk dapat menghandle view click
	 */
	private View.OnClickListener mOnClickListener = new View.OnClickListener(){
		@Override
		public void onClick(View view){
			if(mOnItemClickListener != null){
				RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(view);
				// Use the interface method based on adapter position in RecyclerView
				mOnItemClickListener.onItemClicked(mRecyclerView , holder.getAdapterPosition() , view);
			}
		}
	};

	/**
	 * Create constructor that set tag to recyclerview
	 * as well as attach listener to recyclerview items
	 * @param recyclerView RecyclerView item
	 */
	private ItemClickSupport(RecyclerView recyclerView){
		// Set recyclerview global variable dari parameter
		mRecyclerView = recyclerView;
		mRecyclerView.setTag(R.id.item_click_support , this); // Set tag ke RecyclerView object
		/*
		  Line ini berguna sbg listener ketika
		  {@link com.example.cataloguemoviefinal.adapter.MovieAdapter.MovieViewHolder} ataupun
		  {@link com.example.cataloguemoviefinal.adapter.TvShowAdapter.TvShowViewHolder}
		  attached ke/detatched dari {@link RecyclerView}
		 */
		RecyclerView.OnChildAttachStateChangeListener mAttachListener = new RecyclerView.OnChildAttachStateChangeListener() {
			// triggered ketika view nempel ke {@link RecyclerView}
			@Override
			public void onChildViewAttachedToWindow(@NonNull View view) {
				if(mOnItemClickListener != null) {
					view.setOnClickListener(mOnClickListener);
				}
			}
			
			@Override
			public void onChildViewDetachedFromWindow(@NonNull View view) {
			
			}
		};
		mRecyclerView.addOnChildAttachStateChangeListener(mAttachListener); // Add
		// RecyclerView.OnChildAttachStateChangeListener object ke RecyclerView
	}

	/**
	 * Method to attach {@link ItemClickSupport} ke {@link RecyclerView}
	 * @param view RecyclerView
	 * @return ItemClickSupport object
	 */
	public static ItemClickSupport addSupportToView(RecyclerView view){
		// Get tag from id in {@link res} files, yang merepresentasikan RecyclerView
		ItemClickSupport support = (ItemClickSupport) view.getTag(R.id.item_click_support);
		// Check if there is no ItemClickSupport class
		if(support == null){
			support = new ItemClickSupport(view); // Initiate new ItemClickSupport object
		}
		return support;
	}

	/**
	 * Method ini berguna utk set {@link OnItemClickListener} object value
	 * @param listener value utk di set ke global variable {@link OnItemClickListener}
	 */
	public void setOnItemClickListener(OnItemClickListener listener){
		this.mOnItemClickListener = listener;
	}

	/**
	 * Interface object yang berguna untuk set method yang dipakai untuk
	 * {@link android.support.v4.app.Fragment} yang berisi data2
	 * {@link com.example.cataloguemoviefinal.entity.MovieItem} maupun
	 * {@link com.example.cataloguemoviefinal.entity.TvShowItem}
	 * sebagai callback method yang berguna untuk transfer
	 * {@link com.example.cataloguemoviefinal.entity.MovieItem} ataupun
	 * {@link com.example.cataloguemoviefinal.entity.TvShowItem} ke
	 * {@link com.example.cataloguemoviefinal.DetailActivity}
	 */
	public interface OnItemClickListener{
		void onItemClicked(RecyclerView recyclerView, int position, View view);
	}
}
