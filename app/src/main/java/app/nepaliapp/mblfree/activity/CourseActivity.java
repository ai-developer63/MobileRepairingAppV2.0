package app.nepaliapp.mblfree.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import app.nepaliapp.mblfree.common.CommonFunctions;
import app.nepaliapp.mblfree.common.MySingleton;
import app.nepaliapp.mblfree.common.StorageClass;
import app.nepaliapp.mblfree.common.Url;
import app.nepaliapp.mblfree.fragmentmanager.DashBoardManager;
import app.nepaliapp.mblfree.recyclerAdapter.CourseSetterAdapter;

public class CourseActivity extends AppCompatActivity {
    TextView heading;
    ImageView backBtn;
    RecyclerView courseRecycler;
    Url url;
    RequestQueue requestQueue;
    CommonFunctions commonFunctions;
    FrameLayout loadingOverlay;
    StorageClass storageClass;
    JSONArray array;

    //Added for Video
    private CourseSetterAdapter courseAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_course);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            String courseData = intent.getStringExtra("courseData");
            String fromWhere = intent.getStringExtra("fromWhere");
            heading.setText(courseData);
            getCourseContent(courseData, fromWhere);

            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent1 = new Intent(CourseActivity.this, DashBoardManager.class);
                    if ("main".equalsIgnoreCase(fromWhere)) {
                        intent1.putExtra("openThisFragment", "CourseFirstFragment");
                    } else {
                        intent1.putExtra("courseData", courseData);
                        intent1.putExtra("openThisFragment", "CourseSubTopicFragment");
                    }
                    intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent1);
                    finish(); // optional: close current activity
                }
            });

        }


    }

    private void init() {
        heading = findViewById(R.id.Heading);
        backBtn = findViewById(R.id.backIcon);
        courseRecycler = findViewById(R.id.course);
        loadingOverlay = findViewById(R.id.loadingOverlay);


        //Initialization
        url = new Url();
        requestQueue = MySingleton.getInstance(this).getRequestQueue();
        storageClass = new StorageClass(this);
        commonFunctions  = new CommonFunctions();

    }

    private void getCourseContent(String TopicName, String fromWhere) {

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url.getRequestCourse(TopicName), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("courseObject", jsonObject.toString());

                array = jsonObject.optJSONArray("course");
                if (array!=null){
                    courseRecycler.setLayoutManager(new LinearLayoutManager(CourseActivity.this));
                    courseAdapter = new CourseSetterAdapter(CourseActivity.this, jsonObject.optJSONArray("course"));
                    courseRecycler.setAdapter(courseAdapter);
                    loadingOverlay.setVisibility(View.GONE);
                }else {
                    Intent intent1 = CourseActivity.this.getIntent(fromWhere, TopicName);
                    startActivity(intent1);
                    finish(); // optional: close current activity
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                commonFunctions.handleErrorResponse(CourseActivity.this, volleyError);
                loadingOverlay.setVisibility(View.GONE);
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + storageClass.getJwtToken());
                return headers;
            }
        };
        objectRequest.setShouldCache(false);
        requestQueue.add(objectRequest);
    }

    @NonNull
    private Intent getIntent(String fromWhere, String TopicName) {
        Intent intent1 = new Intent(CourseActivity.this, DashBoardManager.class);
        if ("main".equalsIgnoreCase(fromWhere)) {
            intent1.putExtra("openThisFragment", "CourseFirstFragment");
        } else {
            intent1.putExtra("courseData", TopicName);
            intent1.putExtra("openThisFragment", "CourseSubTopicFragment");
        }
        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return intent1;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (courseAdapter != null) {  // your RecyclerView adapter instance
            courseAdapter.releasePlayer();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (courseAdapter != null) {
            courseAdapter.releasePlayer();
        }
    }


}