package sjsu.edu.musicapp;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ListView;

/**
 * Created by fredericknguyen on 4/22/24.
 */

public class ListSong extends ListActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeComponent();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        launchMediaPlayer(position);
    }


    private void initializeComponent() {
        mCursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, null, null, MediaStore.Audio.Media.TITLE + " ASC");
        adapter = new ListSongAdapter(this, mCursor);
        setListAdapter(adapter);

    }

    private void launchMediaPlayer(int _position) {
        Intent mediaPlayerIntent = new Intent(getApplicationContext(), MediaPlayerActivity.class);
        mediaPlayerIntent.putExtra("ItemId",_position);
        startActivity(mediaPlayerIntent);
        finishActivity(ListSong.this);

    }
    private void finishActivity(ListSong listSong) {
        // TODO Auto-generated method stub
        finish();
    }

    private Cursor mCursor;
    private ListSongAdapter adapter;
}
