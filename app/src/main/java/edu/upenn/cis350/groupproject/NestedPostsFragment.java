package edu.upenn.cis350.groupproject;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class NestedPostsFragment extends Fragment implements FetchResults, FragmentUpdated {
    private static final String TAG = "NestedPostsFragment";
    public static final String ARG_PAGE = "ARG_PAGE";
    public static final String POST_TYPE = "Post_Type";
    private EndlessRecyclerViewScrollListener scrollListener;
    private SwipeRefreshLayout swipeContainer;
    RecyclerView rvUserPosts;
    ArrayList<Post> postArrayList;
    PostAdapter postsAdapter;
    private boolean isBookmarks;
    private BroadcastReceiver detailsChangedReceiver;

    public static NestedPostsFragment newInstance(int page, int postType) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        args.putInt(POST_TYPE, postType);
        NestedPostsFragment fragment = new NestedPostsFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // inflates recycler view in viewpager
        View rootView = inflater.inflate(R.layout.nested_fragment_posts, container, false);

        // link variable to layout id
        rvUserPosts = rootView.findViewById(R.id.rvNestPosts);

        rvUserPosts.addItemDecoration(new VerticalSpaceItemDecoration(12));
        rvUserPosts.addItemDecoration(new HorizontalSpaceItemDecoration(12));

        //create new array list  for posts
        postArrayList = new ArrayList<>();
        // create adapter and link posts
        postsAdapter = new PostAdapter(postArrayList);
        rvUserPosts.setAdapter(postsAdapter);

        detailsChangedReceiver = new PostBroadcastReceiver(this);
        IntentFilter filter = new IntentFilter(PostDetailsActivity.ACTION);
        getActivity().registerReceiver(detailsChangedReceiver, filter);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvUserPosts.setLayoutManager(linearLayoutManager);

        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                view.post(new Runnable() {
                    @Override
                    public void run() {
                        // load more posts.. but since on profile  current user posts will not need to be updated
                    }
                });
            }
        };
        // Adds the scroll listener to RecyclerView
        rvUserPosts.addOnScrollListener(scrollListener);


        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code here
                Toast.makeText(getContext(), "Refreshed!", Toast.LENGTH_LONG).show();
                // To keep animation for 4 seconds
                postArrayList.clear();
                postsAdapter.clear();
                int postType = getArguments().getInt(POST_TYPE);
                if (postType == 0) {
                    isBookmarks = false;
                    loadTopPosts();
                } else {
                    isBookmarks = true;
                    loadBookmarkedEvents();

                }

            }
        });

        // Scheme colors for animation
        swipeContainer.setColorSchemeColors(
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.colorAccentDark),
                getResources().getColor(R.color.colorPrimaryDark)
        );


        // toggle created to display user posts in database and user bookmarks in database
        int postType = getArguments().getInt(POST_TYPE);
        if (postType == 0) {
            isBookmarks = false;
            loadTopPosts();
        } else {
            isBookmarks = true;
            loadBookmarkedEvents();
        }
    }

    // function that reloads recycler view on case of bookmarks selected
    public void loadBookmarkedEvents() {
        final Post.Query eventsQuery = new Post.Query();

        eventsQuery.whereEqualTo(Post.KEY_IS_EVENT, true);
        eventsQuery.addDescendingOrder(Post.KEY_DATE);

        postsAdapter.clear();
        postArrayList.clear();

        FragmentHelper fragmentHelper = new FragmentHelper(eventsQuery);
        fragmentHelper.fetchPosts(this);
    }

    public void loadTopPosts(){
        FragmentHelper fragmentHelper = new FragmentHelper(getPostQuery());
        fragmentHelper.fetchPosts(this);
    }

    @Override
    public Post.Query getPostQuery() {
        final Post.Query postQuery = new Post.Query();
        postQuery.getTop().withUser();
        // Only load the current user's posts
        postQuery.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        postQuery.addDescendingOrder(Post.KEY_DATE);
        return postQuery;
    }

    @Override
    public void onFetchSuccess(List<Post> objects) {
        if(isBookmarks) {
            ParseUser currentUser = ParseUser.getCurrentUser();
            final JSONArray bookmarked = currentUser.getJSONArray("bookmarked");
            if (bookmarked == null) {
                return;
            }
            for (int i = 0; i < bookmarked.length(); i++) {
                for (int j = 0; j < objects.size(); j++) {
                    try {
                        if (objects.get(j).getObjectId().equals(bookmarked.get(i).toString())) {
                            postArrayList.add(0, objects.get(j));
                            postsAdapter.notifyItemInserted(postArrayList.size() - 1);
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        } else {
            postArrayList.addAll(objects);
            postsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onFetchFailure() {
        Toast.makeText(getContext(), "Failed to query posts", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFetchFinish() {
        swipeContainer.setRefreshing(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister the listener when the application is paused
        getActivity().unregisterReceiver(detailsChangedReceiver);
    }

    @Override
    public void updatePosts(Intent intent) {
        Post postChanged = (Post) intent.getSerializableExtra(Post.class.getSimpleName());
        int indexOfChange = -1;
        for (int i = 0; i < postArrayList.size(); i++) {
            if (postArrayList.get(i).hasSameId(postChanged)) {
                indexOfChange = i;
                break;
            }
        }
        if (indexOfChange != -1) {
            postArrayList.set(indexOfChange, postChanged);
            postsAdapter.notifyItemChanged(indexOfChange);
        }
    }
}