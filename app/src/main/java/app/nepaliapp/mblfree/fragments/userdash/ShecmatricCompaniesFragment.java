package app.nepaliapp.mblfree.fragments.userdash;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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
import app.nepaliapp.mblfree.common.MySingleton;
import app.nepaliapp.mblfree.common.StorageClass;
import app.nepaliapp.mblfree.common.Url;
import app.nepaliapp.mblfree.fragmentmanager.DashBoardManager;
import app.nepaliapp.mblfree.recyclerAdapter.SchematricDiagramCompanies;

public class ShecmatricCompaniesFragment extends Fragment {

    RecyclerView recyclerView;
    Url url;
    RequestQueue requestQueue;
    StorageClass storageClass;

    public ShecmatricCompaniesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shematric_companies, container, false);
        init(view);
        requestCompanies();

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                fragmentChanger(new HomeFragment());
                updateNav("home");
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                callback
        );
        return view;
    }

    private void init(View view) {
        recyclerView = view.findViewById(R.id.copanies);
        url = new Url();
        requestQueue = MySingleton.getInstance(requireContext()).getRequestQueue();
        storageClass = new StorageClass(requireContext());
    }


    private void requestCompanies() {
        JsonObjectRequest companies = new JsonObjectRequest(Request.Method.GET, url.getSchematicsCompanies(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                recyclerView.setLayoutManager(new GridLayoutManager(requireContext(),2));
                recyclerView.setAdapter(new SchematricDiagramCompanies(requireContext(), jsonObject.optJSONArray("Schematric")));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + storageClass.getJwtToken());
                return headers;
            }
        };
        requestQueue.add(companies);

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

}