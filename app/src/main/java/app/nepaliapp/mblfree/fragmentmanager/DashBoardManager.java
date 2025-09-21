package app.nepaliapp.mblfree.fragmentmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import app.nepaliapp.mblfree.R;
import app.nepaliapp.mblfree.common.StorageClass;

public class DashBoardManager extends AppCompatActivity {

    TextView textView;
    Button logout;
    StorageClass storageClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dash_board);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
init();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashBoardManager.this, SigninManager.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                storageClass.UpdateJwtToken("Jwt_kali_xa");
                finish();
            }
        });

        String who = getIntent().getStringExtra("who");

        assert who != null;
        if (who.equalsIgnoreCase("narayan")) {
            textView.setText("Hello admin, Feeling Happy Thanks for  building");
        } else {
            textView.setText("Hello User, Wait Sometime I am building");
        }

    }
    private void init(){
        logout = findViewById(R.id.btnLogout);
        textView = findViewById(R.id.textview);
        storageClass = new StorageClass(getApplicationContext());
    }
}