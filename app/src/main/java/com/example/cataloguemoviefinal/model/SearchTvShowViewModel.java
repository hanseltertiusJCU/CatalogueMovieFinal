package com.example.cataloguemoviefinal.model;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.cataloguemoviefinal.BuildConfig;
import com.example.cataloguemoviefinal.entity.TvShowItem;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Kelas ini berguna untuk:
 * - Membuat ViewModel object yang berisi {@link LiveData<ArrayList> yang berisi {@link TvShowItem}}
 * - Membuat LiveData object yang berguna untuk:
 * *) Membuat data berupa {@link ArrayList<TvShowItem>} dari URL Search TV Show
 * *) Getter method untuk dipasang ke {@link java.util.Observer<ArrayList> yang membawa
 * {@link TvShowItem}}
 * - Set Tv Show search keyword yang berguna untuk menghandle change dari keyword
 * - Recall LiveData ketika ada perubahan dari keyword search
 */
public class SearchTvShowViewModel extends AndroidViewModel {

	// Akses informasi yang berisi Search TV Show URL dari BuildConfig untuk menjaga credential
	private String apiKey = BuildConfig.API_KEY;
	private String searchTvShowUrlBase = BuildConfig.BASE_TV_SHOW_SEARCH_URL;
	private String tvShowSearchQuery = BuildConfig.SEARCH_QUERY;
	
	private TvShowSearchLiveData tvShowSearchLiveData;
	// Search keyword
	private String mTvShowSearchKeyword;

	/**
	 * Constuctor untuk membuat {@link android.arch.lifecycle.ViewModel} yang berguna untuk
	 * membuat {@link LiveData} object dengan memanggil constructor, constructor tsb di triggered
	 * oleh {@link android.arch.lifecycle.ViewModelProviders} of() method dengan membawa
	 * {@link com.example.cataloguemoviefinal.fragment.SearchTvShowFragment dan
	 * {@link com.example.cataloguemoviefinal.factory.SearchTvShowViewModelFactory}}
	 * berhubungan untuk ingin membuat {@link android.arch.lifecycle.ViewModel}
	 * dengan > 1 param yang berguna untuk memasang ViewModel di
	 * {@link com.example.cataloguemoviefinal.fragment.SearchTvShowFragment} lalu memanggil
	 * get() method
	 * @param application
	 * @param tvShowSearchKeyword keyword dari user input
	 */
	public SearchTvShowViewModel(@NonNull Application application, String tvShowSearchKeyword){
		super(application);
		this.mTvShowSearchKeyword = tvShowSearchKeyword;
		tvShowSearchLiveData = new TvShowSearchLiveData(application, tvShowSearchKeyword);
	}

	/**
	 * Getter method untuk dapat mengakses LiveData object yang berguna untuk observe data dengan
	 * memasang {@link android.arch.lifecycle.Observer}
	 * @return LiveData object / data2 yang ada
	 */
	public LiveData<ArrayList<TvShowItem>> getSearchTvShows(){
		return tvShowSearchLiveData;
	}

	/**
	 * Method tsb berguna untuk merubah keyword search
	 * @param tvShowSearchKeyword search tv show keyword dari {@link android.widget.SearchView}
	 */
	public void setTvShowSearchKeyword(String tvShowSearchKeyword){
		this.mTvShowSearchKeyword = tvShowSearchKeyword;
	}

	/**
	 * Method tsb berguna untuk merubah data dengan memanggil constructor LiveData object
	 * untuk membuat {@link LiveData<ArrayList> yang membawa {@link TvShowItem}} object kembali
	 */
	public void searchTvShowRecall(){
		// Panggil kembali live data dengan search keyword yang baru
		tvShowSearchLiveData = new TvShowSearchLiveData(getApplication(), mTvShowSearchKeyword);
	}

	/**
	 * Kelas ini berguna untuk:
	 * - Menampung data di ViewModel
	 * - Melakukan operation {@link AsyncTask} sebagai pengganti dari
	 * {@link android.support.v4.content.Loader}
	 */
	private class TvShowSearchLiveData extends LiveData<ArrayList<TvShowItem>>{
		
		private final Context context;
		private String tvShowSearchKeyword;

		/**
		 * Constructor untuk membuat LiveData object dan memanggil method untuk melakukan
		 * AsyncTask operation
		 * @param context
		 * @param tvShowSearchKeyword search tv show keyword
		 */
		public TvShowSearchLiveData(Context context, String tvShowSearchKeyword){
			this.context = context;
			this.tvShowSearchKeyword = tvShowSearchKeyword;
			loadSearchTvShowLiveData();
		}

		/**
		 * Method tsb berguna untuk menjalankan {@link AsyncTask} operation sbg pengganti dari
		 * loadInBackground() di AsyncTaskLoader, kita ini ingin mendapatkan data bedasarkan
		 * URL Search TV Show
		 */
		@SuppressLint("StaticFieldLeak")
		private void loadSearchTvShowLiveData(){
			
			new AsyncTask<Void, Void, ArrayList<TvShowItem>>(){
				
				@Override
				protected ArrayList<TvShowItem> doInBackground(Void... voids) {

					// Menginisiasikan SyncHttpClient object yang berguna untuk menjalankan proses
					// scr Synchronous krn Loader itu sudah berjalan pada
					// background thread
					SyncHttpClient syncHttpClient = new SyncHttpClient();
					
					final ArrayList<TvShowItem> tvShowItems = new ArrayList<>();
					
					String tvShowSearchUrl = searchTvShowUrlBase + apiKey + tvShowSearchQuery + mTvShowSearchKeyword;

					// Melakukan GET Operation di HTTP dengan menggunakan SyncHttpClient
					syncHttpClient.get(tvShowSearchUrl, new AsyncHttpResponseHandler() {

						@Override
						public void onStart(){
							super.onStart();
							// make the handler synchronous
							setUseSynchronousMode(true);
						}
						
						@Override
						public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
							try{
								String result = new String(responseBody);
								JSONObject responseObject = new JSONObject(result);
								JSONArray results = responseObject.getJSONArray("results");
								// Iterate semua data yg ada dan tambahkan ke ArrayList
								for(int i = 0; i < results.length() ; i++){
									JSONObject tvShow = results.getJSONObject(i);
									TvShowItem tvShowItem = new TvShowItem(tvShow, false);
									// Cek jika posterPath itu tidak "null" karena null dr JSON itu berupa
									// String, sehingga perlu menggunakan "" di dalam null
									if(!tvShowItem.getTvShowPosterPath().equals("null")){
										tvShowItems.add(tvShowItem);
									}
								}
							} catch(Exception e){
								e.printStackTrace();
							}
						}
						
						@Override
						public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
						
						}
					});
					
					return tvShowItems;
				}

				/**
				 * Method ini berguna untuk set value bawaan dari doInBackground() method
				 * dan juga pasang value ke Observer jika ada
				 * @param tvShowItems ArrayList hasil dari doInBackground method
				 */
				@Override
				protected void onPostExecute(ArrayList<TvShowItem> tvShowItems) {
					setValue(tvShowItems);
				}
			}.execute();
		}
	}
}
