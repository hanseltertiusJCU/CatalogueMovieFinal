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

/**
 * Kelas ini berguna untuk:
 * - Membuat ViewModel object yang berisi {@link LiveData<ArrayList> yang berisi {@link MovieItem}}
 * - Membuat LiveData object yang berguna untuk:
 * *) Membuat data berupa {@link ArrayList<MovieItem>} dari URL Detailed Movie dengan movieId
 * *) Getter method untuk dipasang ke {@link java.util.Observer<ArrayList> yang membawa
 * {@link MovieItem}}
 */
public class DetailedMovieViewModel extends AndroidViewModel {

    // akses informasi yang berisi Detailed Movie URL dari BuildConfig untuk menjaga credential
    private String apiKey = BuildConfig.API_KEY;
    private String detailedUrlBase = BuildConfig.BASE_MOVIE_DETAILED_URL;
    private String apiKeyFiller = BuildConfig.DETAILED_ITEM_API_KEY_FILLER;

    // Create object yang mengextend LiveData<ArrayList<MovieItem>>
    private DetailedMovieLiveData detailedMovieLiveData;

    // Movie id untuk display 1 detailed movie item
    private int mDetailedMovieId;

    /**
     * Constuctor untuk membuat {@link android.arch.lifecycle.ViewModel} yang berguna untuk
     * membuat {@link LiveData} object dengan memanggil constructor, constructor tsb di triggered
     * oleh {@link android.arch.lifecycle.ViewModelProviders} of yang berguna untuk memasang ViewModel di
     * {@link com.example.cataloguemoviefinal.DetailActivity} lalu memanggil get() method
     * dan ini berlaku jika mode intentnya itu berada di movie
     *
     * @param application
     * @param detailedMovieId movie id item
     */
    public DetailedMovieViewModel(@NonNull Application application, int detailedMovieId) {
        super(application);
        this.mDetailedMovieId = detailedMovieId;
        // Buat LiveData agar dapat di return ke getDetailedMovie method
        detailedMovieLiveData = new DetailedMovieLiveData(application, detailedMovieId);
    }

    /**
     * Getter method untuk dapat mengakses LiveData object yang berguna untuk observe data dengan
     * memasang {@link android.arch.lifecycle.Observer}
     *
     * @return LiveData object / data2 yang ada
     */
    public LiveData<ArrayList<MovieItem>> getDetailedMovie() {
        return detailedMovieLiveData;
    }

    /**
     * Kelas ini berguna untuk:
     * - Menampung data di ViewModel
     * - Melakukan operation {@link AsyncTask} sebagai pengganti dari
     * {@link android.support.v4.content.Loader}
     */
    private class DetailedMovieLiveData extends LiveData<ArrayList<MovieItem>> {
        private final Context context;
        private final int id;

        /**
         * Constructor untuk membuat LiveData object dan memanggil method untuk melakukan
         * AsyncTask operation
         *
         * @param context
         * @param id      movie id untuk di display
         */
        DetailedMovieLiveData(Context context, int id) {
            this.context = context;
            this.id = id;
            loadDetailedMovieLiveData();
        }

        /**
         * Method tsb berguna untuk menjalankan {@link AsyncTask} operation sbg pengganti dari
         * loadInBackground() di AsyncTaskLoader, kita ini ingin mendapatkan data bedasarkan
         * URL Detailed Movie dengan movie item id
         */
        @SuppressLint("StaticFieldLeak")
        private void loadDetailedMovieLiveData() {

            new AsyncTask<Void, Void, ArrayList<MovieItem>>() {

                @Override
                protected ArrayList<MovieItem> doInBackground(Void... voids) {

                    // Menginisiasikan SyncHttpClient object yang berguna untuk menjalankan proses
                    // scr Synchronous krn Loader itu sudah berjalan pada
                    // background thread
                    SyncHttpClient syncHttpClient = new SyncHttpClient();

                    final ArrayList<MovieItem> movieItemses = new ArrayList<>();

                    String detailedMovieUrl = detailedUrlBase + mDetailedMovieId + apiKeyFiller + apiKey;

                    syncHttpClient.get(detailedMovieUrl, new AsyncHttpResponseHandler() {

                        @Override
                        public void onStart() {
                            super.onStart();
                            // make the handler synchronous
                            setUseSynchronousMode(true);
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            try {
                                String result = new String(responseBody);
                                JSONObject responseObject = new JSONObject(result);
                                MovieItem movieItem = new MovieItem(responseObject, true);
                                movieItemses.add(movieItem);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        }
                    });

                    return movieItemses;
                }

                /**
                 * Method ini berguna untuk set value bawaan dari doInBackground() method
                 * dan juga pasang value ke Observer jika ada
                 * @param movieItems ArrayList hasil dari doInBackground method
                 */
                @Override
                protected void onPostExecute(ArrayList<MovieItem> movieItems) {
                    setValue(movieItems);
                }
            }.execute(); // Execute AsyncTask
        }


    }
}
