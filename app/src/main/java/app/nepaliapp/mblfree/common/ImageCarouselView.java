package app.nepaliapp.mblfree.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
    private LinearLayout dotsLayout;
    private ArrayList<String> images;
    private ImagePagerAdapter adapter;

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
        dotsLayout = findViewById(R.id.dotsLayout);

        adapter = new ImagePagerAdapter(getContext());
        viewPager.setAdapter(adapter);

        // Carousel effects
        viewPager.setPageMargin(16);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setPageTransformer(true, (page, position) -> {
            float scale = 0.85f + (1 - Math.abs(position)) * 0.15f;
            page.setScaleY(scale);
            page.setAlpha(0.5f + (1 - Math.abs(position)) * 0.5f);
        });
    }

    /**
     * Sets images for the carousel.
     * Filters out null or empty strings automatically.
     */
    public void setImages(ArrayList<String> images) {
        if (images == null) images = new ArrayList<>();

        // Filter out null or empty strings
        ArrayList<String> filtered = new ArrayList<>();
        for (String img : images) {
            if (img != null && !img.trim().isEmpty()) filtered.add(img);
        }

        this.images = filtered;
        adapter.setImages(this.images);
        setupDots();
    }

    private void setupDots() {
        dotsLayout.removeAllViews();
        if (images == null || images.isEmpty()) return;

        ImageView[] dots = new ImageView[images.size()];
        for (int i = 0; i < images.size(); i++) {
            dots[i] = new ImageView(getContext());
            dots[i].setImageResource(i == 0 ? R.drawable.dot_active : R.drawable.dot_inactive);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(4, 0, 4, 0);
            dotsLayout.addView(dots[i], params);
        }

        // Remove previous listeners to avoid duplicates
        viewPager.clearOnPageChangeListeners();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < dots.length; i++) {
                    dots[i].setImageResource(i == position ? R.drawable.dot_active : R.drawable.dot_inactive);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    private class ImagePagerAdapter extends PagerAdapter {

        private Context context;
        private ArrayList<String> images;

        public ImagePagerAdapter(Context context) {
            this.context = context;
            this.images = new ArrayList<>();
        }

        public void setImages(ArrayList<String> images) {
            this.images.clear();
            if (images != null) this.images.addAll(images);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ImageView imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            String imageLink = images.get(position);

            // Glide load with placeholder/error and disk caching
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
            ImageZoomingFunction imageZoomingFunction = new ImageZoomingFunction(context, imageUrl);
            imageZoomingFunction.show();
        }
    }
}
