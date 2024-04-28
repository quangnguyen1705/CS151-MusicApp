package edu.sjsu.musicapp.fragments.InternalMusicFragments;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.sjsu.musicapp.R;
import edu.sjsu.musicapp.adapters.song.FavouritesAdapter;
import edu.sjsu.musicapp.database.PlayListDB;
import edu.sjsu.musicapp.models.Song;
import edu.sjsu.musicapp.shared.PlayListSync;

import java.util.ArrayList;

/**
 * Created by fredericknguyen on 04/10/2024
 *
 */

public class FavouriteFragment extends Fragment {

    private ArrayList<Song> favouriteSongs;
    private PlayListDB playListDB;
    private Typeface face;
    private RecyclerView favouritesRecyclerView;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);

        favouriteSongs = new ArrayList<>();
        playListDB = PlayListSync.getDataBaseHandler();
        face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Lato-Regular.ttf");

        favouritesRecyclerView = (RecyclerView) view.findViewById(R.id.fav_recycler_view);
        favouritesRecyclerView.setHasFixedSize(true);
        favouritesRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));

        new getFavouriteSongs().execute();

        return view;
    }

    private void getFavSongs() {
        try {
            favouriteSongs = playListDB.getAllFavSongs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class getFavouriteSongs extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // get all play lists (if any)
            getFavSongs();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            FavouritesAdapter favouritesAdapter = new FavouritesAdapter(favouriteSongs, getActivity(), face);
            favouritesRecyclerView.setAdapter(favouritesAdapter);
            PlayListSync.updateFavouritesAdapter(favouritesAdapter);
        }
    }
}
