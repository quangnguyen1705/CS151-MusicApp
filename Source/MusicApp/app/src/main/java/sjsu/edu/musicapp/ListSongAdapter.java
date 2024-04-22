package sjsu.edu.musicapp;


import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by fredericknguyen on 4/22/24.
 */

public class ListSongAdapter extends BaseAdapter{
    public ListSongAdapter(Context context, Cursor data) {
        mInflate = LayoutInflater.from(context);
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.getCount();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder _holder;

        if(convertView == null) {
            convertView = mInflate.inflate(R.layout.list_item, null);

            _holder 			= new ViewHolder();
            _holder.mTittle 	= (TextView)convertView.findViewById(R.id.title);
            _holder.mDuration 	= (TextView)convertView.findViewById(R.id.duration);
            _holder.mArtist		= (TextView)convertView.findViewById(R.id.artist);

            convertView.setTag(_holder);
        } else {
            _holder = (ViewHolder)convertView.getTag();
        }

        mData.moveToPosition(position);

        mTempDuration = Long.valueOf(mData.getString(mData.getColumnIndex(MediaStore.Audio.Media.DURATION)));
        _holder.mDuration.setText(Utility.createHMFormatfromM(mTempDuration));
        _holder.mTittle.setText(mData.getString(mData.getColumnIndex(MediaStore.Audio.Media.TITLE)));
        _holder.mArtist.setText(mData.getString(mData.getColumnIndex(MediaStore.Audio.Media.ARTIST)));


        return convertView;
    }

    private class ViewHolder {
        TextView mTittle;
        TextView mDuration;
        TextView mArtist;
    }

    private LayoutInflater mInflate;
    private Cursor mData;
    private long mTempDuration;
}
