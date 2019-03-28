package com.example.cataloguemoviefinal;

import android.database.Cursor;

/**
 * Interface ini berguna untuk Load Favorite Movie dan dipanggil di asynctask pre execute dan post
 */
public interface LoadFavoriteMoviesCallback {
    // Preexecute untuk prepare array list data
    void favoriteMoviePreExecute();

    // Postexecute untuk memberi hasil kepada array list
    void favoriteMoviePostExecute(Cursor movieItems);
}
