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

public class TvShowViewModel extends AndroidViewModel{
	
	// Create object yang mengextend LiveData<ArrayList<TvShowItem>>
	private TvShowLiveData tvShowLiveData;
	
	// Beberapa informasi diakses dari BuildConfig untuk menjaga credential
	private String apiKey = BuildConfig.API_KEY;
	private String tvShowUrlBase = BuildConfig.BASE_DISCOVER_TV_SHOW_URL;
	private String languageUs = BuildConfig.LANGUAGE_US;
	
	public TvShowViewModel(@NonNull Application application){
		super(application);
		tvShowLiveData = new TvShowLiveData(application);
	}
	
	public LiveData <ArrayList <TvShowItem>> getTvShows(){
		return tvShowLiveData;
	}
	
	public class TvShowLiveData extends LiveData <ArrayList <TvShowItem>>{
		private final Context context;
		
		TvShowLiveData(Context context){
			this.context = context;
			loadTvShowLiveData();
		}
		
		// Method tsb berguna untuk menjalankan tugas scr async sbg pengganti dari loadInBackground()
		// di AsyncTaskLoader
		@SuppressLint("StaticFieldLeak")
		private void loadTvShowLiveData(){
			new AsyncTask <Void, Void, ArrayList <TvShowItem>>(){
				
				@Override
				protected ArrayList <TvShowItem> doInBackground(Void... voids){
					
					// Menginisiasikan SyncHttpClientObject krn Loader itu sudah berjalan pada background thread
					SyncHttpClient syncHttpClient = new SyncHttpClient();
					
					final ArrayList <TvShowItem> tvShowItems = new ArrayList <>();
					
					String tvShowUrl = tvShowUrlBase + apiKey + languageUs;
					syncHttpClient.get(tvShowUrl , new AsyncHttpResponseHandler(){
						@Override
						public void onSuccess(int statusCode , Header[] headers , byte[] responseBody){
							try{
								String result = new String(responseBody);
								JSONObject responseObject = new JSONObject(result);
								JSONArray results = responseObject.getJSONArray("results");
								// Iterate semua data yg ada dan tambahkan ke ArrayList
								for(int i = 0 ; i < results.length() ; i++){
									JSONObject tvShow = results.getJSONObject(i);
									boolean detailedItem = false;
									TvShowItem tvShowItem = new TvShowItem(tvShow , detailedItem);
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
				
				@Override
				protected void onPostExecute(ArrayList <TvShowItem> tvShowItems){
					// Set value dari Observer yang berisi ArrayList yang merupakan
					// hasil dari doInBackground method
					setValue(tvShowItems);
				}
			}.execute(); // Execute AsyncTask
		}
	}
}
