package app.nepaliapp.mblfree.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.HttpDataSource;
import androidx.media3.exoplayer.DefaultLoadControl;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.nepaliapp.mblfree.R;
import app.nepaliapp.mblfree.common.CustomHttpDataSourceFactory;
import app.nepaliapp.mblfree.common.StorageClass;
import app.nepaliapp.mblfree.recyclerAdapter.VideoCardAdapter;

@UnstableApi
public class VideoPlayingActivity extends AppCompatActivity {

    private PlayerView playerView;
    private TextView videoTitle;
    private ExoPlayer player;
    private boolean isFullScreen = false;
    private RecyclerView otherVideoRecycler;
    private VideoCardAdapter adapter;
    private JSONArray otherVideos = new JSONArray();
    private String currentVideoUrl;
    private JSONArray allVideos= new JSONArray();    // store all videos initially

    @UnstableApi
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_playing);

        // Views
        playerView = findViewById(R.id.player_view);
        videoTitle = findViewById(R.id.titleOfTheVideo);
        otherVideoRecycler = findViewById(R.id.recyclerViewOtherVideos);

        videoTitle.setText("Loading...");
        playerView.setControllerShowTimeoutMs(5000);
        playerView.setFullscreenButtonClickListener(isFull -> toggleFullScreen());

        otherVideoRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // Get intent extras
        String videoUrl = getIntent().getStringExtra("videoUrl");
        String videoTitleStr = getIntent().getStringExtra("videoTitle");
        currentVideoUrl = videoUrl;

        if (videoTitleStr != null) videoTitle.setText(videoTitleStr);

        // Initialize player with the main video
        if (videoUrl != null) initializePlayer(videoUrl);

        // Setup other videos
        String jsonData = getIntent().getStringExtra("videoListJson");
        if (jsonData != null) {
            try {
                allVideos = new JSONArray(jsonData); // assign to class field
                otherVideos = new JSONArray();

                // populate otherVideos with all except currently playing
                for (int i = 0; i < allVideos.length(); i++) {
                    JSONObject obj = allVideos.optJSONObject(i);
                    if (!obj.optString("link").equals(currentVideoUrl)) {
                        otherVideos.put(obj);
                    }
                }

                setupRecyclerView();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void setupRecyclerView() {
        adapter = new VideoCardAdapter(this, otherVideos, video -> {
            try {
                // Get clicked video details
                String newUrl = video.optString("link");
                String newTitle = video.optString("title");

                // Update player
                videoTitle.setText(newTitle);
                currentVideoUrl = newUrl;
                playVideo(newUrl);

                // Rebuild RecyclerView list after video change
                rebuildOtherVideosList();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Initially populate RecyclerView with videos except currently playing
        rebuildOtherVideosList();
        otherVideoRecycler.setAdapter(adapter);
    }

    private void rebuildOtherVideosList() {
        try {
            JSONArray updatedList = new JSONArray();
            for (int i = 0; i < allVideos.length(); i++) {
                JSONObject obj = allVideos.getJSONObject(i);
                // Exclude the currently playing video
                if (!obj.optString("link").equals(currentVideoUrl)) {
                    updatedList.put(obj);
                }
            }
            otherVideos = updatedList;
            if (adapter != null) {
                adapter.updateData(updatedList);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @UnstableApi
    private void initializePlayer(String videoUrl) {
        if (player != null) player.release();

        StorageClass tokenStore = new StorageClass(getApplicationContext());
        String jwtToken = tokenStore.getJwtToken();
        HttpDataSource.Factory dataSourceFactory = new CustomHttpDataSourceFactory(jwtToken);

        DefaultLoadControl loadControl = new DefaultLoadControl.Builder()
                .setBufferDurationsMs(
                        3000,  // min buffer
                        5000,  // max buffer
                        1500,  // buffer to start playback
                        2000   // buffer after rebuffer
                )
                .setPrioritizeTimeOverSizeThresholds(true)
                .build();

        player = new ExoPlayer.Builder(this)
                .setMediaSourceFactory(new DefaultMediaSourceFactory(dataSourceFactory))

                .setLoadControl(loadControl)
                .build();

        playerView.setPlayer(player);
        playVideo(videoUrl);
    }

    private void playVideo(String videoUrl) {
        if (player == null) return;
        MediaItem mediaItem = MediaItem.fromUri(videoUrl);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
    }

    private void toggleFullScreen() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
        if (isFullScreen) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            if (getSupportActionBar() != null) getSupportActionBar().show();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = (int) (200 * getResources().getDisplayMetrics().density);
            isFullScreen = false;
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            if (getSupportActionBar() != null) getSupportActionBar().hide();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            isFullScreen = true;
        }
        playerView.setLayoutParams(params);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.setPlayWhenReady(false);
            player.pause();
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
