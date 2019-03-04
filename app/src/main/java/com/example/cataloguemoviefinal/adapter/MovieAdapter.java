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
import com.example.cataloguemoviefinal.entity.MovieItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
	
	private ArrayList<MovieItem> mMovieData = new ArrayList<>();
	private Context context;
	
	public MovieAdapter(Context context) {
		this.context = context;
	}
	
	public ArrayList<MovieItem> getmMovieData() {
		return mMovieData;
	}
	
	public Context getContext() {
		return context;
	}
	
	public void setData(ArrayList<MovieItem> mData) {
		
		// Jika ada data di parameter, maka clear isi data di ArrayList global variable
		if(mData.size() > 0) {
			this.mMovieData.clear();
			// Add semua isi data ke global variable ArrayList
			this.mMovieData.addAll(mData);
		} else {
			this.mMovieData.clear();
		}
		
		
		// Method tersebut berguna untuk memanggil adapter bahwa ada data yg bru, sehingga data tsb
		// dpt ditampilkan pada RecyclerView yg berisi adapter yg berkaitan dengan RecyclerView
		notifyDataSetChanged();
	}
	
	@NonNull
	@Override
	public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
		// Set layout xml yang berisi movie items ke View
		View movieItem = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movie_items, viewGroup, false);
		// Return MovieViewHolder dengan memanggil constructor MovieViewHolder yang berisi View sbg
		// parameter
		return new MovieViewHolder(movieItem);
	}
	
	@Override
	public void onBindViewHolder(@NonNull MovieViewHolder movieViewHolder, int position) {
		// Load image jika ada poster path
		// Gunakan BuildConfig untuk menjaga credential
		String baseImageUrl = BuildConfig.POSTER_IMAGE_ITEM_URL;
		Picasso.get().load(baseImageUrl + mMovieData.get(position).getMoviePosterPath()).into(movieViewHolder.imageViewMoviePoster);
		
		movieViewHolder.textViewMovieTitle.setText(mMovieData.get(position).getMovieTitle());
		
		// Set textview content in movie item rating to contain a variety of different colors
		Spannable ratingMovieItemWord = new SpannableString(context.getString(R.string.span_movie_item_ratings) + " ");
		ratingMovieItemWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ratingMovieItemWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		movieViewHolder.textViewMovieRatings.setText(ratingMovieItemWord);
		Spannable ratingMovieItem = new SpannableString(mMovieData.get(position).getMovieRatings());
		ratingMovieItem.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.colorAccent)), 0, ratingMovieItem.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		movieViewHolder.textViewMovieRatings.append(ratingMovieItem);
		
		// Set textview content in movie item release date to contain a variety of different colors
		Spannable releaseDateMovieItemWord = new SpannableString(context.getString(R.string.span_movie_item_release_date) + " ");
		releaseDateMovieItemWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, releaseDateMovieItemWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		movieViewHolder.textViewMovieReleaseDate.setText(releaseDateMovieItemWord);
		Spannable releaseDateMovieItem = new SpannableString(mMovieData.get(position).getMovieReleaseDate());
		releaseDateMovieItem.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.colorAccent)), 0, releaseDateMovieItem.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		movieViewHolder.textViewMovieReleaseDate.append(releaseDateMovieItem);
		
		// Set textview content in movie item original language to contain a variety of different colors
		Spannable languageMovieItemWord = new SpannableString(context.getString(R.string.span_movie_item_language) + " ");
		languageMovieItemWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, languageMovieItemWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		movieViewHolder.textViewMovieOriginalLanguage.setText(languageMovieItemWord);
		Spannable languageMovieItem = new SpannableString(mMovieData.get(position).getMovieOriginalLanguage());
		languageMovieItem.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.colorAccent)), 0, languageMovieItem.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		movieViewHolder.textViewMovieOriginalLanguage.append(languageMovieItem);
	}
	
	@Override
	public long getItemId(int position) {
		// Return position dari sebuah item di RecyclerView
		return position;
	}
	
	@Override
	public int getItemCount() {
		return getmMovieData().size();
	}
	
	// Kelas ini berguna untuk menampung view yang ada tanpa mendeclare view di sebuah Adapter
	class MovieViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.poster_image)
		ImageView imageViewMoviePoster;
		@BindView(R.id.movie_title_text)
		TextView textViewMovieTitle;
		@BindView(R.id.movie_ratings_text)
		TextView textViewMovieRatings;
		@BindView(R.id.movie_release_date_text)
		TextView textViewMovieReleaseDate;
		@BindView(R.id.movie_language_text)
		TextView textViewMovieOriginalLanguage;
		
		// Assign view di dalam constructor
		MovieViewHolder(@NonNull View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
	
}