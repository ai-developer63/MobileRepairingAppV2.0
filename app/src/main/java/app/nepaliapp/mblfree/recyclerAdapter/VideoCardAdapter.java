package app.nepaliapp.mblfree.recyclerAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import app.nepaliapp.mblfree.R;
import app.nepaliapp.mblfree.common.CommonFunctions;

public class VideoCardAdapter extends RecyclerView.Adapter<VideoCardAdapter.ViewHolder> {
    Context context;
    JSONArray array;
    CommonFunctions commonFunctions;
    private OnVideoClickListener listener;

    public VideoCardAdapter(Context context, JSONArray array, OnVideoClickListener listener) {
        this.context = context;
        this.array = array;
        this.listener = listener;
        this.commonFunctions = new CommonFunctions();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_model, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JSONObject object = array.optJSONObject(position);
        if (object == null) return;

        holder.title.setText(object.optString("title", "No Title"));
        holder.author.setText(object.optString("authorName", ""));
        holder.subTitle.setText(object.optString("description", ""));

        Glide.with(holder.thumnailView.getContext())
                .load(object.optString("image", ""))
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(holder.thumnailView);

        holder.cardView.setOnClickListener(v -> {

                if (listener != null) listener.onVideoClick(object);

        });
    }

    public void updateData(JSONArray newArray) {
        this.array = newArray;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return array.length();
    }

    public interface OnVideoClickListener {
        void onVideoClick(JSONObject video);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumnailView;
        TextView title, subTitle, author;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.videoTitle);
            subTitle = itemView.findViewById(R.id.videoSubTitle);
            author = itemView.findViewById(R.id.videoAuthor);
            thumnailView = itemView.findViewById(R.id.videoThumbnail);
            cardView = itemView.findViewById(R.id.videoCardID);
        }
    }
}
