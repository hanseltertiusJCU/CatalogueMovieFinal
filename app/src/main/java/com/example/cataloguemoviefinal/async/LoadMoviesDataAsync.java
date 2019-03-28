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

/**
 * Kelas ini berguna untuk load data dari URL Discover Movie untuk dapat digunakan oleh
 * {@link com.example.cataloguemoviefinal.alarm.ReleaseTodayReminderAlarmReceiver} yang bertugas utk
 * mengolah data bedasarkan release date yang di trigger notif ketika release date = tanggal system
 */
public class LoadMoviesDataAsync extends AsyncTask<Void, Void, ArrayList<MovieItem>> {

    // Initiate URL component dan juga
    private String apiKey = BuildConfig.API_KEY;
    private String discoverMovieUrlBase = BuildConfig.BASE_DISCOVER_MOVIE_URL;
    private String languageUs = BuildConfig.LANGUAGE_US;

    // Empty constructor
    public LoadMoviesDataAsync() {
    }

    /**
     * Method ini berguna utk melakukan JSON Operation lalu memasukkan data yang ada
     *
     * @param voids
     * @return ArrayList yang menampung data, hasil dari JSON Operation di URL discover movie
     */
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

                try {
                    String result = new String(responseBody);

                    JSONObject responseObject = new JSONObject(result);

                    JSONArray jsonArray = responseObject.getJSONArray("results");

                    // Iterate semua data yg ada dan tambahkan ke ArrayList
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject movie = jsonArray.getJSONObject(i);

                        boolean detailedItem = false;

                        MovieItem movieItem = new MovieItem(movie, detailedItem);

                        // Cek jika posterPath itu tidak "null"/data ada poster imagenya.
                        // Di JSON, null nya itu berupa String sehingga perlu membungkus ""
                        if (!movieItem.getMoviePosterPath().equals("null")) {
                            // Add item ke ArrayList
                            movieItemArrayList.add(movieItem);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace(); // Print exception message di StackTrace
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

        return movieItemArrayList; // Return ArrayList that shows MovieItem object based on URL discover
    }

    @Override
    protected void onPostExecute(ArrayList<MovieItem> movieItems) {
        super.onPostExecute(movieItems);
    }
}
