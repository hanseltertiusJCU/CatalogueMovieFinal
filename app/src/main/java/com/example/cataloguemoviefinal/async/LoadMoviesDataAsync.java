package com.example.cataloguemoviefinal.async;

import android.os.AsyncTask;

import com.example.cataloguemoviefinal.BuildConfig;
import com.example.cataloguemoviefinal.entity.MovieItem;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

// Kelas ini berguna untuk load data dari URL
public class LoadMoviesDataAsync extends AsyncTask<Void, Void, ArrayList<MovieItem>> {
	
	// Initiate URL component dan juga
	private String apiKey = BuildConfig.API_KEY;
	private String discoverMovieUrlBase = BuildConfig.BASE_DISCOVER_MOVIE_URL;
	private String languageUs = BuildConfig.LANGUAGE_US;
	
	// Empty constructor
	public LoadMoviesDataAsync() {
	}
	
	@Override
	protected ArrayList<MovieItem> doInBackground(Void... voids) {
		
		SyncHttpClient syncHttpClient = new SyncHttpClient();
		// Set time out in milliseconds (set to 1 minute mark), yang berguna untuk
		// mengaccomodate device ketika kualitas koneksi sedang buruk
		syncHttpClient.setTimeout(60000);
		syncHttpClient.setConnectTimeout(60000);
		syncHttpClient.setResponseTimeout(60000);
		
		final ArrayList<MovieItem> movieItemArrayList = new ArrayList<>();
		
		String discoverMovieUrl = discoverMovieUrlBase + apiKey + languageUs;
		
		syncHttpClient.get(discoverMovieUrl, new AsyncHttpResponseHandler() {
			
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
					
					JSONArray jsonArray = responseObject.getJSONArray("results");
					
					// Iterate semua data yg ada dan tambahkan ke ArrayList
					for(int i = 0; i < jsonArray.length(); i++){
						
						JSONObject movie = jsonArray.getJSONObject(i);
						
						boolean detailedItem = false;
						
						MovieItem movieItem = new MovieItem(movie, detailedItem);
						
						// Cek jika posterPath itu tidak "null" karena null dr JSON itu berupa
						// String, sehingga perlu menggunakan "" di dalam null
						if(!movieItem.getMoviePosterPath().equals("null")){
							
							movieItemArrayList.add(movieItem);
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
		
		return movieItemArrayList;
	}
	
	@Override
	protected void onPostExecute(ArrayList<MovieItem> movieItems) {
		super.onPostExecute(movieItems);
	}
}
