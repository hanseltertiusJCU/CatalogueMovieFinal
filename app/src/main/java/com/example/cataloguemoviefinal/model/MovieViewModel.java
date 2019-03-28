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

/**
 * Kelas ini berguna untuk membuat:
 * - ViewModel object yang berisi {@link LiveData<ArrayList> yang berisi {@link MovieItem}}
 * - LiveData object yang berguna untuk:
 * *) Membuat data berupa {@link ArrayList<MovieItem>} dari URL Discover Movie
 * *) Getter method untuk dipasang ke {@link java.util.Observer<ArrayList> yang membawa
 * {@link MovieItem}}
 */
public class MovieViewModel extends AndroidViewModel{
	
	// Create object yang mengextend LiveData<ArrayList<MovieItem>>
	private MovieLiveData movieLiveData;
	
	// akses informasi yang berisi Discover Movie URL dari BuildConfig untuk menjaga credential
	private String apiKey = BuildConfig.API_KEY;
	private String discoverMovieUrlBase = BuildConfig.BASE_DISCOVER_MOVIE_URL;
	private String languageUs = BuildConfig.LANGUAGE_US;

	/**
	 * Constuctor untuk membuat {@link android.arch.lifecycle.ViewModel} yang berguna untuk
	 * membuat {@link LiveData} object dengan memanggil constructor, constructor tsb di triggered
	 * oleh {@link android.arch.lifecycle.ViewModelProviders} of yang berguna untuk memasang ViewModel di
	 * {@link com.example.cataloguemoviefinal.fragment.MovieFragment} lalu memanggil get() method
	 * @param application
	 */
	public MovieViewModel(@NonNull Application application){
		super(application);
		movieLiveData = new MovieLiveData(application);
	}

	/**
	 * Getter method untuk dapat mengakses LiveData object yang berguna untuk observe data dengan
	 * memasang {@link android.arch.lifecycle.Observer}
	 * @return LiveData object / data2 yang ada
	 */
	public LiveData <ArrayList <MovieItem>> getMovies(){
		return movieLiveData;
	}

	/**
	 * Kelas ini berguna untuk:
	 * - Menampung data di ViewModel
	 * - Melakukan operation {@link AsyncTask} sebagai pengganti dari
	 * {@link android.support.v4.content.Loader}
	 */
	public class MovieLiveData extends LiveData <ArrayList <MovieItem>>{
		private final Context context;

		/**
		 * Constructor untuk membuat LiveData object dan memanggil method untuk melakukan
		 * AsyncTask operation
		 * @param context
		 */
		MovieLiveData(Context context){
			this.context = context;
			loadMovieLiveData();
		}

		/**
		 * Method tsb berguna untuk menjalankan {@link AsyncTask} operation sbg pengganti dari
		 * loadInBackground() di AsyncTaskLoader, kita ini ingin mendapatkan data bedasarkan
		 * URL Discover Movie
		 */
		@SuppressLint("StaticFieldLeak")
		private void loadMovieLiveData(){
			
			new AsyncTask <Void, Void, ArrayList <MovieItem>>(){
				@Override
				protected ArrayList <MovieItem> doInBackground(Void... voids){

					// Menginisiasikan SyncHttpClient object yang berguna untuk menjalankan proses
					// scr Synchronous krn Loader itu sudah berjalan pada
					// background thread
					SyncHttpClient syncHttpClient = new SyncHttpClient();
					
					final ArrayList <MovieItem> movieItems = new ArrayList <>();
					
					String movieUrl = discoverMovieUrlBase + apiKey + languageUs;

					syncHttpClient.get(movieUrl , new AsyncHttpResponseHandler(){

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
									JSONObject movie = results.getJSONObject(i);
									MovieItem movieItem = new MovieItem(movie , false);
									// Cek jika posterPath itu tidak "null" karena null dr JSON itu berupa
									// String, sehingga perlu menggunakan "" di dalam null
									if(! movieItem.getMoviePosterPath().equals("null")){
										movieItems.add(movieItem);
									}
								}
							} catch(Exception e){
								e.printStackTrace();
							}
						}
						
						@Override
						public void onFailure(int statusCode , Header[] headers , byte[] responseBody , Throwable error){
							// Do nothing jika responsenya itu tidak berhasil
						}
					});
					
					return movieItems;
				}

				/**
				 * Method ini berguna untuk set value bawaan dari doInBackground() method
				 * dan juga pasang value ke Observer jika ada
				 * @param movieItems ArrayList hasil dari doInBackground method
				 */
				@Override
				protected void onPostExecute(ArrayList <MovieItem> movieItems){
					setValue(movieItems);
				}
			}.execute(); // Execute AsyncTask
		}
	}
}


