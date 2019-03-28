package com.example.cataloguemoviefinal.entity;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.cataloguemoviefinal.database.FavoriteDatabaseContract;

import org.json.JSONArray;
import org.json.JSONObject;

import static android.provider.BaseColumns._ID;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.getColumnInt;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.getColumnString;

/**
 * Class ini berguna untuk membuat TvShowItem object
 */
public class TvShowItem implements Parcelable {

    // Parcelable creator object
    public static final Creator<TvShowItem> CREATOR = new Creator<TvShowItem>() {
        @Override
        public TvShowItem createFromParcel(Parcel in) {
            return new TvShowItem(in);
        }

        @Override
        public TvShowItem[] newArray(int size) {
            return new TvShowItem[size];
        }
    };
    // Nilai dari value untuk TvShowItem
    private int id;
    private String tvShowName;
    private String tvShowSeasons;
    private String tvShowEpisodes;
    private String tvShowRuntimeEpisodes;
    private String tvShowRatings;
    private String tvShowRatingsVote;
    private String tvShowOriginalLanguage;
    private String tvShowNetworks;
    private String tvShowGenres;
    private String tvShowFirstAirDate;
    private String tvShowOverview;
    private String tvShowPosterPath;
    // Nilai untuk mengetahui waktu dimana sebuah data di add menjadi favorite
    private String dateAddedFavorite;
    // Nilai untuk tahu bahwa tv show item itu termasuk dalam kategori favorit ato tidak
    private int favoriteBooleanState;

