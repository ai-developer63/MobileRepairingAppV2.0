package app.nepaliapp.mblfree.recyclerAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.HttpDataSource;
import androidx.media3.exoplayer.DefaultLoadControl;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import org.json.JSONArray;
import org.json.JSONObject;
import app.nepaliapp.mblfree.R;
import app.nepaliapp.mblfree.common.CustomHttpDataSourceFactory;
import app.nepaliapp.mblfree.common.StorageClass;


public class CourseSetterAdapter extends RecyclerView.Adapter<CourseSetterAdapter.ViewHolder> {
    Context context;
    JSONArray array;
    private ExoPlayer player;
    private boolean isFullScreen;// Shared player inside adapter
    private FrameLayout fullscreenContainer;
    private int originalIndex;
    private ViewGroup originalParent;
    private ExoPlayer currentPlayer = null;
    private PlayerView currentPlayerView = null;
    public int currentlyPlayingPosition = -1;

    public CourseSetterAdapter(Context context, JSONArray array) {
        this.context = context;
        this.array = array;
        this.isFullScreen  = false;
        Activity activity = (Activity) context;
        fullscreenContainer = activity.findViewById(R.id.fullscreen_container);
            }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.course_model, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        JSONObject object = array.optJSONObject(position);
        String type = object.optString("type");

        if (type.equalsIgnoreCase("text")) {
            if (position == 0) {
                holder.textContent.setGravity(Gravity.CENTER);
            }
            holder.textContent.setVisibility(View.VISIBLE);
            holder.textContent.setText(object.optString("value"));

        } else if (type.equalsIgnoreCase("image")) {
            holder.imageContent.setVisibility(View.VISIBLE);
            Glide.with(holder.imageContent.getContext())
                    .load(object.optString("value"))
                    .error(R.mipmap.ic_launcher)
                    .into(holder.imageContent);

        } else if (type.equalsIgnoreCase("video")) {
            if (position == currentlyPlayingPosition && player != null) {
                holder.videoContent.setPlayer(player);
                holder.videoThumbnail.setVisibility(View.GONE);
            } else {
                holder.videoContent.setPlayer(null);
                holder.videoThumbnail.setVisibility(View.VISIBLE);
            }

            holder.frameLayout.setVisibility(View.VISIBLE);
            holder.videoContent.setVisibility(View.VISIBLE);
            holder.videoThumbnail.setVisibility(View.VISIBLE);

            JSONArray videoArray = object.optJSONArray("value");
            if (videoArray != null && videoArray.length() > 0) {
                JSONObject videoObj = videoArray.optJSONObject(0);
                String videoUrl = videoObj.optString("videoUrl");
                String thumbnailUrl = videoObj.optString("videoThumnail");

                // Load thumbnail
                Glide.with(context)
                        .load(thumbnailUrl)
                        .placeholder(R.mipmap.ic_launcher)
                        .centerInside()
                        .into(holder.videoThumbnail);

                // On thumbnail click, hide thumbnail & play video
                holder.videoThumbnail.setOnClickListener(v -> {
                    holder.videoThumbnail.setVisibility(View.GONE);
                    initializePlayer(videoUrl, holder.videoContent);
                    currentlyPlayingPosition = position;
                });


            }
        }
    }

    @Override
    public int getItemCount() {
        return array.length();
    }

    // Initialize and play video inside adapter
    @OptIn(markerClass = UnstableApi.class)
    private void initializePlayer(String videoUrl, PlayerView playerView) {
        if (player != null) {
            player.release();
        }
        if (currentPlayer != null && currentPlayer != player) {
            currentPlayer.stop();
            currentPlayer.release();
            if (currentPlayerView != null) {
                currentPlayerView.setPlayer(null);
            }
        }

     playerView.setFullscreenButtonClickListener(isFull -> toggleFullScreen(playerView));
        StorageClass tokenStore = new StorageClass(context);
        String jwtToken = tokenStore.getJwtToken();
        HttpDataSource.Factory dataSourceFactory = new CustomHttpDataSourceFactory(jwtToken);

        DefaultLoadControl loadControl = new DefaultLoadControl.Builder()
                .setBufferDurationsMs(3000, 5000, 1500, 2000)
                .setPrioritizeTimeOverSizeThresholds(true)
                .build();

        player = new ExoPlayer.Builder(context)
                .setMediaSourceFactory(new DefaultMediaSourceFactory(dataSourceFactory))
                .setLoadControl(loadControl)
                .build();

        playerView.setPlayer(player);

        MediaItem mediaItem = MediaItem.fromUri(videoUrl);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
    }


    private void toggleFullScreen(PlayerView playerView) {
        Activity activity = (Activity) context;

        if (!isFullScreen) {
            // Save original parent and index position
            originalParent = (ViewGroup) playerView.getParent();
            originalIndex = originalParent.indexOfChild(playerView);

            // Move to fullscreen container
            originalParent.removeView(playerView);
            fullscreenContainer.addView(playerView);
            fullscreenContainer.setVisibility(View.VISIBLE);

            // Hide system UI
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            isFullScreen = true;

        } else {
            // Move back to original parent
            fullscreenContainer.removeView(playerView);
            fullscreenContainer.setVisibility(View.GONE);

            if (originalParent != null) {
                originalParent.addView(playerView, originalIndex);
            }

            // Restore system UI
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

            isFullScreen = false;
        }
    }


    // Optional: release player if ViewHolder is recycled
    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder.videoContent.getPlayer() != null) {
            holder.videoContent.getPlayer().pause();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textContent;
        ImageView imageContent;
        PlayerView videoContent;
        ImageView videoThumbnail;
        FrameLayout frameLayout;

        ViewHolder(View itemView) {
            super(itemView);
            textContent = itemView.findViewById(R.id.textContent);
            imageContent = itemView.findViewById(R.id.imageContent);
            videoContent = itemView.findViewById(R.id.player_view);
            videoThumbnail = itemView.findViewById(R.id.videoThumbnail);
            frameLayout = itemView.findViewById(R.id.wholeFrame);
        }
    }
    public void releasePlayer() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.pause();
            player.release();
            player = null;
            currentlyPlayingPosition = -1;
        }
    }
}
