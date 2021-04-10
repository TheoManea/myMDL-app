package com.mymdl.george.myymdll;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.mymdl.george.myymdll.R;
import com.nightonke.jellytogglebutton.JellyToggleButton;

import org.w3c.dom.Text;

public class userSettings extends AppCompatActivity {

    // layout
    Switch prefNotif;
    Button changeSchoolBtn, resetSurveyBtn;
    JellyToggleButton jelly;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        // Pout NavigationDrawer/Menu
        mDrawerLayout = findViewById(R.id.drawerSettUser);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        TextView headTxt = findViewById(R.id.tvTitle);
        headTxt.setText("My Paramètres");
        //getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        // elements du layout
        //      Switch des notifs
        prefNotif = findViewById(R.id.prefNotif);
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SETTINGS_NAME, MODE_PRIVATE);
        prefNotif.setChecked(sharedPreferences.getBoolean(MainActivity.NOTIFICATION_KEY, true));

        prefNotif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent intent = new Intent(userSettings.this, HelloService.class);
                    startService(intent);
                } else {
                    Intent intent = new Intent(userSettings.this, HelloService.class);
                    stopService(intent);
                }
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(MainActivity.NOTIFICATION_KEY, isChecked);
                editor.apply();
            }
        });

        //      Bouton pour changer de lycée
        changeSchoolBtn = findViewById(R.id.changeSchoolBtn);
        changeSchoolBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSharedPreferences(MainActivity.SCHOOL_NAME, MODE_PRIVATE).edit().clear().apply();
                finish();
            }
        });

        //      Bouton pour reset l'id des sondages auquelle on a déjà répondu
        resetSurveyBtn = findViewById(R.id.resetSurveyBtn);
        resetSurveyBtn.setVisibility(View.GONE);
        resetSurveyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSharedPreferences(MainActivity.SURVEY_CHECKED_NAME, MODE_PRIVATE).edit().clear().apply();
                finish();
            }
        });
    }
}
