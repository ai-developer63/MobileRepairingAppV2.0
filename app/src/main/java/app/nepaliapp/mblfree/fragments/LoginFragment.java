package app.nepaliapp.mblfree.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import app.nepaliapp.mblfree.R;
import app.nepaliapp.mblfree.fragmentmanager.SigninManager;

public class LoginFragment extends Fragment {

    Button signinButton, signUpButton;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        init(view);
signUpButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        fragmentChanger(new SignupFragment());
    }
});

        return view;
    }
    private void fragmentChanger(Fragment fragment) {
        if (getActivity() != null && getActivity() instanceof SigninManager) {
            ((SigninManager) getActivity()).replaceFragments(fragment);
        }

    }

    private void init(View view) {
        signUpButton = view.findViewById(R.id.signUpButton);

    }

}