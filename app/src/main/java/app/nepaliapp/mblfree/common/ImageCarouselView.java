package app.nepaliapp.mblfree.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import app.nepaliapp.mblfree.R;

public class ImageCarouselView extends FrameLayout {

    private ViewPager viewPager;
    private ArrayList<String> images;

    public ImageCarouselView(@NonNull Context context) {
        super(context);
        init();
    }

    public ImageCarouselView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageCarouselView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.image_carousel_view, this);
        viewPager = findViewById(R.id.viewPager);
    }

    /**
     * Sets images for carousel.
     * Stateless: replaces old images fully.
     */
    public void setImages(ArrayList<String> images) {
        if (images == null) images = new ArrayList<>();
        this.images = new ArrayList<>(images);

        // Each time, create a fresh adapter to avoid recycled views
        ImagePagerAdapter adapter = new ImagePagerAdapter(getContext(), this.images);
        viewPager.setAdapter(adapter);
    }

    private static class ImagePagerAdapter extends PagerAdapter {

        private final Context context;
        private final ArrayList<String> images;

        public ImagePagerAdapter(Context context, ArrayList<String> images) {
            this.context = context;
            this.images = new ArrayList<>(images);
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull android.view.View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ImageView imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            String imageLink = images.get(position);

            // Load image via Glide
            Glide.with(context)
                    .load(imageLink)
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_error)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);

            imageView.setOnClickListener(v -> showImagePopup(imageLink));

            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((ImageView) object);
        }

        private void showImagePopup(String imageUrl) {
            ImageZoomingFunction zoom = new ImageZoomingFunction(context, imageUrl);
            zoom.show();
        }
    }
}
