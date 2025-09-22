package app.nepaliapp.mblfree.recycler.home_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.nepaliapp.mblfree.R;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder>{
    Context context;

    JSONArray array;
    FragmentManager fragmentManager;
    public CategoriesAdapter(Context context, JSONArray array, FragmentManager fragmentManager) {
        this.context = context;
        this.array = array;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
  View view = LayoutInflater.from(context).inflate(R.layout.categories_model,parent,false);
  return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JSONObject jsonObject = null;
        try {
            jsonObject = array.getJSONObject(position);
            Glide.with(context)
                    .load(jsonObject.optString("icon"))
                    .into(holder.icons);

            holder.txtView.setText(jsonObject.optString("name"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public int getItemCount() {
        return array.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icons;
        TextView txtView;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icons = itemView.findViewById(R.id.imageView5);
            txtView = itemView.findViewById(R.id.txtIcon);
            cardView= itemView.findViewById(R.id.categoriesCards);
        }
    }
}
