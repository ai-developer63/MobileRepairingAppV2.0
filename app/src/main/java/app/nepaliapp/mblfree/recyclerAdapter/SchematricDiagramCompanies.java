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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.nepaliapp.mblfree.R;
import app.nepaliapp.mblfree.fragments.servicefragment.SchematricModelFragment;

public class SchematricDiagramCompanies extends RecyclerView.Adapter<SchematricDiagramCompanies.ViewHolder> {
    Context context;
    JSONArray array;

    public SchematricDiagramCompanies(Context context, JSONArray array) {
        this.context = context;
        this.array = array;

    }
    public void updateData(JSONArray newData) {
        this.array = newData;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public SchematricDiagramCompanies.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.modelforcardui, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SchematricDiagramCompanies.ViewHolder holder, int position) {
            try {
                JSONObject obj = array.getJSONObject(position);
                holder.logo_Name.setText(obj.optString("name"));
                Glide.with(holder.logoImage.getContext())
                        .load(obj.optString("logo"))
                        .error(R.mipmap.ic_launcher)
                        .into(holder.logoImage);
                holder.clickAble.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openPdfViewerFragment(obj.optString("name"));
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
        ImageView logoImage;
        TextView logo_Name;
        CardView clickAble;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            logoImage = itemView.findViewById(R.id.logoCompany);
            logo_Name = itemView.findViewById(R.id.CompanyName);
            clickAble = itemView.findViewById(R.id.cardOkModel);
        }
    }

    private void openPdfViewerFragment(String companyName) {
        SchematricModelFragment schematricModelFragment = new SchematricModelFragment();
        Bundle bundle = new Bundle();
        bundle.putString("companyName", companyName);
        schematricModelFragment.setArguments(bundle);
        FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayoutInMain, schematricModelFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
