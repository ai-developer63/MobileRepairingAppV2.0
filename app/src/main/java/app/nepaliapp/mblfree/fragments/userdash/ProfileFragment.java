package app.nepaliapp.mblfree.fragments.userdash;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import app.nepaliapp.mblfree.R;
import app.nepaliapp.mblfree.common.StorageClass;
import app.nepaliapp.mblfree.fragmentmanager.DashBoardManager;
import app.nepaliapp.mblfree.fragmentmanager.SigninManager;
import app.nepaliapp.mblfree.fragments.login.LoginFragment;

public class ProfileFragment extends Fragment {
    CardView logoutBtn;
    StorageClass storageClass;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        init(view);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertForLogout();
            }
        });


        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                fragmentChanger(new HomeFragment());
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                callback
        );
        return view;
    }


    private void init(View view) {
        logoutBtn = view.findViewById(R.id.menu_logout);
        //Initialization
        storageClass = new StorageClass(requireContext());
    }


    private void alertForLogout(){
        Drawable icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_logout);
        if (icon != null) {
            icon.setTint(ContextCompat.getColor(requireContext(), R.color.red));
        }
      AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setIcon(icon)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        storageClass.UpdateJwtToken("Jwt_kali_xa");
                        Intent intent = new Intent(requireActivity(), SigninManager.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        requireActivity().finish();
                    }
                })
                .setNegativeButton("Cancel",null)
                .show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.red));
    }



    private void fragmentChanger(Fragment fragment) {
        if (getActivity() != null && getActivity() instanceof DashBoardManager) {
            ((DashBoardManager) getActivity()).replaceFragments(fragment);
        }

    }
}