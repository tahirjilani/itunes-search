package com.tj.itunessearch.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import com.tj.itunessearch.App;
import com.tj.itunessearch.dataModel.Track;
import com.tj.itunessearch.databinding.VideoPlayerActivityBinding;

import com.tj.itunessearch.R;
import com.tj.itunessearch.util.Utils;

public class VideoPlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    private static final String KEY_URL = "url";

    public static Intent createIntent(Context context, Track track){

        Intent intent = new Intent(context, VideoPlayerActivity.class);
        intent.putExtra(KEY_URL, track.getPreviewUrl());
        return intent;
    }

    private VideoPlayerActivityBinding binding;
    private Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        App.getInstance().getAppComponent().inject(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_video_player);

        binding.videoView.setOnCompletionListener(this);
        binding.videoView.setOnInfoListener(onInfoToPlayStateListener);

        binding.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                mp.setLooping(true);
                binding.durationTextView.setVisibility(View.VISIBLE);
                handler.postDelayed(timerRunnable, 1000);
            }
        });

        binding.videoView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (((VideoView) v).isPlaying()) {
                    ((VideoView) v).pause();
                    binding.playImageView.setVisibility(View.VISIBLE);
                } else {
                    ((VideoView) v).start();
                    binding.playImageView.setVisibility(View.GONE);
                }
            }
            return true;
        });

        Bundle extras = getIntent().getExtras();
        String videoPath = extras.getString(KEY_URL);

        binding.videoView.setVideoURI(Uri.parse(videoPath) );
        binding.videoView.start();
    }

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            int duration = binding.videoView.getDuration();
            int currentTime = binding.videoView.getCurrentPosition();

            String time = Utils.getDuration(currentTime) + "/" + Utils.getDuration(duration);
            binding.durationTextView.setText(time);

            handler.postDelayed(this, 1000);
        }
    };


    private final MediaPlayer.OnInfoListener onInfoToPlayStateListener = (mp, what, extra) -> {
       switch (what) {
            case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START: {
                binding.progressBar.setVisibility(View.GONE);
                binding.playImageView.setEnabled(true);
                return true;
            }
        }
        return false;
    };

    public void stopPlaying() {
        binding.videoView.stopPlayback();
        this.finish();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //finish();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
