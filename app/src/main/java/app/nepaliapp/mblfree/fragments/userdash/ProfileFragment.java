package app.nepaliapp.mblfree.fragments.userdash;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.nepaliapp.mblfree.R;
import app.nepaliapp.mblfree.common.CommonFunctions;
import app.nepaliapp.mblfree.common.CouponDialog;
import app.nepaliapp.mblfree.common.MySingleton;
import app.nepaliapp.mblfree.common.StorageClass;
import app.nepaliapp.mblfree.common.Url;
import app.nepaliapp.mblfree.fragmentmanager.DashBoardManager;
import app.nepaliapp.mblfree.fragmentmanager.SigninManager;

public class ProfileFragment extends Fragment {
    CardView logoutBtn;
    StorageClass storageClass;
    Url url;

    TextView helloUserText, welcomeText, countryText, accountType, subscriptionDays, emailText;
    MaterialButton upgrade;
    RequestQueue requestQueue;
    FrameLayout loadingOverlay;
    CommonFunctions commonFunctions;
    CardView reedemCopoun;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        init(view);
        getProfile();

        upgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commonFunctions.showDialogWithPrice(requireContext());
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertForLogout(requireContext());
            }
        });

        reedemCopoun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CouponDialog.show(requireContext(),false);
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
        helloUserText = view.findViewById(R.id.tvHelloUser);
        welcomeText = view.findViewById(R.id.tvWelcome);
        countryText = view.findViewById(R.id.tvCountry);
        accountType = view.findViewById(R.id.tvAccountType);
        subscriptionDays = view.findViewById(R.id.tvSubscription);
        upgrade = view.findViewById(R.id.btnUpgrade);
        emailText = view.findViewById(R.id.tvEmail);
        loadingOverlay = view.findViewById(R.id.loadingOverlay);
        reedemCopoun = view.findViewById(R.id.menu_redeem_coupon);
        //Initialization
        storageClass = new StorageClass(requireContext());
        url = new Url();
        requestQueue = MySingleton.getInstance(requireContext()).getRequestQueue();
        commonFunctions = new CommonFunctions();
    }


    private void getProfile() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url.getRequestProfile(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (!isAdded()){
                    return;
                }
                String country = jsonObject.optString("country");
                Boolean accountTypes = jsonObject.optBoolean("isPaid");
                String subscriptionDay = jsonObject.optString("days");
                emailText.setText(jsonObject.optString("email"));
                String firstName = getFirstName(jsonObject.optString("name"));
                if (accountTypes) {
                    accountType.setText("Premium Account");
                    try {
                        int days = Integer.parseInt(subscriptionDay);

                        if (days > 500) {
                            subscriptionDays.setText("LifeTime Membership");
                        } else {
                            subscriptionDays.setText("Remaining Days: " + days);
                        }
                    } catch (NumberFormatException e) {
                        subscriptionDays.setText("Invalid days");
                    }
                    subscriptionDays.setVisibility(View.VISIBLE);
                    upgrade.setVisibility(View.GONE);
                } else {
                    accountType.setText("Free Account");
                    subscriptionDays.setVisibility(View.GONE);
                    upgrade.setVisibility(View.VISIBLE);
                }
                if (country.equalsIgnoreCase("Nepal")) {
                    helloUserText.setText("Namaste, " + firstName);
                    welcomeText.setText("Mobile Repairing App मा स्वागत छ");
                } else {
                    helloUserText.setText("Hello, " + firstName);
                    welcomeText.setText("Welcome to Mobile Repairing App");
                }
                countryText.setText(country);
                loadingOverlay.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (!isAdded()){
                    return;
                }
                loadingOverlay.setVisibility(View.GONE);
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
        request.setShouldCache(false);
        requestQueue.add(request);
    }


    private void alertForLogout(Context context) {
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
                        Intent intent = new Intent(context.getApplicationContext(), SigninManager.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);

                        if (context instanceof Activity) {
                            ((Activity) context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            ((Activity) context).finish();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.red));
    }


    public String getFirstName(String fullName) {
        if (fullName == null || fullName.isEmpty()) return "";

        String[] parts = fullName.trim().split("\\s+"); // split by spaces
        return parts.length > 0 ? parts[0] : "";
    }

    private void fragmentChanger(Fragment fragment) {
        if (getActivity() != null && getActivity() instanceof DashBoardManager) {
            ((DashBoardManager) getActivity()).replaceFragments(fragment);
        }

    }



}