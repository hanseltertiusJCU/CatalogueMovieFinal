package com.example.cataloguemoviefinal;


import android.database.Cursor;

/**
 * Interface ini berguna untuk Load Favorite TV Show dan dipanggil di asynctask pre execute dan post
 */
public interface LoadFavoriteTvShowCallback {
    // Preexecute untuk prepare array list data
    void favoriteTvShowPreExecute();

    // Postexecute untuk memberi hasil kepada array list
    void favoriteTvShowPostExecute(Cursor tvShowItems);
}
