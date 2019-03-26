package com.example.cataloguemoviefinal.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cataloguemoviefinal.BuildConfig;
import com.example.cataloguemoviefinal.R;
import com.example.cataloguemoviefinal.entity.TvShowItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TvShowAdapter extends RecyclerView.Adapter<TvShowAdapter.TvShowViewHolder> {
	
	private ArrayList<TvShowItem> mTvShowData = new ArrayList<>();
	private Context context;
	
	public TvShowAdapter(Context context) {
		this.context = context;
	}
	
	public ArrayList<TvShowItem> getTvShowData() {
		return mTvShowData;
	}
	
	public Context getContext() {
		return context;
	}
	
	public void setTvShowData(ArrayList<TvShowItem> mData) {
		
		// Jika ada data di parameter, maka clear isi data di ArrayList global variable
		if(mData.size() > 0) {
			this.mTvShowData.clear();
			// Add semua isi data ke global variable ArrayList
			this.mTvShowData.addAll(mData);
		} else {
			this.mTvShowData.clear();
		}
		
		
		
		// Method tersebut berguna untuk memanggil adapter bahwa ada data yg bru, sehingga data tsb
		// dpt ditampilkan pada RecyclerView yg berisi adapter yg berkaitan dengan RecyclerView
		notifyDataSetChanged();
	}
	
	@NonNull
	@Override
	public TvShowViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
		// Set layout xml yang berisi movie items ke View
		View tvShowItem = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tv_show_items, viewGroup, false);
		// Return TvShowViewHolder dengan memanggil constructor TvShowViewHolder yang berisi View sbg
		// parameter
		return new TvShowViewHolder(tvShowItem);
	}
	
	@Override
	public void onBindViewHolder(@NonNull TvShowViewHolder tvShowViewHolder, int position) {
		// Load image jika ada poster path
		// Gunakan BuildConfig untuk menjaga credential
		String baseImageUrl = BuildConfig.POSTER_IMAGE_ITEM_URL;
		Picasso.get().load(baseImageUrl + mTvShowData.get(position).getTvShowPosterPath()).into(tvShowViewHolder.imageViewTvShowPoster);
		
		tvShowViewHolder.textViewTvShowName.setText(mTvShowData.get(position).getTvShowName());
		
		// Set textview content in tv show item rating to contain a variety of different colors
		Spannable ratingTvShowItemWord = new SpannableString(context.getString(R.string.span_tv_show_item_ratings) + " ");
		ratingTvShowItemWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ratingTvShowItemWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		tvShowViewHolder.textViewTvShowRatings.setText(ratingTvShowItemWord);
		Spannable ratingTvShowItem = new SpannableString(mTvShowData.get(position).getTvShowRatings());
		ratingTvShowItem.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.colorAccent)), 0, ratingTvShowItem.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		tvShowViewHolder.textViewTvShowRatings.append(ratingTvShowItem);
		
		// Set textview content in tv show item first air date to contain a variety of different colors
		Spannable firstAirDateTvShowItemWord = new SpannableString(context.getString(R.string.span_tv_show_item_first_air_date) + " ");
		firstAirDateTvShowItemWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, firstAirDateTvShowItemWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		tvShowViewHolder.textViewTvShowFirstAirDate.setText(firstAirDateTvShowItemWord);
		Spannable firstAirDateTvShowItem = new SpannableString(mTvShowData.get(position).getTvShowFirstAirDate());
		firstAirDateTvShowItem.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.colorAccent)), 0, firstAirDateTvShowItem.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		tvShowViewHolder.textViewTvShowFirstAirDate.append(firstAirDateTvShowItem);
		
		// Set textview content in tv show item original language to contain a variety of different colors
		Spannable originalLanguageTvShowItemWord = new SpannableString(context.getString(R.string.span_tv_show_item_language) + " ");
		originalLanguageTvShowItemWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, originalLanguageTvShowItemWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		tvShowViewHolder.textViewTvShowOriginalLanguage.setText(originalLanguageTvShowItemWord);
		Spannable originalLanguageTvShowItem = new SpannableString(mTvShowData.get(position).getTvShowOriginalLanguage());
		originalLanguageTvShowItem.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.colorAccent)), 0, originalLanguageTvShowItem.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		tvShowViewHolder.textViewTvShowOriginalLanguage.append(originalLanguageTvShowItem);
	}
	
	@Override
	public long getItemId(int position) {
		// Return position dari sebuah item di RecyclerView
		return position;
	}
	
	@Override
	public int getItemCount() {
		return getTvShowData().size();
	}
	
	class TvShowViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.poster_image)
		ImageView imageViewTvShowPoster;
		@BindView(R.id.tv_show_name_text)
		TextView textViewTvShowName;
		@BindView(R.id.tv_show_ratings_text)
		TextView textViewTvShowRatings;
		@BindView(R.id.tv_show_first_air_date_text)
		TextView textViewTvShowFirstAirDate;
		@BindView(R.id.tv_show_language_text)
		TextView textViewTvShowOriginalLanguage;
		
		TvShowViewHolder(@NonNull View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
