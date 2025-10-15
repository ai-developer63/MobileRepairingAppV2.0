package app.nepaliapp.mblfree.fragments.userdash;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import app.nepaliapp.mblfree.R;
import app.nepaliapp.mblfree.fragmentmanager.DashBoardManager;
import app.nepaliapp.mblfree.fragments.servicefragment.VideoTabAdapter;

public class VideosFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    public VideosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_videos, container, false);

        // Back navigation handling
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                fragmentChanger(new PracticalFragment());
                updateNav("practical");
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                callback
        );

        init(view);
        return view;
    }

    private void init(View view) {
        tabLayout = view.findViewById(R.id.tabLayoutVideos);
        viewPager = view.findViewById(R.id.viewPagerVideos);

        // Temporary: set up with simple adapter until real fragments are added
        viewPager.setAdapter(new VideoTabAdapter(this));

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) tab.setText("App Tutorial");
            else tab.setText("Courses Video");
        }).attach();
    }

    private void fragmentChanger(Fragment fragment) {
        if (getActivity() != null && getActivity() instanceof DashBoardManager) {
            ((DashBoardManager) getActivity()).replaceFragments(fragment);
        }
    }

    private void updateNav(String whichSelect) {
        int menuId;
        Fragment fragment;

        switch (whichSelect.toLowerCase()) {
            case "home":
                menuId = R.id.homes;
                fragment = new HomeFragment();
                break;
            case "schematric":
                menuId = R.id.schematri;
                fragment = new ShecmatricCompaniesFragment();
                break;
            case "videos":
                menuId = R.id.Videos;
                fragment = new VideosFragment();
                break;
            case "practical":
                menuId = R.id.practical;
                fragment = new PracticalFragment();
                break;
            default:
                menuId = R.id.homes;
                fragment = new HomeFragment();
                break;
        }

        ((DashBoardManager) requireActivity()).navigateTo(fragment, menuId);
    }
}
