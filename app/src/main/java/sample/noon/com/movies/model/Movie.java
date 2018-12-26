package sample.noon.com.movies.model;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;

public class Movie {



  public static DiffUtil.ItemCallback<Movie> DIFF_CALLBACK = new DiffUtil.ItemCallback<Movie>() {
    @Override
    public boolean areItemsTheSame(@NonNull Movie oldItem, @NonNull Movie newItem) {
      return oldItem.equals(newItem);
    }

    @Override
    public boolean areContentsTheSame(@NonNull Movie oldItem, @NonNull Movie newItem) {
      return oldItem.equals(newItem);
    }
  };


  public String Title;
  public String Year;
  public String Poster;
  public String imdbID;
  public String Director; //part of detail
  public String Genre;//part of detail
  public String Actors;//part of detail
  public String imdbRating;//part of detail
  public int source;


  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Movie) {
      Movie movie = (Movie) obj;
      return this.imdbID.equals(movie.imdbID);
    }
    return false;
  }


  @Override
  public int hashCode() {
    return this.imdbID.hashCode();
  }
}
