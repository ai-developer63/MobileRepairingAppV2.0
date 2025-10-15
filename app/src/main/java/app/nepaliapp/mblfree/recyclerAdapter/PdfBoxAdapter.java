package app.nepaliapp.mblfree.recyclerAdapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.chrisbanes.photoview.PhotoView;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.rendering.ImageType;
import com.tom_roush.pdfbox.rendering.PDFRenderer;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.nepaliapp.mblfree.R;

public class PdfBoxAdapter extends RecyclerView.Adapter<PdfBoxAdapter.PageViewHolder> {

    private final Context context;
    private final PDDocument document;
    private final PDFRenderer pdfRenderer;
    private final Map<Integer, Bitmap> bitmapCache = new HashMap<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(2);
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final float dpi;

    public PdfBoxAdapter(Context context, PDDocument document, float dpi) {
        this.context = context;
        this.document = document;
        this.pdfRenderer = new PDFRenderer(document);
        this.dpi = dpi; // DPI for clarity (e.g., 150-300)
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pdf_page, parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return document.getNumberOfPages();
    }

    @Override
    public void onViewRecycled(@NonNull PageViewHolder holder) {
        super.onViewRecycled(holder);
        holder.clear();
    }

    class PageViewHolder extends RecyclerView.ViewHolder {
        PhotoView photoView;
        ProgressBar progressBar;

        PageViewHolder(@NonNull View itemView) {
            super(itemView);
            photoView = itemView.findViewById(R.id.photoView);
            progressBar = itemView.findViewById(R.id.pageLoadingBar);

            photoView.setMinimumScale(1f);
            photoView.setMaximumScale(5f);
            photoView.setScale(1f, false);
        }

        void bind(int position) {
            if (bitmapCache.containsKey(position)) {
                photoView.setImageBitmap(bitmapCache.get(position));
                progressBar.setVisibility(View.GONE);
                return;
            }

            photoView.setImageDrawable(null);
            progressBar.setVisibility(View.VISIBLE);

            WeakReference<PhotoView> photoRef = new WeakReference<>(photoView);
            WeakReference<ProgressBar> progressRef = new WeakReference<>(progressBar);

            executor.execute(() -> {
                Bitmap bitmap = null;
                try {
                    // Render PDF page to bitmap with DPI scaling
                    bitmap = pdfRenderer.renderImageWithDPI(position, dpi, ImageType.RGB);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Bitmap finalBitmap = bitmap;
                mainHandler.post(() -> {
                    PhotoView p = photoRef.get();
                    ProgressBar pb = progressRef.get();
                    if (p != null && pb != null) {
                        pb.setVisibility(View.GONE);
                        if (finalBitmap != null) {
                            p.setImageBitmap(finalBitmap);
                            bitmapCache.put(position, finalBitmap);
                        }
                    }
                });
            });
        }

        void clear() {
            photoView.setImageDrawable(null);
        }
    }
}
