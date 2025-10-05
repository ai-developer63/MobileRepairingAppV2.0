package app.nepaliapp.mblfree.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.HttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.ui.PlayerView;
import androidx.media3.exoplayer.DefaultLoadControl;
import java.util.HashMap;
import java.util.Map;

import app.nepaliapp.mblfree.R;
import app.nepaliapp.mblfree.common.CustomHttpDataSourceFactory;
import app.nepaliapp.mblfree.common.StorageClass;

@UnstableApi
public class VideoPlayingActivity extends AppCompatActivity {
    private PlayerView playerView;
    private TextView videoTitle;
    private ExoPlayer player;
    private boolean isFullScreen = false;

    @UnstableApi
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_playing);

        playerView = findViewById(R.id.player_view);
        videoTitle = findViewById(R.id.titleOfTheVideo);
        videoTitle.setText("Loading...");

        playerView.setControllerShowTimeoutMs(5000);
        playerView.setFullscreenButtonClickListener(isFull -> toggleFullScreen());

        // Get video URL and title from intent
        String videoUrl = getIntent().getStringExtra("videoUrl");
        String title = getIntent().getStringExtra("videoTitle");

        if (videoUrl != null) {
            videoTitle.setText(title != null ? title : "Video");
            initializePlayer(videoUrl);
        }
    }

    // In your VideoPlayingActivity
    @UnstableApi
    private void initializePlayer(String videoUrl) {
        if (player != null) player.release();

        StorageClass tokenStore = new StorageClass(getApplicationContext());
        String jwtToken = tokenStore.getJwtToken();

        HttpDataSource.Factory dataSourceFactory = new CustomHttpDataSourceFactory(jwtToken);

        // Configure LoadControl
        long minBufferMs = 3_000; // 5 seconds minimum
        long maxBufferMs = 10_000; // 50 seconds maximum preload
        long bufferForPlaybackMs = 1_500; // start playback after ~1.5s buffered
        long bufferForPlaybackAfterRebufferMs = 2_000; // resume after stall

        DefaultLoadControl loadControl = new DefaultLoadControl.Builder()
                .setBufferDurationsMs((int) minBufferMs,
                        (int) maxBufferMs,
                        (int) bufferForPlaybackMs,
                        (int) bufferForPlaybackAfterRebufferMs)
                .setPrioritizeTimeOverSizeThresholds(true)
                .build();

        player = new ExoPlayer.Builder(this)
                .setMediaSourceFactory(new DefaultMediaSourceFactory(dataSourceFactory))
                .setLoadControl(loadControl)
                .build();

        playerView.setPlayer(player);

        // Add video
        MediaItem mediaItem = MediaItem.fromUri(videoUrl);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
    }

    private void toggleFullScreen() {
        if (isFullScreen) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            if (getSupportActionBar() != null) getSupportActionBar().show();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = (int) (200 * getResources().getDisplayMetrics().density);
            playerView.setLayoutParams(params);
            isFullScreen = false;
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            );
            if (getSupportActionBar() != null) getSupportActionBar().hide();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            playerView.setLayoutParams(params);
            isFullScreen = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
