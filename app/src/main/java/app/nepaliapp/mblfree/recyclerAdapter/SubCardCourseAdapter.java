package app.nepaliapp.mblfree.recyclerAdapter;

import android.content.Context;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.cardview.widget.CardView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.nepaliapp.mblfree.R;
import app.nepaliapp.mblfree.activity.CourseActivity;
import app.nepaliapp.mblfree.common.CommonFunctions;

public class SubCardCourseAdapter extends RecyclerView.Adapter<SubCardCourseAdapter.ViewHolder> {
    Context context;
    JSONArray array;
    CommonFunctions commonFunctions;

    public SubCardCourseAdapter(Context context, JSONArray array) {
        this.context = context;
        this.array = array;
        this.commonFunctions = new CommonFunctions();
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
            JSONObject obj = array.getJSONObject(position);
            holder.logo_Name.setText(obj.optString("subTopicName"));
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
                        OpenCourse(obj.optString("subTopicName"));
                    } else {
                        commonFunctions.showDialogWithPrice(context,"course");
                    }

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



 private void OpenCourse(String courseHeading){
     Intent intent = new Intent(context, CourseActivity.class);
     intent.putExtra("courseData", courseHeading);
     context.startActivity(intent);
 }

}
