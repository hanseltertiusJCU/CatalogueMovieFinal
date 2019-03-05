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

public class SearchTvShowViewModel extends AndroidViewModel {
	
	// Gunakan beberapa informasi dari Build Config untuk melindungi credential
	private String apiKey = BuildConfig.API_KEY;
	private String searchTvShowUrlBase = BuildConfig.BASE_TV_SHOW_SEARCH_URL;
	private String tvShowSearchQuery = BuildConfig.SEARCH_QUERY;
	
	private TvShowSearchLiveData tvShowSearchLiveData;
	private String mTvShowSearchKeyword;
	
	public SearchTvShowViewModel(@NonNull Application application, String tvShowSearchKeyword){
		super(application);
		this.mTvShowSearchKeyword = tvShowSearchKeyword;
		tvShowSearchLiveData = new TvShowSearchLiveData(application, tvShowSearchKeyword);
	}
	
	public LiveData<ArrayList<TvShowItem>> getSearchTvShows(){
		return tvShowSearchLiveData;
	}
	
	public void setTvShowSearchKeyword(String tvShowSearchKeyword){
		this.mTvShowSearchKeyword = tvShowSearchKeyword;
	}
	
	public void searchTvShowRecall(){
		// Panggil live data dengan search keyword yang baru
		tvShowSearchLiveData = new TvShowSearchLiveData(getApplication(), mTvShowSearchKeyword);
	}
	
	private class TvShowSearchLiveData extends LiveData<ArrayList<TvShowItem>>{
		
		private final Context context;
		private String tvShowSearchKeyword;
		
		// Buat constructor untuk mengakomodasi parameter yang ada dari {@link SearchTvShowViewModel}
		
		public TvShowSearchLiveData(Context context, String tvShowSearchKeyword){
			this.context = context;
			this.tvShowSearchKeyword = tvShowSearchKeyword;
			loadSearchTvShowLiveData();
		}
		
		@SuppressLint("StaticFieldLeak")
		private void loadSearchTvShowLiveData(){
			
			new AsyncTask<Void, Void, ArrayList<TvShowItem>>(){
				
				@Override
				protected ArrayList<TvShowItem> doInBackground(Void... voids) {
					
					// Menginisiasikan SyncHttpClientObject krn Loader itu sudah berjalan pada background thread
					SyncHttpClient syncHttpClient = new SyncHttpClient();
					
					final ArrayList<TvShowItem> tvShowItems = new ArrayList<>();
					
					String tvShowSearchUrl = searchTvShowUrlBase + apiKey + tvShowSearchQuery + mTvShowSearchKeyword;
					
					syncHttpClient.get(tvShowSearchUrl, new AsyncHttpResponseHandler() {
						
						@Override
						public void onStart() {
							super.onStart();
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
									boolean detailedItem = false;
									TvShowItem tvShowItem = new TvShowItem(tvShow, detailedItem);
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
				
				@Override
				protected void onPostExecute(ArrayList<TvShowItem> tvShowItems) {
					setValue(tvShowItems);
				}
			}.execute();
		}
	}
}
