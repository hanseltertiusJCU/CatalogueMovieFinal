package com.example.cataloguemoviefinal.model;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.cataloguemoviefinal.BuildConfig;
import com.example.cataloguemoviefinal.entity.MovieItem;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class DetailedMovieViewModel extends AndroidViewModel{
	
	// Gunakan Build Config untuk melindungi credential
	private String apiKey = BuildConfig.API_KEY;
	private String detailedUrlBase = BuildConfig.BASE_MOVIE_DETAILED_URL;
	private String apiKeyFiller = BuildConfig.DETAILED_ITEM_API_KEY_FILLER;
	
	
	private DetailedMovieLiveData detailedMovieLiveData;
	
	private int mDetailedMovieId;
	
	public DetailedMovieViewModel(@NonNull Application application , int detailedMovieId){
		super(application);
		this.mDetailedMovieId = detailedMovieId;
		// Buat LiveData agar dapat di return ke getDetailedMovie method
		detailedMovieLiveData = new DetailedMovieLiveData(application , detailedMovieId);
	}
	
	public LiveData <ArrayList <MovieItem>> getDetailedMovie(){
		return detailedMovieLiveData;
	}
	
	private class DetailedMovieLiveData extends LiveData <ArrayList <MovieItem>>{
		private final Context context;
		private final int id;
		
		// Buat constructor untuk mengakomodasi parameter yang ada dari {@link DetailedMovieViewModel}
		DetailedMovieLiveData(Context context, int id){
			this.context = context;
			this.id = id;
			loadDetailedMovieLiveData();
		}
		
		@SuppressLint("StaticFieldLeak")
		private void loadDetailedMovieLiveData(){
			
			new AsyncTask <Void, Void, ArrayList <MovieItem>>(){
				
				@Override
				protected ArrayList <MovieItem> doInBackground(Void... voids){
					
					SyncHttpClient syncHttpClient = new SyncHttpClient();
					
					final ArrayList <MovieItem> movieItemses = new ArrayList <>();
					
					String detailedMovieUrl = detailedUrlBase + mDetailedMovieId + apiKeyFiller + apiKey;

					syncHttpClient.get(detailedMovieUrl , new AsyncHttpResponseHandler(){

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
								boolean detailedItem = true;
								MovieItem movieItem = new MovieItem(responseObject , detailedItem);
								movieItemses.add(movieItem);
							} catch(Exception e){
								e.printStackTrace();
							}
						}
						
						@Override
						public void onFailure(int statusCode , Header[] headers , byte[] responseBody , Throwable error){
						
						}
					});
					
					return movieItemses;
				}
				
				@Override
				protected void onPostExecute(ArrayList <MovieItem> movieItems){
					// Set value dari Observer yang berisi ArrayList yang merupakan
					// hasil dari doInBackground method
					setValue(movieItems);
				}
			}.execute(); // Execute AsyncTask
		}
		
		
	}
}
