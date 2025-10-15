package app.nepaliapp.mblfree.common;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.Objects;
import java.util.UUID;

import app.nepaliapp.mblfree.R;
import app.nepaliapp.mblfree.fragmentmanager.SigninManager;

public class CommonFunctions {


    public static String getDeviceId(Context context) {
        StorageClass storageClass = new StorageClass(context);
        String deviceId = storageClass.getDeviceUniqueID();

        if (Objects.equals(deviceId, "token_kali_xa") || deviceId == null) {
            deviceId = UUID.randomUUID().toString();
            storageClass.UpdateDeviceUniqueID(deviceId);
        }

        return deviceId;
    }

    public static void smartRestart(Context context) {
        if (context == null) return;

        // Case 1: Context is an Activity
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            activity.finish();
            context.startActivity(activity.getIntent());
            return;
        }

        // Case 2: Context is a ContextWrapper (like Fragment or custom context)
        Context baseContext = context;
        while (baseContext instanceof ContextWrapper) {
            if (baseContext instanceof Activity) {
                Activity activity = (Activity) baseContext;
                activity.finish();
                context.startActivity(activity.getIntent());
                return;
            }
            baseContext = ((ContextWrapper) baseContext).getBaseContext();
        }

        // Case 3: Fallback - no Activity found
        Log.e("SmartRestart", "No Activity context found for restart");
    }

    public void showDialogWithPrice(Context context) {
        Url url = new Url();
        RequestQueue requestQueue = MySingleton.getInstance(context).getRequestQueue();
        StorageClass storageClass = new StorageClass(context);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url.getPrice(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String country= storageClass.getUserCountry().trim();
                Log.d("country", country);
                if (country.equalsIgnoreCase("Nepal")){
                    SubscriptionDialog.show(context, jsonObject);
                }else{
                 CouponDialog.show(context,true);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                handleErrorResponse(context, volleyError);
            }
        });
        request.setShouldCache(false);
        requestQueue.add(request);
    }

    public void handleErrorResponse(Context context, VolleyError error) {
        StorageClass storageClass = new StorageClass(context);
        String message = "Unknown error";
        if (error instanceof AuthFailureError) {
            message = "Auth Expired Please Relogin";
            storageClass.UpdateJwtToken("Jwt_kali_xa");
            Intent intent = new Intent(context.getApplicationContext(), SigninManager.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);

            if (context instanceof Activity) {
                ((Activity) context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                ((Activity) context).finish();
            }
        } else if (error instanceof NetworkError) {
            message = "Network Error";
            alertDialogForNetworkError(context);
        } else if (error instanceof ServerError) {
            message = "Server Error";
        } else if (error instanceof TimeoutError) {
            message = "Timeout Error";
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private void alertDialogForNetworkError(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Network Error");
        builder.setMessage("Please check your internet connection and try again.");
        builder.setCancelable(false);
        builder.setPositiveButton("Retry", (dialog, which) -> {
            smartRestart(context);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog alert = builder.create();
        alert.show();

    }

}
