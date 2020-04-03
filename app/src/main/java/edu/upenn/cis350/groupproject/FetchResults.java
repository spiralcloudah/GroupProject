package edu.upenn.cis350.groupproject;

import java.util.List;

public interface FetchResults {

    void onFetchSuccess(List<Post> objects);
    void onFetchFailure();
    void onFetchFinish();
    Post.Query getPostQuery();
}
