package app.nepaliapp.mblfree.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import app.nepaliapp.mblfree.R;

public class MaintainaceActivity extends AppCompatActivity {

    private static final String TAG = "MaintainaceActivity";
    private TextView tvCountdownTimer;
    FrameLayout loadingOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_maintainace);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findByID();
        loadingOverlay.setVisibility(View.VISIBLE);


        // Fetch Nepali time from API
        fetchNepaliTime(getIntent().getStringExtra("END_TIME"));
    }

    private void findByID() {
        tvCountdownTimer = findViewById(R.id.tvCountdownTimer);
        loadingOverlay = findViewById(R.id.loadingOverlay);
    }

    private void fetchNepaliTime(String END_TIME) {
        String apiUrl = "https://timeapi.io/api/Time/current/zone?timeZone=Asia/Kathmandu";

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                apiUrl,
                null,
                response -> {
                    try {
                        String dateTime = response.getString("dateTime");

                        // Parse API datetime
                        SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                        apiFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kathmandu"));
                        Date currentDate = apiFormat.parse(dateTime.substring(0, 19));

                        // Parse maintenance end time
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kathmandu"));
                        Date endDate = sdf.parse(END_TIME);

                        if (currentDate != null && endDate != null) {
                            long diffMillis = endDate.getTime() - currentDate.getTime();
                            startCountdown(diffMillis);
                            loadingOverlay.setVisibility(View.GONE);
                        }

                    } catch (Exception e) {
                        Log.e(TAG, "Parse error: " + e.getMessage());
                        tvCountdownTimer.setText("Error");
                        loadingOverlay.setVisibility(View.GONE);
                    }
                },
                error -> {
                    Log.e(TAG, "API Error: " + error.toString());
                    tvCountdownTimer.setText("Failed to load time");
                    loadingOverlay.setVisibility(View.GONE);
                }
        );
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000, // wait max 10 seconds
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        queue.add(jsonObjectRequest);
    }

    private void startCountdown(long diffMillis) {
        if (diffMillis <= 0) {
            tvCountdownTimer.setText("00:00:00");
            loadingOverlay.setVisibility(View.GONE);
            return;
        }

        new CountDownTimer(diffMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long days = millisUntilFinished / (1000 * 60 * 60 * 24);
                long hours = (millisUntilFinished / (1000 * 60 * 60)) % 24;
                long minutes = (millisUntilFinished / (1000 * 60)) % 60;
                long seconds = (millisUntilFinished / 1000) % 60;

                String timeFormatted;
                if (days > 0) {
                    timeFormatted = String.format(Locale.getDefault(),
                            "%d days %02d:%02d:%02d", days, hours, minutes, seconds);
                } else {
                    timeFormatted = String.format(Locale.getDefault(),
                            "%02d:%02d:%02d", hours, minutes, seconds);
                }

                tvCountdownTimer.setText(timeFormatted);
                loadingOverlay.setVisibility(View.GONE);
            }

            @Override
            public void onFinish() {
                tvCountdownTimer.setText("00:00:00");
            }
        }.start();
    }



}
