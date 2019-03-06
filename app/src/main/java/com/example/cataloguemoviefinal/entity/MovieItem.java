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

public class MovieItem implements Parcelable {
	
	public static final Creator<MovieItem> CREATOR = new Creator<MovieItem>() {
		@Override
		public MovieItem createFromParcel(Parcel in) {
			return new MovieItem(in);
		}
		
		@Override
		public MovieItem[] newArray(int size) {
			return new MovieItem[size];
		}
	};
	// Nilai dari value untuk MovieItem
	private int id;
	private String movieTitle;
	private String movieTagline;
	private String movieStatus;
	private String movieRatings;
	private String movieRatingsVote;
	private String movieOriginalLanguage;
	private String movieLanguages;
	private String movieGenres;
	private String movieReleaseDate;
	private String movieOverview;
	private String moviePosterPath;
	// Nilai untuk mengetahui waktu dimana sebuah data di add menjadi favorite
	private String dateAddedFavorite;
	// Nilai untuk tahu bahwa movie item itu termasuk dalam kategori favorit ato tidak
	private int favoriteBooleanState;
	
	public MovieItem(JSONObject object, boolean isMovieDetailed) {
		// Cek jika app berada di section DetailActivity agar dapat mengakses URL Movie Details
		if(isMovieDetailed) {
			try {
				int dataId = object.getInt("id");
				String dataTitle = object.getString("title");
				String dataTagline = object.getString("tagline");
				String dataStatus = object.getString("status");
				String dataVoteAverage = object.getString("vote_average");
				String dataVoteCount = object.getString("vote_count");
				// value tsb berguna untuk mentransfer ke MainActivity agar bisa mendisplay
				// ke favorite movie item list
				String dataOriginalLanguage = object.getString("original_language");
				// Ubah language menjadi upper case
				String displayed_language = dataOriginalLanguage.toUpperCase();
				JSONArray dataLanguageArray = object.getJSONArray("spoken_languages");
				String dataLanguages = null;
				// Cek jika languageArray ada datanya atau tidak
				if(dataLanguageArray.length() > 0) {
					// Iterate language array untuk mendapatkan language yang akan ditambahkan ke languages
					// fyi: languages itu adalah koleksi dari language field
					for(int i = 0 ; i < dataLanguageArray.length() ; i++) {
						JSONObject languageObject = dataLanguageArray.getJSONObject(i);
						String language = languageObject.getString("name");
						if(dataLanguageArray.length() == 1) {
							if(i == 0)
								dataLanguages = language;
						} else {
							if(i == 0)
								dataLanguages = language + ", ";
							else if(i == dataLanguageArray.length() - 1)
								dataLanguages += language;
							else
								dataLanguages += language + ", ";
						}
					}
				} else {
					dataLanguages = "Language Unknown";
				}
				
				JSONArray dataGenreArray = object.getJSONArray("genres");
				String dataGenres = null;
				
				// Cek jika genreArray ada datanya atau tidak, jika tidak set default value untuk String
				// genres (isinya adalah item yg ada di array)
				if(dataGenreArray.length() > 0) {
					// Iterate genre array untuk mendapatkan genre yang akan ditambahkan ke genres
					// fyi: genres itu adalah koleksi dari genre field
					for(int i = 0 ; i < dataGenreArray.length() ; i++) {
						JSONObject genreObject = dataGenreArray.getJSONObject(i);
						String genre = genreObject.getString("name");
						if(dataGenreArray.length() == 1) {
							dataGenres = genre;
						} else {
							if(i == 0)
								dataGenres = genre + ", ";
							else if(i == dataGenreArray.length() - 1)
								dataGenres += genre;
							else
								dataGenres += genre + ", ";
						}
					}
				} else {
					dataGenres = "Genre Unknown";
				}
				
				String dataReleaseDate = object.getString("release_date");
				String dataOverview = object.getString("overview");
				// Dapatkan detailed movie poster path untuk url {@link DetailActivity}
				String dataPosterPath = object.getString("poster_path");
				
				// Set values bedasarkan variable-variable yang merepresentasikan field dari sebuah JSON
				// object
				this.id = dataId;
				this.movieTitle = dataTitle;
				this.movieTagline = dataTagline;
				this.movieStatus = dataStatus;
				this.movieRatings = dataVoteAverage;
				this.movieRatingsVote = dataVoteCount;
				this.movieOriginalLanguage = displayed_language;
				this.movieLanguages = dataLanguages;
				this.movieGenres = dataGenres;
				this.movieReleaseDate = dataReleaseDate;
				this.movieOverview = dataOverview;
				this.moviePosterPath = dataPosterPath;
				
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else { // Jika tidak, maka kita akan mengakses URL movie NowPlaying/Upcoming
			try {
				// Get JSON object fields
				int dataId = object.getInt("id");
				String dataTitle = object.getString("title");
				String dataVoteAverage = object.getString("vote_average");
				String dataReleaseDate = object.getString("release_date");
				String dataOriginalLanguage = object.getString("original_language");
				// Ubah language menjadi upper case
				String displayed_language = dataOriginalLanguage.toUpperCase();
				// Dapatkan poster path untuk di extract ke url {@link MovieAdapter}
				String dataPosterPath = object.getString("poster_path");
				
				this.id = dataId;
				this.movieTitle = dataTitle;
				this.movieRatings = dataVoteAverage;
				this.movieReleaseDate = dataReleaseDate;
				this.movieOriginalLanguage = displayed_language;
				this.moviePosterPath = dataPosterPath;
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public MovieItem() {
	
	}
	
	// Constructor untuk menampung value columns yang ada
	public MovieItem(int id, String movieTitle, String movieRatings, String movieOriginalLanguage, String movieReleaseDate, String moviePosterPath, String dateAddedFavorite, int favoriteBooleanState) {
		this.id = id;
		this.movieTitle = movieTitle;
		this.movieRatings = movieRatings;
		this.movieOriginalLanguage = movieOriginalLanguage;
		this.movieReleaseDate = movieReleaseDate;
		this.moviePosterPath = moviePosterPath;
		this.dateAddedFavorite = dateAddedFavorite;
		this.favoriteBooleanState = favoriteBooleanState;
	}
	
	// Constructor untuk menampung Cursor
	public MovieItem(Cursor cursor){
		this.id = getColumnInt(cursor, _ID);
		this.movieTitle = getColumnString(cursor, FavoriteDatabaseContract.FavoriteMovieItemColumns.TITLE_COLUMN);
		this.movieRatings = getColumnString(cursor, FavoriteDatabaseContract.FavoriteMovieItemColumns.RATINGS_COLUMN);
		this.movieOriginalLanguage = getColumnString(cursor, FavoriteDatabaseContract.FavoriteMovieItemColumns.ORIGINAL_LANGUAGE_COLUMN);
		this.movieReleaseDate = getColumnString(cursor, FavoriteDatabaseContract.FavoriteMovieItemColumns.RELEASE_DATE_COLUMN);
		this.moviePosterPath = getColumnString(cursor, FavoriteDatabaseContract.FavoriteMovieItemColumns.FILE_PATH_COLUMN);
		this.dateAddedFavorite = getColumnString(cursor, FavoriteDatabaseContract.FavoriteMovieItemColumns.DATE_ADDED_COLUMN);
		this.favoriteBooleanState = getColumnInt(cursor, FavoriteDatabaseContract.FavoriteMovieItemColumns.FAVORITE_COLUMN);
	}
	
	protected MovieItem(Parcel in) {
		id = in.readInt();
		movieTitle = in.readString();
		movieTagline = in.readString();
		movieStatus = in.readString();
		movieRatings = in.readString();
		movieRatingsVote = in.readString();
		movieOriginalLanguage = in.readString();
		movieLanguages = in.readString();
		movieGenres = in.readString();
		movieReleaseDate = in.readString();
		movieOverview = in.readString();
		moviePosterPath = in.readString();
		dateAddedFavorite = in.readString();
		favoriteBooleanState = in.readInt();
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getMovieTitle() {
		// Set default value for DetailedMovieTitle if DetailedMovieTitle is null or ""
		if(movieTitle != null && ! movieTitle.isEmpty()) {
			return movieTitle;
		} else {
			return "Title Unknown";
		}
	}
	
	public void setMovieTitle(String movieTitle) {
		this.movieTitle = movieTitle;
	}
	
	public String getMovieTagline() {
		// Set default value for DetailedMovieTagline if DetailedMovieTagline is null or ""
		if(movieTagline != null && ! movieTagline.isEmpty()) {
			return movieTagline;
		} else {
			return "Tagline Unknown";
		}
		
	}
	
	public String getMovieStatus() {
		// Set default value for DetailedMovieStatus if DetailedMovieStatus is null or ""
		if(movieStatus != null && ! movieStatus.isEmpty()) {
			return movieStatus;
		} else {
			return "Status Unknown";
		}
		
	}
	
	public String getMovieRatings() {
		return movieRatings;
	}
	
	public void setMovieRatings(String movieRatings) {
		this.movieRatings = movieRatings;
	}
	
	public String getMovieRatingsVote() {
		return movieRatingsVote;
	}
	
	public String getMovieOriginalLanguage() {
		if(movieOriginalLanguage != null) {
			return movieOriginalLanguage;
		} else {
			return "Language Unknown";
		}
	}
	
	public void setMovieOriginalLanguage(String movieOriginalLanguage) {
		this.movieOriginalLanguage = movieOriginalLanguage;
	}
	
	public String getMovieLanguages() {
		if(movieLanguages != null) {
			return movieLanguages;
		} else {
			return "Languages Unknown";
		}
	}
	
	public String getMovieGenres() {
		if(movieGenres != null && ! movieGenres.isEmpty()) {
			return movieGenres;
		} else {
			return "Genres Unknown";
		}
		
	}
	
	public String getMovieReleaseDate() {
		// Set default value for DetailedMovieReleaseDate if DetailedMovieReleaseDate is null or ""
		if(movieReleaseDate != null && ! movieReleaseDate.isEmpty()) {
			return movieReleaseDate;
		} else {
			return "Release Date Unknown";
		}
	}
	
	public void setMovieReleaseDate(String movieReleaseDate) {
		this.movieReleaseDate = movieReleaseDate;
	}
	
	public String getMovieOverview() {
		// Set default value for DetailedMovieOverview if DetailedMovieOverview is null or ""
		if(movieOverview != null && ! movieOverview.isEmpty()) {
			return movieOverview;
		} else {
			return "Overview Unknown";
		}
	}
	
	public String getMoviePosterPath() {
		return moviePosterPath;
	}
	
	public void setMoviePosterPath(String moviePosterPath) {
		this.moviePosterPath = moviePosterPath;
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
	
	// Parcelable implementation
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeInt(id);
		dest.writeString(movieTitle);
		dest.writeString(movieTagline);
		dest.writeString(movieStatus);
		dest.writeString(movieRatings);
		dest.writeString(movieRatingsVote);
		dest.writeString(movieOriginalLanguage);
		dest.writeString(movieLanguages);
		dest.writeString(movieGenres);
		dest.writeString(movieReleaseDate);
		dest.writeString(movieOverview);
		dest.writeString(moviePosterPath);
		dest.writeString(dateAddedFavorite);
		dest.writeInt(favoriteBooleanState);
	}
}
