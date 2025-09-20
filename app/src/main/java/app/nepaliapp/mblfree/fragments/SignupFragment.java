package app.nepaliapp.mblfree.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import app.nepaliapp.mblfree.R;
import app.nepaliapp.mblfree.fragmentmanager.SigninManager;

public class SignupFragment extends Fragment {
    ImageView backImg;
    LinearLayout stepOneLayout, stepTwoLayout;
    AppCompatButton nextButton, signUpButton, backToStepOneButton;
    EditText nameEditText, emailEditText, passwordEditText, refferedEditText, phoneEditText;

    public SignupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        init(view);
        stepOneLayout.setVisibility(View.VISIBLE);
        stepTwoLayout.setVisibility(View.GONE);
        nextButton.setOnClickListener(v -> {
            stepOneLayout.setVisibility(View.GONE);
            stepTwoLayout.setVisibility(View.VISIBLE);
        });
        backToStepOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stepOneLayout.setVisibility(View.VISIBLE);
                stepTwoLayout.setVisibility(View.GONE);
            }
        });
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isVisible(stepOneLayout)) {
                    if (nameEditText.getText().toString().isEmpty() && emailEditText.getText().toString().isEmpty() && passwordEditText.getText().toString().isEmpty() && phoneEditText.getText().toString().isEmpty()) {
                        fragmentChanger(new LoginFragment());
                    } else {
                        showWarningAlertDialog(requireContext());
                    }
                } else {
                    stepOneLayout.setVisibility(View.VISIBLE);
                    stepTwoLayout.setVisibility(View.GONE);
                }
            }
        });


        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isVisible(stepOneLayout)) {
                    if (nameEditText.getText().toString().isEmpty() && emailEditText.getText().toString().isEmpty() && passwordEditText.getText().toString().isEmpty() && phoneEditText.getText().toString().isEmpty()) {
                        fragmentChanger(new LoginFragment());
                    } else {
                        showWarningAlertDialog(requireContext());
                    }
                } else {
                    stepOneLayout.setVisibility(View.VISIBLE);
                    stepTwoLayout.setVisibility(View.GONE);
                }
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                callback
        );

        return view;
    }

    private void fragmentChanger(Fragment fragment) {
        if (getActivity() != null && getActivity() instanceof SigninManager) {
            ((SigninManager) getActivity()).replaceFragments(fragment);
        }

    }

    public void init(View view) {
        // Step groups
        stepOneLayout = view.findViewById(R.id.stepOneLayout);
        stepTwoLayout = view.findViewById(R.id.stepTwoLayout);

        // Buttons
        nextButton = view.findViewById(R.id.nextButton);
        signUpButton = view.findViewById(R.id.signUpButton);
        backToStepOneButton = view.findViewById(R.id.backToStepOneButton);
        backImg = view.findViewById(R.id.backBtn);
        // EditTexts
        nameEditText = view.findViewById(R.id.FullName);
        emailEditText = view.findViewById(R.id.EmailEdittext);
        passwordEditText = view.findViewById(R.id.PasswordEdittextEdittext);
        refferedEditText = view.findViewById(R.id.Referral_id);
        phoneEditText = view.findViewById(R.id.PhoneEdittext);


    }

    private boolean isVisible(View v) {
        return v.getVisibility() == View.VISIBLE;
    }

    private void showWarningAlertDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Going to Sign in page");
        builder.setMessage("Are you sure you want back? All Entered data will be lost");
        builder.setPositiveButton("Yes", (dialog, which) -> fragmentChanger(new LoginFragment()));
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();

    }
}