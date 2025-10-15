package app.nepaliapp.mblfree.fragments.course;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import app.nepaliapp.mblfree.fragmentmanager.DashBoardManager;
import app.nepaliapp.mblfree.recyclerAdapter.SubCardCourseAdapter;

public class CourseSubTopicFragment extends Fragment {

    RecyclerView recyclerView;
    Url url;
    RequestQueue requestQueue;
    StorageClass storageClass;
    CommonFunctions commonFunctions;
    TextView title;
    FrameLayout loadingOverlay;

    public CourseSubTopicFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_subtopic, container, false);
        init(view);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        if (getArguments() != null) {
            String mainTopicName = getArguments().getString("mainTopicName");
            title.setText(mainTopicName);
            getAllSubCards(mainTopicName);
        }

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                fragmentChanger(new CoursefirstFragment());
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
        title = view.findViewById(R.id.Heading);
        loadingOverlay = view.findViewById(R.id.loadingOverlay);
        //Initialization
        url = new Url();
        storageClass = new StorageClass(requireContext());
        requestQueue = MySingleton.getInstance(requireContext()).getRequestQueue();
        commonFunctions = new CommonFunctions();
    }

    private void fragmentChanger(Fragment fragment) {
        if (getActivity() != null && getActivity() instanceof DashBoardManager) {
            ((DashBoardManager) getActivity()).replaceFragments(fragment);
        }

    }


    private void getAllSubCards(String mainTopicName) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url.getSecondTopicsCourses(mainTopicName), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (!isAdded()) {
                    return;
                }
                JSONArray array = jsonObject.optJSONArray("subcards");
                SubCardCourseAdapter adapter = new SubCardCourseAdapter(requireContext(), array);
                recyclerView.setAdapter(adapter);
                loadingOverlay.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                commonFunctions.handleErrorResponse(requireContext(), volleyError);
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