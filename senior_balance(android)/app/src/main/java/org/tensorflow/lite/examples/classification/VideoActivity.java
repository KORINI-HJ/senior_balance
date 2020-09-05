package org.tensorflow.lite.examples.classification;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;

import org.tensorflow.lite.examples.classification.tflite.Classifier;


public class VideoActivity extends AppCompatActivity {
    int video_model_list[] = {R.raw.sit_video, R.raw.sit_video2, R.raw.stand_video, R.raw.stand_video2, R.raw.lay_video, R.raw.lay_video2, R.raw.tool_video, R.raw.tool_video2};

    PlayerView playerView;
    PlayerControlView playerControlView;

    SimpleExoPlayer exoPlayer;
    String url;
    String str_model;
    int rawVedieo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        playerView = findViewById(R.id.exo_view);
        playerControlView = findViewById(R.id.exo_control_view);
        //url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";

        str_model = getIntent().getExtras().getString("model");
        int model = 0;
        int set_number = getIntent().getExtras().getInt("set");

        switch(str_model)
        {
            case "sit":
                model = 0;
                break;
            case "stand":
                model = 1;
                break;
            case "lay":
                model = 2;
                break;
            case "tool":
                model = 3;
                break;
        }

        int model_number = 2*model + set_number;
        if(model_number < video_model_list.length)
            rawVedieo = video_model_list[model_number];
        else
            rawVedieo = video_model_list[0];
    }

    @Override
    protected  void onStart(){
        super.onStart();
        exoPlayer = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
        playerView.setPlayer(exoPlayer);
        playerControlView.setPlayer(exoPlayer);

        DataSource.Factory dataFactory = new DefaultDataSourceFactory(this, "Tester");
        //ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(dataFactory).createMediaSource(Uri.parse(url));
        ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(dataFactory).createMediaSource(RawResourceDataSource.buildRawResourceUri(rawVedieo));

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
        Intent intent = new Intent(this, ClassifierActivity.class);
        intent.putExtra("model", str_model);
        startActivity(intent);
        finish();
    }
}
