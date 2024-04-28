package edu.sjsu.musicapp.utilities;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import edu.sjsu.musicapp.R;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.io.InputStream;

/**
 * Created by fredericknguyen on 04/10/2024
 *
 */

public class AlbumArtLoader {

    private static Uri defaultUri;

    public static void initializeDefaultArt() {
        try {
            defaultUri = new Uri.Builder()
                    .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                    .path(String.valueOf(R.drawable.default_art))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Uri getDefaultArt() {
        return defaultUri;
    }

    public static Bitmap getTrackCoverArt(Context context, Uri uri){
        Bitmap songCoverArt = null;
            ContentResolver res = context.getContentResolver();
            try {
                InputStream in = res.openInputStream(uri);
                songCoverArt = BitmapFactory.decodeStream(in);
            } catch (Exception e){
                e.printStackTrace();
            }

        return songCoverArt;
    }

    public static void setImage(Uri uri, SimpleDraweeView imageView) {

        imageView.setImageURI(defaultUri);
        if (uri != null) {
            ContentResolver cr = ContextProvider.getContext().getContentResolver();
            String[] projection = {MediaStore.MediaColumns.DATA};
            Cursor cur = cr.query(uri, projection, null, null, null);
            if (cur != null) {
                if (cur.moveToFirst()) {
                    String filePath = cur.getString(0);
                    if (!new File(filePath).exists()) {
                        uri = null;
                    }
                } else {
                    uri = null;
                }
                cur.close();
            } else {
                uri = null;
            }
            if (uri != null) {
                imageView.setImageURI(uri);
            }
        }
    }
}
