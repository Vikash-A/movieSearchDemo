package sample.noon.com.movies.model;

import android.content.Context;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class MovieFeed {


  public List<Movie> Search;
  public String totalResults;

  public int getTotalResults() {

    int count = 0;
    try {
      count = Integer.parseInt(totalResults);
    } catch (Exception e) {
    }
    return count;


}

  public int currentPage = 1;


  public static MovieFeed getSeed(Context ctx) {

    BufferedReader reader = null;
    StringBuilder builder = new StringBuilder();
    try {
      reader = new BufferedReader(
          new InputStreamReader(ctx.getAssets().open("seed.json"), "UTF-8"));
      String mLine;
      while ((mLine = reader.readLine()) != null) {
        builder.append(mLine);
      }

      return new Gson().fromJson(builder.toString(), MovieFeed.class);

    } catch (IOException e) {
      //log the exception
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
          //log the exception
        }
      }
    }

    return null;
  }
}