    // Constructor untuk membuat TvShowItem object melalui JSON dan
    // menentukan keadaan bedasarkan data dari detail activity atau tidak
    public TvShowItem(JSONObject object, boolean isTvShowDetailed) {
        // Cek jika app berada di section DetailActivity agar dapat mengakses URL TV Show Details
        if (isTvShowDetailed) {
            try {
                int dataId = object.getInt("id");
                String dataName = object.getString("name");
                String dataNumberOfSeasons = object.getString("number_of_seasons");
                String dataNumberOfEpisodes = object.getString("number_of_episodes");
                JSONArray dataEpisodesRuntimeArray = object.getJSONArray("episode_run_time");
                String dataRuntimeEpisodes = null;
                if (dataEpisodesRuntimeArray.length() > 0) {
                    dataRuntimeEpisodes = dataEpisodesRuntimeArray.getString(0); // retrieve value from episode_run_time JSON array
                }
                String dataVoteAverage = object.getString("vote_average");
                String dataVoteCount = object.getString("vote_count");
                // value tsb berguna untuk mentransfer ke MainActivity agar bisa mendisplay
                // ke favorite tv show item list
                String dataOriginalLanguage = object.getString("original_language");
                // Ubah language menjadi upper case
                String displayed_language = dataOriginalLanguage.toUpperCase();
                JSONArray dataNetworksArray = object.getJSONArray("networks");
                String dataNetworks = null;
                // Cek jika networksArray (TV Channel array) ada datanya atau tidak
                if (dataNetworksArray.length() > 0) {
                    for (int i = 0; i < dataNetworksArray.length(); i++) {
                        JSONObject networkObject = dataNetworksArray.getJSONObject(i);
                        String network = networkObject.getString("name");
                        if (dataNetworksArray.length() == 1) {
                            if (i == 0)
                                dataNetworks = network;
                        } else {
                            if (i == 0)
                                dataNetworks = network + ", ";
                            else if (i == dataNetworksArray.length() - 1)
                                dataNetworks += network;
                            else
                                dataNetworks += network + ", ";
                        }
                    }
                }

                JSONArray dataGenresArray = object.getJSONArray("genres");
                String dataGenres = null;

                // Cek jika genresArray ada datanya atau tidak, jika tidak set default value untuk String
                // genres (isinya adalah item yg ada di array)
                if (dataGenresArray.length() > 0) {
                    // Iterate genre array untuk mendapatkan genre yang akan ditambahkan ke genres
                    // fyi: genres itu adalah koleksi dari genre field
                    for (int i = 0; i < dataGenresArray.length(); i++) {
                        JSONObject genreObject = dataGenresArray.getJSONObject(i);
                        String genre = genreObject.getString("name");
                        if (dataGenresArray.length() == 1) {
                            if (i == 0)
                                dataGenres = genre;
                        } else {
                            if (i == 0)
                                dataGenres = genre + ", ";
                            else if (i == dataGenresArray.length() - 1)
                                dataGenres += genre;
                            else
                                dataGenres += genre + ", ";
                        }
                    }
                }

                String dataFirstAirDate = object.getString("first_air_date");
                String dataOverview = object.getString("overview");
                // Dapatkan detailed tv show poster path untuk url {@link DetailActivity}
                String dataPosterPath = object.getString("poster_path");

                // Set values bedasarkan variable-variable yang merepresentasikan field dari sebuah JSON
                // object
                this.id = dataId;
                this.tvShowName = dataName;
                this.tvShowSeasons = dataNumberOfSeasons;
                this.tvShowEpisodes = dataNumberOfEpisodes;
                this.tvShowRuntimeEpisodes = dataRuntimeEpisodes;
                this.tvShowRatings = dataVoteAverage;
                this.tvShowRatingsVote = dataVoteCount;
                this.tvShowOriginalLanguage = displayed_language;
                this.tvShowNetworks = dataNetworks;
                this.tvShowGenres = dataGenres;
                this.tvShowFirstAirDate = dataFirstAirDate;
                this.tvShowOverview = dataOverview;
                this.tvShowPosterPath = dataPosterPath;

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else { // Jika tidak, maka kita akan mengakses URL tv show
            try {
                // Get JSON object fields
                int dataId = object.getInt("id");
                String dataName = object.getString("name");
                String dataVoteAverage = object.getString("vote_average");
                String dataFirstAirDate = object.getString("first_air_date");
                String dataOriginalLanguage = object.getString("original_language");
                // Ubah language menjadi upper case
                String displayed_language = dataOriginalLanguage.toUpperCase();
                // Dapatkan poster path untuk di extract ke url {@link TvShowAdapter}
                String dataPosterPath = object.getString("poster_path");

                this.id = dataId;
                this.tvShowName = dataName;
                this.tvShowRatings = dataVoteAverage;
                this.tvShowFirstAirDate = dataFirstAirDate;
                this.tvShowOriginalLanguage = displayed_language;
                this.tvShowPosterPath = dataPosterPath;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public TvShowItem() {

    }

    public TvShowItem(int id, String tvShowName, String tvShowRatings, String tvShowOriginalLanguage, String tvShowFirstAirDate, String tvShowPosterPath, String dateAddedFavorite, int favoriteBooleanState) {
        this.id = id;
        this.tvShowName = tvShowName;
        this.tvShowRatings = tvShowRatings;
        this.tvShowOriginalLanguage = tvShowOriginalLanguage;
        this.tvShowFirstAirDate = tvShowFirstAirDate;
        this.tvShowPosterPath = tvShowPosterPath;
        this.dateAddedFavorite = dateAddedFavorite;
        this.favoriteBooleanState = favoriteBooleanState;
    }

    // Constructor untuk menampung Cursor
    public TvShowItem(Cursor cursor) {
        this.id = getColumnInt(cursor, _ID);
        this.tvShowName = getColumnString(cursor, FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_NAME_COLUMN);
        this.tvShowRatings = getColumnString(cursor, FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_RATINGS_COLUMN);
        this.tvShowOriginalLanguage = getColumnString(cursor, FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_ORIGINAL_LANGUAGE_COLUMN);
        this.tvShowFirstAirDate = getColumnString(cursor, FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_FIRST_AIR_DATE_COLUMN);
        this.tvShowPosterPath = getColumnString(cursor, FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_FILE_PATH_COLUMN);
        this.dateAddedFavorite = getColumnString(cursor, FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_DATE_ADDED_COLUMN);
        this.favoriteBooleanState = getColumnInt(cursor, FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_FAVORITE_COLUMN);
    }

    public TvShowItem(Parcel in) {
        id = in.readInt();
        tvShowName = in.readString();
        tvShowSeasons = in.readString();
        tvShowEpisodes = in.readString();
        tvShowRuntimeEpisodes = in.readString();
        tvShowRatings = in.readString();
        tvShowRatingsVote = in.readString();
        tvShowOriginalLanguage = in.readString();
        tvShowNetworks = in.readString();
        tvShowGenres = in.readString();
        tvShowFirstAirDate = in.readString();
        tvShowOverview = in.readString();
        tvShowPosterPath = in.readString();
        dateAddedFavorite = in.readString();
        favoriteBooleanState = in.readInt();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTvShowName() {
        // Set default value for DetailedTvShowName if DetailedTvShowName is null or ""
        return tvShowName;
    }

    public String getTvShowSeasons() {
        return tvShowSeasons;
    }

    public String getTvShowEpisodes() {
        return tvShowEpisodes;
    }

    public String getTvShowRuntimeEpisodes() {
        return tvShowRuntimeEpisodes;
    }

    public String getTvShowRatings() {
        return tvShowRatings;
    }

    public String getTvShowRatingsVote() {
        return tvShowRatingsVote;
    }

    public String getTvShowOriginalLanguage() {
        return tvShowOriginalLanguage;
    }

    public String getTvShowNetworks() {
        return tvShowNetworks;
    }

    public String getTvShowGenres() {
        return tvShowGenres;
    }

    public String getTvShowFirstAirDate() {
        return tvShowFirstAirDate;
    }

    public String getTvShowOverview() {
        return tvShowOverview;
    }

    public String getTvShowPosterPath() {
        return tvShowPosterPath;
    }

    public String getDateAddedFavorite() {
        return dateAddedFavorite;
    }

    public void setDateAddedFavorite(String dateAddedFavorite) {
        this.dateAddedFavorite = dateAddedFavorite;
    }

    public int getFavoriteBooleanState() {
        return favoriteBooleanState;
    }

    public void setFavoriteBooleanState(int favoriteBooleanState) {
        this.favoriteBooleanState = favoriteBooleanState;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(id);
        dest.writeString(tvShowName);
        dest.writeString(tvShowSeasons);
        dest.writeString(tvShowEpisodes);
        dest.writeString(tvShowRuntimeEpisodes);
        dest.writeString(tvShowRatings);
        dest.writeString(tvShowRatingsVote);
        dest.writeString(tvShowOriginalLanguage);
        dest.writeString(tvShowNetworks);
        dest.writeString(tvShowGenres);
        dest.writeString(tvShowFirstAirDate);
        dest.writeString(tvShowOverview);
        dest.writeString(tvShowPosterPath);
        dest.writeString(dateAddedFavorite);
        dest.writeInt(favoriteBooleanState);
    }
}
