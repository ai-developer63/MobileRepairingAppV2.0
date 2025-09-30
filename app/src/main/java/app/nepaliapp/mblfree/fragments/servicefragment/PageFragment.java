package app.nepaliapp.mblfree.fragments.servicefragment;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.github.chrisbanes.photoview.PhotoView;

public class PageFragment extends Fragment {

    private static final String ARG_PAGE_INDEX = "page_index";

    private PdfRenderer pdfRenderer;
    private int pageIndex;
    private float renderScale;
    private ViewPager2 viewPager;

    public static PageFragment newInstance(int pageIndex, PdfRenderer pdfRenderer, ViewPager2 viewPager, float scale) {
        PageFragment fragment = new PageFragment();
        fragment.pdfRenderer = pdfRenderer;
        fragment.viewPager = viewPager;
        fragment.renderScale = scale;

        Bundle args = new Bundle();
        args.putInt(ARG_PAGE_INDEX, pageIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        if (getArguments() != null) {
            pageIndex = getArguments().getInt(ARG_PAGE_INDEX);
        }

        PhotoView photoView = new PhotoView(requireContext());
        photoView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        photoView.setMinimumScale(1f);
        photoView.setMaximumScale(5f);
        photoView.setScale(1f, false);

        // Render PDF page
        try {
            PdfRenderer.Page page = pdfRenderer.openPage(pageIndex);
            Bitmap bitmap = Bitmap.createBitmap(
                    (int)(page.getWidth() * renderScale),
                    (int)(page.getHeight() * renderScale),
                    Bitmap.Config.ARGB_8888
            );
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            page.close();
            photoView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Disable ViewPager swipe when zoomed
        photoView.setOnScaleChangeListener((scaleFactor, focusX, focusY) -> {
            viewPager.setUserInputEnabled(photoView.getScale() <= photoView.getMinimumScale());
        });

        // Handle multi-touch properly
        photoView.setOnTouchListener((v, event) -> {

            // Multi-touch detected: prevent parent from intercepting
            if (event.getPointerCount() > 1) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
            }

            // Re-enable swipe on finger lift
            if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                v.getParent().requestDisallowInterceptTouchEvent(false);

                if (photoView.getScale() > photoView.getMinimumScale()) {
                    Toast.makeText(requireContext(), "Please zoom out to switch page", Toast.LENGTH_SHORT).show();
                }
            }

            return false; // VERY IMPORTANT: allow PhotoView to handle pinch
        });

        return photoView;
    }
}
