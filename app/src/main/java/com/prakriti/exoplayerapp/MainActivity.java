package com.prakriti.exoplayerapp;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {
// create exoplayer for playing media
// create playlist of song files
// create listener for exoplayer
// work with exoplayer UI
// external library for user permission to download audio to storage -> physical device

    private static final String TAG = "MainActivity";
    private SimpleExoPlayer simpleExoPlayer;
    private PlayerView myPlayerView;
    private ProgressBar myProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate called");
        setContentView(R.layout.activity_main);

        myProgressBar = findViewById(R.id.myProgressBar);
        myPlayerView = findViewById(R.id.myPlayerView);
        simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();

        // init fab for download music
        FloatingActionButton myFab = findViewById(R.id.myFab);
        myFab.setOnClickListener(v -> setupPermissions());

        initializePlayer();
    }

    private void initializePlayer() {
        // create media item with url of media/song file
//        String songUrl = "https://opengameart.org/sites/default/files/Rise%20of%20spirit.mp3";
        // create playlist using array of urls

        String[] songUrlList = new String[]{
                "https://opengameart.org/sites/default/files/Rise%20of%20spirit.mp3",
                "https://opengameart.org/sites/default/files/Cyberpunk%20Moonlight%20Sonata_0.mp3",
                "https://opengameart.org/sites/default/files/Path%20to%20Lake%20Land.ogg",
                "https://opengameart.org/sites/default/files/Caketown%201.mp3",
                "https://opengameart.org/sites/default/files/song18_0.mp3",
                "https://opengameart.org/sites/default/files/Orbital%20Colossus_0.mp3"
        };
        myPlayerView.setPlayer(simpleExoPlayer);

        for (String url : songUrlList) { // add list or urls
            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(url)); // returns uri
            simpleExoPlayer.addMediaItem(mediaItem); // add one by one to player
        }
//        simpleExoPlayer.setMediaItem(mediaItem);
        simpleExoPlayer.prepare(); // prepare before playing
        simpleExoPlayer.play();

        // initialise listener after calling play()
        Player.Listener listener = new Player.Listener() { // EventListener is deprecated
            @Override
            public void onPlaybackStateChanged(int state) {
                Log.i(TAG, "onPlaybackStateChanged called");
                if (state == Player.STATE_BUFFERING) {
                    // when song is loading
                    myProgressBar.setVisibility(View.VISIBLE);
                } else if (state == Player.STATE_READY) {
                    myProgressBar.setVisibility(View.GONE);
                }
                //else if(state == Player.EVENT_PLAYBACK_STATE_CHANGED)
            }
        };
        simpleExoPlayer.addListener(listener);
    }

    private void releasePlayer() {
        // release player memory
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
    }

    private void setupPermissions() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                // if given, download current music file
                if (report.areAllPermissionsGranted()) {
                    try {
                        if(simpleExoPlayer != null) { // insert null check for timeline
                            downloadCurrentMusicFile(simpleExoPlayer.getCurrentMediaItem().playbackProperties.uri.toString());
                        }
                    } catch (NullPointerException n) {
                        Toast.makeText(MainActivity.this, R.string.null_error, Toast.LENGTH_SHORT).show();
                        n.printStackTrace();
                    }
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest(); // will continue asking user for permission
            }
        })
                .check();
    }

    private void downloadCurrentMusicFile(String musicUrl) {
        Log.i(TAG, "downloadCurrentMusicFile called");
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE); // dl form internet or server
        Uri uri = Uri.parse(musicUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        String musicName = musicUrl.substring(musicUrl.lastIndexOf("/") + 1).replace("%20", " "); // last '/' onwards till end
        request.setTitle(musicName);
        request.setVisibleInDownloadsUi(true);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, uri.getLastPathSegment()); // store dl here
        downloadManager.enqueue(request); // start downloading when ready
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy called");
        releasePlayer();
    }

}