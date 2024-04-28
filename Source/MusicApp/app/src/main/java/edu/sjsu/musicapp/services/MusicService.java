package edu.sjsu.musicapp.services;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import edu.sjsu.musicapp.R;
import edu.sjsu.musicapp.broadcasts.NoisyAudioStreamReceiver;
import edu.sjsu.musicapp.broadcasts.NotificationBroadcast;
import edu.sjsu.musicapp.models.Song;
import edu.sjsu.musicapp.utilities.AlbumArtLoader;
import edu.sjsu.musicapp.utilities.TimeFormatter;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by fredericknguyen on 04/10/2024
 *
 */

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener, AudioManager.OnAudioFocusChangeListener {

    int NOTIFICATION_ID = 78339023;
    public static final String NOTIFY_CLOSE = "edu.sjsu.musicapp.close";
    public static final String NOTIFY_PREV = "edu.sjsu.musicapp.prev";
    public static final String NOTIFY_NEXT = "edu.sjsu.musicapp.next";
    public static final String NOTIFY_PAUSE_PLAY = "edu.sjsu.musicapp.pauseplay";
    public static final String NOTIFY_FULL_MUSIC = "edu.sjsu.musicapp.full_screen";

    //media player
    private MediaPlayer mediaPlayer;

    // current list
    private ArrayList<Song> songs;

    Song currentSong = null;
    //current position
    private int currentPosition;

    private LinearLayout controlLayout;
    private ImageView controlPlayPauseButton;
    private SimpleDraweeView controlAlbumArt;
    private TextView controlTrackName;
    private TextView controlArtistName;

    boolean shuffle = false;
    boolean repeat = false;
    private Random rand;

    // variables to control full screen
    private boolean isFullScreen = false;
    private SimpleDraweeView fullScreenAlbumArt;

    private final Handler handler = new Handler();
    public static final String BROADCAST_ACTION = "com.bmustapha.gaimediaplayer.services.seekProgres";

    Intent seekIntent;
    private NoisyAudioStreamReceiver noisyReceiver;

    public static MusicService musicService;

    private NotificationBroadcast notificationBroadcast;
    private FloatingActionButton pausePlay;


    private SimpleDraweeView playListFullAlbumArt;
    private boolean isFromPlayList = false;
    private int playListId;
    private ImageView fullPlayListPlayPauseButton;
    private AudioManager audioManager;

    private boolean isMute = false;
    private boolean wasPlaying = false;


    @Override
    public void onCreate() {
        //create the service
        super.onCreate();
        // put the service instance in a static variable
        musicService = this;
        //initialize position
        currentPosition = 0;
        // initialize the random number generator
        rand = new Random();
        //create player
        mediaPlayer = new MediaPlayer();
        initMusicPlayer();

        // set up intent for seekBar broadcast
        seekIntent = new Intent(BROADCAST_ACTION);

        noisyReceiver = new NoisyAudioStreamReceiver();
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(noisyReceiver, intentFilter);

        // instance of custom broadcast receiver
        notificationBroadcast = new NotificationBroadcast();

        IntentFilter notificationIntentFilter = new IntentFilter();
        notificationIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        notificationIntentFilter.addAction(NOTIFY_PAUSE_PLAY);
        notificationIntentFilter.addAction(NOTIFY_NEXT);
        notificationIntentFilter.addAction(NOTIFY_PREV);
        notificationIntentFilter.addAction(NOTIFY_CLOSE);
        notificationIntentFilter.addAction(NOTIFY_FULL_MUSIC);
        registerReceiver(notificationBroadcast, notificationIntentFilter);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        musicService = null;
        mediaPlayer.reset();
        mediaPlayer = null;
        unregisterReceiver(notificationBroadcast);
        unregisterReceiver(noisyReceiver);
        super.onDestroy();
    }

    // method to initialize player components
    public void initMusicPlayer() {

        //set player properties
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        // mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                if (wasPlaying) {
                    toggleState();
                    wasPlaying = false;
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                if (isPlaying()) {
                    toggleState();
                    wasPlaying = true;
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (isPlaying()) {
                    toggleState();
                    wasPlaying = true;
                }
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                // if (mediaPlayer.isPlaying()) mediaPlayer.setVolume(0.1f, 0.1f);
                break;
        }
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        newNotification();
    }

    // method to set control objects
    public void setParams(LinearLayout controlLayout, ImageView controlPlayPauseButton, SimpleDraweeView controlAlbumArt, TextView controlTrackName, TextView controlArtistName) {
        this.controlLayout = controlLayout;
        this.controlPlayPauseButton = controlPlayPauseButton;
        this.controlAlbumArt = controlAlbumArt;
        this.controlTrackName = controlTrackName;
        this.controlArtistName = controlArtistName;
    }

    // method to play song
    public void playSong() {
        Runnable runnable = new Runnable() {
            public void run() {
                mediaPlayer.reset();
                try {
                    currentSong = songs.get(currentPosition);
                    Uri trackUri = currentSong.getTrackUri();
                    try {
                        mediaPlayer.setDataSource(getApplicationContext(), trackUri);
                    } catch (Exception e) {
                        // do something on error
                    }
                    mediaPlayer.prepareAsync();
                } catch (Exception e) {
                    // do something on error
                    e.printStackTrace();
                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void toggleMute() {
        if (isMute) {
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            isMute = false;
        } else {
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            isMute = true;
        }
    }

    public boolean getMuteState() {
        return isMute;
    }

    public void startSong(int position) {
        currentPosition = position;
        playSong();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public void seekTo(int position) {
        mediaPlayer.seekTo(position);
    }

    public void playPrevious() {
        if (!repeat) {
            if (shuffle) {
                currentPosition = rand.nextInt(songs.size());
            } else {
                currentPosition -= 1;
                if (currentPosition < 0) {
                    currentPosition = songs.size() - 1;
                }
            }
        }
        playSong();
    }

    public void playNext() {
        if (!repeat) {
            if (shuffle) {
                currentPosition = rand.nextInt(songs.size());
            } else {
                currentPosition += 1;
                if (currentPosition >= songs.size()) {
                    currentPosition = 0;
                }
            }
        }
        playSong();
    }

    public void toggleState() {

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            controlPlayPauseButton.setImageResource(R.drawable.ic_activity_play);
            newNotification();
        } else {
            mediaPlayer.start();
            controlPlayPauseButton.setImageResource(R.drawable.ic_activity_pause);
            newNotification();
        }

        if (isFullScreen) {
            if (mediaPlayer.isPlaying()) {
                pausePlay.setImageResource(R.drawable.ic_pause);
            } else {
                pausePlay.setImageResource(R.drawable.ic_play_arrow);
            }
        }

        if (isFromPlayList) {
            if (mediaPlayer.isPlaying()) {
                fullPlayListPlayPauseButton.setImageResource(R.drawable.ic_playlist_full_pause);
            } else {
                fullPlayListPlayPauseButton.setImageResource(R.drawable.ic_playlist_full_play_pause);
            }
        }
    }

    public void toggleShuffle() {
        shuffle = !shuffle;
    }

    public void toggleShuffle(boolean status) {
        shuffle = status;
    }

    public boolean getShuffledState() {
        return shuffle;
    }

    public void toggleRepeat() {
        repeat = !repeat;
    }

    public void toggleRepeat(boolean status) {
        repeat = status;
    }

    public boolean getRepeatState() {
        return repeat;
    }

    public int getCurrentIndex() {
        return currentPosition;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        playNext();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        controlTrackName.setText(currentSong.getTitle());
        controlArtistName.setText(currentSong.getArtist());

        AlbumArtLoader.setImage(currentSong.getAlbumArtUri(), controlAlbumArt);

        if (controlLayout.getVisibility() != View.VISIBLE) {
            controlLayout.setVisibility(View.VISIBLE);
        }

        mediaPlayer.start();
        controlPlayPauseButton.setImageResource(R.drawable.ic_activity_pause);

        // check if in full screen mode
        if (isFullScreen) {
            AlbumArtLoader.setImage(currentSong.getAlbumArtUri(), fullScreenAlbumArt);
            if (mediaPlayer.isPlaying()) {
                pausePlay.setImageResource(R.drawable.ic_pause);
            } else {
                pausePlay.setImageResource(R.drawable.ic_play_arrow);
            }
        }

        if (isFromPlayList) {
            AlbumArtLoader.setImage(currentSong.getAlbumArtUri(), playListFullAlbumArt);

            if (mediaPlayer.isPlaying()) {
                fullPlayListPlayPauseButton.setImageResource(R.drawable.ic_playlist_full_pause);
            } else {
                fullPlayListPlayPauseButton.setImageResource(R.drawable.ic_playlist_full_play_pause);
            }
        }

        newNotification();
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public ImageView getPlayPauseButton() {
        return controlPlayPauseButton;
    }

    public void setFullScreenParams(SimpleDraweeView fullMusicLayout, FloatingActionButton pausePlay) {
        isFullScreen = true;
        this.fullScreenAlbumArt = fullMusicLayout;
        this.pausePlay = pausePlay;
    }

    public void clearFullScreenParams() {
        isFullScreen = false;
        fullScreenAlbumArt = null;
        this.pausePlay = null;
    }

    public void setSongList(ArrayList<Song> songList) {
        songs = songList;
    }

    public void setSongList(ArrayList<Song> songList, int playListId) {
        songs = songList;
        this.playListId = playListId;
    }

    public void setSongListFromPlayList(ArrayList<Song> songList, boolean isFromPlayList,
                                        int playListId, SimpleDraweeView currentAlbumArt, ImageView fullPlayListPlayPauseButton) {
        songs = songList;
        this.isFromPlayList = isFromPlayList;
        this.playListId = playListId;
        this.playListFullAlbumArt = currentAlbumArt;
        this.fullPlayListPlayPauseButton = fullPlayListPlayPauseButton;
    }

    public void setSongListFromPlayList(boolean isFromPlayList, SimpleDraweeView currentAlbumArt, ImageView fullPlayListPlayPauseButton) {
        this.isFromPlayList = isFromPlayList;
        this.playListFullAlbumArt = currentAlbumArt;
        this.fullPlayListPlayPauseButton = fullPlayListPlayPauseButton;
    }

    public void clearFullPlaylistParams() {
        this.isFromPlayList = false;
        this.playListFullAlbumArt = null;
        this.fullPlayListPlayPauseButton = null;
    }

    public void setSongsFromAlbum(ArrayList<Song> songsList) {
        this.songs = songsList;
        shuffle = true;
        playNext();
    }

    public int getPlayListId() {
        return playListId;
    }

    public void setUpHandler() {
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 1000);
    }

    public void disableHandler() {
        handler.removeCallbacks(sendUpdatesToUI);
    }

    private Runnable sendUpdatesToUI = new Runnable() {
        @Override
        public void run() {
            logMediaPosition();
            handler.postDelayed(this, 1000);
        }
    };

    private void logMediaPosition() {
        if (isPlaying()) {
            int mediaPosition = getCurrentPosition();
            int mediaMax = getDuration();
            seekIntent.putExtra("counter", String.valueOf(mediaPosition));
            seekIntent.putExtra("mediaMax", String.valueOf(mediaMax));
            seekIntent.putExtra("formattedTime", TimeFormatter.getTimeString(mediaPosition));
            seekIntent.putExtra("songName", currentSong.getTitle());
            seekIntent.putExtra("artistName", currentSong.getArtist());
            seekIntent.putExtra("albumName", currentSong.getAlbum());
            sendBroadcast(seekIntent);
        }
    }

    public void removeNotification() {
        if (isPlaying()) {
            toggleState();
        }
        try {
            stopForeground(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openFullMusicScreen() {
//        Intent fullMusicScreenIntent = new Intent(getApplicationContext(), FullMusicScreen.class);
//        fullMusicScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(fullMusicScreenIntent);
        // closeNotificationBar();
    }

    private void closeNotificationBar() {
        Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        sendBroadcast(closeDialog);
    }

    @SuppressLint("NewApi")
    // @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void newNotification() {

        String songName = currentSong.getTitle();
        String artistName = currentSong.getArtist();
        Bitmap albumArt = null;

        String notificationService = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager = (NotificationManager) getSystemService(notificationService);

        RemoteViews simpleContentView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.custom_notification);

        Notification notification = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_music)
                .setContentTitle(songName).build();

        notification.contentView = simpleContentView;

        try {
            albumArt = AlbumArtLoader.getTrackCoverArt(this, currentSong.getAlbumArtUri());
            if (albumArt != null) {
                notification.contentView.setImageViewBitmap(R.id.imageViewAlbumArt, albumArt);
            } else {
                notification.contentView.setImageViewResource(R.id.imageViewAlbumArt, R.drawable.default_art);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!isPlaying()) {
            notification.contentView.setViewVisibility(R.id.btnPause, View.GONE);
            notification.contentView.setViewVisibility(R.id.btnPlay, View.VISIBLE);
        } else {
            notification.contentView.setViewVisibility(R.id.btnPause, View.VISIBLE);
            notification.contentView.setViewVisibility(R.id.btnPlay, View.GONE);
        }

        notification.contentView.setTextViewText(R.id.textSongName, songName);
        notification.contentView.setTextViewText(R.id.textAlbumName, artistName);


        Intent pausePlayIntent = new Intent(NOTIFY_PAUSE_PLAY);
        PendingIntent pendingPausePlayIntent = PendingIntent.getBroadcast(this, 100, pausePlayIntent, 0);
        simpleContentView.setOnClickPendingIntent(R.id.btnPause, pendingPausePlayIntent);
        simpleContentView.setOnClickPendingIntent(R.id.btnPlay, pendingPausePlayIntent);

        Intent nextIntent = new Intent(NOTIFY_NEXT);
        PendingIntent pendingNextIntent = PendingIntent.getBroadcast(this, 100, nextIntent, 0);
        simpleContentView.setOnClickPendingIntent(R.id.btnNext, pendingNextIntent);

        Intent previousIntent = new Intent(NOTIFY_PREV);
        PendingIntent pendingPreviousIntent = PendingIntent.getBroadcast(this, 100, previousIntent, 0);
        simpleContentView.setOnClickPendingIntent(R.id.btnPrev, pendingPreviousIntent);

        Intent closeIntent = new Intent(NOTIFY_CLOSE);
        PendingIntent closePendingIntent = PendingIntent.getBroadcast(this, 100, closeIntent, 0);
        simpleContentView.setOnClickPendingIntent(R.id.btnStop, closePendingIntent);

        Intent openFullScreenIntent = new Intent(NOTIFY_FULL_MUSIC);
        PendingIntent fullMusicScreenPendingIntent = PendingIntent.getBroadcast(this, 100, openFullScreenIntent, 0);
        simpleContentView.setOnClickPendingIntent(R.id.imageViewAlbumArt, fullMusicScreenPendingIntent);

        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        startForeground(NOTIFICATION_ID, notification);
    }
}
