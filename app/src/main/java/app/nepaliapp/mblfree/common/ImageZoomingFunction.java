package app.nepaliapp.mblfree.common;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import app.nepaliapp.mblfree.R;

public class ImageZoomingFunction extends Dialog {

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;

    private Context context;
    private String imageUrl;
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private PointF startPoint = new PointF();
    private PointF midPoint = new PointF();
    private float oldDist = 1f;
    private int mode = NONE;

    public ImageZoomingFunction(Context context, String imageUrl) {
        super(context);
        this.context = context;
        this.imageUrl = imageUrl;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.image_popup_layout);

        ImageView imageView = findViewById(R.id.popup_image);
        Drawable placeholderDrawable = ContextCompat.getDrawable(context, R.drawable.image_error);

        // Load image with Glide
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            imageView.setImageDrawable(placeholderDrawable);
        } else {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_error)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        }

        // Set dialog size
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Cancel button
        ImageView btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(v -> dismiss());

        // Center the image initially
        imageView.post(() -> centerImage(imageView));

        // Set pinch & drag listener
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handleTouch(event, (ImageView) v);
                return true;
            }
        });
    }

    /** Centers the image in the ImageView */
    private void centerImage(ImageView imageView) {
        if (imageView.getDrawable() == null) return;

        float imageWidth = imageView.getDrawable().getIntrinsicWidth();
        float imageHeight = imageView.getDrawable().getIntrinsicHeight();
        int imageViewWidth = imageView.getWidth();
        int imageViewHeight = imageView.getHeight();

        float scale = Math.min(
                (float) imageViewWidth / imageWidth,
                (float) imageViewHeight / imageHeight
        );

        matrix.reset();
        matrix.postScale(scale, scale);
        float translateX = (imageViewWidth - imageWidth * scale) / 2f;
        float translateY = (imageViewHeight - imageHeight * scale) / 2f;
        matrix.postTranslate(translateX, translateY);
        imageView.setImageMatrix(matrix);
    }

    /** Handles drag and pinch events */
    private void handleTouch(MotionEvent event, ImageView view) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                startPoint.set(event.getX(), event.getY());
                mode = DRAG;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(midPoint, event);
                    mode = ZOOM;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - startPoint.x, event.getY() - startPoint.y);
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = newDist / oldDist;
                        matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
        }
        view.setImageMatrix(matrix);
    }

    /** Calculate distance between two fingers */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /** Calculate midpoint between two fingers */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
}
