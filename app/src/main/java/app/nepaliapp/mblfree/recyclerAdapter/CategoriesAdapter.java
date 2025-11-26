package app.nepaliapp.mblfree.recyclerAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.nepaliapp.mblfree.R;
import app.nepaliapp.mblfree.fragmentmanager.DashBoardManager;
import app.nepaliapp.mblfree.fragments.course.CoursefirstFragment;
import app.nepaliapp.mblfree.fragments.servicefragment.ShopFragment;
import app.nepaliapp.mblfree.fragments.userdash.HomeFragment;
import app.nepaliapp.mblfree.fragments.userdash.PracticalFragment;
import app.nepaliapp.mblfree.fragments.userdash.ShecmatricCompaniesFragment;
import app.nepaliapp.mblfree.fragments.userdash.VideosFragment;

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
        try {
           JSONObject jsonObject = array.getJSONObject(position);
            Glide.with(context)
                    .load(jsonObject.optString("icon"))
                    .into(holder.icons);

            holder.txtView.setText(jsonObject.optString("name"));
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean clickable =jsonObject.optBoolean("enable");
                    if (clickable){
                    changeByCategoryClick(jsonObject.optString("name"));
                    }else{
                        Toast.makeText(context, "Not available in your country", Toast.LENGTH_SHORT).show();
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
    private void fragmentChanger(Fragment fragment) {
        if (context != null && context instanceof DashBoardManager) {
            ((DashBoardManager) context).replaceFragments(fragment);
        }

    }

    private void changeByCategoryClick(String clickedName){

        switch (clickedName){
            case "course":
                fragmentChanger(new CoursefirstFragment());
                break;

            case "Practical":
                fragmentChanger(new PracticalFragment());
                updateNav("practical");
                break;

            case "Buy Tools":
                fragmentChanger(new ShopFragment());
                break;

            case "Videos":
                fragmentChanger(new VideosFragment());
                updateNav("videos");
                break;

            case "Schematric Companies":
                fragmentChanger(new ShecmatricCompaniesFragment());
                break;

            default:
                Toast.makeText(context, "Unknown category: " + clickedName, Toast.LENGTH_SHORT).show();
                break;
        }



    }

    private void updateNav(String whichSelect) {
        int menuId;
        Fragment fragment;

        switch (whichSelect.toLowerCase()) {
            case "home":
                menuId = R.id.homes;
                fragment = new HomeFragment();
                break;
            case "schematric":
                menuId = R.id.schematri;
                fragment = new ShecmatricCompaniesFragment();
                break;
            case "videos":
                menuId = R.id.Videos;
                fragment = new VideosFragment();
                break;
            case "practical":
                menuId = R.id.practical;
                fragment = new PracticalFragment();
                break;
            default:
                menuId = R.id.homes;
                fragment = new HomeFragment();
                break;
        }

        ((DashBoardManager) context).navigateTo(fragment, menuId);
    }
}
