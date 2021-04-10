package com.mymdl.george.myymdll;

import android.arch.lifecycle.ReportFragment;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


import com.mymdl.george.myymdll.R;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.sql.Connection;
import java.sql.Statement;


public class ManageSurvey extends AppCompatActivity {

    //Initialisation des variables
    private List<Event> allSurveyList;
    Statement stmt;
    Connection conn;
    RecyclerView surveyrRv;
    private int Id, idSchool, lvlAccess;
    CardAdapter cardAdapter;
    SwipeRefreshLayout swipeRefreshLayout;

    // base de donnée
    static  int BASIC_INFO_MODE = 0, QUERY_SURVEY_MODE = 1;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_survey3);

        // Pout NavigationDrawer/Menu
        mDrawerLayout = findViewById(R.id.drawerManageSurvey);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        TextView headTxt = findViewById(R.id.tvTitle);
        headTxt.setText("Gestion Sondages");
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

        //on récupère l'id
        Intent intent = getIntent();
        Id = Integer.valueOf(intent.getStringExtra("idAuthor"));

        // elements du layout
        surveyrRv = findViewById(R.id.allsurveyrv);
        swipeRefreshLayout = findViewById(R.id.SRefreshManageSurvey);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkIfAbleRefresh();
            }
        });
    }

    // test la connection internet et réagit par rapport à ça
    public void checkIfAbleRefresh(){
        // test si il est connecté à internet
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            swipeRefreshLayout.setRefreshing(true);
            // Commence le processus de rafraichissement des cards, à chaque retour à la page
            new surveyListDbCo(BASIC_INFO_MODE).execute();

            // Toast.makeText(MainActivity.this, "Coco à internet", Toast.LENGTH_SHORT).show();
        } else { // n'est pas connecté à internet
            //afficher un message d'info et afficher les events pré-enregistrés
            Snackbar.make(swipeRefreshLayout, "Vous n'êtes pas connecté à internet", Snackbar.LENGTH_LONG).show();
            swipeRefreshLayout.setRefreshing(false);
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

    //Méthode pour rafraîchir les sondages
    public void refreshCards(List<Event> allSurveyList){

        swipeRefreshLayout.setRefreshing(false);
        if (allSurveyList.size() != 0) {
            // Création CardAdapter
            cardAdapter = new CardAdapter(allSurveyList, CardAdapter.EDIT_MODE, Id);
            surveyrRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

            surveyrRv.setAdapter(cardAdapter);
        } else {
            Toast.makeText(ManageSurvey.this, "Pas de sondages", Toast.LENGTH_LONG).show();
        }

    }


    //méthode pour lancer AsyncTask
    @Override
    protected void onResume(){
        super.onResume();
        new surveyListDbCo(BASIC_INFO_MODE).execute();
    }

    class surveyListDbCo extends AsyncTask<Void, Void, List> {

        int mode;

        public surveyListDbCo(int mode){
            this.mode = mode;
        }

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected void onPostExecute(List surveyList)
        {
            if(mode == BASIC_INFO_MODE) {
                new surveyListDbCo(QUERY_SURVEY_MODE).execute();
            } else if (mode == QUERY_SURVEY_MODE) {
                refreshCards(surveyList);
            }

        }

        @Override
        protected List doInBackground(Void... voids){
            allSurveyList = new ArrayList<>();

            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(MainActivity.DB_URL,MainActivity.USER,MainActivity.PASS);
                stmt = conn.createStatement();

                if(mode == BASIC_INFO_MODE) {
                    ResultSet resultSet = stmt.executeQuery("SELECT idSchool,levelAccess FROM accountdb WHERE idUser="+Id);
                    if(resultSet.next()) {
                        idSchool = resultSet.getInt("idSchool");
                        lvlAccess = resultSet.getInt("levelAccess");
                    }
                    resultSet.close();
                }else if(mode == QUERY_SURVEY_MODE) {
                    // recupère tous les sondage du lycée
                    String query;
                    if (lvlAccess >= 2) {
                        query = "SELECT * FROM eventsdb WHERE detailsEvent IS NULL AND idSchool="+idSchool;
                    } else {
                        query = "SELECT * FROM eventsdb WHERE detailsEvent IS NULL AND idSchool="+idSchool+" AND idAuthor="+Id;
                    }

                    ResultSet resultSet = stmt.executeQuery(query);
                    while(resultSet.next()){
                        // prend les valeurs dans depuis la requête
                        int idEvt = resultSet.getInt("idEvent");
                        String title = resultSet.getString("titleEvent");
                        String question = resultSet.getString("question");
                        int nb_rep_1 = resultSet.getInt("nb_rep_1");
                        int nb_rep_2 = resultSet.getInt("nb_rep_2");
                        String answer1 = resultSet.getString("answer1");
                        String answer2 = resultSet.getString("answer2");

                        Date dateOfEvent = resultSet.getTimestamp("dayOfEvent");
                        Date dateOfCreation = resultSet.getDate("dayOfCreation");
                        Date dateEndOfEvent = resultSet.getTimestamp("dayEndOfEvent");


                        Event evt = new Event(idEvt, title, question, answer1, answer2);
                        evt.setNb_answer1(nb_rep_1);
                        evt.setNb_answer2(nb_rep_2);
                        evt.setDayOfEvent(dateOfEvent);
                        evt.setDayOfCreation(dateOfCreation);
                        evt.setDayEndOfEvent(dateEndOfEvent);

                        allSurveyList.add(evt);
                    }

                    resultSet.close();
                }


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
            return allSurveyList;

        }




    }



}
