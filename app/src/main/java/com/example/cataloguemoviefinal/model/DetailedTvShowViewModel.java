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

/**
 * Kelas ini berguna untuk:
 * - Membuat ViewModel object yang berisi {@link LiveData<ArrayList> yang berisi {@link TvShowItem}}
 * - Membuat LiveData object yang berguna untuk:
 * *) Membuat data berupa {@link ArrayList<TvShowItem>} dari URL Detailed Tv Show dengan tvShowId
 * *) Getter method untuk dipasang ke {@link java.util.Observer<ArrayList> yang membawa
 * {@link TvShowItem}}
 */
public class DetailedTvShowViewModel extends AndroidViewModel{

	// akses informasi yang berisi Detailed TV Show URL dari BuildConfig untuk menjaga credential
	private String apiKey = BuildConfig.API_KEY;
	private String detailedTvShowUrlBase = BuildConfig.BASE_TV_SHOW_DETAILED_URL;
	private String apiKeyFiller = BuildConfig.DETAILED_ITEM_API_KEY_FILLER;
	
	private DetailedTvShowLiveData detailedTvShowLiveData;
	
	private int mDetailedTvShowId;

	/**
	 * Constuctor untuk membuat {@link android.arch.lifecycle.ViewModel} yang berguna untuk
	 * membuat {@link LiveData} object dengan memanggil constructor, constructor tsb di triggered
	 * oleh {@link android.arch.lifecycle.ViewModelProviders} of yang berguna untuk memasang ViewModel di
	 * {@link com.example.cataloguemoviefinal.DetailActivity} lalu memanggil get() method
	 * dan ini berlaku jika mode intentnya itu berada di Tv Show
	 * @param application
	 * @param detailedTvShowId tv show id item
	 */
	public DetailedTvShowViewModel(Application application , int detailedTvShowId){
		super(application);
		this.mDetailedTvShowId = detailedTvShowId;
		// Buat LiveData agar dapat di return ke getDetailedTvShow method
		detailedTvShowLiveData = new DetailedTvShowLiveData(application , detailedTvShowId);
	}

	/**
	 * Getter method untuk dapat mengakses LiveData object hasil dari {@link DetailedTvShowLiveData}
	 * yang berguna untuk observe data dengan memasang {@link android.arch.lifecycle.Observer}
	 * @return LiveData object / data2 yang ada
	 */
	public LiveData <ArrayList <TvShowItem>> getDetailedTvShow(){
		return detailedTvShowLiveData;
	}

	/**
	 * Kelas ini berguna untuk:
	 * - Menampung data di ViewModel
	 * - Melakukan operation {@link AsyncTask} sebagai pengganti dari
	 * {@link android.support.v4.content.Loader}
	 */
	private class DetailedTvShowLiveData extends LiveData <ArrayList <TvShowItem>>{
		private final Context context;
		private final int id;

		/**
		 * Constructor untuk membuat LiveData object dan memanggil method untuk melakukan
		 * AsyncTask operation
		 * @param context Application context
		 * @param id tv show id untuk di display
		 */
		DetailedTvShowLiveData(Context context , int id){
			this.context = context;
			this.id = id;
			loadDetailedTvShowLiveData();
		}

		/**
		 * Method tsb berguna untuk menjalankan {@link AsyncTask} operation sbg pengganti dari
		 * loadInBackground() di AsyncTaskLoader, kita ini ingin mendapatkan data bedasarkan
		 * URL Detailed TV Show dengan TV Show item id
		 */
		@SuppressLint("StaticFieldLeak")
		private void loadDetailedTvShowLiveData(){
			
			new AsyncTask <Void, Void, ArrayList <TvShowItem>>(){
				
				@Override
				protected ArrayList <TvShowItem> doInBackground(Void... voids){

					// Menginisiasikan SyncHttpClient object yang berguna untuk menjalankan proses
					// scr Synchronous krn Loader itu sudah berjalan pada
					// background thread
					SyncHttpClient syncHttpClient = new SyncHttpClient();
					
					final ArrayList <TvShowItem> tvShowItems = new ArrayList <>();
					
					String detailedTvShowUrl = detailedTvShowUrlBase + mDetailedTvShowId + apiKeyFiller + apiKey;

					syncHttpClient.get(detailedTvShowUrl , new AsyncHttpResponseHandler(){

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
								TvShowItem tvShowItem = new TvShowItem(responseObject , true);
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

				/**
				 * Method ini berguna untuk set value bawaan dari doInBackground() method
				 * dan juga pasang value ke Observer jika ada
				 * @param tvShowItems ArrayList hasil dari doInBackground method
				 */
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
