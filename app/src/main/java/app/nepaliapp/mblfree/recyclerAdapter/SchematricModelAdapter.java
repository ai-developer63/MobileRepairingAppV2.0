package app.nepaliapp.mblfree.recyclerAdapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.nepaliapp.mblfree.R;
import app.nepaliapp.mblfree.fragments.servicefragment.PdfViewFragment;

public class SchematricModelAdapter extends RecyclerView.Adapter<SchematricModelAdapter.ViewHolder> {
    Context context;
    JSONArray array;

    public SchematricModelAdapter(Context context, JSONArray array) {
        this.context = context;
        this.array = array;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.modelforcardui, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        for (int i = 0; i <= array.length(); i++) {
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
                        openPdfViewerFragment(obj.optString("pdfLink"));
                    }
                });
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @Override
    public int getItemCount() {
        return array.length();
    }

    private void openPdfViewerFragment(String pdfUrl) {
        PdfViewFragment pdfViewFragmet = new PdfViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString("pdf_url", pdfUrl);
        pdfViewFragmet.setArguments(bundle);
        FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayoutInMain, pdfViewFragmet);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView logoImage;
        TextView logo_Name;
        CardView clickAble;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            logo_Name = itemView.findViewById(R.id.CompanyName);
            logoImage = itemView.findViewById(R.id.logoCompany);
            clickAble = itemView.findViewById(R.id.cardOkModel);
        }
    }
}
