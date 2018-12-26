package sample.noon.com.movies.model;

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

}
