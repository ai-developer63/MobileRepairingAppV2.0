package app.nepaliapp.mblfree.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.nepaliapp.mblfree.R;
import app.nepaliapp.mblfree.common.MySingleton;
import app.nepaliapp.mblfree.common.ResetPasswordStage;
import app.nepaliapp.mblfree.common.Url;
import app.nepaliapp.mblfree.fragmentmanager.SigninManager;

public class ForgetPasswordFragment extends Fragment {

    Button actionButton;
    Context context;

    EditText emailEditText, passwordEditText, otpEditTxt;
    LoginFragment loginFragment = new LoginFragment();
    TextView emailHeaderTxt, passwordHeaderTxt;
    ImageView imageView;
    ToggleButton togglePasswordVisibility;
    Url url;
    FrameLayout loadingOverlay;
    private RequestQueue requestQueue;
    private ResetPasswordStage currentStage;

    public ForgetPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forget_password, container, false);
        init(view);
        currentStage = ResetPasswordStage.EMAIL_INPUT;
        loadingOverlay.setVisibility(View.GONE);
        imageView.setOnClickListener(v -> {
            otpEditTxt.clearFocus();
            if (otpEditTxt.getText().toString().trim().isEmpty()) {
                fragmentChanger(loginFragment);
            } else {
                showWarningAlertDialog(requireContext());
            }
        });


        togglePasswordVisibility.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Show password
                    passwordEditText.setTransformationMethod(null); // Show password in plain text
                } else {
                    // Hide password
                    passwordEditText.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                otpEditTxt.clearFocus();
                if (otpEditTxt.getText().toString().trim().isEmpty()) {
                    fragmentChanger(loginFragment);
                } else {
                    showWarningAlertDialog(requireContext());
                }
            }
        };
        updateUIForCurrentStage();
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentStage.equals(ResetPasswordStage.EMAIL_INPUT)) {
                    if (TextUtils.isEmpty(emailEditText.getText().toString())) {
                        emailEditText.setError("Email is Required");
                        emailEditText.requestFocus();
                    } else if (!emailCheckerRegex(emailEditText.getText().toString())) {
                        emailEditText.setError("Enter valid Email");
                        emailEditText.requestFocus();
                    } else {
                        actionButton.setVisibility(View.GONE);
                        loadingOverlay.setVisibility(View.VISIBLE);
                        sendEmail();
                        currentStage = ResetPasswordStage.OTP_VALIDATION;
                    }
                } else if (currentStage.equals(ResetPasswordStage.PASSWORD_UPDATE)) {
                    emailEditText.setEnabled(false);
                    if (TextUtils.isEmpty(passwordEditText.getText().toString())) {
                        passwordEditText.setError("Password is Required");
                    } else if (!(passwordEditText.getText().length() >= 6)) {
                        passwordEditText.setError("Minimum 6 digit Password is Required");
                    } else {
                        actionButton.setVisibility(View.GONE);


                        updateUIForCurrentStage();
                    }
                } else if (currentStage.equals(ResetPasswordStage.OTP_VALIDATION)) {
                    emailEditText.setEnabled(false);
                    actionButton.setVisibility(View.GONE);
                    verifyOtp();
                    currentStage = ResetPasswordStage.PASSWORD_UPDATE;
                    updateUIForCurrentStage();

                }

            }
        });


        return view;
    }

    private void init(View view) {
        //Initialization
        url = new Url();
        requestQueue = MySingleton.getInstance(requireContext()).getRequestQueue();

        //findViewByID
        actionButton = view.findViewById(R.id.signUpButton);
        emailEditText = view.findViewById(R.id.EmailEdittext);
        passwordEditText = view.findViewById(R.id.PasswordEdittextEdittext);
        otpEditTxt = view.findViewById(R.id.otpEditText);
        imageView = view.findViewById(R.id.backBtn);
        emailHeaderTxt = view.findViewById(R.id.TextEmail);
        passwordHeaderTxt = view.findViewById(R.id.TextPassword);
        togglePasswordVisibility = view.findViewById(R.id.togglePasswordVisibility);
        loadingOverlay = view.findViewById(R.id.loadingOverlay);


    }

    private void fragmentChanger(Fragment fragment) {
        if (getActivity() != null && getActivity() instanceof SigninManager) {
            ((SigninManager) getActivity()).replaceFragments(fragment);
        }

    }

    private void adjustContainerHeight() {
        if (getActivity() != null && getActivity() instanceof SigninManager) {
            ((SigninManager) getActivity()).adjustFragmentHeight();
        }
    }

    private void updateUIForCurrentStage() {
        // Hide all views initially
        otpEditTxt.setVisibility(View.GONE);
        passwordHeaderTxt.setVisibility(View.GONE);
        passwordEditText.setVisibility(View.GONE);
        actionButton.setVisibility(View.GONE);

        switch (currentStage) {
            case EMAIL_INPUT:
                actionButton.setVisibility(View.VISIBLE);
                actionButton.setText("Send OTP");
                break;
            case OTP_VALIDATION:
                otpEditTxt.setVisibility(View.VISIBLE);
                actionButton.setVisibility(View.VISIBLE);
                actionButton.setText("Validate OTP");
                break;
            case PASSWORD_UPDATE:
                passwordEditText.setVisibility(View.VISIBLE);
                actionButton.setVisibility(View.VISIBLE);
                actionButton.setText("Update Password");
                break;
        }
    }

    private boolean emailCheckerRegex(String Email) {
        if (Email.isEmpty()) {
            return false;
        } else {
            Pattern pattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
            Matcher matcher = pattern.matcher(Email);
            return matcher.matches();
        }

    }

    private void showWarningAlertDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Going to Sign in page");
        builder.setMessage("Are you sure you want to go back ?");
        builder.setPositiveButton("Yes", (dialog, which) -> fragmentChanger(new LoginFragment()));
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();

    }

    private JSONObject objectMaker() {
        JSONObject obj = new JSONObject();
        try {
            if (currentStage.equals(ResetPasswordStage.OTP_VALIDATION)) {
                obj.put("email", emailEditText.getText().toString());
                obj.put("otp", otpEditTxt.getText().toString());
            } else if (currentStage.equals(ResetPasswordStage.PASSWORD_UPDATE)) {
                obj.put("email", emailEditText.getText().toString());
                obj.put("newpassword", passwordEditText.getText().toString());
            } else {
                obj.put("email", emailEditText.getText().toString());
            }
        } catch (JSONException e) {
            Toast.makeText(context, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
        }
        return obj;
    }


    private void verifyOtp() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url.getCheckOtp(), objectMaker(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Toast.makeText(context, "OTP Verified", Toast.LENGTH_SHORT).show();
                loadingOverlay.setVisibility(View.GONE);
                currentStage = ResetPasswordStage.PASSWORD_UPDATE;
                emailEditText.setEnabled(false);
                updateUIForCurrentStage();
                adjustContainerHeight();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError instanceof AuthFailureError) {
                    actionButton.setVisibility(View.VISIBLE);
                    emailEditText.setError("Multi Account in our System");
                } else if (volleyError instanceof NetworkError) {
                    Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show();

                } else if (volleyError instanceof ServerError) {
                    Toast.makeText(context, "Server Error", Toast.LENGTH_SHORT).show();

                } else if (volleyError instanceof TimeoutError) {
                    Toast.makeText(context, "Timeout Error", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "SomeThing went Wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void sendEmail() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url.getOtpRequest(),
                objectMaker(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        if (!isAdded()) return;
                        Toast.makeText(context, "OTP Sent", Toast.LENGTH_SHORT).show();
                        loadingOverlay.setVisibility(View.GONE);
                        currentStage = ResetPasswordStage.OTP_VALIDATION;
                        emailEditText.setEnabled(false);
                        updateUIForCurrentStage();
                        adjustContainerHeight();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (volleyError instanceof AuthFailureError) {
                            loadingOverlay.setVisibility(View.GONE);
                            actionButton.setVisibility(View.VISIBLE);
                            emailEditText.setError("Multiple accounts in our system");
                        } else if (volleyError instanceof NetworkError) {
                            Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show();
                        } else if (volleyError instanceof ServerError) {
                            Toast.makeText(context, "Server Error", Toast.LENGTH_SHORT).show();
                        } else if (volleyError instanceof TimeoutError) {
                            Toast.makeText(context, "Timeout Error", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // Set RetryPolicy once
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                30 * 1000, // 30-second timeout
                0,         // no retries
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(jsonObjectRequest);
    }


}