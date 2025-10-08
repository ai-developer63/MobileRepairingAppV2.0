package app.nepaliapp.mblfree.fragments.workshop;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
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
import app.nepaliapp.mblfree.common.MySingleton;
import app.nepaliapp.mblfree.common.StorageClass;
import app.nepaliapp.mblfree.common.Url;
import app.nepaliapp.mblfree.recyclerAdapter.WorkshopTopicAdapter;

public class WorkshopTopicsFragment extends Fragment {
    RequestQueue requestQueue;
    RecyclerView recyclerView;
    StorageClass storageClass;
    Url url;
    JSONArray jsonArrayData;
    WorkshopTopicAdapter adapter;
    TextView ModelName;

    public WorkshopTopicsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workshop, container, false);
        init(view);
        if (getArguments() != null) {
            String companyName = getArguments().getString("companyName");
            String modelName = getArguments().getString("modelName");
            ModelName.setText(modelName);
            TopicRequest(companyName,modelName);
        }
        return view;
    }

    private void init(View view) {
        requestQueue = MySingleton.getInstance(requireContext()).getRequestQueue();
        storageClass = new StorageClass(requireContext());
        url = new Url();

        //findByID
        recyclerView = view.findViewById(R.id.topics);
        ModelName = view.findViewById(R.id.ModelName);
    }

    private void TopicRequest(String company,String model) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url.getRequestWorkShopTopic(), makeObject(company, model), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                jsonArrayData = jsonObject.optJSONArray("topics");
                if (!isAdded()) {
                    return;
                }
                adapter = new WorkshopTopicAdapter(requireContext(), jsonArrayData);
                recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
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

    private JSONObject makeObject (String company,String model){
        JSONObject obj = new JSONObject();
        try {
            obj.put("company", company);
            obj.put("model", model);
        } catch (JSONException ignored) {

        }
        return obj;

    }

}