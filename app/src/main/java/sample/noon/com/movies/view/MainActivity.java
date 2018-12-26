package sample.noon.com.movies.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.transition.ChangeBounds;
import android.support.transition.ChangeTransform;
import android.support.transition.TransitionSet;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import sample.noon.com.movies.R;
import sample.noon.com.movies.model.Movie;
import sample.noon.com.movies.vm.MovieViewModel;

public class MainActivity extends AppCompatActivity {

  private MovieViewModel mViewModel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);

    mViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);

    mViewModel.initNetwork();

    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MoviesListFragment(), "mlf")
        .commit();

    mViewModel.getMovieSelectionLiveData().observe(this, movie -> {

      MoviesListFragment listFragment = (MoviesListFragment) getSupportFragmentManager().findFragmentByTag("mlf");

      View sharedView = listFragment.getSharedView(movie.source);

      performTransition(movie, sharedView);

    });

    SharedPreferences sharedPref = getSharedPreferences("bookmarks", Context.MODE_PRIVATE);

    String bookmarks = sharedPref.getString("bookmarks", "");

    if (!TextUtils.isEmpty(bookmarks)) {
      Type myType = new TypeToken<ArrayList<Movie>>() {
      }.getType();
      List<Movie> movies = new Gson().fromJson(bookmarks, myType);
      if (movies != null) {
        mViewModel.getMovieBookmarkLiveData().postValue(movies);
      }

    }


  }


  private void performTransition(Movie movie, View sharedView) {
    if (isDestroyed()) {
      return;
    }

    String transitionName = ViewCompat.getTransitionName(sharedView);

    Fragment nextFragment = MoviesDetailFragment.getInstance(movie, transitionName);

    nextFragment.setSharedElementEnterTransition(new DetailsTransition());

    ViewCompat.setTransitionName(sharedView, transitionName);

//    OnBackStackChangedListener backStackListener = new OnBackStackChangedListener() {
//
//      @Override
//      public void onBackStackChanged() {
//
//        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
//          sharedView.postDelayed(() -> ViewCompat.setTransitionName(sharedView, ""), 500);
//        }
//      }
//    };

    //  getSupportFragmentManager().addOnBackStackChangedListener(backStackListener);

    getSupportFragmentManager()
        .

            beginTransaction()
        .

            addSharedElement(sharedView, transitionName)
        .

            replace(R.id.fragment_container, nextFragment)
        .

            addToBackStack(null)
        .

            commit();

  }


  public class DetailsTransition extends TransitionSet {

    public DetailsTransition() {
      setDuration(500);
      setOrdering(ORDERING_TOGETHER);
      addTransition(new ChangeBounds()).
          addTransition(new ChangeTransform());

    }
  }

}
