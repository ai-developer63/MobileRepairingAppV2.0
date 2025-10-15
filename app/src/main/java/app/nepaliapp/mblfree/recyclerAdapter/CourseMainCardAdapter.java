package app.nepaliapp.mblfree.recyclerAdapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import app.nepaliapp.mblfree.R;

public class CourseMainCardAdapter extends RecyclerView.Adapter<CourseMainCardAdapter.ViewHolder> {
    Context context;
    JSONArray heading;
    JSONArray mainCard;

    public CourseMainCardAdapter(Context context, JSONArray heading, JSONArray mainCard) {
        this.context = context;
        this.heading = heading;
        this.mainCard = mainCard;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.course_heading_model, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JSONObject object = heading.optJSONObject(position);
        holder.heading.setText(object.optString("name"));

        JSONArray array = filterByHeading(mainCard, object.optString("name"));
        CoursecardAdapter adapter = new CoursecardAdapter(context,array);
         holder.recyclerView.setLayoutManager(new GridLayoutManager(context,2)
         );
         holder.recyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return heading.length();
    }
    public static JSONArray filterByHeading(JSONArray mainCardArray, String heading) {
        JSONArray filteredArray = new JSONArray();

        if (mainCardArray == null || heading == null) return filteredArray;

        for (int i = 0; i < mainCardArray.length(); i++) {
            JSONObject obj = mainCardArray.optJSONObject(i);
            if (obj != null) {
                String headingName = obj.optString("headingName");
                if (heading.equals(headingName)) {
                    filteredArray.put(obj);
                }
            }
        }

        return filteredArray;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView heading;
        RecyclerView recyclerView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            heading = itemView.findViewById(R.id.heading);
            recyclerView = itemView.findViewById(R.id.mainTopicCourseRecycler);
        }
    }
}
