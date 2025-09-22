package app.nepaliapp.mblfree.fragments.userdash;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import app.nepaliapp.mblfree.R;
import app.nepaliapp.mblfree.fragmentmanager.DashBoardManager;

public class PracticalFragment extends Fragment {


    public PracticalFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_practical, container, false);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                fragmentChanger(new ShematricFragment());
                updateNav("schematric");
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                callback
        );
        return view;
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
                fragment = new ShematricFragment();
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