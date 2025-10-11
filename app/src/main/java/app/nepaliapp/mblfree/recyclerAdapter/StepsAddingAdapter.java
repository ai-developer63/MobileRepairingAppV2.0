package app.nepaliapp.mblfree.recyclerAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import app.nepaliapp.mblfree.R;
import app.nepaliapp.mblfree.common.ImageCarouselView;

public class StepsAddingAdapter extends RecyclerView.Adapter<StepsAddingAdapter.ViewHolder> {
    Context context;
    JSONArray array;

    public StepsAddingAdapter(Context context, JSONArray array) {
        this.context = context;
        this.array = array;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_layout_for_steps, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JSONObject step = array.optJSONObject(position);

        // Set step number and heading
        holder.stepNumber.setText("Step: "+ step.optString( "stepNumber"));
        holder.stepHeading.setText(step.optString("heading"));

        // Set description
        String description = step.optString("description")
                .replace("\\n", "\n")  // convert escaped newlines to real newlines
                .replace("\" +", "");   // remove concatenation artifacts
        holder.stepDescription.setText(description);

        //Set image
        JSONArray carouselArray = step.optJSONArray("carousel_images");
        if (carouselArray != null && carouselArray.length() > 0) {
            JSONObject imagesObj = carouselArray.optJSONObject(0);
            if (imagesObj != null) {
                ArrayList<String> imageUrls = new ArrayList<>();
                for (int i = 1; i <= 4; i++) {
                    String key = "image" + i;
                    if (imagesObj.has(key) && !imagesObj.isNull(key)) {
                        String image = imagesObj.optString(key);
                        if (image != null && !image.isEmpty()) {
                            imageUrls.add(image);  // full URL already provided by backend
                        }
                    }
                }
                holder.carousel.setImages(imageUrls);
            }
        }

    }

    @Override
    public int getItemCount() {
        return array.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView stepNumber, stepHeading, stepDescription;
        ImageCarouselView carousel;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            stepNumber = itemView.findViewById(R.id.StepNumber);
            stepHeading = itemView.findViewById(R.id.StepHeading);
            stepDescription = itemView.findViewById(R.id.StepDescription);
            carousel = itemView.findViewById(R.id.carousel);
        }
    }
}
