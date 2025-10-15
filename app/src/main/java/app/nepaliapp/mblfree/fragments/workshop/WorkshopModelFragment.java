package app.nepaliapp.mblfree.fragments.workshop;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.nepaliapp.mblfree.R;
import app.nepaliapp.mblfree.common.CommonFunctions;
import app.nepaliapp.mblfree.common.MySingleton;
import app.nepaliapp.mblfree.common.StorageClass;
import app.nepaliapp.mblfree.common.Url;
import app.nepaliapp.mblfree.fragmentmanager.DashBoardManager;
import app.nepaliapp.mblfree.fragments.userdash.PracticalFragment;
import app.nepaliapp.mblfree.recyclerAdapter.WorkshopModelAdapter;

public class WorkshopModelFragment extends Fragment {
    RequestQueue requestQueue;
    RecyclerView recyclerView;
    StorageClass storageClass;
    Url url;

    EditText searchText;
    WorkshopModelAdapter adapter;
    JSONArray jsonArrayData;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    CommonFunctions commonFunctions;
    FrameLayout loadingOverlay;
    public WorkshopModelFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_workshop_model, container, false);
        init(view);
        setupTextWatcher();

        if (getArguments() != null) {
            String companyName = getArguments().getString("companyName");
            modelRequest(companyName);
        }

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                fragmentChanger(new PracticalFragment());
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                callback
        );
        return view;


    }

    private void init(View view) {
        requestQueue = MySingleton.getInstance(requireContext()).getRequestQueue();
        storageClass = new StorageClass(requireContext());
        url = new Url();
        commonFunctions = new CommonFunctions();
        //findbyID
        recyclerView = view.findViewById(R.id.workShopModels);
        searchText = view.findViewById(R.id.searchEditText);
        loadingOverlay = view.findViewById(R.id.loadingOverlay);
    }

    private JSONArray reversePassedArray(JSONArray array) {
        JSONArray reversedArray = new JSONArray();

        for (int i = array.length() - 1; i >= 0; i--) {
            try {
                reversedArray.put(array.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return reversedArray;
    }

    private void modelRequest(String company) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url.getRequestWorkShopModel(company), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (!isAdded()) {
                    return;
                }
                jsonArrayData = jsonObject.optJSONArray("models");

                adapter = new WorkshopModelAdapter(requireContext(), reversePassedArray(jsonArrayData));
                recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
                recyclerView.setAdapter(adapter);
                loadingOverlay.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (!isAdded()) {
                    return;
                }
                commonFunctions.handleErrorResponse(requireContext(),volleyError);
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


    private void fragmentChanger(Fragment fragment) {
        if (getActivity() != null && getActivity() instanceof DashBoardManager) {
            ((DashBoardManager) getActivity()).replaceFragments(fragment);
        }

    }


    private void setupTextWatcher() {
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) handler.removeCallbacks(searchRunnable);

                searchRunnable = () -> runSearch(s.toString());
                handler.postDelayed(searchRunnable, 300);
            }
        });
    }


    private void runSearch(String query) {
        if (jsonArrayData == null) return;

        try {
            if (TextUtils.isEmpty(query)) {
                adapter.updateData(jsonArrayData);
            } else {
                JSONArray filtered = findMatchingObjects(query, jsonArrayData);
                adapter.updateData(filtered);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONArray findMatchingObjects(String searchText, JSONArray originalJsonArray) throws JSONException {
        JSONArray matchingArray = new JSONArray();

        for (int i = 0; i < originalJsonArray.length(); i++) {
            JSONObject jsonObject = originalJsonArray.getJSONObject(i);
            String name = jsonObject.optString("name");
            if (containsPartialMatch(name, searchText)) {
                matchingArray.put(jsonObject);
            }
        }

        return matchingArray;
    }

    private boolean containsPartialMatch(String name, String searchText) {
        name = name.toLowerCase();
        searchText = searchText.toLowerCase();

        for (int i = 0; i <= name.length() - searchText.length(); i++) {
            if (name.regionMatches(i, searchText, 0, searchText.length())) {
                return true;
            }
        }
        return false;
    }
}