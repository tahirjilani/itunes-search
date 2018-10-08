package com.tj.itunessearch.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.tj.itunessearch.App;
import com.tj.itunessearch.R;
import com.tj.itunessearch.activity.SearchActivity;
import com.tj.itunessearch.dataModel.Track;
import com.tj.itunessearch.repository.SearchRepository;

import java.io.IOException;

import javax.inject.Inject;

import static com.tj.itunessearch.service.Constants.NOTIFICATION_ID.CHANNEL_ID;

public class AudioPlayerService extends Service implements MediaPlayer.OnPreparedListener , MediaPlayer.OnErrorListener{

    private final String TAG = "AudioPlayerService";

    @Inject
    SearchRepository repository;

    private static MutableLiveData<PlayingTrack> currentlyPlayingTrackData = new MutableLiveData<>();

    private Track currentlyPlayedTrack;
    private MediaPlayer mediaPlayer;
    private WifiManager.WifiLock wifiLock;


    public static MutableLiveData<PlayingTrack> getCurrentlyPlayingTrackLiveData(){
        return currentlyPlayingTrackData;
    }

    private static AudioPlayerService instance = null;
    public static AudioPlayerService getInstance(){
        return instance;
    }

    public AudioPlayerService() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null){
            mediaPlayer.release();
        }
        currentlyPlayingTrackData.postValue(null);
        instance = null;
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        App.getInstance().getAppComponent().inject(this);
    }

    private Notification getNotification(Track track){

        Intent notificationIntent = new Intent(this, AudioPlayerService.class);
        notificationIntent.setAction(Constants.ACTION.NOTIFICATION_CLICKED_ACTION);
        //notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        //notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, notificationIntent, 0);

        Intent previousIntent = new Intent(this, AudioPlayerService.class);
        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0, previousIntent, 0);

        Intent playIntent = new Intent(this, AudioPlayerService.class);
        playIntent.setAction(Constants.ACTION.PLAY_PAUSE_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0, playIntent, 0);

        Intent nextIntent = new Intent(this, AudioPlayerService.class);
        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0, nextIntent, 0);

        Intent deleteIntent = new Intent(this, AudioPlayerService.class);
        deleteIntent.setAction(Constants.ACTION.STOP_ACTION);
        PendingIntent deletePendingIntent = PendingIntent.getService(this,
                0,
                deleteIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.notification_icon);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "tj_channel_01";
            String description = "Audio player channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        int playPauseIcon = android.R.drawable.ic_media_play;
        if (mediaPlayer != null && mediaPlayer.isPlaying()){
            playPauseIcon = android.R.drawable.ic_media_pause;
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(deletePendingIntent))

                .setContentTitle(track.getTrackName() != null ? track.getTrackName() : getString(R.string.app_name))
                .setTicker(track.getArtistName() != null ? track.getArtistName() : getString(R.string.unkown_artist))
                .setContentText(track.getCollectionName() != null ? track.getCollectionName() : getString(R.string.unkown_collection))

                .setSmallIcon(R.drawable.notification_icon)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))

                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setShowWhen(false)
                .setContentIntent(pendingIntent)
                .setDeleteIntent(deletePendingIntent)

                .addAction(android.R.drawable.ic_media_previous, "Previous", ppreviousIntent)
                .addAction(playPauseIcon, "Play", pplayIntent)
                .addAction(android.R.drawable.ic_media_next, "Next", pnextIntent)

                .build();

        return notification;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {

            Bundle bundle = intent.getExtras();
            if (intent.getAction().equals(Constants.ACTION.NOTIFICATION_CLICKED_ACTION)) {

                if (SearchActivity.instance == null){

                    Intent intent1 = new Intent(this, SearchActivity.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent1.setAction(Constants.ACTION.NOTIFICATION_CLICKED_ACTION);
                    startActivity(intent1);
                }

            }else if (intent.getAction().equals(Constants.ACTION.START_ACTION)) {

                int trackId = bundle.getInt("trackId");
                Track track = repository.getTrack(trackId);

                playTrack(track);

            }else if (intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {
                Log.i(TAG, "Clicked Previous");

                playPreviousTrack();

            } else if (intent.getAction().equals(Constants.ACTION.PLAY_PAUSE_ACTION)) {
                Log.i(TAG, "Clicked Play");
                playPause();

            } else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {
                Log.i(TAG, "Clicked Next");
                playNextTrack();
            }else if (intent.getAction().equals(Constants.ACTION.STOP_ACTION)) {
                // Stop the service
                stopForeground(true);
                stopSelf();
            }
        }
        return START_STICKY;
    }

    public void stopForeground(){

        this.stopForeground(false);
    }

    public void stopServiceAndClearNotification(){

        stopForeground(true);
        stopSelf();
    }


    private void playTrack(Track track){
        if (track != null) {
            currentlyPlayedTrack = track;

            //play audio
            playAudioFromUrl(track.getPreviewUrl());

            //update notification
            updateNotification(track);

            currentlyPlayingTrackData.postValue(new PlayingTrack(track, true));
        }
    }

    private void updateNotification(Track track){

        Notification notification = getNotification(track);
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
    }

    private void playPause(){

        if (mediaPlayer != null){
            if (mediaPlayer.isPlaying()){
                mediaPlayer.pause();
            }else{
                mediaPlayer.start();
            }
            currentlyPlayingTrackData.postValue(
                    new PlayingTrack(currentlyPlayedTrack, mediaPlayer.isPlaying()));
            updateNotification(currentlyPlayedTrack);
        }
    }

    private void playNextTrack(){
        Track nextTrack = repository.getNextAudioTrack(currentlyPlayedTrack);
        playTrack(nextTrack);
    }

    private void playPreviousTrack(){
        Track previousAudioTrack = repository.getPreviousAudioTrack(currentlyPlayedTrack);
        playTrack(previousAudioTrack);
    }

    private void playAudioFromUrl(String previewUrl){

        if (mediaPlayer != null){
            mediaPlayer.reset();
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mediaPlayer.setDataSource(previewUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.prepareAsync(); // might take long! (for buffering, etc)

    }

    @Override
    public void onPrepared(MediaPlayer mp) {

        /** Called when MediaPlayer is ready */
        mp.start();
        wakeLock();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        releaseWakeLock();
        mp.reset();
        return false;
    }


    private void wakeLock(){
        if (wifiLock == null) {
            wifiLock = ((WifiManager) this.getSystemService(Context.WIFI_SERVICE))
                    .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");
        }
        wifiLock.acquire();
    }

    private void releaseWakeLock(){
        wifiLock.release();
    }
}
