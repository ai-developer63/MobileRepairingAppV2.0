package app.nepaliapp.mblfree.common;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.nepaliapp.mblfree.fragmentmanager.SigninManager;

public class DeviceIDChecker implements DefaultLifecycleObserver {
    private static DeviceIDChecker instance;
    StorageClass storageClass;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Context context;
    private RequestQueue requestQueue;
    private boolean isChecking = false;

    private DeviceIDChecker(Context context) {
        this.context = context.getApplicationContext(); // prevent leaks
        this.requestQueue = MySingleton.getInstance(this.context).getRequestQueue();
        this.storageClass = new StorageClass(this.context);

    }

    // Singleton getter
    public static synchronized DeviceIDChecker getInstance(Context context) {
        if (instance == null) {
            instance = new DeviceIDChecker(context);
            // Attach observer to app lifecycle
            ProcessLifecycleOwner.get().getLifecycle().addObserver(instance);
        }
        return instance;
    }

    private Runnable periodicTask = new Runnable() {
        @Override
        public void run() {
            ReceieveDeviceId();
            // Schedule again after 10 minutes
            handler.postDelayed(this, 5 * 60 * 1000); // 10 minutes
        }
    };

    /** Start the periodic check manually if needed */
    public void startChecking() {
        if (!isChecking) {
            handler.post(periodicTask);
            isChecking = true;
        }
    }


    public void stopChecking() {
        if (isChecking) {
            handler.removeCallbacks(periodicTask);
            isChecking = false;
        }
        if (requestQueue != null) {
            requestQueue.cancelAll("DEVICE_ID_CHECK");
        }
    }

    @Override
    public void onResume(LifecycleOwner owner) {
        if (!isChecking) {
            handler.post(periodicTask);
            isChecking = true;
        }
    }

    @Override
    public void onPause(LifecycleOwner owner) {
        handler.removeCallbacks(periodicTask);
        isChecking = false;
    }


    private void ReceieveDeviceId() {
        Url url = new Url();
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url.getDeviceIdFetch(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String deviceId = response.optString("deviceId");
                Log.d("DeviceIdChecker", "Device ID: " + deviceId);
                Log.d("DeviceIdChecker", "Device ID: " + storageClass.getDeviceUniqueID());
                if (!deviceId.equals(storageClass.getDeviceUniqueID())) {
                    storageClass.UpdateJwtToken("Jwt_kali_xa");
                    Intent intent = new Intent(context.getApplicationContext(), SigninManager.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                    Toast.makeText(context, "Multiple Device Detected", Toast.LENGTH_SHORT).show();
                    stopChecking();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("DeviceIdChecker", "Error: " + error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + storageClass.getJwtToken());
                Log.d("TAG", headers.toString());
                return headers;
            }

        };

        objectRequest.setTag("DEVICE_ID_CHECK");
        requestQueue.add(objectRequest);
    }





}


