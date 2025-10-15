package app.nepaliapp.mblfree.common;

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

import androidx.annotation.OptIn;
import androidx.media3.common.util.UnstableApi;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.nepaliapp.mblfree.R;
import app.nepaliapp.mblfree.activity.VideoPlayingActivity;

public class CouponDialog {


    public static void show(Context context, Boolean isCountryChecked) {
        StorageClass storageClass = new StorageClass(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_coupon, null);

        // UI references
        EditText editTextCoupon = dialogView.findViewById(R.id.edittext_coupon);
        Button buttonRedeem = dialogView.findViewById(R.id.button_redeem);

        Button buttonRequest = dialogView.findViewById(R.id.button_request);

        ImageView buttonHelp = dialogView.findViewById(R.id.icon_help_video);
        ImageView closeBtn = dialogView.findViewById(R.id.closeicon);
        if (!isCountryChecked) {
            if (storageClass.getUserCountry().equalsIgnoreCase("Nepal")){
                buttonRequest.setVisibility(View.GONE);
            }else {
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
                if (editTextCoupon.getText().toString().trim().isEmpty()) {
                    Toast.makeText(context, "Please enter your coupon code", Toast.LENGTH_SHORT).show();

                    // Move cursor & focus to EditText
                    editTextCoupon.requestFocus();
                    editTextCoupon.setSelection(editTextCoupon.getText().length());
                }
            } else {
                // TODO: Handle redeem logic here
                Toast.makeText(context, "Redeeming: " + code, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        // 💬 Request Coupon
        buttonRequest.setOnClickListener(v -> {
            String messengerLink = "https://m.me/106704358421953?text=" + Uri.encode("I want to request a coupon code.");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(messengerLink));
            context.startActivity(intent);
            dialog.dismiss();
        });

        // 🎥 Help Button
        buttonHelp.setOnClickListener(v -> {
            if (storageClass.getUserCountry().equalsIgnoreCase("Nepal")) {
                getRequestedVideos(context, "Mobile Repairing Marketing Video");
            } else {
                getRequestedVideos(context, "Ads English");
            }

        });


        // ❌ Close Button
        closeBtn.setOnClickListener(v -> dialog.dismiss());
    }

    private static void getRequestedVideos(Context context, String videoTitle) {
        CommonFunctions commonFunctions = new CommonFunctions();
        RequestQueue requestQueue = MySingleton.getInstance(context).getRequestQueue();
        Url url = new Url();
        StorageClass storageClass = new StorageClass(context);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url.getHomeVideos(), null, new Response.Listener<JSONObject>() {
            @OptIn(markerClass = UnstableApi.class)
            @Override
            public void onResponse(JSONObject jsonObject) {
                JSONArray array = jsonObject.optJSONArray("videos");
                if (array != null && videoTitle != null) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.optJSONObject(i);
                        if (object != null) {
                            String title = object.optString("title");
                            if (videoTitle.equals(title)) {
                                // Found the video matching the title
                                Intent intent = new Intent(context, VideoPlayingActivity.class);
                                intent.putExtra("videoUrl", object.optString("link"));
                                intent.putExtra("videoTitle", title);
                                intent.putExtra("videoListJson", array.toString());
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                                break;
                            }
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                commonFunctions.handleErrorResponse(context, volleyError);

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
