package com.mymdl.george.myymdll;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mymdl.george.myymdll.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class event_admin_list extends AppCompatActivity {

    private List<Event> allEvtList;
    Statement stmt;
    Connection conn;
    RecyclerView EvtRv;
    CardAdapter cardAdapter;
    TextView noEvtTv;
    SwipeRefreshLayout swipeRefreshLayout;

    int ownId;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_admin_list);

        // Pout NavigationDrawer/Menu
        mDrawerLayout = findViewById(R.id.drawnerEventAdmin);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        TextView headTxt = findViewById(R.id.tvTitle);
        headTxt.setText("My Evénements");
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
        ownId = Integer.valueOf(intent.getStringExtra("idAuthor"));

        noEvtTv = findViewById(R.id.noEvtTv);
        swipeRefreshLayout = findViewById(R.id.SRefreshEventAdmin);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkIfAbleRefresh();
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


    // test la connection internet et réagit par rapport à ça
    public void checkIfAbleRefresh(){
        // test si il est connecté à internet
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            swipeRefreshLayout.setRefreshing(true);
            // Commence le processus de rafraichissement des cards, à chaque retour à la page
            new eventListDbCo(ownId).execute();

            // Toast.makeText(MainActivity.this, "Coco à internet", Toast.LENGTH_SHORT).show();
        } else { // n'est pas connecté à internet
            //afficher un message d'info et afficher les events pré-enregistrés
            Snackbar.make(swipeRefreshLayout, "Vous n'êtes pas connecté à internet", Snackbar.LENGTH_LONG).show();
            swipeRefreshLayout.setRefreshing(false);
        }
    }


    public void refreshCards(List<Event> allEvtList) {
        swipeRefreshLayout.setRefreshing(false);
        //Repérage du RecyclerView
        EvtRv = findViewById(R.id.allEvtRv);
        noEvtTv.setVisibility(View.GONE);
        if (allEvtList.size() != 0) {
            noEvtTv.setVisibility(View.GONE);
            // Création CardAdapter
            cardAdapter = new CardAdapter(allEvtList, CardAdapter.EDIT_MODE, ownId);
            EvtRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

            EvtRv.setAdapter(cardAdapter);
        } else {
            noEvtTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new eventListDbCo(ownId).execute();
    }

    class eventListDbCo extends AsyncTask<Void, Void,List> {

        int id, idSchool;

        public eventListDbCo(int id) {
            this.id = id;
        }

        @Override
        protected void onPreExecute() { }

        @Override
        protected void onPostExecute(List eventList) {
            refreshCards(eventList);
        }

        @Override
        protected List doInBackground(Void... voids) {
            allEvtList = new ArrayList<>();
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(MainActivity.DB_URL,MainActivity.USER,MainActivity.PASS);
                stmt = conn.createStatement();

                // regarde le niveau d'accès de l'utilisateur
                ResultSet resultSet = stmt.executeQuery("SELECT levelAccess FROM accountdb WHERE idUser='"+id+"'");
                int levelAccess = 0;
                if(resultSet.next()) {
                    levelAccess = resultSet.getInt("levelAccess");
                }


                // Affiche les événements en conséquence
                // requête n°1 pour avoir les objects dans eventsdb, sans nameClub
                if (levelAccess == 2) {
                    resultSet = stmt.executeQuery("SELECT idSchool FROM accountdb WHERE idUser = "+id);
                    if (resultSet.next()) {
                        idSchool = resultSet.getInt("idSchool");
                    }
                    resultSet = stmt.executeQuery("SELECT * FROM eventsdb WHERE idSchool = "+idSchool+" AND (question IS NULL OR question = '') ORDER BY DATE(dayOfEvent) ASC, dayOfEvent ASC");
                } else if (levelAccess == 1){
                    // affichage de tous les events de son club, que lui a posté (double condition)

                    //  rechercher l'id du club du l'utilisateur
                    resultSet = stmt.executeQuery("SELECT idClub FROM accountdb WHERE idUser="+id);
                    int idClub = 0;
                    if (resultSet.next()) {
                        idClub = resultSet.getInt("idClub");
                    }
                    resultSet = stmt.executeQuery("SELECT * FROM eventsdb WHERE idAuthor="+id+" AND idClub="+idClub+" AND (question IS NULL OR question = '') ORDER BY DATE(dayOfEvent) ASC, dayOfEvent ASC");
                }

                while(resultSet.next()){
                    // prend les valeurs dans depuis la requête
                    int id  = resultSet.getInt("idEvent");
                    String title  = resultSet.getString("titleEvent");
                    String detail = resultSet.getString("detailsEvent");
                    String titleClub  = "Error name";
                    int idClub  = resultSet.getInt("idClub");

                    Date dateOfCreation = resultSet.getDate("dayOfCreation");
                    Date dateOfEvent = resultSet.getTimestamp("dayOfEvent");
                    Date dateEndOfEvent = resultSet.getTimestamp("dayEndOfEvent");

                    Event evt = new Event(id,title,detail,titleClub,idClub);
                    evt.setDayOfCreation(dateOfCreation);
                    evt.setDayOfEvent(dateOfEvent);
                    evt.setDayEndOfEvent(dateEndOfEvent);
                    allEvtList.add(evt);
                }
                //Si 1er requête à fonctionné :
                // requête n°2 pour associé idClub à un nameClub trouvé dans clubdb
                resultSet = stmt.executeQuery("SELECT idClub, titleClub FROM clubdb");
                int objectSetted=0;
                while (resultSet.next() && allEvtList.size()>objectSetted) {
                    int idClub = resultSet.getInt("idClub");
                    String titleClub = resultSet.getString("titleClub");

                    for(Event evtTemp : allEvtList) {
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
            return allEvtList;
        }
    }
}
