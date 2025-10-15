package app.nepaliapp.mblfree.fragments.servicefragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.nepaliapp.mblfree.R;
import app.nepaliapp.mblfree.common.CommonFunctions;
import app.nepaliapp.mblfree.common.MySingleton;
import app.nepaliapp.mblfree.common.StorageClass;
import app.nepaliapp.mblfree.common.Url;
import app.nepaliapp.mblfree.recyclerAdapter.FragmentVideoCardAdapter;

public class AppTutorialVideosFragment extends Fragment {
    Url url;
    RequestQueue requestQueue;
    RecyclerView  videoRecyclerView;
    StorageClass storageClass;
    FrameLayout loadingOverlay;
    CommonFunctions commonFunctions;

    public AppTutorialVideosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_tutorial_videos, container, false);
        init(view);
        videoRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        loadingOverlay.setVisibility(View.VISIBLE);
        requestTutorialVideos();
        return view;
    }
    private void init(View view) {
        loadingOverlay = view.findViewById(R.id.loadingOverlay);
        videoRecyclerView = view.findViewById(R.id.homeVideoRecycler);
        //initialization
        url = new Url();
        requestQueue = MySingleton.getInstance(requireContext()).getRequestQueue();
        storageClass = new StorageClass(requireContext());
        commonFunctions = new CommonFunctions();
    }
    private void requestTutorialVideos() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url.getHomeVideos(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (!isAdded()){
                    return;
                }
                JSONArray array = jsonObject.optJSONArray("videos");
                FragmentVideoCardAdapter adapter = new FragmentVideoCardAdapter(requireContext(),array);
                videoRecyclerView.setAdapter(adapter);
                loadingOverlay.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (!isAdded()){
                    return;
                }commonFunctions.handleErrorResponse(requireContext(),volleyError);
                loadingOverlay.setVisibility(View.GONE);
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

}
