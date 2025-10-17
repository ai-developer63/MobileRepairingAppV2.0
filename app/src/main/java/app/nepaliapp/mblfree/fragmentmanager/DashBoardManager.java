package app.nepaliapp.mblfree.fragmentmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
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
import app.nepaliapp.mblfree.fragments.course.CourseSubTopicFragment;
import app.nepaliapp.mblfree.fragments.course.CoursefirstFragment;
import app.nepaliapp.mblfree.fragments.servicefragment.SupportFragment;
import app.nepaliapp.mblfree.fragments.userdash.HomeFragment;
import app.nepaliapp.mblfree.fragments.userdash.PracticalFragment;
import app.nepaliapp.mblfree.fragments.userdash.ProfileFragment;
import app.nepaliapp.mblfree.fragments.userdash.ShecmatricCompaniesFragment;
import app.nepaliapp.mblfree.fragments.userdash.VideosFragment;
import app.nepaliapp.mblfree.fragments.workshop.WorkshopModelFragment;
import app.nepaliapp.mblfree.fragments.workshop.WorkshopPracticalFragment;
import app.nepaliapp.mblfree.fragments.workshop.WorkshopTopicsFragment;

public class DashBoardManager extends AppCompatActivity {

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

        Intent intent = getIntent();
        String which = null;

        if (intent != null) {
            which = intent.getStringExtra("openThisFragment");
        }

        if ("CourseFirstFragment".equalsIgnoreCase(which)) {
            replaceFragments(new CoursefirstFragment());
        } else if ("CourseSubTopicFragment".equalsIgnoreCase(which)) {
            replaceFragments(new CourseSubTopicFragment());
        } else {
            replaceFragments(new HomeFragment()); // default fragment
        }


        setupBottomNavigation();

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Fragment currentFragment = getSupportFragmentManager()
                        .findFragmentById(R.id.frameLayoutInMain);

                if (currentFragment instanceof CoursefirstFragment){
                    replaceFragments(new HomeFragment());
                } else if ((currentFragment instanceof CourseSubTopicFragment)) {
                    replaceFragments(new CoursefirstFragment());
                }else if ((currentFragment instanceof SupportFragment)) {
                    replaceFragments(new ProfileFragment());
                }else if ((currentFragment instanceof VideosFragment)) {
                    navigateTo(new PracticalFragment(), R.id.practical);
                }else if ((currentFragment instanceof WorkshopPracticalFragment)) {
                    replaceFragments(new PracticalFragment());
                } else if ((currentFragment instanceof WorkshopTopicsFragment)) {
                    replaceFragments(new PracticalFragment());
                }


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

        // 🌟 Add custom animations for smooth transitions
        fragmentTransaction.setCustomAnimations(
                R.anim.fade_in_fast,
                R.anim.fade_out_fast,
                R.anim.fade_in_fast,
                R.anim.fade_out_fast
        );

        fragmentTransaction.replace(R.id.frameLayoutInMain, fragment);
        fragmentTransaction.commit();
    }



    public void navigateTo(Fragment fragment, int navItemId) {
        replaceFragments(fragment);
        btmNavigation.setSelectedItemId(navItemId);
    }


}