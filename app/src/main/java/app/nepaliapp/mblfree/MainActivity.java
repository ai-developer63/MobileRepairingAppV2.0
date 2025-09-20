package app.nepaliapp.mblfree;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import app.nepaliapp.mblfree.activity.MaintainaceActivity;
import app.nepaliapp.mblfree.common.MySingleton;
import app.nepaliapp.mblfree.common.StorageClass;
import app.nepaliapp.mblfree.common.Url;
import app.nepaliapp.mblfree.fragmentmanager.SigninManager;

public class MainActivity extends AppCompatActivity {
    private static final int SPLASH_DELAY = 2000;
    RequestQueue requestQueue;
    Url url;
    ProgressBar progressBar;
    Button restartBtn;
    TextView sloganTxt;
    StorageClass storageClass;

    public static int compareVersion(String localVersion, String serverVersion) {
        if (localVersion == null || serverVersion == null) return 0;

        String[] localParts = localVersion.split("\\.");
        String[] serverParts = serverVersion.split("\\.");

        int length = Math.max(localParts.length, serverParts.length);

        for (int i = 0; i < length; i++) {
            int localPart = i < localParts.length ? Integer.parseInt(localParts[i]) : 0;
            int serverPart = i < serverParts.length ? Integer.parseInt(serverParts[i]) : 0;

            if (localPart < serverPart) return -1;
            if (localPart > serverPart) return 1;
            // else continue
        }
        return 0; // equal
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findingById();
        restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(0, 0); // Disable close animation
                recreate(); // Restart the activity
                overridePendingTransition(0, 0); // Disable start animation
            }
        });

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url.getApp_checkup(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String serverContext = jsonObject.optString("mobilerepairingapp");
                if (serverContext.equalsIgnoreCase("running")) {
                    String serverVersion = jsonObject.optString("mblversonName");
                    String localVersion = getAppVersionName();
                    int result = compareVersion(localVersion, serverVersion);
                    if (result < 0) {
                        showUpdateDialog(jsonObject.optBoolean("mblupdatecancelable"), isUpdateNow -> {
                            if (isUpdateNow) {
                                redirectToPlayStore();
                            } else {
                                storageClass.userUpdateDecision("later");
                            }
                        });
                    } else if (result == 0) {
                        Toast.makeText(MainActivity.this, "App will go forward", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this,SigninManager.class));
                    } else {
                        //noinspection SpellCheckingInspection
                        Toast.makeText(MainActivity.this, "Hi Subash, How are you??", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this,SigninManager.class));
                    }
                } else if (serverContext.equalsIgnoreCase("maintainace")) {
                    Intent intent = new Intent(MainActivity.this, MaintainaceActivity.class);
                    intent.putExtra("END_TIME", jsonObject.optString("mblmaintainceEnd"));
                    startActivity(intent);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                progressBar.setVisibility(View.INVISIBLE);
                restartBtn.setVisibility(View.VISIBLE);
                String message = "Unknown error";
                if (volleyError instanceof AuthFailureError) {
                    message = "Email or Password is wrong";
                } else if (volleyError instanceof NetworkError) {
                    message = "Network Error";
                } else if (volleyError instanceof ServerError) {
                    message = "Server Error";
                } else if (volleyError instanceof TimeoutError) {
                    message = "Timeout Error";
                }
                sloganTxt.setText(message);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);

    }

    private void findingById() {
        progressBar = findViewById(R.id.progressBar);
        restartBtn = findViewById(R.id.retryBtn);
        sloganTxt = findViewById(R.id.slogan);

        //initialization
        storageClass = new StorageClass(getApplicationContext());
        requestQueue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();
        url = new Url();
    }

    private String getAppVersionName() {
        try {
            Context context = getApplicationContext();
            PackageManager pm = context.getPackageManager();
            PackageInfo pInfo = pm.getPackageInfo(context.getPackageName(), 0);

            return pInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void showUpdateDialog(boolean isCancelable, UpdateDialogCallback callback) {
        // Inflate custom layout
        View dialogView = getLayoutInflater().inflate(R.layout.update_layout, null);

        // Create dialog
        final android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(isCancelable)
                .create();

        // Initialize views
        ImageView ivLogo = dialogView.findViewById(R.id.ivAppLogo);
        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        TextView tvMessage = dialogView.findViewById(R.id.tvDialogMessage);
        Button btnUpdate = dialogView.findViewById(R.id.btnUpdateNow);
        Button btnLater = dialogView.findViewById(R.id.btnLater);

        if (!isCancelable) {
            btnLater.setVisibility(View.GONE);
        }

        // Button listeners
        btnUpdate.setOnClickListener(v -> {
            dialog.dismiss();
            if (callback != null) callback.onUpdateClicked(true);
        });

        btnLater.setOnClickListener(v -> {
            dialog.dismiss();
            if (callback != null) callback.onUpdateClicked(false);
        });

        // Show dialog
        dialog.show();
    }

    private void redirectToPlayStore() {
        String packageName = "app.nepaliapp.mblfree";
        try {
            // Try to open Play Store app
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + packageName)));
        } catch (android.content.ActivityNotFoundException e) {
            // Play Store app not installed → open in browser
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
        }
    }

    public interface UpdateDialogCallback {
        void onUpdateClicked(boolean isUpdateNow);
    }
}