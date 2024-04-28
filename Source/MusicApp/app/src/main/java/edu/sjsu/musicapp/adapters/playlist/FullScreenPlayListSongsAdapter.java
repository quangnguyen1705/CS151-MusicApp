package edu.sjsu.musicapp.adapters.playlist;

import android.app.Activity;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import edu.sjsu.musicapp.R;
import edu.sjsu.musicapp.models.Song;
import edu.sjsu.musicapp.shared.PlayListSync;
import edu.sjsu.musicapp.utilities.AlbumArtLoader;
import com.facebook.drawee.view.SimpleDraweeView;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by fredericknguyen on 04/10/2024
 *
 */

public class FullScreenPlayListSongsAdapter extends BaseAdapter implements PopupMenu.OnMenuItemClickListener {

    private final int playListId;
    private ArrayList<Song> playListSongs;
    private int songPosition;
    private Typeface face;
    private Activity activity;
    private LayoutInflater playListInflater;

    public FullScreenPlayListSongsAdapter(Activity activity, ArrayList<Song> playListSongs, Typeface face, int playListId) {
        this.face = face;
        this.playListSongs = playListSongs;
        this.activity = activity;
        this.playListInflater = LayoutInflater.from(activity);
        this.playListId = playListId;
    }

    public class ViewHolder {
        SimpleDraweeView albumArt;
        TextView songTitle;
        TextView songArtist;
        ImageView moreButton;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {

        ViewHolder holder;

        if (view == null) {
            view = playListInflater.inflate(R.layout.full_screen_playlist_song, viewGroup, false);
            holder = new ViewHolder();
            holder.songTitle = (TextView) view.findViewById(R.id.song_title);
            holder.songTitle.setTypeface(face);
            holder.songArtist = (TextView) view.findViewById(R.id.song_artist);
            holder.songArtist.setTypeface(face);
            holder.albumArt = (SimpleDraweeView) view.findViewById(R.id.song_album_art);
            holder.moreButton = (ImageView) view.findViewById(R.id.more_button);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        //get song using position
        final Song song = playListSongs.get(position);
        //get title and artist strings
        holder.songTitle.setText(song.getTitle());
        holder.songArtist.setText(song.getArtist());

        AlbumArtLoader.setImage(song.getAlbumArtUri(), holder.albumArt);

        holder.moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                songPosition = position;
                showMenu(view);
            }
        });

        return view;
    }

    @Override
    public int getCount() {
        return playListSongs.size();
    }

    @Override
    public Song getItem(int index) {
        return playListSongs.get(index);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void setSongs(ArrayList<Song> newSongs) {
        playListSongs = newSongs;
    }

    @Override
    public boolean onMenuItemClick(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.remove_song:
                removeSong();
                return true;
            default:
                return false;
        }
    }

    private void removeSong() {
        Song song = playListSongs.get(songPosition);
        try {
            PlayListSync.getDataBaseHandler().deleteSong(song.getSongDbId());
            // refresh adapter
            this.setSongs(PlayListSync.getDataBaseHandler().getAllPlayListSongs(playListId));
            this.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, "Unable to delete song.", Toast.LENGTH_SHORT).show();
        }
    }

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(activity, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.song_list);
        // Force icons to show
        Object menuHelper;
        Class[] argTypes;
        try {
            Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
            fMenuHelper.setAccessible(true);
            menuHelper = fMenuHelper.get(popup);
            argTypes = new Class[]{boolean.class};
            menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
        } catch (Exception e) {
            Log.w("TAG", "error forcing menu icons to show", e);
            popup.show();
            return;
        }
        popup.show();
    }
}
