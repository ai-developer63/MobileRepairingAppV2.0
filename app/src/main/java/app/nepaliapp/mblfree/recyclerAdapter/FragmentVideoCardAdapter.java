package app.nepaliapp.mblfree.recyclerAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.cardview.widget.CardView;
import androidx.media3.common.util.UnstableApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import app.nepaliapp.mblfree.R;
import app.nepaliapp.mblfree.activity.VideoPlayingActivity;

public class FragmentVideoCardAdapter extends RecyclerView.Adapter<FragmentVideoCardAdapter.ViewHolder> {
    Context context;
    JSONArray array;

    public FragmentVideoCardAdapter(Context context, JSONArray array) {
        this.context = context;
        this.array = array;
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
        holder.title.setText(object.optString("title"));
        holder.author.setText(object.optString("authorName"));
        holder.subTitle.setText(object.optString("description"));
        Glide.with(context)
                .load(object.optString("image"))
                .error(R.mipmap.ic_launcher)
                .into(holder.thumnailView);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @OptIn(markerClass = UnstableApi.class)
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, VideoPlayingActivity.class);
                intent.putExtra("videoUrl", object.optString("link"));
                intent.putExtra("videoTitle", object.optString("title"));
                intent.putExtra("videoListJson", array.toString());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return array.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
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
