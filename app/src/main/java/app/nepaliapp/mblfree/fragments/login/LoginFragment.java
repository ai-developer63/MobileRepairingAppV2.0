package app.nepaliapp.mblfree.fragments.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.nepaliapp.mblfree.MainActivity;
import app.nepaliapp.mblfree.R;
import app.nepaliapp.mblfree.common.CommonFunctions;
import app.nepaliapp.mblfree.common.MySingleton;
import app.nepaliapp.mblfree.common.StorageClass;
import app.nepaliapp.mblfree.common.Url;
import app.nepaliapp.mblfree.fragmentmanager.DashBoardManager;
import app.nepaliapp.mblfree.fragmentmanager.SigninManager;

public class LoginFragment extends Fragment {

    Button signinButton, signUpButton;
    TextView forgetpass;
    EditText emailEditText, passwordEditText;
    ToggleButton togglePasswordVisibility;
    Context context;
    Url url;
    StorageClass storageClass;
    private RequestQueue requestQueue;
    FrameLayout loadingOverlay;
    CommonFunctions commonFunctions;
    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        this.requestQueue = MySingleton.getInstance(context).getRequestQueue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        init(view);


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

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    emailEditText.setError("Email is required");
                    emailEditText.requestFocus();
                    return;
                }
                if (!emailCheckerRegex(email)) {
                    emailEditText.setError("invalid Email");
                    emailEditText.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    passwordEditText.setError("Password is required");
                    passwordEditText.requestFocus();
                    return;
                }
                if (password.length() >= 6) {
                    sendLoginRequest();
                } else {
                    passwordEditText.setError("Password must be at least 6 characters");
                    passwordEditText.requestFocus();
                }


            }
        });


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentChanger(new SignupFragment());
            }
        });
        forgetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentChanger(new ForgetPasswordFragment());
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitAlertDialog(requireContext());
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                callback
        );
        return view;
    }

    private void showExitAlertDialog(Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Exit App");
        builder.setMessage("Are you sure you want to exit the app?");
        builder.setPositiveButton("Yes", (dialog, which) -> requireActivity().finishAffinity());
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();

    }

    private void fragmentChanger(Fragment fragment) {
        if (getActivity() != null && getActivity() instanceof SigninManager) {
            ((SigninManager) getActivity()).replaceFragments(fragment);
        }

    }

    private void init(View view) {
        signUpButton = view.findViewById(R.id.signUpButton);
        forgetpass = view.findViewById(R.id.Forgetpassword);
        signinButton = view.findViewById(R.id.signinButton);
        emailEditText = view.findViewById(R.id.EmailEditText);
        passwordEditText = view.findViewById(R.id.PasswordEdittext);
        togglePasswordVisibility = view.findViewById(R.id.togglePasswordVisibility);
        loadingOverlay = view.findViewById(R.id.loadingOverlay);
        //initialization
        url = new Url();
        storageClass = new StorageClass(context);
        commonFunctions = new CommonFunctions();
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

    private void sendLoginRequest() {
        loadingOverlay.setVisibility(View.VISIBLE);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url.getLogin(), objectMaker(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String token = response.getString("token");
                            storageClass.UpdateJwtToken(token);
                            UpdateCountry();
                            Intent intent = new Intent(getActivity(), DashBoardManager.class);
                            intent.putExtra("who", response.optString("who"));
                            startActivity(intent);
                        } catch (JSONException e) {
                            Toast.makeText(context, "Error on Getting Server Code", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadingOverlay.setVisibility(View.GONE);
                        handleErrorResponse(error);
                    }
                });

        requestQueue.add(request);
    }

    private void handleErrorResponse(VolleyError error) {
        String message = "Unknown error";
        if (error instanceof AuthFailureError) {
            message = "Email or Password is wrong";
        } else if (error instanceof NetworkError) {
            message = "Network Error";
        } else if (error instanceof ServerError) {
            message = "Server Error";
        } else if (error instanceof TimeoutError) {
            message = "Timeout Error";
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public JSONObject objectMaker() {
        JSONObject object = new JSONObject();
        try {
            String deviceID = CommonFunctions.getDeviceId(context);

            object.put("emailOrPhone", emailEditText.getText().toString().trim());
            object.put("password", passwordEditText.getText().toString().trim());
            object.put("deviceId", deviceID);

        } catch (JSONException e) {
            Toast.makeText(context, "Data Creation went Wrong", Toast.LENGTH_SHORT).show();
            Log.e("TAG", "JSON error", e);
        }

        Log.d("TAG", "Request JSON: " + object.toString());
        return object;
    }

    private void UpdateCountry(){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url.getUpdateCountry(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                storageClass.UpdateUserCountry(jsonObject.optString("country"));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                commonFunctions.handleErrorResponse(requireContext(),volleyError);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + storageClass.getJwtToken());
                return headers;
            }
        };
        requestQueue.add(request);
    }

}