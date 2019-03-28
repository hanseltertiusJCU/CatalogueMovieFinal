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

/**
 * Class ini berguna untuk membuat RecyclerView item view yang menampung data2 penting di
 * {@link TvShowItem}
 */
public class TvShowAdapter extends RecyclerView.Adapter<TvShowAdapter.TvShowViewHolder> {

    // Initiate variable ArrayList<TvShowItem> utk data dan Context utk dapatin resources
    // (guna untuk mengatur {@link Spannable} object)
    private ArrayList<TvShowItem> mTvShowData = new ArrayList<>();
    private Context context;

    // Constructor yg membawa Fragment TV Show classes
    public TvShowAdapter(Context context) {
        this.context = context;
    }

    // Getter untuk return ArrayList<TvShowItem> variable beserta context variable
    public ArrayList<TvShowItem> getTvShowData() {
        return mTvShowData;
    }

    public Context getContext() {
        return context;
    }

    /**
     * Method tsb berguna untuk set tv show data ke adapter dan notify bahwa data
     * yang ada itu berubah
     *
     * @param mData ArrayList yang menampung TvShowItem object
     */
    public void setTvShowData(ArrayList<TvShowItem> mData) {

        // Clear existing array list content
        this.mTvShowData.clear();
        // Add semua isi data ke global variable ArrayList
        this.mTvShowData.addAll(mData);

        // Method tersebut berguna untuk memanggil adapter bahwa ada data yg bru, sehingga data tsb
        // dpt ditampilkan pada RecyclerView yg berisi adapter yg berkaitan dengan RecyclerView
        notifyDataSetChanged();
    }

    /**
     * Method tsb berguna untuk inflate xml layout lalu membuat {@link TvShowViewHolder} object
     *
     * @param viewGroup
     * @param i
     * @return {@link TvShowViewHolder} object
     */
    @NonNull
    @Override
    public TvShowViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // Set layout xml yang berisi movie items ke View
        View tvShowItem = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tv_show_items, viewGroup, false);
        // Return TvShowViewHolder dengan memanggil constructor TvShowViewHolder yang berisi View sbg
        // parameter
        return new TvShowViewHolder(tvShowItem);
    }

    /**
     * Method ini berguna untuk bind view yang ada di ViewHolder lalu assign values bedasarkan
     * {@link TvShowItem} object to view
     *
     * @param tvShowViewHolder {@link TvShowViewHolder} object bawaan dari onCreateViewHolder() method
     * @param position         position dari ArrayList
     */
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
        // Return seberapa banyak data yg di tampung di ArrayList
        return getTvShowData().size();
    }

    /**
     * Kelas ini berguna untuk menampung view yang ada tanpa mendeclare view di sebuah Adapter.
     * Selain itu, kelas tsb berguna untuk assign view ke tv_show_items.xml dan sbg parameter dari
     * onBindView() method
     */
    class TvShowViewHolder extends RecyclerView.ViewHolder {
        // Bind Views by find view by id
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

        // Assign views di dalam constructor
        TvShowViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
