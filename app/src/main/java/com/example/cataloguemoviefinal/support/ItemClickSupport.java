package com.example.cataloguemoviefinal.support;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.cataloguemoviefinal.R;


public class ItemClickSupport {
	private final RecyclerView mRecyclerView;
	private OnItemClickListener mOnItemClickListener;
	
	// Create View.OnClickListener object
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
	
	// Create constructor that set tag to recyclerview
	// as well as attach listener to recyclerview items
	private ItemClickSupport(RecyclerView recyclerView){
		mRecyclerView = recyclerView;
		mRecyclerView.setTag(R.id.item_click_support , this);
		// Attach on click listener to recyclerview
		RecyclerView.OnChildAttachStateChangeListener mAttachListener = new RecyclerView.OnChildAttachStateChangeListener() {
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
		mRecyclerView.addOnChildAttachStateChangeListener(mAttachListener);
	}
	
	// Method to return support that attach support to the view
	public static ItemClickSupport addSupportToView(RecyclerView view){
		ItemClickSupport support = (ItemClickSupport) view.getTag(R.id.item_click_support);
		if(support == null){
			support = new ItemClickSupport(view);
		}
		return support;
	}
	
	public void setOnItemClickListener(OnItemClickListener listener){
		this.mOnItemClickListener = listener;
	}
	
	public interface OnItemClickListener{
		void onItemClicked(RecyclerView recyclerView, int position, View view);
	}
}
