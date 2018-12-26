package sample.noon.com.movies.view;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import java.util.ArrayList;
import java.util.List;
import sample.noon.com.movies.R;
import sample.noon.com.movies.model.Movie;
import sample.noon.com.movies.vm.MovieViewModel;

public class MovieBookmarkAdapter extends RecyclerView.Adapter<MovieBookmarkAdapter.ViewHolder> {

  private List<Movie> mDataset = new ArrayList<>();

  private MovieViewModel mViewModel;

  private RequestManager requestManager;

  private View clickedView;

  public void updateDataSet(List<Movie> search) {
    mDataset.clear();
    mDataset.addAll(search);
    notifyDataSetChanged();
  }


  public static class ViewHolder extends RecyclerView.ViewHolder {

    private final ImageView posterView;
    private final TextView nameText;

    public ViewHolder(CardView v) {
      super(v);
      posterView = v.findViewById(R.id.imgPoster_small);
      nameText = v.findViewById(R.id.txtName);
    }
  }

  public MovieBookmarkAdapter(List myDataset, MovieViewModel viewModel, Context ctx) {
    mDataset.addAll(myDataset);
    mViewModel = viewModel;
    requestManager = Glide.with(ctx)
        .applyDefaultRequestOptions(RequestOptions.downsampleOf(DownsampleStrategy.CENTER_INSIDE))
        .applyDefaultRequestOptions(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
        .applyDefaultRequestOptions(RequestOptions.formatOf(DecodeFormat.PREFER_RGB_565))
        .applyDefaultRequestOptions(RequestOptions.bitmapTransform(new RoundedCorners(12)))
        .applyDefaultRequestOptions(RequestOptions.placeholderOf(R.drawable.movie_placeholder));

  }

  @Override
  public MovieBookmarkAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
      int viewType) {

    CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
        .inflate(R.layout.movie_bookmark_item, parent, false);

    final ViewHolder holder = new ViewHolder(cardView);

    cardView.setOnClickListener(v -> {

      int position = holder.getAdapterPosition();
      clickedView = holder.posterView;
      mDataset.get(position).source = 0;
      mViewModel.setSelection(mDataset.get(position));

    });

    return holder;
  }


  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {

    requestManager.load(mDataset.get(position).Poster).into(holder.posterView);
    holder.nameText.setText(mDataset.get(position).Title);
    ViewCompat.setTransitionName(holder.posterView, "bookmark"+position);

  }

  @Override
  public int getItemCount() {
    return mDataset.size();
  }

  public View getClickedItem() {
    return clickedView;
  }


}