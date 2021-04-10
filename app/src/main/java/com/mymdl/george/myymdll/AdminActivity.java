package com.mymdl.george.myymdll;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mymdl.george.myymdll.R;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



public class AdminActivity extends AppCompatActivity{

    // Variables des éléments du layout
    CardView adduser, addclub, survey, addevent, manageevent, settings, statistiques,managehighschools, messagerie, config;
    Button back;
    TextView greetings;

    // id user
    int id;
    String name, email, username, password;
    int levelAccess;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Pout NavigationDrawer/Menu
        mDrawerLayout = findViewById(R.id.drawerAdmin);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        TextView headTxt = findViewById(R.id.tvTitle);
        headTxt.setText("My Admin");
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


        Intent intent = getIntent();
        id = Integer.valueOf(intent.getStringExtra("idAuthor"));
        levelAccess = Integer.valueOf(intent.getStringExtra("levelAccess"));
        // email du connécté
        email = intent.getStringExtra("email");
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");




        // elements du layout
        greetings = findViewById(R.id.greetings);
        greetings.setText("Bienvenue");

        adduser = findViewById(R.id.adduser);
        addclub = findViewById(R.id.addclub);
        addevent = findViewById(R.id.addevent);
        manageevent = findViewById(R.id.manageevent);
        settings = findViewById(R.id.settings);
        statistiques = findViewById(R.id.statistiques);
        managehighschools = findViewById(R.id.managehighschools);
        messagerie = findViewById(R.id.messagerie);
        config = findViewById(R.id.config); // pas fait le bouton
        survey = findViewById(R.id.sondage);



