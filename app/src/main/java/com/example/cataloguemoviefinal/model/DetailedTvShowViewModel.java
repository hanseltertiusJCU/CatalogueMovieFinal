package com.example.cataloguemoviefinal.model;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;


import com.example.cataloguemoviefinal.BuildConfig;
import com.example.cataloguemoviefinal.entity.TvShowItem;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class DetailedTvShowViewModel extends AndroidViewModel{
	
	// Gunakan Build Config untuk melindungi credential
	private String apiKey = BuildConfig.API_KEY;
	private String detailedTvShowUrlBase = BuildConfig.BASE_TV_SHOW_DETAILED_URL;
	private String apiKeyFiller = BuildConfig.DETAILED_ITEM_API_KEY_FILLER;
	
	private DetailedTvShowLiveData detailedTvShowLiveData;
	
	private int mDetailedTvShowId;
	
	public DetailedTvShowViewModel(Application application , int detailedTvShowId){
		super(application);
		this.mDetailedTvShowId = detailedTvShowId;
		// Buat LiveData agar dapat di return ke getDetailedTvShow method
		detailedTvShowLiveData = new DetailedTvShowLiveData(application , detailedTvShowId);
	}
	
	public LiveData <ArrayList <TvShowItem>> getDetailedTvShow(){
		return detailedTvShowLiveData;
	}
	
	private class DetailedTvShowLiveData extends LiveData <ArrayList <TvShowItem>>{
		private final Context context;
		private final int id;
		
		// Buat constructor untuk mengakomodasi parameter yang ada dari {@link DetailedTvShowViewModel}
		DetailedTvShowLiveData(Context context , int id){
			this.context = context;
			this.id = id;
			loadDetailedTvShowLiveData();
		}
		
		@SuppressLint("StaticFieldLeak")
		private void loadDetailedTvShowLiveData(){
			
			new AsyncTask <Void, Void, ArrayList <TvShowItem>>(){
				
				@Override
				protected ArrayList <TvShowItem> doInBackground(Void... voids){
					
					SyncHttpClient syncHttpClient = new SyncHttpClient();
					
					final ArrayList <TvShowItem> tvShowItems = new ArrayList <>();
					
					String detailedTvShowUrl = detailedTvShowUrlBase + mDetailedTvShowId + apiKeyFiller + apiKey;
					
					syncHttpClient.get(detailedTvShowUrl , new AsyncHttpResponseHandler(){
						
						@Override
						public void onStart(){
							super.onStart();
							
							setUseSynchronousMode(true);
						}
						
						@Override
						public void onSuccess(int statusCode , Header[] headers , byte[] responseBody){
							try{
								String result = new String(responseBody);
								JSONObject responseObject = new JSONObject(result);
								boolean detailedItem = true;
								TvShowItem tvShowItem = new TvShowItem(responseObject , detailedItem);
								tvShowItems.add(tvShowItem);
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
