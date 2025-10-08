package app.nepaliapp.mblfree.fragments.userdash;

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
import app.nepaliapp.mblfree.common.MySingleton;
import app.nepaliapp.mblfree.common.StorageClass;
import app.nepaliapp.mblfree.common.Url;
import app.nepaliapp.mblfree.fragmentmanager.DashBoardManager;
import app.nepaliapp.mblfree.recyclerAdapter.WorkshopCompanyAdapter;

public class PracticalFragment extends Fragment {

    RequestQueue requestQueue;
    RecyclerView recyclerView;
    StorageClass storageClass;
    Url url;
    JSONArray jsonArrayData;
    EditText searchText;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    WorkshopCompanyAdapter adapter;

    public PracticalFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_practical, container, false);
        init(view);
        companiesRequest();
        setupTextWatcher();


        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                fragmentChanger(new ShecmatricCompaniesFragment());
                updateNav("schematric");
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
        //findbyID
        recyclerView = view.findViewById(R.id.companies);
        searchText = view.findViewById(R.id.searchEditText);
    }


    private void companiesRequest() {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url.getRequestWorkShopCompany(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                jsonArrayData = jsonObject.optJSONArray("devices");
                if (!isAdded()) {
                    return;
                }
                adapter = new WorkshopCompanyAdapter(requireContext(), jsonArrayData);
                recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
                recyclerView.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (!isAdded()) {
                    return;
                }
                Log.d("workshop copanyError", volleyError.toString());
                Toast.makeText(requireContext(), "connecting to server", Toast.LENGTH_SHORT).show();
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

    private void updateNav(String whichSelect) {
        int menuId;
        Fragment fragment;

        switch (whichSelect.toLowerCase()) {
            case "home":
                menuId = R.id.homes;
                fragment = new HomeFragment();
                break;
            case "schematric":
                menuId = R.id.schematri;
                fragment = new ShecmatricCompaniesFragment();
                break;
            case "videos":
                menuId = R.id.Videos;
                fragment = new VideosFragment();
                break;
            case "practical":
                menuId = R.id.practical;
                fragment = new PracticalFragment();
                break;
            default:
                menuId = R.id.homes;
                fragment = new HomeFragment();
                break;
        }

        ((DashBoardManager) requireActivity()).navigateTo(fragment, menuId);
    }

    private void setupTextWatcher() {
        searchText.addTextChangedListener(new TextWatcher() {
            @Override public void afterTextChanged(Editable s) {}
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

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