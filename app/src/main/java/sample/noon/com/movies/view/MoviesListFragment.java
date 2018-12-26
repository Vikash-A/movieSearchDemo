package sample.noon.com.movies.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import java.util.ArrayList;
import sample.noon.com.movies.R;
import sample.noon.com.movies.model.Movie;
import sample.noon.com.movies.vm.MovieViewModel;


public class MoviesListFragment extends Fragment {

  private static final String TAG = "MAF";

  private SearchView mSearchView;
  private MovieViewModel mViewModel;
  private MovieAdapter mAdapter;
  private View mFragmentRoot;
  private RecyclerView mRecyclerView;
  private RecyclerView mBookmarksRecycleView;
  private String mQueryText;

  public MoviesListFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    if (mFragmentRoot == null) {

      mFragmentRoot = inflater.inflate(R.layout.fragment_main, container, false);
      Toolbar toolbar = mFragmentRoot.findViewById(R.id.toolbar);
      AppCompatActivity activity = (AppCompatActivity) getActivity();
      activity.setSupportActionBar(toolbar);

      setHasOptionsMenu(true);

      Gson gson = new Gson();

      SharedPreferences sharedPref = getActivity().getSharedPreferences("bookmarks", Context.MODE_PRIVATE);

      mRecyclerView = mFragmentRoot.findViewById(R.id.recycler_view);

      mBookmarksRecycleView = mFragmentRoot.findViewById(R.id.bookmark_recyclerView);

      mBookmarksRecycleView
          .setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

      mViewModel = ViewModelProviders.of(getActivity()).get(MovieViewModel.class);

      LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());

      mRecyclerView.setLayoutManager(mLayoutManager);

      mAdapter = new MovieAdapter(new ArrayList<Movie>(), mViewModel, getActivity());

      mRecyclerView.setAdapter(mAdapter);

      mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
          super.onScrollStateChanged(recyclerView, newState);

          if (!recyclerView.canScrollVertically(1)) {
            if (mAdapter.getItemCount() < mViewModel.currentFeedCount) {
              mViewModel.getNextPage(mAdapter.getItemCount(), mQueryText);
            }

          }
        }
      });

      mViewModel.getMovieBookmarkLiveData().observe(this, movies -> {
        if (movies != null && movies.size() > 0) {
          if (mBookmarksRecycleView.getAdapter() == null) {
            MovieBookmarkAdapter mAdapter = new MovieBookmarkAdapter(movies, mViewModel, getContext());
            mBookmarksRecycleView.setAdapter(mAdapter);
          } else {
            MovieBookmarkAdapter mAdapter = (MovieBookmarkAdapter) mBookmarksRecycleView.getAdapter();
            mAdapter.updateDataSet(movies);
            sharedPref.edit().putString("bookmarks", gson.toJson(movies)).apply();

          }
          mBookmarksRecycleView.setVisibility(View.VISIBLE);
        } else {
          mBookmarksRecycleView.setVisibility(View.GONE);
        }

      });
    } else {
      mViewModel.resetDetail();
    }
    return mFragmentRoot;
  }


  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    inflater.inflate(R.menu.menu_search, menu);

    MenuItem mSearch = menu.findItem(R.id.action_search);

    mSearchView = (SearchView) mSearch.getActionView();

    ((EditText) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text))
        .setHintTextColor(getResources().getColor(R.color.colorAccent));
    ((EditText) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text))
        .setHint("Search movies by title");

    mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        return false;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        newText = newText.trim();
        if (!TextUtils.isEmpty(newText) && newText.length() >= 3) {
          mQueryText = newText;
          fetchListForQuery(newText);
        } else {
          mRecyclerView.setVisibility(View.GONE);
          Glide.get(getActivity()).clearMemory();
          mFragmentRoot.findViewById(R.id.empty).setVisibility(View.VISIBLE);
        }
        return true;
      }
    });
  }

  private void fetchListForQuery(String newText) {

    mViewModel.getMovieFeed(newText, "1").observe(this, movieFeed -> {

      if (mAdapter != null && movieFeed != null && movieFeed.size() > 0) {
        mAdapter.updateDataSet(movieFeed);
        mRecyclerView.setVisibility(View.VISIBLE);
        mFragmentRoot.findViewById(R.id.empty).setVisibility(View.GONE);
      } else {
        mRecyclerView.setVisibility(View.GONE);
        mFragmentRoot.findViewById(R.id.empty).setVisibility(View.VISIBLE);
      }
    });


  }

  public View getSharedView(int source) {
    if (source == 0) {
      MovieBookmarkAdapter adapter = (MovieBookmarkAdapter) mBookmarksRecycleView.getAdapter();
      return adapter.getClickedItem();

    }
    return mAdapter.getClickedItem();
  }
}
