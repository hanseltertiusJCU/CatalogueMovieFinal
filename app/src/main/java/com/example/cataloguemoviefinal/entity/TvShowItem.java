package com.example.cataloguemoviefinal.entity;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

public class TvShowItem implements Parcelable {
	
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
	
	public TvShowItem(JSONObject object, boolean isTvShowDetailed) {
		// Cek jika app berada di section DetailActivity agar dapat mengakses URL TV Show Details
		if(isTvShowDetailed) {
			try {
				int dataId = object.getInt("id");
				String dataName = object.getString("name");
				String dataNumberOfSeasons = object.getString("number_of_seasons");
				String dataNumberOfEpisodes = object.getString("number_of_episodes");
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
				if(dataNetworksArray.length() > 0) {
					for(int i = 0 ; i < dataNetworksArray.length() ; i++) {
						JSONObject networkObject = dataNetworksArray.getJSONObject(i);
						String network = networkObject.getString("name");
						if(dataNetworksArray.length() == 1) {
							if(i == 0)
								dataNetworks = network;
						} else {
							if(i == 0)
								dataNetworks = network + ", ";
							else if(i == dataNetworksArray.length() - 1)
								dataNetworks += network;
							else
								dataNetworks += network + ", ";
						}
					}
				} else {
					dataNetworks = "Network Unknown";
				}
				
				JSONArray dataGenresArray = object.getJSONArray("genres");
				String dataGenres = null;
				
				// Cek jika genresArray ada datanya atau tidak, jika tidak set default value untuk String
				// genres (isinya adalah item yg ada di array)
				if(dataGenresArray.length() > 0) {
					// Iterate genre array untuk mendapatkan genre yang akan ditambahkan ke genres
					// fyi: genres itu adalah koleksi dari genre field
					for(int i = 0 ; i < dataGenresArray.length() ; i++) {
						JSONObject genreObject = dataGenresArray.getJSONObject(i);
						String genre = genreObject.getString("name");
						if(dataGenresArray.length() == 1) {
							if(i == 0)
								dataGenres = genre;
						} else {
							if(i == 0)
								dataGenres = genre + ", ";
							else if(i == dataGenresArray.length() - 1)
								dataGenres += genre;
							else
								dataGenres += genre + ", ";
						}
					}
				} else {
					dataGenres = "Genre Unknown";
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
				this.tvShowRatings = dataVoteAverage;
				this.tvShowRatingsVote = dataVoteCount;
				this.tvShowOriginalLanguage = displayed_language;
				this.tvShowNetworks = dataNetworks;
				this.tvShowGenres = dataGenres;
				this.tvShowFirstAirDate = dataFirstAirDate;
				this.tvShowOverview = dataOverview;
				this.tvShowPosterPath = dataPosterPath;
				
			} catch(Exception e) {
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
			} catch(Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public TvShowItem() {
	
	}
	
	protected TvShowItem(Parcel in) {
		id = in.readInt();
		tvShowName = in.readString();
		tvShowSeasons = in.readString();
		tvShowEpisodes = in.readString();
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
		if(tvShowName != null && ! tvShowName.isEmpty()) {
			return tvShowName;
		} else {
			return "Name Unknown";
		}
	}
	
	public void setTvShowName(String tvShowName) {
		this.tvShowName = tvShowName;
	}
	
	public String getTvShowSeasons() {
		return tvShowSeasons;
	}
	
	public String getTvShowEpisodes() {
		return tvShowEpisodes;
	}
	
	public String getTvShowRatings() {
		return tvShowRatings;
	}
	
	public void setTvShowRatings(String tvShowRatings) {
		this.tvShowRatings = tvShowRatings;
	}
	
	public String getTvShowRatingsVote() {
		return tvShowRatingsVote;
	}
	
	public String getTvShowOriginalLanguage() {
		if(tvShowOriginalLanguage != null) {
			return tvShowOriginalLanguage;
		} else {
			return "Language Unknown";
		}
	}
	
	public void setTvShowOriginalLanguage(String tvShowOriginalLanguage) {
		this.tvShowOriginalLanguage = tvShowOriginalLanguage;
	}
	
	public String getTvShowNetworks() {
		if(tvShowNetworks != null) {
			return tvShowNetworks;
		} else {
			return "Networks Unknown";
		}
	}
	
	public String getTvShowGenres() {
		if(tvShowGenres != null && ! tvShowGenres.isEmpty()) {
			return tvShowGenres;
		} else {
			return "Genres Unknown";
		}
	}
	
	public String getTvShowFirstAirDate() {
		if(tvShowFirstAirDate != null && ! tvShowFirstAirDate.isEmpty()) {
			return tvShowFirstAirDate;
		} else {
			return "First Air Date Unknown";
		}
	}
	
	public void setTvShowFirstAirDate(String tvShowFirstAirDate) {
		this.tvShowFirstAirDate = tvShowFirstAirDate;
	}
	
	public String getTvShowOverview() {
		if(tvShowOverview != null && ! tvShowOverview.isEmpty()) {
			return tvShowOverview;
		} else {
			return "Overview Unknown";
		}
	}
	
	public String getTvShowPosterPath() {
		return tvShowPosterPath;
	}
	
	public void setTvShowPosterPath(String tvShowPosterPath) {
		this.tvShowPosterPath = tvShowPosterPath;
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
