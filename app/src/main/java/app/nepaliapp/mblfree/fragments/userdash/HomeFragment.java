package app.nepaliapp.mblfree.fragments.userdash;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.nepaliapp.mblfree.R;
import app.nepaliapp.mblfree.common.MySingleton;
import app.nepaliapp.mblfree.common.StorageClass;
import app.nepaliapp.mblfree.common.Url;
import app.nepaliapp.mblfree.recyclerAdapter.CategoriesAdapter;

public class HomeFragment extends Fragment {
    ImageCarousel carousel;
    Url url;
    RequestQueue requestQueue;
    RecyclerView categoriesRecycler;
    StorageClass storageClass;
    FrameLayout loadingOverlay;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        init(view);
        loadingOverlay.setVisibility(View.VISIBLE);
        carousel.registerLifecycle(getLifecycle());
        List<CarouselItem> items = new ArrayList<>();
        carousel.setImageScaleType(ImageView.ScaleType.FIT_XY);
        requestImage(items);
        requestCategories();
        Log.d("beartoken", storageClass.getJwtToken());

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                callback
        );
        return view;
    }

    private void requestCategories() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url.getCategoriesSystem(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (!isAdded()) return;
                JSONArray result = new JSONArray();
                try {
                    result = jsonObject.getJSONArray("categories");
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    CategoriesAdapter adapter = new CategoriesAdapter(requireContext(), result, fragmentManager);
                    categoriesRecycler.setLayoutManager(new GridLayoutManager(requireContext(), 4));
                    categoriesRecycler.setAdapter(adapter);
                    loadingOverlay.setVisibility(View.GONE);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
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
        requestQueue.add(jsonObjectRequest);
    }


    private void requestImage(List<CarouselItem> items) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url.getHomeImage(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (!isAdded()) return;
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("image_link");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            String link = jsonArray.getString(i);
                            items.add(new CarouselItem(link));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    carousel.setData(items);

                } catch (JSONException e) {
                    loadingOverlay.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }

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
        requestQueue.add(jsonObjectRequest);
    }


    private void init(View view) {
        carousel = view.findViewById(R.id.carousel);
        categoriesRecycler = view.findViewById(R.id.categories);
        loadingOverlay = view.findViewById(R.id.loadingOverlay);
        //initialization
        url = new Url();
        requestQueue = MySingleton.getInstance(requireContext()).getRequestQueue();
        storageClass = new StorageClass(requireContext());
    }

    private void showExitAlertDialog(Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Exit App");
        builder.setMessage("Are you sure you want to exit the app?");
        builder.setPositiveButton("Yes", (dialog, which) -> requireActivity().finishAffinity());
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();

    }


}