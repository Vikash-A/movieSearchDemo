package sample.noon.com.movies.network;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import sample.noon.com.movies.model.Movie;
import sample.noon.com.movies.model.MovieFeed;

public interface RestApi {


  @GET("/")
  Call<MovieFeed> fetchMovies(@Query("s") String s,
      @Query("apiKey") String apiKey,
      @Query("page") String page);


  @GET("/")
  Call<Movie> fetchMovieById(@Query("i") String i,
      @Query("apiKey") String apiKey);
}