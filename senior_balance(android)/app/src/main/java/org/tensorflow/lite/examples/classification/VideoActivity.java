package org.tensorflow.lite.examples.classification;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;


public class VideoActivity extends AppCompatActivity {
    PlayerView playerView;
    SimpleExoPlayer exoPlayer;

    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        playerView = findViewById(R.id.exo_view);
        url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";
    }

    @Override
    protected  void onStart(){
        super.onStart();
        exoPlayer = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
        playerView.setPlayer(exoPlayer);

        DataSource.Factory dataFactory = new DefaultDataSourceFactory(this, "Tester");
        ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(dataFactory).createMediaSource(Uri.parse(url));

        exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(true);
    }

    @Override
    protected void onStop(){
        super.onStop();

        playerView.setPlayer(null);
        exoPlayer.release();
        exoPlayer = null;
    }

    public void clickFinish(View view)
    {
        finish();
    }
}
