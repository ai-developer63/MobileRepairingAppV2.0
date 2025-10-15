package app.nepaliapp.mblfree.recyclerAdapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.nepaliapp.mblfree.R;
import app.nepaliapp.mblfree.activity.CourseActivity;
import app.nepaliapp.mblfree.fragments.course.CourseSubTopicFragment;

public class CoursecardAdapter extends RecyclerView.Adapter<CoursecardAdapter.ViewHolder> {
   Context context;
   JSONArray cardData;

    public CoursecardAdapter(Context context, JSONArray cardData) {
        this.context = context;
        this.cardData = cardData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.modelforcardui,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            JSONObject obj = cardData.getJSONObject(position);
            holder.logo_Name.setText(obj.optString("name"));
            Glide.with(holder.logoImage.getContext())
                    .load(obj.optString("iconUrl"))
                    .error(R.mipmap.ic_launcher)
                    .into(holder.logoImage);
            boolean isPaid = obj.optBoolean("isPaid", false);
            if (!isPaid) {
                holder.lockIcon.setVisibility(View.VISIBLE);
                holder.itemView.setAlpha(0.7f);
            } else {
                holder.lockIcon.setVisibility(View.GONE);
                holder.itemView.setAlpha(1f);
            }

            holder.clickAble.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isPaid) {
                        onClickContext(obj.optString("opens"),obj.optString("name"));
                    } else {
                        Toast.makeText(context, "Seems Lock", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getItemCount() {
        return cardData.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView logoImage, lockIcon;
        TextView logo_Name;
        CardView clickAble;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            logo_Name = itemView.findViewById(R.id.CompanyName);
            logoImage = itemView.findViewById(R.id.logoCompany);
            clickAble = itemView.findViewById(R.id.cardOkModel);
            lockIcon = itemView.findViewById(R.id.lockIcon);
        }
    }
    private void onClickContext(String type,String which){
        if (type.equals("courses")) {
            Intent intent = new Intent(context, CourseActivity.class);
            intent.putExtra("fromWhere","Main");
            intent.putExtra("courseData", which);
            context.startActivity(intent);

        } else if (type.equals("subtopic")) {
            AppCompatActivity activity = (AppCompatActivity) context;
            Fragment fragment = new CourseSubTopicFragment();
            Bundle bundle = new Bundle();
            bundle.putString("mainTopicName", which);
            fragment.setArguments(bundle);

            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayoutInMain, fragment)
                    .addToBackStack(null)
                    .commit();
        }

    }

}
