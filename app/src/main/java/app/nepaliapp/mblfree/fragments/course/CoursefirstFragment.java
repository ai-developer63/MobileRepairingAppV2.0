package app.nepaliapp.mblfree.fragments.course;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.nepaliapp.mblfree.R;
import app.nepaliapp.mblfree.common.CommonFunctions;
import app.nepaliapp.mblfree.common.MySingleton;
import app.nepaliapp.mblfree.common.StorageClass;
import app.nepaliapp.mblfree.common.Url;
import app.nepaliapp.mblfree.fragmentmanager.DashBoardManager;
import app.nepaliapp.mblfree.fragments.userdash.HomeFragment;
import app.nepaliapp.mblfree.recyclerAdapter.CourseMainCardAdapter;

public class CoursefirstFragment extends Fragment {

    RecyclerView recyclerView;
    RequestQueue requestQueue;
    Url url;
    CommonFunctions commonFunctions;
    StorageClass storageClass;
    FrameLayout loadingOverlay;

    public CoursefirstFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coursefirst, container, false);

        init(view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        requestMainCourseTopic();
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                fragmentChanger(new HomeFragment());
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                callback
        );

        return view;
    }

    private void init(View view) {
        recyclerView = view.findViewById(R.id.topics);
        loadingOverlay = view.findViewById(R.id.loadingOverlay);
        //Initialization
        requestQueue = MySingleton.getInstance(requireContext()).getRequestQueue();
        url = new Url();
        commonFunctions = new CommonFunctions();
        storageClass = new StorageClass(requireContext());
    }

    private void requestMainCourseTopic() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url.getFirstTopicsCourses(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (!isAdded()) {
                    return;
                }
                CourseMainCardAdapter adapter = new CourseMainCardAdapter(requireContext(), jsonObject.optJSONArray("heading"), jsonObject.optJSONArray("mainCard"));
                recyclerView.setAdapter(adapter);
                loadingOverlay.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (!isAdded()) {
                    return;
                }
                commonFunctions.handleErrorResponse(requireContext(), volleyError);
                loadingOverlay.setVisibility(View.GONE);
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + storageClass.getJwtToken());
                return headers;
            }
        };
        requestQueue.add(request);
    }
    private void fragmentChanger(Fragment fragment) {
        if (getActivity() != null && getActivity() instanceof DashBoardManager) {
            ((DashBoardManager) getActivity()).replaceFragments(fragment);
        }

    }
}