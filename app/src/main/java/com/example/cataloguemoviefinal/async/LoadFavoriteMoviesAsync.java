package com.example.cataloguemoviefinal.async;

import android.content.ContentProvider;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.example.cataloguemoviefinal.LoadFavoriteMoviesCallback;

import java.lang.ref.WeakReference;

import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_FAVORITE_CONTENT_URI;

/**
 * Class tsb berguna untuk membaca data dari {@link ContentProvider} dengan memanggil
 * {@link android.content.ContentResolver}, lalu menampilkan result dengan memanggil
 * postExecute method di {@link LoadFavoriteMoviesCallback} interface yang diggunakan di
 * {@link com.example.cataloguemoviefinal.MainActivity} untuk mendapatkan data beserta
 * {@link com.example.cataloguemoviefinal.fragment.FavoriteMovieFragment} untuk menggunakan data
 * dari {@link com.example.cataloguemoviefinal.MainActivity}
 */
public class LoadFavoriteMoviesAsync extends AsyncTask<Void, Void, Cursor> {
    // WeakReference digunakan karena AsyncTask akan dibuat dan dieksekusi scr bersamaan di method onCreate().
    // Selain itu, ketika Activity destroyed, Activity tsb dapat dikumpulkan oleh GarbageCollector, sehingga
    // dapat mencegah memory leak
    private final WeakReference<Context> weakContext;
    private final WeakReference<LoadFavoriteMoviesCallback> weakCallback;

    public LoadFavoriteMoviesAsync(Context context, LoadFavoriteMoviesCallback callback) {
        weakContext = new WeakReference<>(context);
        weakCallback = new WeakReference<>(callback);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        weakCallback.get().favoriteMoviePreExecute(); // memanggil method preExecute di interface untuk mempersiapkan data
    }

    @Override
    protected Cursor doInBackground(Void... voids) {
        Context context = weakContext.get();
        return context.getContentResolver().query(MOVIE_FAVORITE_CONTENT_URI, null, null, null, null); // Mengakses content resolver agar URI dapat dioper ke ContentProvider
    }

    @Override
    protected void onPostExecute(Cursor movieItems) {
        super.onPostExecute(movieItems);
        weakCallback.get().favoriteMoviePostExecute(movieItems); // memanggil method postExecute di interface {@link LoadFavoriteMoviesCallback}
    }
}