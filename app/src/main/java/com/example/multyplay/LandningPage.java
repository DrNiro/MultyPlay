package com.example.multyplay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class LandningPage extends AppCompatActivity {

    private ImageView landing_IMG_LOGO;
    private TextView landing_TXT_startBtn;

    private MySharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landning_page);

        prefs = new MySharedPreferences(this);

        String loggedState = prefs.getString(Constants.PREFS_KEY_CURRENT_LOGGED_IN, Constants.LOGGED_OUT);
        if(loggedState.equals(Constants.LOGGED_IN)) {
//            prefs.putString(Constants.PREFS_KEY_CURRENT_LOGGED_IN, Constants.LOGGED_IN);
            Intent profileIntent = new Intent(LandningPage.this, FragmentManager.class);
            profileIntent.putExtra(Constants.KEY_MARK_IS_OWNER, Constants.MY_PROFILE);
            startActivity(profileIntent);
            finish();
        }

        landing_IMG_LOGO = findViewById(R.id.landing_IMG_LOGO);
        landing_TXT_startBtn = findViewById(R.id.landing_TXT_startBtn);

        landing_TXT_startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(LandningPage.this, Login.class);
                startActivity(loginIntent);
                finish();
            }
        });

    }
}
