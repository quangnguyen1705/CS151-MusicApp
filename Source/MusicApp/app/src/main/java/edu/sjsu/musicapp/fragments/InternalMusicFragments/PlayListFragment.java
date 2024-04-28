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
import edu.sjsu.musicapp.adapters.playlist.PlayListAdapter;
import edu.sjsu.musicapp.adapters.song.SongAdapter;
import edu.sjsu.musicapp.database.PlayListDB;
import edu.sjsu.musicapp.models.PlayList;
import edu.sjsu.musicapp.shared.PlayListSync;

import java.util.ArrayList;

/**
 * Created by fredericknguyen on 04/10/2024
 *
 */

public class PlayListFragment extends Fragment {

    private ArrayList<PlayList> playLists;
    private PlayListDB playListDB;
    private Typeface face;
    private SongAdapter songAdapter;

    RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    PlayListAdapter recyclerAdapter;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist_recycler, container, false);

        playLists = new ArrayList<>();
        playListDB = PlayListSync.getDataBaseHandler();

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(layoutManager);


        new getPlayLists().execute();

        return view;
    }

    private class getPlayLists extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // set font
            face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Lato-Regular.ttf");
            // get all play lists (if any)
            getPlayLists();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            recyclerAdapter = new PlayListAdapter(playLists, getActivity(), face);
            recyclerView.setAdapter(recyclerAdapter);
            PlayListSync.updateAdapter(recyclerAdapter);
        }
    }

    private void getPlayLists() {
        try {
            playLists = playListDB.getAllPlayLists();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
