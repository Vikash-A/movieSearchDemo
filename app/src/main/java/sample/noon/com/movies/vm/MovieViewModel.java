package sample.noon.com.movies.vm;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sample.noon.com.movies.model.Movie;
import sample.noon.com.movies.model.MovieFeed;
import sample.noon.com.movies.network.RestApi;
import sample.noon.com.movies.network.RestApiFactory;

public class MovieViewModel extends ViewModel {

  Executor executor = Executors.newSingleThreadExecutor();

  private MutableLiveData<Movie> movieSelectionLiveData = new MutableLiveData<>();

  private MutableLiveData<List<Movie>> movieBookmarkLiveData = new MutableLiveData<>();

  private MutableLiveData<List<Movie>> movieFeedLiveData = new MutableLiveData<>();

  private MutableLiveData<Movie> movieDetailLiveData = new MutableLiveData<>();

  private RestApi restApi;

  public int currentFeedCount;

  private String pageLoadInProgress;


  public void initNetwork() {

    if (restApi == null) {
      restApi = RestApiFactory.create();
    }

  }


  public LiveData<Movie> getMovieSelectionLiveData() {
    return movieSelectionLiveData;
  }

  public void setSelection(Movie selection) {
    if (selection != null && !TextUtils.isEmpty(selection.imdbID)) {
      movieSelectionLiveData.postValue(selection);
    }

  }

  public MutableLiveData<List<Movie>> getMovieBookmarkLiveData() {
    return movieBookmarkLiveData;
  }

  public LiveData<Movie> getMovieDetails(String id) {

    Call<Movie> call = restApi.fetchMovieById(id, "ce4bd4f2");

    executor.execute(() -> call.enqueue(new Callback<Movie>() {
      @Override
      public void onResponse(Call<Movie> call1, Response<Movie> response) {
        Movie resource = response.body();
        movieDetailLiveData.postValue(resource);
      }

      @Override
      public void onFailure(Call<Movie> call1, Throwable t) {
        call1.cancel();
      }
    }));

    return movieDetailLiveData;

  }

  public LiveData<List<Movie>> getMovieFeed(String newText, String page) {

    pageLoadInProgress = page;

    Call<MovieFeed> call = restApi.fetchMovies(newText, "ce4bd4f2", page);

    executor.execute(() -> call.enqueue(new Callback<MovieFeed>() {
      @Override
      public void onResponse(Call<MovieFeed> call1, Response<MovieFeed> response) {
        MovieFeed resource = response.body();
        if (response.errorBody() == null) {
          currentFeedCount = resource.getTotalResults();
          String page1 = call1.request().url().queryParameter("page");
          if ("1".equals(page1)) {
            movieFeedLiveData.postValue(resource.Search);
          } else {
            List<Movie> feed = movieFeedLiveData.getValue();
            if (resource.Search != null) {
              feed.addAll(resource.Search);
              movieFeedLiveData.postValue(feed);
            }
          }
        }
      }

      @Override
      public void onFailure(Call<MovieFeed> call1, Throwable t) {
        call1.cancel();
      }
    }));
    return movieFeedLiveData;
  }

  public void resetDetail() {
    movieDetailLiveData.setValue(null);
  }

  public void updateBookmark(Movie movie, boolean added) {

    if (added) {
      if (movieBookmarkLiveData.getValue() == null) {
        List<Movie> movies = new ArrayList<>();
        movies.add(movie);
        movieBookmarkLiveData.setValue(movies);

      } else {
        List<Movie> movies = movieBookmarkLiveData.getValue();
        movies.add(movie);
        movieBookmarkLiveData.postValue(movies);
      }

    } else if (movieBookmarkLiveData.getValue() != null) {
      List<Movie> movies = movieBookmarkLiveData.getValue();
      movies.remove(movie);
      movieBookmarkLiveData.postValue(movies);
    }


  }

  public boolean isBookmarked(Movie movie) {
    if (movieBookmarkLiveData.getValue() != null) {
      List<Movie> movies = movieBookmarkLiveData.getValue();
      return movies.contains(movie);
    }
    return false;

  }

  public void getNextPage(int listSize, String query) {
    int nextPage = listSize / 10 + 1;
    String nextPageStr = String.valueOf(nextPage);
    if (!pageLoadInProgress.equals(nextPageStr) && !TextUtils.isEmpty(query)) {
      getMovieFeed(query, String.valueOf(nextPage));
    }

  }
}
