package sample.noon.com.movies.view;

import android.content.Context;
import android.graphics.Bitmap.CompressFormat;
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

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

  private List<Movie> mDataset = new ArrayList<>();

  private Context context;

  private MovieViewModel viewModel;

  private RequestManager requestManager;

  private View clickedView;

  public void updateDataSet(List<Movie> search) {
    mDataset.clear();
    mDataset.addAll(search);
    notifyDataSetChanged();
  }

  public View getClickedItem() {
    return clickedView;
  }


  public static class ViewHolder extends RecyclerView.ViewHolder {

    private final ImageView posterView;
    private final TextView yearText;
    private final TextView nameText;
    private final View bookmark;


    public CardView cardView;

    public ViewHolder(CardView v) {
      super(v);
      cardView = v;
      posterView = v.findViewById(R.id.imgPoster);
      yearText = v.findViewById(R.id.txtYear);
      nameText = v.findViewById(R.id.txtName);
      bookmark = v.findViewById(R.id.bookmark);
    }
  }

  public MovieAdapter(List myDataset, MovieViewModel mViewModel, Context ctx) {
    mDataset.addAll(myDataset);
    viewModel = mViewModel;
    requestManager = Glide.with(ctx)
        .applyDefaultRequestOptions(RequestOptions.downsampleOf(DownsampleStrategy.CENTER_INSIDE))
        .applyDefaultRequestOptions(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
        .applyDefaultRequestOptions(RequestOptions.formatOf(DecodeFormat.PREFER_RGB_565))
        .applyDefaultRequestOptions(RequestOptions.encodeFormatOf(CompressFormat.JPEG))
        .applyDefaultRequestOptions(RequestOptions.bitmapTransform(new RoundedCorners(12)))
        .applyDefaultRequestOptions(RequestOptions.placeholderOf(R.drawable.movie_placeholder));

  }

  @Override
  public MovieAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
      int viewType) {

    context = parent.getContext();

    CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
        .inflate(R.layout.movie_item, parent, false);

    final ViewHolder holder = new ViewHolder(cardView);

    cardView.setOnClickListener(v -> {

      int positon = holder.getAdapterPosition();

      mDataset.get(positon).source = 1;
      clickedView = holder.posterView;

      viewModel.setSelection(mDataset.get(positon));

    });

    cardView.findViewById(R.id.bookmark).setOnClickListener(v -> {

      int positon = holder.getAdapterPosition();

      boolean selected = v.isSelected();

      v.setSelected(!selected);

      viewModel.updateBookmark(mDataset.get(positon), !selected);

    });

    return holder;
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {

    requestManager.load(mDataset.get(position).Poster).into(holder.posterView);

    holder.nameText.setText(mDataset.get(position).Title);

    holder.yearText.setText(mDataset.get(position).Year);

    holder.bookmark.setSelected(viewModel.isBookmarked(mDataset.get(position)));

    ViewCompat.setTransitionName(holder.posterView, "movie"+position);
  }

  @Override
  public int getItemCount() {
    return mDataset.size();
  }

}