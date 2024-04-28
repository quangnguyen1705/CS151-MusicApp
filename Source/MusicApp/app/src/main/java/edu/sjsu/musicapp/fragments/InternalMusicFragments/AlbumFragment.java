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
import edu.sjsu.musicapp.adapters.song.AlbumAdapter;
import edu.sjsu.musicapp.models.Album;
import edu.sjsu.musicapp.utilities.MediaQuery;

import java.util.ArrayList;

/**
 * Created by fredericknguyen on 04/10/2024
 *
 */
public class AlbumFragment extends Fragment {

    private ArrayList<Album> albums;
    private Typeface face;
    private RecyclerView albumRecyclerView;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);

        albums = new ArrayList<>();
        face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Lato-Regular.ttf");

        albumRecyclerView = (RecyclerView) view.findViewById(R.id.album_recycler_view);
        albumRecyclerView.setHasFixedSize(true);
        albumRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        new getAlbumList().execute();

        return view;
    }

    private void getAllAlbums() {
        try {
            albums = MediaQuery.getAlbumList(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class getAlbumList extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            getAllAlbums();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            AlbumAdapter albumAdapter = new AlbumAdapter(albums, getActivity(), face);
            albumRecyclerView.setAdapter(albumAdapter);
        }
    }
}