        String tutorialKey = "SOME_KEY";
        Boolean firstTime = getPreferences(MODE_PRIVATE).getBoolean(tutorialKey, true);
        if (firstTime) {
            TapTargetView.showFor(this,                 // `this` is an Activity
                    TapTarget.forView(findViewById(R.id.greetings), "Voici votre espace d'admnistration !", "Selon votre degrès de responsabilité vous avez plus ou moins d'actions à votre porté .")
                            // All options below are optional
                            .outerCircleColor(R.color.redfmdl)      // Specify a color for the outer circle
                            .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                            .targetCircleColor(R.color.white)   // Specify a color for the target circle
                            .titleTextSize(20)                  // Specify the size (in sp) of the title text
                            .titleTextColor(R.color.white)      // Specify the color of the title text
                            .descriptionTextSize(15)            // Specify the size (in sp) of the description text
                            .descriptionTextColor(R.color.redfmdldark)  // Specify the color of the description text
                            .textColor(R.color.white)            // Specify a color for both the title and description text
                            .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                            .dimColor(R.color.redfmdl)            // If set, will dim behind the view with 30% opacity of the given color
                            .drawShadow(true)                   // Whether to draw a drop shadow or not
                            .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                            .tintTarget(true)
                            // Whether to tint the target view's color
                            .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
                            // Specify a custom drawable to draw as the target
                            .targetRadius(60),// Specify the target radius (in dp)
                    new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                        @Override
                        public void onTargetClick(TapTargetView view) {
                            super.onTargetClick(view);      // This call is optional
                        }
                    });
            getPreferences(MODE_PRIVATE).edit().putBoolean(tutorialKey, false).apply();
        }



        if(levelAccess == 1)
        {
            adduser.setVisibility(View.GONE);
            addclub.setVisibility(View.GONE);

            ViewGroup layout = (ViewGroup) managehighschools.getParent();
            layout.removeView(managehighschools);
            ViewGroup layout1 = (ViewGroup) messagerie.getParent();
            layout1.removeView(messagerie);
            ViewGroup layout4 = (ViewGroup) config.getParent();
            layout4.removeView(config);
            ((ViewGroup) statistiques.getParent()).removeView(statistiques);


        }
        else if(levelAccess == 2)
        {
            ViewGroup layout = (ViewGroup) managehighschools.getParent();
            layout.removeView(managehighschools);
            ViewGroup layout1 = (ViewGroup) messagerie.getParent();
            layout1.removeView(messagerie);
        }
        else if(levelAccess == 3)
        {
            ViewGroup layout1 = (ViewGroup) addclub.getParent();
            layout1.removeView(addclub);

            ViewGroup layout2 = (ViewGroup) addevent.getParent();
            layout2.removeView(addevent);

            ViewGroup layout3 = (ViewGroup) manageevent.getParent();
            layout3.removeView(manageevent);

            ViewGroup layout4 = (ViewGroup) config.getParent();
            layout1.removeView(config);

            ((ViewGroup) survey.getParent()).removeView(survey);


        }
        else if(levelAccess == 4)
        {

            addclub.setVisibility(View.GONE);

            ViewGroup layout = (ViewGroup) addevent.getParent();
            layout.removeView(addevent);

            ViewGroup layout1 = (ViewGroup) addclub.getParent();
            layout1.removeView(addclub);

            ViewGroup layout2 = (ViewGroup) manageevent.getParent();
            layout2.removeView(manageevent);

            ViewGroup layout4 = (ViewGroup) config.getParent();
            layout1.removeView(config);

            ((ViewGroup) survey.getParent()).removeView(survey);

        }


        // bouton sondage
        survey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, ManageOrCreateSurvey.class);
                intent.putExtra("idAuthor", String.valueOf(id));
                startActivity(intent);
            }
        });

        // bouton des stats
        statistiques.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stats = new Intent(AdminActivity.this, StatsActivity.class);
                stats.putExtra("idAuthor", String.valueOf(id));
                startActivity(stats);
            }
        });

        // bouton de création d'évents
        addevent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addEvent = new Intent(AdminActivity.this, AddEventActivity.class);
                addEvent.putExtra("idAuthor", String.valueOf(id));
                addEvent.putExtra("levelAccess", String.valueOf(levelAccess));
                startActivity(addEvent);
            }
        });

        // bouton de gestion des events
        manageevent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent manageEvent = new Intent(AdminActivity.this, event_admin_list.class);
                manageEvent.putExtra("idAuthor", String.valueOf(id));
                startActivity(manageEvent);
            }
        });

        // bouton de gestion des membres
        adduser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent members = new Intent(AdminActivity.this, MembersActivity.class);
                members.putExtra("idAuthor", String.valueOf(id));
                members.putExtra("emailUser", String.valueOf(email));
                startActivity(members);
            }
        });

        // bouton de gestion des clubs
        addclub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent club = new Intent(AdminActivity.this, ManageClubActivity.class);
                club.putExtra("idAuthor", String.valueOf(id));
                startActivity(club);
            }
        });

        // bouton des settings
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settings = new Intent(AdminActivity.this, SettingsActivity.class);
                settings.putExtra("levelAccess", String.valueOf(levelAccess));
                settings.putExtra("id", String.valueOf(id));
                settings.putExtra("email", email);
                settings.putExtra("username", username);
                settings.putExtra("password", password);
                startActivity(settings);
            }
        });

        // le bouton de messagerie est définie dans le OnPostExecute du AsyncTask

        // bouton de gestion des lycées
        managehighschools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(AdminActivity.this, ManageHighSchoolActivity.class);
                intent.putExtra("idAuthor", String.valueOf(id));
                startActivity(intent);*/
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

    @Override
    protected void onResume() {
        super.onResume();
        new TrumpRefresh().execute();
    }

    public class TrumpRefresh extends AsyncTask<Void, Void, Void> {

        // variables de connexion
        Connection conn = null;
        Statement stmt = null;

        String username;

        @Override
        protected void onPostExecute(Void aVoid) {
            greetings.setText("Bienvenue " + username);

            // bouton messagerie
            messagerie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent roc = new Intent(AdminActivity.this, ReadOrCreateMessage.class);
                    roc.putExtra("id", String.valueOf(id));
                    roc.putExtra("name", name);
                    startActivity(roc);
                }
            });
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(MainActivity.DB_URL,MainActivity.USER,MainActivity.PASS);
                stmt = conn.createStatement();

                ResultSet resultSet = stmt.executeQuery("SELECT nameUser FROM accountdb WHERE idUser=" + id);
                if (resultSet.next()) {
                    username = resultSet.getString("nameUser");
                }

                stmt.close();
                conn.close();

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}