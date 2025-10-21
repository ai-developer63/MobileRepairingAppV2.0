package app.nepaliapp.mblfree.fragments.workshop;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.OptIn;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.HttpDataSource;
import androidx.media3.exoplayer.DefaultLoadControl;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.nepaliapp.mblfree.R;
import app.nepaliapp.mblfree.common.CommonFunctions;
import app.nepaliapp.mblfree.common.CustomHttpDataSourceFactory;
import app.nepaliapp.mblfree.common.MySingleton;
import app.nepaliapp.mblfree.common.StorageClass;
import app.nepaliapp.mblfree.common.Url;
import app.nepaliapp.mblfree.recyclerAdapter.StepsAddingAdapter;

public class WorkshopPracticalFragment extends Fragment {
    RecyclerView recyclerView;
    RequestQueue requestQueue;
    PlayerView playerView;
    StorageClass storageClass;
    Url url;
    TextView topicText;
    CommonFunctions commonFunctions;
    private boolean isFullScreen = false;
    private ExoPlayer player;

    public WorkshopPracticalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workshop_practical, container, false);
        init(view);
        if (getArguments() != null) {
            String companyName = getArguments().getString("companyName");
            String modelName = getArguments().getString("modelName");
            String topicName = getArguments().getString("topicName");
            setupPracticalFragment(companyName, modelName, topicName);

            OnBackPressedCallback callback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    changeFragment(companyName, modelName);
                }
            };

            requireActivity().getOnBackPressedDispatcher().addCallback(
                    getViewLifecycleOwner(),
                    callback
            );
        }
        return view;
    }

    private void init(View view) {
        //Initialization
        requestQueue = MySingleton.getInstance(requireContext()).getRequestQueue();
        url = new Url();
        storageClass = new StorageClass(requireContext());
        commonFunctions = new CommonFunctions();
        //find by ids
        recyclerView = view.findViewById(R.id.recyclerviewForPractical);
        playerView = view.findViewById(R.id.practicalVideoPlayer);
        topicText = view.findViewById(R.id.topicHead);
    }

    private void setupPracticalFragment(String companyName, String modelName, String topicName) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url.getRequestWorkShopSteps(), getObjectForSteps(companyName, modelName, topicName), new Response.Listener<JSONObject>() {
            @OptIn(markerClass = UnstableApi.class)
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (!isAdded()) {
                    return;
                }
                topicText.setText(jsonObject.optString("topicName") + " of " + jsonObject.optString("ModelName"));
                String videoUrl = jsonObject.optString("videoLink");
                initializePlayer(videoUrl);
                JSONArray array = jsonObject.optJSONArray("steps");
                StepsAddingAdapter adapter = new StepsAddingAdapter(requireContext(), array);
                recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                recyclerView.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (!isAdded()) return;
                commonFunctions.handleErrorResponse(requireContext(), volleyError);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + storageClass.getJwtToken());
                return headers;
            }
        };
        requestQueue.add(request);


    }


    private JSONObject getObjectForSteps(String companyName, String modelName, String topicName) {
        JSONObject object = new JSONObject();
        try {
            object.put("companyName", companyName);
            object.put("modelName", modelName);
            object.put("topicName", topicName);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        Log.d("Required data", object.toString());
        return object;
    }


    @UnstableApi
    private void initializePlayer(String videoUrl) {
        if (player != null) player.release();

        StorageClass tokenStore = new StorageClass(requireContext());
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

        player = new ExoPlayer.Builder(requireContext())
                .setMediaSourceFactory(new DefaultMediaSourceFactory(dataSourceFactory))
                .setLoadControl(loadControl)
                .build();

        playerView.setPlayer(player);

        // Add video
        MediaItem mediaItem = MediaItem.fromUri(videoUrl);
        player.setMediaItem(mediaItem);
        player.prepare();
        playerView.setControllerAutoShow(false);
        playerView.setControllerShowTimeoutMs(2000);
        player.play();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopPlayer();
    }

    private void stopPlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    private void changeFragment(String companyName, String modelName) {
        WorkshopTopicsFragment workshopTopicsFragment = new WorkshopTopicsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("companyName", companyName);
        bundle.putString("modelName", modelName);
        workshopTopicsFragment.setArguments(bundle);
        FragmentTransaction transaction = ((FragmentActivity) requireContext()).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayoutInMain, workshopTopicsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}