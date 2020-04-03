package edu.upenn.cis350.groupproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.Serializable;

public class HomeActivity extends AppCompatActivity {

    // Create the variables for fragments and request code
    public HomeFragment homeFragment;
    public ProfileFragment profileFragment;
    private boolean change_fragment=false;
    private BottomNavigationView bottomNavigationView;
    public static final int COMPOSE_REQUEST_CODE = 20;
    public static final int DETAILS_REQUEST_CODE = 31;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    public void showDetailsFor(Serializable post) {
        // create intent for the new activity
        Intent toDetails = new Intent(this, PostDetailsActivity.class);
        // serialize the post using parceler, use its short name as a key
        toDetails.putExtra(Post.class.getSimpleName(), (Serializable) post);
        // show the activity
        startActivityForResult(toDetails,DETAILS_REQUEST_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = findViewById(R.id.tbMain);

        // Sets the Toolbar to act as the ActionBar for this Activity window
        setSupportActionBar(toolbar);

        // Creates new fragments
        homeFragment = new HomeFragment();
        profileFragment = new ProfileFragment();

        // Sets the action bar to the corresponding ID
        setSupportActionBar((Toolbar) findViewById(R.id.tbMain));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Setup the fragment manager and bottom naviagtion view
        final FragmentManager fragmentManager = getSupportFragmentManager();
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        // handle navigation selection
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment fragment;
                        switch (item.getItemId()) {
                            case R.id.miHome:
                                fragment = homeFragment;
                                break;
                            case R.id.miProfile:
                                fragment = profileFragment;
                                break;

                            // default to home fragment
                            default:
                                fragment = homeFragment;
                                break;
                        }
                        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                        return true;
                    }
                });

        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.miHome);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == HomeActivity.COMPOSE_REQUEST_CODE && resultCode == RESULT_OK) {
            HomeFragment.posts.add(0, (Post) data.getSerializableExtra(Post.class.getSimpleName()));
            HomeFragment.adapter.notifyItemInserted(0);
            HomeFragment.rvPost.scrollToPosition(0);
            change_fragment = true;
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(change_fragment) {
            bottomNavigationView.setSelectedItemId(R.id.miHome);
            change_fragment = false;
        }
    }
}
