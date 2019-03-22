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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchMovieViewModel extends AndroidViewModel {
	
	// Gunakan beberapa informasi dari Build Config untuk melindungi credential
	private String apiKey = BuildConfig.API_KEY;
	private String searchMovieUrlBase = BuildConfig.BASE_MOVIE_SEARCH_URL;
	private String movieSearchQuery = BuildConfig.SEARCH_QUERY;
	
	private MovieSearchLiveData movieSearchLiveData;
	private String mMovieSearchKeyword;
	
	public SearchMovieViewModel(@NonNull Application application, String movieSearchKeyword){
		super(application);
		this.mMovieSearchKeyword = movieSearchKeyword;
		movieSearchLiveData = new MovieSearchLiveData(application, movieSearchKeyword);
	}
	
	public LiveData<ArrayList<MovieItem>> getSearchMovies(){
		return movieSearchLiveData;
	}
	
	public void setMovieSearchKeyword(String movieSearchKeyword){
		this.mMovieSearchKeyword = movieSearchKeyword;
	}
	
	public void searchMovieRecall(){
		// Panggil live data dengan search keyword yang baru
		movieSearchLiveData = new MovieSearchLiveData(getApplication(), mMovieSearchKeyword);
	}
	
	private class MovieSearchLiveData extends LiveData<ArrayList<MovieItem>>{
		
		private final Context context;
		private String movieSearchKeyword;
		
		// Buat constructor untuk mengakomodasi parameter yang ada dari {@link SearchMovieViewModel}
		
		public MovieSearchLiveData(Context context, String movieSearchKeyword){
			this.context = context;
			this.movieSearchKeyword = movieSearchKeyword;
			loadSearchMovieLiveData();
		}
		
		@SuppressLint("StaticFieldLeak")
		private void loadSearchMovieLiveData() {
			
			new AsyncTask<Void, Void, ArrayList<MovieItem>>(){
				
				@Override
				protected ArrayList<MovieItem> doInBackground(Void... voids) {
					
					// Menginisiasikan SyncHttpClientObject krn Loader itu sudah berjalan pada background thread
					SyncHttpClient syncHttpClient = new SyncHttpClient();
					
					final ArrayList<MovieItem> movieItems = new ArrayList<>();
					
					String movieSearchUrl = searchMovieUrlBase + apiKey + movieSearchQuery + mMovieSearchKeyword;

					syncHttpClient.get(movieSearchUrl, new AsyncHttpResponseHandler() {

						@Override
						public void onStart() {
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
								for (int i = 0; i < results.length(); i++){
									JSONObject movie = results.getJSONObject(i);
									boolean detailedItem = false;
									MovieItem movieItem = new MovieItem(movie, detailedItem);
									// Cek jika posterPath itu tidak "null" karena null dr JSON itu berupa
									// String, sehingga perlu menggunakan "" di dalam null
									if (!movieItem.getMoviePosterPath().equals("null")){
										movieItems.add(movieItem);
									}
									
								}
							} catch(Exception e){
								e.printStackTrace();
							}
						}
						
						@Override
						public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
							// Do nothing jika responsenya itu tidak berhasil, todo: maybe find a way to retrieve no data or smth (idk)
						}
					});
					
					
					return movieItems;
				}
				
				@Override
				protected void onPostExecute(ArrayList<MovieItem> movieItems) {
					// Set value dari Observer yang berisi ArrayList yang merupakan
					// hasil dari doInBackground method
					setValue(movieItems);
				}
			}.execute(); // Execute AsyncTask
			
		}
		
	}
}
