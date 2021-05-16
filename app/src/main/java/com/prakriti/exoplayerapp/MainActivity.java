package com.prakriti.exoplayerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ui.PlayerView;

public class MainActivity extends AppCompatActivity {
// create exoplayer for playing media
// create playlist of song files
// create listener for exoplayer
// work with exoplayer UI

    private SimpleExoPlayer simpleExoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ProgressBar myProgressBar = findViewById(R.id.myProgressBar);

        // initialise
        PlayerView myPlayerView = findViewById(R.id.myPlayerView);
        simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();

        // create media item with url of media/song file
//        String songUrl = "https://opengameart.org/sites/default/files/Rise%20of%20spirit.mp3";
        // create playlist using array of urls
        String songUrlList[] = new String[] {
                "https://opengameart.org/sites/default/files/Rise%20of%20spirit.mp3",
                "https://opengameart.org/sites/default/files/Cyberpunk%20Moonlight%20Sonata_0.mp3",
                "https://opengameart.org/sites/default/files/Path%20to%20Lake%20Land.ogg",
                "https://opengameart.org/sites/default/files/Caketown%201.mp3",
                "https://opengameart.org/sites/default/files/song18_0.mp3",
                "https://opengameart.org/sites/default/files/Orbital%20Colossus_0.mp3"
        };
        myPlayerView.setPlayer(simpleExoPlayer);

        for(String url : songUrlList) { // add list or urls
            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(url)); // returns uri
            simpleExoPlayer.addMediaItem(mediaItem); // add one by one to player
        }
//        simpleExoPlayer.setMediaItem(mediaItem);
        simpleExoPlayer.prepare(); // prepare before playing
        simpleExoPlayer.play();

        // initialise listener after calling play()
        Player.EventListener eventListener = new Player.EventListener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if(state == Player.STATE_BUFFERING) {
                    // when song is loading
                    myProgressBar.setVisibility(View.VISIBLE);
                }
                else if(state == Player.STATE_READY) {
                    myProgressBar.setVisibility(View.GONE);
                }
            }
        };
        simpleExoPlayer.addListener(eventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // release player memory
        if(simpleExoPlayer != null) {
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
    }
}