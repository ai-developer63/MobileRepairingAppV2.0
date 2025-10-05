package app.nepaliapp.mblfree.recyclerAdapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.chrisbanes.photoview.PhotoView;

public class PdfPageAdapter extends RecyclerView.Adapter<PdfPageAdapter.ViewHolder> {

     Context context;
    private final PdfRenderer pdfRenderer;

    public PdfPageAdapter(Context context, PdfRenderer pdfRenderer) {
        this.context = context;
        this.pdfRenderer = pdfRenderer;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PhotoView photoView = new PhotoView(parent.getContext());
        photoView.setLayoutParams(new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        photoView.setAdjustViewBounds(true);
        photoView.setMinimumScale(1f);
        photoView.setMaximumScale(5f);
        return new ViewHolder(photoView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PdfRenderer.Page page = pdfRenderer.openPage(position);

        float scale = 6f; // higher number more clear but phone processor is highly used.
        Bitmap bitmap = Bitmap.createBitmap(
                (int)(page.getWidth() * scale),
                (int)(page.getHeight() * scale),
                Bitmap.Config.ARGB_8888
        );

        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        holder.photoView.setImageBitmap(bitmap);
        page.close();
    }

    @Override
    public int getItemCount() {
        return pdfRenderer.getPageCount();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        PhotoView photoView;

        public ViewHolder(@NonNull PhotoView itemView) {
            super(itemView);
            photoView = itemView;
        }
    }
}
