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
 * Kelas ini berguna untuk membuat:
 * - ViewModel object yang berisi {@link LiveData<ArrayList> yang berisi {@link TvShowItem}}
 * - LiveData object yang berguna untuk:
 * *) Membuat data berupa {@link ArrayList<TvShowItem>} dari URL Discover TV Show
 * *) Getter method untuk dipasang ke {@link java.util.Observer<ArrayList> yang membawa
 * {@link TvShowItem}}
 */
public class TvShowViewModel extends AndroidViewModel{
	
	// Create object yang mengextend LiveData<ArrayList<TvShowItem>>
	private TvShowLiveData tvShowLiveData;

	// akses informasi yang berisi Discover Tv Show URL dari BuildConfig untuk menjaga credential
	private String apiKey = BuildConfig.API_KEY;
	private String tvShowUrlBase = BuildConfig.BASE_DISCOVER_TV_SHOW_URL;
	private String languageUs = BuildConfig.LANGUAGE_US;

	/**
	 * Constuctor untuk membuat {@link android.arch.lifecycle.ViewModel} yang berguna untuk
	 * membuat {@link LiveData} object dengan memanggil constructor, constructor tsb di triggered
	 * oleh {@link android.arch.lifecycle.ViewModelProviders} of yang berguna untuk memasang
	 * ViewModel di {@link com.example.cataloguemoviefinal.fragment.TvShowFragment} lalu memanggil get() method
	 * @param application
	 */
	public TvShowViewModel(@NonNull Application application){
		super(application);
		tvShowLiveData = new TvShowLiveData(application);
	}

	/**
	 * Getter method untuk dapat mengakses LiveData object yang berguna untuk observe data dengan
	 * memasang {@link android.arch.lifecycle.Observer}
	 * @return LiveData object / data2 yang ada
	 */
	public LiveData <ArrayList <TvShowItem>> getTvShows(){
		return tvShowLiveData;
	}

	/**
	 * Kelas ini berguna untuk:
	 * - Menampung data di ViewModel
	 * - Melakukan operation {@link AsyncTask} sebagai pengganti dari
	 * {@link android.support.v4.content.Loader}
	 */
	public class TvShowLiveData extends LiveData <ArrayList <TvShowItem>>{
		private final Context context;

		/**
		 * Constructor untuk membuat LiveData object dan memanggil method untuk melakukan
		 * AsyncTask operation
		 * @param context
		 */
		TvShowLiveData(Context context){
			this.context = context;
			loadTvShowLiveData();
		}

		/**
		 * Method tsb berguna untuk menjalankan {@link AsyncTask} operation sbg pengganti dari
		 * loadInBackground() di AsyncTaskLoader, kita ini ingin mendapatkan data bedasarkan
		 * URL Discover Tv Show
		 */
		@SuppressLint("StaticFieldLeak")
		private void loadTvShowLiveData(){
			new AsyncTask <Void, Void, ArrayList <TvShowItem>>(){
				
				@Override
				protected ArrayList <TvShowItem> doInBackground(Void... voids){
					
					// Menginisiasikan SyncHttpClient object yang berguna untuk menjalankan proses
					// scr Synchronous krn Loader itu sudah berjalan pada
					// background thread
					SyncHttpClient syncHttpClient = new SyncHttpClient();
					
					final ArrayList <TvShowItem> tvShowItems = new ArrayList <>();
					
					String tvShowUrl = tvShowUrlBase + apiKey + languageUs;

					syncHttpClient.get(tvShowUrl , new AsyncHttpResponseHandler(){

						@Override
						public void onStart(){
							super.onStart();
							// make the handler synchronous
							setUseSynchronousMode(true);
						}

						@Override
						public void onSuccess(int statusCode , Header[] headers , byte[] responseBody){
							try{
								String result = new String(responseBody);
								JSONObject responseObject = new JSONObject(result);
								JSONArray results = responseObject.getJSONArray("results");
								// Iterate semua data yg ada dan tambahkan ke ArrayList
								for(int i = 0 ; i < results.length() ; i++){
									JSONObject tvShow = results.getJSONObject(i);
									TvShowItem tvShowItem = new TvShowItem(tvShow , false);
									// Cek jika posterPath itu tidak "null" karena null dr JSON itu berupa
									// String, sehingga perlu menggunakan "" di dalam null
									if(! tvShowItem.getTvShowPosterPath().equals("null")){
										tvShowItems.add(tvShowItem);
									}
								}
							} catch(Exception e){
								e.printStackTrace();
							}
							
						}
						
						@Override
						public void onFailure(int statusCode , Header[] headers , byte[] responseBody , Throwable error){
						
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
				protected void onPostExecute(ArrayList <TvShowItem> tvShowItems){
					setValue(tvShowItems);
				}
			}.execute(); // Execute AsyncTask
		}
	}
}
