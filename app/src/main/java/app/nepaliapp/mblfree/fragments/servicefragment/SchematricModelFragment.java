package app.nepaliapp.mblfree.fragments.servicefragment;

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
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

import app.nepaliapp.mblfree.R;
import app.nepaliapp.mblfree.common.MySingleton;
import app.nepaliapp.mblfree.common.StorageClass;
import app.nepaliapp.mblfree.common.Url;
import app.nepaliapp.mblfree.fragmentmanager.DashBoardManager;
import app.nepaliapp.mblfree.fragments.userdash.HomeFragment;
import app.nepaliapp.mblfree.fragments.userdash.PracticalFragment;
import app.nepaliapp.mblfree.fragments.userdash.ShecmatricCompaniesFragment;
import app.nepaliapp.mblfree.fragments.userdash.VideosFragment;
import app.nepaliapp.mblfree.recyclerAdapter.SchematricModelAdapter;

public class SchematricModelFragment extends Fragment {

    RecyclerView recyclerView;
    Url url;
    RequestQueue requestQueue;
    StorageClass storageClass;

    public SchematricModelFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schematric_model, container, false);
        init(view);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String companyName = bundle.getString("companyName");
            requestModel(companyName);
        }


        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                fragmentChanger(new ShecmatricCompaniesFragment());

            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                callback
        );

        return view;
    }

    private void init(View view) {
        //find by id
        recyclerView = view.findViewById(R.id.models);

        //Urls
        url = new Url();
        requestQueue = MySingleton.getInstance(requireContext()).getRequestQueue();
        storageClass = new StorageClass(requireContext());
    }


    private void requestModel(String compinesName) {

        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, url.getSchematicsLinks(compinesName), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                SchematricModelAdapter adapter = new SchematricModelAdapter(requireContext(), jsonArray);
                recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
                recyclerView.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + storageClass.getJwtToken());
                return headers;
            }
        };
        ;

        requestQueue.add(arrayRequest);

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