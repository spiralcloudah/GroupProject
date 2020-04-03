package edu.upenn.cis350.groupproject;

import android.util.Log;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.List;

public class FragmentHelper {

    private Post.Query query;

    public FragmentHelper(Post.Query postQuery) {
        query = postQuery;
    }

    public void fetchPosts(final FetchResults curFragment) {
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    curFragment.onFetchSuccess(objects);
                } else {
                    Log.e("FragmentHelper", e.getMessage());
                    curFragment.onFetchFailure();
                }
                curFragment.onFetchFinish();
            }
        });
    }
}
