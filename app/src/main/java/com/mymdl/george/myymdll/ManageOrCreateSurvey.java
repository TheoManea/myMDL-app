package com.mymdl.george.myymdll;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.mymdl.george.myymdll.R;

public class ManageOrCreateSurvey extends AppCompatActivity {

    private Button createsurvey, managesurvey;
    private int idAuthor;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_or_create_survey);

        // Pout NavigationDrawer/Menu
        mDrawerLayout = findViewById(R.id.manaOrCreaDrawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        TextView headTxt = findViewById(R.id.tvTitle);
        headTxt.setText("Sondages");
        headTxt.setTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.redfmdl)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arr);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.redfmdldark));
        }

        //Récupérer idAuthor
        Intent intent = getIntent();
        idAuthor = Integer.valueOf(intent.getStringExtra("idAuthor"));


        //recherche des boutons
        createsurvey = findViewById(R.id.createsurvey);
        managesurvey = findViewById(R.id.managesurvey);


        //gérer les interactions avec les boutons
        createsurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent act = new Intent(ManageOrCreateSurvey.this, SurveyActivity.class);
                act.putExtra("idAuthor", String.valueOf(idAuthor));
                startActivity(act);
            }
        });

        managesurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent act = new Intent(ManageOrCreateSurvey.this, ManageSurvey.class);
                act.putExtra("idAuthor", String.valueOf(idAuthor));
                startActivity(act);
            }
        });


    }

    // pour le bouton de retour
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
