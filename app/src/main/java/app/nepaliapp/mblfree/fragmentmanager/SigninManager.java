package app.nepaliapp.mblfree.fragmentmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import app.nepaliapp.mblfree.R;
import app.nepaliapp.mblfree.fragments.LoginFragment;

public class SigninManager extends AppCompatActivity {

    FrameLayout fragmentArea;
    TextView slogan;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signin_manager);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
        slogan.post(() -> {
            // Get total available height from slogan bottom to screen bottom
            int availableHeight = fragmentArea.getRootView().getHeight() - slogan.getBottom();

            // Only update if current height exceeds available
            if (fragmentArea.getHeight() > availableHeight) {
                ViewGroup.LayoutParams params = fragmentArea.getLayoutParams();
                params.height = availableHeight-15; // limit the fragment height
                fragmentArea.setLayoutParams(params);
            }
        });

        replaceFragments(new LoginFragment());


        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitAlertDialog();
            }
        };

        findViewById(android.R.id.content).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });
    }

    public void init() {
        fragmentArea = findViewById(R.id.Fragmentarea);
        slogan = findViewById(R.id.slogan);
    }

    public void adjustFragmentHeight() {
        slogan.post(() -> {
            int screenHeight = fragmentArea.getRootView().getHeight();
            int availableHeight = screenHeight - slogan.getBottom() - 10; // 10px margin below slogan

            ViewGroup.LayoutParams params = fragmentArea.getLayoutParams();

            // Measure fragment content height
            int contentHeight = 0;
            if (fragmentArea.getChildAt(0) != null) {
                fragmentArea.getChildAt(0).measure(
                        View.MeasureSpec.makeMeasureSpec(fragmentArea.getWidth(), View.MeasureSpec.AT_MOST),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                );
                contentHeight = fragmentArea.getChildAt(0).getMeasuredHeight();
            }

            // Set height: smaller of contentHeight or availableHeight
            params.height = Math.min(contentHeight, availableHeight);

            fragmentArea.setLayoutParams(params);
        });
    }



    // Call this AFTER fragmentTransaction.commit()
    public void replaceFragments(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.Fragmentarea, fragment);
        fragmentTransaction.commit();

        // Adjust height after fragment is attached
        fragmentArea.post(this::adjustFragmentHeight);
    }


    private void showExitAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SigninManager.this);
        builder.setTitle("Exit App");
        builder.setMessage("Are you sure you want to exit the app?");
        builder.setPositiveButton("Yes", (dialog, which) -> finish());
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();

    }

    // Method to hide the keyboard
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}