package sample.noon.com.movies.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import sample.noon.com.movies.R;
import sample.noon.com.movies.model.Movie;
import sample.noon.com.movies.vm.MovieViewModel;

/**
 * To show details of selected movie
 */
public class MoviesDetailFragment extends Fragment {

  private static final String TAG = "MDF";
  private ImageView mPosterImageView;
  private TextView mMovieYearText;
  private TextView mMovieNameText;
  private TextView mActorsText;
  private TextView mDirectorText;
  private RatingBar mRatingBar;
  private TextView mGenreText;
  private RequestManager mImageRequester;

  public MoviesDetailFragment() {
  }

  public static Fragment getInstance(Movie movie, String transitionName) {
    MoviesDetailFragment moviesDetailFragment = new MoviesDetailFragment();
    Bundle data = new Bundle();
    data.putString("id", movie.imdbID);
    data.putString("name", movie.Title);
    data.putString("year", movie.Year);
    data.putString("poster_url", movie.Poster);
    data.putString("shared_view_name", transitionName);
    moviesDetailFragment.setArguments(data);
    return moviesDetailFragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View fragmentRoot = inflater.inflate(R.layout.fragment_detail, container, false);
    Toolbar toolbar = fragmentRoot.findViewById(R.id.toolbar);
    AppCompatActivity activity = (AppCompatActivity) getActivity();
    activity.setSupportActionBar(toolbar);

    toolbar.setNavigationOnClickListener(v -> {
      getActivity().onBackPressed();
    });

    setHasOptionsMenu(true);

    MovieViewModel mViewModel = ViewModelProviders.of(getActivity()).get(MovieViewModel.class);

    mPosterImageView = fragmentRoot.findViewById(R.id.imgPoster);

    ViewCompat.setTransitionName(mPosterImageView, getArguments().getString("shared_view_name"));

    mMovieNameText = fragmentRoot.findViewById(R.id.txtName);

    mGenreText = fragmentRoot.findViewById(R.id.txtGenre);

    mDirectorText = fragmentRoot.findViewById(R.id.txtDirector);

    mMovieYearText = fragmentRoot.findViewById(R.id.txtYear);

    mActorsText = fragmentRoot.findViewById(R.id.txtActors);

    mMovieNameText.setText(getArguments().getString("name"));

    mMovieYearText.setText(getArguments().getString("year"));

    mRatingBar = fragmentRoot.findViewById(R.id.ratingBar1);

    mImageRequester = Glide.with(this)
        .applyDefaultRequestOptions(RequestOptions.downsampleOf(DownsampleStrategy.CENTER_INSIDE))
        .applyDefaultRequestOptions(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
        .applyDefaultRequestOptions(RequestOptions.formatOf(DecodeFormat.PREFER_RGB_565))
        .applyDefaultRequestOptions(RequestOptions.bitmapTransform(new RoundedCorners(12)))
        .applyDefaultRequestOptions(RequestOptions.placeholderOf(R.drawable.movie_placeholder));

    mImageRequester.load(getArguments().getString("poster_url")).into(mPosterImageView);

    mViewModel.getMovieDetails(getArguments().getString("id", "")).observe(this, new Observer<Movie>() {
      @Override
      public void onChanged(@Nullable Movie movie) {
        if (movie != null) {
          bindView(movie);
        }
      }
    });

    return fragmentRoot;
  }

  private void bindView(Movie movie) {
    try {
      mRatingBar.setRating(Float.parseFloat(movie.imdbRating) / 2f);
    } catch (NumberFormatException e) {
      //ignore
    }
    mActorsText.setText(movie.Actors);

    mDirectorText.setText(movie.Director);

    mGenreText.setText(movie.Genre);


  }


}
