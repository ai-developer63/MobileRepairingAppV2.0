package app.nepaliapp.mblfree.common;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.nepaliapp.mblfree.R;

public class CouponDialog {

    // ✅ Added interface for callback
    public interface OnCouponRedeemedListener {
        void onCouponRedeemed();
    }

    // ✅ Updated method signature to include listener
    public static void show(Context context, Boolean isCountryChecked, String fromWhere, OnCouponRedeemedListener listener) {
        StorageClass storageClass = new StorageClass(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        CommonFunctions commonFunctions = new CommonFunctions();
        View dialogView = inflater.inflate(R.layout.dialog_coupon, null);

        // UI references
        EditText editTextCoupon = dialogView.findViewById(R.id.edittext_coupon);
        Button buttonRedeem = dialogView.findViewById(R.id.button_redeem);
        Button buttonRequest = dialogView.findViewById(R.id.button_request);
        ImageView buttonHelp = dialogView.findViewById(R.id.icon_help_video);
        ImageView closeBtn = dialogView.findViewById(R.id.closeicon);

        if (!isCountryChecked) {
            if (storageClass.getUserCountry().equalsIgnoreCase("Nepal")) {
                buttonRequest.setVisibility(View.GONE);
            } else {
                buttonRequest.setVisibility(View.VISIBLE);
            }
        }

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        // 🔘 Redeem Button
        buttonRedeem.setOnClickListener(v -> {
            String code = editTextCoupon.getText().toString().trim();
            if (code.isEmpty()) {
                Toast.makeText(context, "Please enter your coupon code", Toast.LENGTH_SHORT).show();
                editTextCoupon.requestFocus();
                editTextCoupon.setSelection(editTextCoupon.getText().length());
            } else {
                updateCopounCode(context, code, listener);
                Toast.makeText(context, "Redeeming: " + code, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        // 💬 Request Coupon
        buttonRequest.setOnClickListener(v -> {
            updateRequest(context, "copoun code", fromWhere);

            String messengerLink = "https://m.me/110702794806928?text=" + Uri.encode("I want to request a coupon code.");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(messengerLink));

            // Modern check: only start if an app can handle it
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);  // ✅ Use context here
            } else {
                // Fallback: open in browser explicitly
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://m.me/110702794806928"));
                context.startActivity(browserIntent);
            }

            dialog.dismiss();
        });

        // 🎥 Help Button
        buttonHelp.setOnClickListener(v -> {
            if (storageClass.getUserCountry().equalsIgnoreCase("Nepal")) {
                CommonFunctions.getRequestedVideos(context, "Mobile Repairing Marketing Video");
            } else {
                CommonFunctions.getRequestedVideos(context, "Ads English");
            }
        });

        // ❌ Close Button
        closeBtn.setOnClickListener(v -> dialog.dismiss());
    }

    private static void updateRequest(Context context, String chooseOption, String fromWhere) {
        RequestQueue requestQueue = MySingleton.getInstance(context).getRequestQueue();
        Url url = new Url();
        StorageClass storageClass = new StorageClass(context);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url.getPurchaseRequest(), objectMaker(fromWhere, chooseOption), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Toast.makeText(context, "Requested Success, we will contact soon", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
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

    public static JSONObject objectMaker(String fromWhere, String subscriptionType) {
        JSONObject object = new JSONObject();
        try {
            object.put("fromWhere", fromWhere);
            object.put("subscriptionType", subscriptionType);
        } catch (JSONException e) {
        }
        return object;
    }

    private static void updateCopounCode(Context context, String coupon, OnCouponRedeemedListener listener) {
        RequestQueue requestQueue = MySingleton.getInstance(context).getRequestQueue();
        Url url = new Url();
        StorageClass storageClass = new StorageClass(context);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url.getCouponCode(), objectCouponMaker(coupon),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Toast.makeText(context, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();

                        if (jsonObject.optBoolean("success") && listener != null) {
                            listener.onCouponRedeemed(); // ✅ Trigger callback after success
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
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

    public static JSONObject objectCouponMaker(String coupon) {
        JSONObject object = new JSONObject();
        try {
            object.put("couponCode", coupon);
        } catch (JSONException e) {
        }
        return object;
    }
}
