package app.nepaliapp.mblfree.fragmentmanager;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import app.nepaliapp.mblfree.R;
import app.nepaliapp.mblfree.common.StorageClass;
import app.nepaliapp.mblfree.fragments.userdash.HomeFragment;
import app.nepaliapp.mblfree.fragments.userdash.PracticalFragment;
import app.nepaliapp.mblfree.fragments.userdash.ShecmatricCompaniesFragment;
import app.nepaliapp.mblfree.fragments.userdash.VideosFragment;

public class DashBoardManager extends AppCompatActivity {

    TextView textView;
    StorageClass storageClass;
    BottomNavigationView btmNavigation;
    FrameLayout frameLayout;
    ConstraintLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dash_board);
        init();
        ViewCompat.setOnApplyWindowInsetsListener(frameLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        replaceFragments(new HomeFragment());
        setupBottomNavigation();

//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(DashBoardManager.this, SigninManager.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//                storageClass.UpdateJwtToken("Jwt_kali_xa");
//                finish();
//            }
//        });
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                    showExitAlertDialog();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);

    }

    private void init() {
        btmNavigation = findViewById(R.id.bottom_navigation);
        frameLayout = findViewById(R.id.frameLayoutInMain);
        mainLayout = findViewById(R.id.main);

    }
    private void setupBottomNavigation() {
        btmNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int itemId = menuItem.getItemId();
                if (itemId == R.id.homes) {
                    replaceFragments(new HomeFragment());
                    return true;
                } else if (itemId == R.id.schematri) {
                    replaceFragments(new ShecmatricCompaniesFragment());
                    return true;
                } else if (itemId == R.id.Videos) {
                    replaceFragments(new VideosFragment());
                    return true;
                } else if (itemId == R.id.practical) {
                    replaceFragments(new PracticalFragment());
                    return true;
                } else {
                    replaceFragments(new HomeFragment());
                }
                return true;
            }
        });
    }


    public void replaceFragments(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutInMain, fragment);
        fragmentTransaction.commit();
    }
    private void showExitAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit App");
        builder.setMessage("Are you sure you want to exit the app?");
        builder.setPositiveButton("Yes", (dialog, which) -> close());
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    private void close() {
        finishAffinity();
        System.exit(0);
    }
    public void navigateTo(Fragment fragment, int navItemId) {
        replaceFragments(fragment);
        btmNavigation.setSelectedItemId(navItemId);
    }



}