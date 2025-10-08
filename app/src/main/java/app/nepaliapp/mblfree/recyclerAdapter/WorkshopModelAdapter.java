package app.nepaliapp.mblfree.recyclerAdapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import app.nepaliapp.mblfree.R;
import app.nepaliapp.mblfree.common.MySingleton;
import app.nepaliapp.mblfree.common.SubscriptionDialog;
import app.nepaliapp.mblfree.common.Url;
import app.nepaliapp.mblfree.fragments.workshop.WorkshopModelFragment;
import app.nepaliapp.mblfree.fragments.workshop.WorkshopTopicsFragment;

public class WorkshopModelAdapter extends RecyclerView.Adapter<WorkshopModelAdapter.ViewHolder> {
    Context context;
    JSONArray array;
    RequestQueue requestQueue;

    public WorkshopModelAdapter(Context context, JSONArray array) {
        this.context = context;
        this.array = array;
        this.requestQueue = MySingleton.getInstance(context).getRequestQueue();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.modelforcardui, parent, false);
        return new ViewHolder(view);
    }
    public void updateData(JSONArray newData) {
        this.array = newData;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            JSONObject obj = array.getJSONObject(position);
            holder.logo_Name.setText(obj.optString("name"));
            Glide.with(holder.logoImage.getContext())
                    .load(obj.optString("logo"))
                    .error(R.mipmap.ic_launcher)
                    .into(holder.logoImage);
            boolean isPaid = obj.optBoolean("ispaid", false);

            if (!isPaid) {
                holder.lockIcon.setVisibility(View.VISIBLE);
                holder.itemView.setAlpha(0.7f);
            } else {
                holder.lockIcon.setVisibility(View.GONE);
                holder.itemView.setAlpha(1f);
            }

            holder.clickAble.setOnClickListener(v -> {
                if (!isPaid) {
                    showDialogWithPrice();
                } else {
                   changeFragment(obj.optString("companyName"),obj.optString("name"));
                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getItemCount() {
        return array.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView logoImage,lockIcon;
        TextView logo_Name;
        CardView clickAble;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            logoImage = itemView.findViewById(R.id.logoCompany);
            logo_Name = itemView.findViewById(R.id.CompanyName);
            clickAble = itemView.findViewById(R.id.cardOkModel);
            lockIcon = itemView.findViewById(R.id.lockIcon);
        }
    }
    private void showDialogWithPrice(){
        Url url= new Url();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url.getPrice(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                SubscriptionDialog.show(context,jsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        requestQueue.add(request);
    }

    private void changeFragment(String companyName,String modelName) {
        WorkshopTopicsFragment workshopTopicsFragment = new WorkshopTopicsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("companyName", companyName);
        bundle.putString("modelName", modelName);
        workshopTopicsFragment.setArguments(bundle);
        FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayoutInMain, workshopTopicsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
