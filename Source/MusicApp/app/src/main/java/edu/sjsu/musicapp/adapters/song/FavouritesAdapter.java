package edu.sjsu.musicapp.adapters.song;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import edu.sjsu.musicapp.R;
import edu.sjsu.musicapp.models.Song;
import edu.sjsu.musicapp.services.MusicService;
import edu.sjsu.musicapp.shared.PlayListSync;
import edu.sjsu.musicapp.utilities.AlbumArtLoader;
import edu.sjsu.musicapp.utilities.ContextProvider;
import com.facebook.drawee.view.SimpleDraweeView;

import java.lang.reflect.Field;
import java.util.ArrayList;
/**
 * Created by fredericknguyen on 04/10/2024
 *
 */
public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.ViewHolder> implements PopupMenu.OnMenuItemClickListener {

    private ArrayList<Song> favouriteSongs;
    private Activity context;
    private Typeface face;

    private int songPosition;

    private MusicService musicService;

    public FavouritesAdapter(ArrayList<Song> favouriteSongs, Activity context, Typeface face) {
        super();
        this.favouriteSongs = favouriteSongs;
        this.context = context;
        this.face = face;
        musicService = MusicService.musicService;
    }

    @Override
    public FavouritesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavouritesAdapter.ViewHolder holder, final int position) {
        Song song = favouriteSongs.get(position);
        holder.songName.setText(song.getTitle());
        holder.artistName.setText(song.getArtist());
        holder.songDuration.setText(song.getFormattedTime());
        holder.moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                songPosition = position;
                showMenu(view);
            }
        });

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ContextProvider.getContext());
        boolean displayFavouritesAlbumArt = sharedPreferences.getBoolean("favouritesAlbumArt", true);
        if (displayFavouritesAlbumArt) {
            holder.favouriteAlbumArt.setVisibility(View.VISIBLE);
            AlbumArtLoader.setImage(song.getAlbumArtUri(), holder.favouriteAlbumArt);
        } else {
            holder.favouriteAlbumArt.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSong(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favouriteSongs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView songName;
        public TextView artistName;
        public ImageView moreButton;
        public SimpleDraweeView favouriteAlbumArt;
        public TextView songDuration;

        public ViewHolder(View itemView) {
            super(itemView);
            songName = (TextView) itemView.findViewById(R.id.song_title);
            songName.setTypeface(face);
            artistName = (TextView) itemView.findViewById(R.id.song_artist);
            artistName.setTypeface(face);
            songDuration = (TextView) itemView.findViewById(R.id.song_duration);
            songDuration.setTypeface(face);
            moreButton = (ImageView) itemView.findViewById(R.id.more_button);
            favouriteAlbumArt = (SimpleDraweeView) itemView.findViewById(R.id.song_album_art);
        }
    }

    public void selectSong(int position) {
        musicService.setSongList(favouriteSongs);
        musicService.startSong(position);
        musicService.getPlayPauseButton().setImageResource(R.drawable.ic_activity_pause);
    }

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(context, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_fav_song_list_pop);
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

    @Override
    public boolean onMenuItemClick(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.remove_fav_song:
                removeSongFromFavourites();
            default:
                return false;
        }
    }

    private void removeSongFromFavourites() {
        String message = "";
        try {
            PlayListSync.getDataBaseHandler().deleteFavSong(favouriteSongs.get(songPosition).getID());
            message = "Song removed from Favourites";
        } catch (Exception e) {
            e.printStackTrace();
            message = "Error removing song from Favourites";
        } finally {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            PlayListSync.refreshFavouritesSongs();
        }
    }

    public void setFavouriteSongs(ArrayList<Song> newSongs) {
        favouriteSongs = newSongs;
    }
}
