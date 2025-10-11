package app.nepaliapp.mblfree.recyclerAdapter;

import static androidx.core.content.ContentProviderCompat.requireContext;

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

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import app.nepaliapp.mblfree.R;
import app.nepaliapp.mblfree.fragments.workshop.WorkshopPracticalFragment;

public class WorkshopTopicAdapter extends RecyclerView.Adapter<WorkshopTopicAdapter.ViewHolder> {
    Context context;
    JSONArray array;

    public WorkshopTopicAdapter(Context context, JSONArray array) {
        this.context = context;
        this.array = array;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.workshop_topic_model, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JSONObject object = array.optJSONObject(position);
        holder.title.setText(object.optString("name"));
        holder.description.setText(object.optString("description"));
        Glide.with(holder.logo.getContext())
                .load(object.optString("logo"))
                .error(R.mipmap.ic_launcher)
                .into(holder.logo);
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment(object.optString("companyName"),object.optString("modelName"),object.optString("name"));
            }
        });

    }

    @Override
    public int getItemCount() {
        return array.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView logo;
        TextView title, description;
        CardView card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            logo = itemView.findViewById(R.id.logo);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            card = itemView.findViewById(R.id.cardClick);
        }
    }

    private void changeFragment(String companyName,String modelName,String topicName) {
        WorkshopPracticalFragment workshopPracticalFragment = new WorkshopPracticalFragment();
        Bundle bundle = new Bundle();
        bundle.putString("companyName", companyName);
        bundle.putString("modelName", modelName);
        bundle.putString("topicName", topicName);
        workshopPracticalFragment.setArguments(bundle);
        FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayoutInMain, workshopPracticalFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
