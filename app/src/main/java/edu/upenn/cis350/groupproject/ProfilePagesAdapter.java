package edu.upenn.cis350.groupproject;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import edu.upenn.cis350.groupproject.NestedPostsFragment;

public class ProfilePagesAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Posts", "Bookmarks" };
    // private final List<Fragment> lstFragment = new ArrayList<>();
    // private final List<String> lstTitles = new ArrayList<>();
    private Context context;


    public ProfilePagesAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return NestedPostsFragment.newInstance(position + 1, position);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        //returning null to just show icons
        return null;
    }

    // function that constructs and adds elements to tab bar..
//    public void AddFragment (Fragment frag, String title) {
//        lstFragment.add(frag);
//        lstTitles.add(title);
//    }
}
