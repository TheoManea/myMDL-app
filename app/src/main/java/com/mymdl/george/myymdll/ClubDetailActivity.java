package com.mymdl.george.myymdll;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mymdl.george.myymdll.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClubDetailActivity extends AppCompatActivity {

    // Variable sympa
    Club club;

    // éléments du layout
    TextView  titleClubTv, detailsClubTv, clubEvtHeaderTv;
    RecyclerView clubEventLinkRv;
    CardAdapter cardAdapter;
    ProgressBar progressBar;

    // pour la nav
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_detail);

        // Pout NavigationDrawer/Menu
        mDrawerLayout = findViewById(R.id.drawerClubDetails);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        TextView headTxt = findViewById(R.id.tvTitle);
        headTxt.setText("My MDL");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.arr);

        titleClubTv = findViewById(R.id.titleClubTv);
        detailsClubTv = findViewById(R.id.detailsClubTv);
        clubEventLinkRv = findViewById(R.id.clubEventLinkRv);
        progressBar = findViewById(R.id.progressBarClub);
        clubEvtHeaderTv = findViewById(R.id.clubEvtHeaderTv);

        // initialise les infos sur le club
        Intent intent = getIntent();
        if (intent != null) {
            club = intent.getParcelableExtra("club");
            if (club != null) {
                titleClubTv.setText(club.getTitleClub());
                detailsClubTv.setText(club.getDetailsClub());
            }
        }

        // actualise les events associés
        // test si il est connecté à internet
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new EventListGen().execute();
        } else { // n'est pas connecté à internet
            //afficher les events pré-enregistrés
            SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.IF_NO_CONNECTED_NAME, MODE_PRIVATE);

            Gson gson = new GsonBuilder().setDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").create();

            String jsonEvent = sharedPreferences.getString(MainActivity.SAVED_EVENT_NO_CO_KEY, null);
            Type typeEvent = new TypeToken<ArrayList<Event>>() {}.getType();

            List<Event> allEventList = gson.fromJson(jsonEvent, typeEvent);

            if (allEventList == null) {
                allEventList = new ArrayList<>();
                refreshCards(allEventList);
            }
            else {
                List<Event> eventOfClubList = new ArrayList<>();
                for (Event tempEventOfClub:allEventList) {
                    if (tempEventOfClub.getIdClubLinked() == club.getIdClub())
                    eventOfClubList.add(tempEventOfClub);
                }
                refreshCards(eventOfClubList);
            }
        }
    }

    // pour le bouton de retour
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshCards(@NonNull List<Event> allEvtList) {
        //https://gist.github.com/gabrielemariotti/4c189fb1124df4556058#file-simpleadapter-java

        progressBar.setVisibility(View.GONE);

        //Repérage du RecyclerView
        clubEventLinkRv = findViewById(R.id.clubEventLinkRv);

        if (allEvtList.size() != 0) {
            clubEventLinkRv.setVisibility(View.VISIBLE);
            clubEvtHeaderTv.setText(R.string.club_showing_event);

            // Création CardAdapter
            cardAdapter = new CardAdapter(allEvtList, CardAdapter.SPECTATOR_MODE, 0, false);
            clubEventLinkRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

            clubEventLinkRv.setAdapter(cardAdapter);
        } else {
            clubEventLinkRv.setVisibility(View.GONE);
            clubEvtHeaderTv.setText("Aucun évènements associés");
        }

    }

    public class EventListGen extends AsyncTask<String,Void,List> {
        // paramètres de connexion
        private Connection conn = null;
        private Statement stmt = null;

        // variable de succès
        private List<Event> allEventList;

        // méthodes d'AsyncTasks
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(List finalListFromDb) {
            refreshCards(finalListFromDb);
        }

        @Override
        protected List doInBackground(String... params) {
            allEventList = new ArrayList<>();
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(MainActivity.DB_URL,MainActivity.USER,MainActivity.PASS);
                stmt = conn.createStatement();

                // requête n°1 pour avoir les objects dans eventsdb, sans nameClub
                ResultSet resultSet = stmt.executeQuery("SELECT * FROM eventsdb WHERE idClub="+club.getIdClub() +
                                " ORDER BY DATE(dayOfEvent) ASC, dayOfEvent ASC");
                while (resultSet.next()) {
                    // prend les valeurs dans depuis la requête
                    int id = resultSet.getInt("idEvent");
                    String title = resultSet.getString("titleEvent");
                    String detail = resultSet.getString("detailsEvent");
                    String titleClub = "Error name";
                    int idClub = resultSet.getInt("idClub");

                    Date dateOfEvent = resultSet.getTimestamp("dayOfEvent");
                    Date dateEndOfEvent = resultSet.getTimestamp("dayEndOfEvent");
                    Date dateOfCreation = resultSet.getDate("dayOfCreation");

                    Event evt = new Event(id, title, detail, titleClub, idClub);
                    evt.setDayOfEvent(dateOfEvent);
                    evt.setDayEndOfEvent(dateEndOfEvent);
                    evt.setDayOfCreation(dateOfCreation);
                    allEventList.add(evt);
                }
                //Si 1er requête à fonctionné :
                // requête n°2 pour associé idClub à un nameClub trouvé dans clubdb
                resultSet = stmt.executeQuery("SELECT idClub, titleClub FROM clubdb");
                int objectSetted = 0;
                while (resultSet.next() && allEventList.size() > objectSetted) {
                    int idClub = resultSet.getInt("idClub");
                    String titleClub = resultSet.getString("titleClub");

                    for (Event evtTemp : allEventList) {
                        if (evtTemp.getIdClubLinked() == idClub) {
                            evtTemp.setTitleClubLinked(titleClub);
                            objectSetted++;
                        }
                    }
                }
                resultSet.close();

                stmt.close();

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }finally{
                try{
                    if(stmt!=null)
                        stmt.close();
                }catch(SQLException se2){
                }// nothing we can do
                try{
                    if(conn!=null)
                        conn.close();
                }catch(SQLException se){
                    se.printStackTrace();
                }
            }

            return allEventList;
        }
    }
}
