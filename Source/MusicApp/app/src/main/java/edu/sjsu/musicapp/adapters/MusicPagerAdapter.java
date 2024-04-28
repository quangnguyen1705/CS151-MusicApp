package edu.sjsu.musicapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import edu.sjsu.musicapp.fragments.InternalMusicFragments.AlbumFragment;
import edu.sjsu.musicapp.fragments.InternalMusicFragments.AllMusicFragment;
import edu.sjsu.musicapp.fragments.InternalMusicFragments.FavouriteFragment;
import edu.sjsu.musicapp.fragments.InternalMusicFragments.PlayListFragment;

/**
 * Created by fredericknguyen on 04/10/2024
 *
 */
public class MusicPagerAdapter extends FragmentStatePagerAdapter {

    public MusicPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AllMusicFragment();
            case 1:
                return new FavouriteFragment();
            case 2:
                return new AlbumFragment();
            case 3:
                return new PlayListFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "All Songs";
            case 1:
                return "Favourites";
            case 2:
                return "Albums";
            case 3:
                return "Playlists";
        }
        return null;
    }
}
