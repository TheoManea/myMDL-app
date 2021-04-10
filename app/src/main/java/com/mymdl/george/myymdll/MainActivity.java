package com.mymdl.george.myymdll;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.mymdl.george.myymdll.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        ConnectionDataBase.Listeners {

    /*Variables pour le "Navigation Drawer"*/
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navigationView;
    private SubMenu clubmenu, fonctionnalityMenu;
    private List<Club> allClubList;
    private List<Event> allEventList;
    private boolean isRemembered = false;
    // variable du layout
    private SwipeRefreshLayout swipeRefreshLayout;
    /*--------------------------------------*/
    /*Variables pour la base de données
    * &verifyServerCertificate=false*/
    /*protected static String DB_URL = "jdbc:mysql://remotemysql.com:3306/UihGXD8jFZ?useSSL=false";
    protected static String USER = "UihGXD8jFZ";
    protected static String PASS = "HccAWW6Y6T";*/
    protected static String DB_URL = "jdbc:mysql://bv91837-001.dbaas.ovh.net:35606/mymdldb?useSSL=false";
    protected static String USER = "mymdladmin";
    protected static String PASS = "Mf352nV4z94XNGPxXhf99Qi4kv";
    /*protected static String DB_URL = "jdbc:mysql://federatitaappli.mysql.db.ovh.com/federatitaappli?useSSL=false";
    protected static String USER = "federatitaappli";
    protected static String PASS = "Mf352nV4z94XNGPxXhf99Qi4kv";*/

    private String event_Query, club_Query;

    private int idSchool;
    /*--------------------------------------*/
    // Variables des CardView et de leur contenu
    private RecyclerView EvtRv;
    private CardAdapter cardAdapter;

    // Shared Preferences Keys
    static String SETTINGS_NAME = "settings", NOTIFICATION_KEY = "notification",
            REMEMBER_USER_NAME = "remember", REMEMBER_ID_KEY = "idUser", REMEBER_LEVEL_ACCESS_KEY = "levelAccess",
            IF_NO_CONNECTED_NAME = "ifNoConnected", SAVED_EVENT_NO_CO_KEY = "savedEvent", SAVED_CLUB_NO_CO_KEY = "savedClub",
            SCHOOL_NAME = "whichSchool", SCHOOL_ID_KEY = "idSchoolKey", SCHOOL_NAME_KEY = "schoolName",
            DIDACTITIEL_NAME = "didactitiel", FIRST_CO_KEY = "firstStart",
            SURVEY_CHECKED_NAME = "surveyCheck", LIST_SURVEY_KEY = "listOfSurvey";

    // DateFormat
    static String UNI_DATE = "yyyy-MM-dd", UNI_DATE_TIME = "yyyy-MM-dd HH:mm",
    SHOWN_DATE = "dd/MM/yyyy", SHOWN_DATE_TIME = "dd/MM/yyyy - HH:mm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new InsertNbConnection().execute();

        // lance le service, si preference notif est activé
        SharedPreferences sharedPreferences = getSharedPreferences(SETTINGS_NAME, MODE_PRIVATE);
        if (sharedPreferences.getBoolean(NOTIFICATION_KEY, true)) {
            Intent intent = new Intent(this, HelloService.class);
            startService(intent);
        }

        Toast.makeText(MainActivity.this, "Bienvenue ! ",Toast.LENGTH_LONG).show();

        // Pout NavigationDrawer/Menu
        mDrawerLayout = findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.newhamburgericonwhite);


        navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);




        Menu menu = navigationView.getMenu();
        clubmenu = menu.findItem(R.id.menuClubs).getSubMenu();
        // pas besoin de les déclarer pour toute l'activity -> juste localement suffit
        fonctionnalityMenu = menu.findItem(R.id.appFonctionnality).getSubMenu();
        amIConnected();

        MenuItem clubs = menu.findItem(R.id.menuClubs);
        SpannableString s = new SpannableString(clubs.getTitle());
        s.setSpan(new TextAppearanceSpan(this, R.style.styleForMenuDrawer), 0, s.length(), 0);
        clubs.setTitle(s);
        navigationView.setNavigationItemSelectedListener(this);

        // elements du layout
        swipeRefreshLayout = findViewById(R.id.SRefreshMain);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkIfAbleRefresh();
            }
        });

        //Repérage du RecyclerView
        EvtRv = findViewById(R.id.EvtRv);


        //checkInitSchool(true);
    }


    // aide utilisation
    public void showStart(){


        //Montre les CGU
        /*final AlertDialog dialogBuilder = new AlertDialog.Builder(MainActivity.this).create();
        dialogBuilder.setCanceledOnTouchOutside(false);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.cgu_popup, null);

        // layout
        Button button1 = dialogView.findViewById(R.id.buttonSubmit);
        CheckBox checkCGU = dialogView.findViewById(R.id.checkBox);
        Button link = dialogView.findViewById(R.id.buttonLink);

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://balivernes.frama.site/user/pages/04.tutos-infos/14.my-mdl-espace-administrateur/cgu.pdf";
                try {
                    Intent i = new Intent("android.intent.action.MAIN");
                    i.setComponent(ComponentName.unflattenFromString("com.android.chrome/com.android.chrome.Main"));
                    i.addCategory("android.intent.category.LAUNCHER");
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
                catch(ActivityNotFoundException e) {
                    // Chrome is not installed
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(i);
                }
            }
        });



        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkCGU.isChecked())
                {
                    //Il peut utiliser l'application
                    dialogBuilder.dismiss();
                    SharedPreferences prefs = getSharedPreferences(DIDACTITIEL_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(FIRST_CO_KEY, false);
                    editor.apply();

                }
                else
                {
                    Snackbar.make(view, "Veuillez cocher la case pour continuer.", Snackbar.LENGTH_SHORT).show();
                }


            }
        });


        dialogBuilder.setView(dialogView);
        dialogBuilder.show();*/

        Intent intent = new Intent(MainActivity.this, Cgu_Activity.class);
        startActivity(intent);
    }


    // methodes pour le navigation drawer
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(mToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.connexion:
                // le bouton de connexion / deconnexion
                if (isRemembered) { // se deconnecte
                    getSharedPreferences(REMEMBER_USER_NAME, MODE_PRIVATE).edit().clear().apply();
                    amIConnected();
                } else {
                    Intent connexion = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(connexion);
                }
                break;
            case R.id.infos:
                Intent infos = new Intent(MainActivity.this,Info.class);
                startActivity(infos);
                break;
            case R.id.param:
                Intent param = new Intent(MainActivity.this,userSettings.class);
                startActivity(param);
                break;
            case R.id.adminZone:
                SharedPreferences preferences = getSharedPreferences(REMEMBER_USER_NAME, MODE_PRIVATE);
                // prepare le changement d'activité
                Intent adminAct = new Intent(MainActivity.this,AdminActivity.class);
                adminAct.putExtra("idAuthor",String.valueOf(preferences.getInt(REMEMBER_ID_KEY, 0)));
                adminAct.putExtra("levelAccess", String.valueOf(preferences.getInt(REMEBER_LEVEL_ACCESS_KEY, 0)));
                startActivity(adminAct);
                break;

            default:
                for(Club clubTmp : allClubList) {
                    if (clubTmp.getIdClub() == item.getItemId()) {
                        Intent intentDetailsClub = new Intent(MainActivity.this,ClubDetailActivity.class);
                        intentDetailsClub.putExtra("club", clubTmp);
                        startActivity(intentDetailsClub);
                    }
                }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // test la connection internet et réagit par rapport à ça
    public void checkIfAbleRefresh(){
        // test si il est connecté à internet
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            swipeRefreshLayout.setRefreshing(true);
            // Commence le processus de rafraichissement des cards, à chaque retour à la page
            startAsyncTask(event_Query, ConnectionDataBase.EVENT_QUERY);
            // rafraichit les clubs
            startAsyncTask(club_Query, ConnectionDataBase.CLUB_QUERY);

            // Toast.makeText(MainActivity.this, "Coco à internet", Toast.LENGTH_SHORT).show();
        } else { // n'est pas connecté à internet
            //afficher un message d'info et afficher les events pré-enregistrés
            Snackbar.make(swipeRefreshLayout, "Vous n'êtes pas connecté à internet", Snackbar.LENGTH_LONG).show();
            swipeRefreshLayout.setRefreshing(false);
            loadData();
        }
    }

    // fonction de génération des cards pour les events
    public void refreshCards(List<Event> allEvtList) {
        //https://gist.github.com/gabrielemariotti/4c189fb1124df4556058#file-simpleadapter-java

        // Pour les évènements actuels
        int howManyNowEvt = 0;
        // cherche le nombre d'event d'aujourd'hui, dans la liste
        Calendar firstDate = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        for (Event evtTemp : allEvtList) {
            firstDate.setTime(evtTemp.getDayOfEvent());
            if ((new Date().after(evtTemp.getDayOfEvent()) && new Date().before(evtTemp.getDayEndOfEvent()) && evtTemp.getDayEndOfEvent().after(evtTemp.getDayOfEvent()))
            || (evtTemp.getDayOfEvent().equals(evtTemp.getDayEndOfEvent()) && firstDate.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR) &&
                    firstDate.get(Calendar.YEAR) == now.get(Calendar.YEAR) )) {
                howManyNowEvt++;
            }
        }

        // Création CardAdapter
        cardAdapter = new CardAdapter(allEvtList, CardAdapter.SPECTATOR_MODE, howManyNowEvt, true);
        //SimpleAdapter simpleAdapter = new SimpleAdapter(this, new String[] {"le 1","le 2", "le 3","le 4"});
        EvtRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        // Création de la section de la list
        List<SimpleSectionedRecyclerViewAdapter.Section> sections =
                new ArrayList<SimpleSectionedRecyclerViewAdapter.Section>();

        // Configurer les sections
        // Afficher les selections selon les evenements presents et futures
        String strEvtNow = "Evenements Actuels", strEvtFtr = "Evenements futurs";
        if (allEvtList.size() == 0) { // si la liste est vide : ni present, ni future
            strEvtNow = "Pas d'évenements";
            strEvtFtr = "";
        } else if (allEvtList.size() == howManyNowEvt) { // si il n'y a QUE des evenements present
            strEvtFtr = "Pas d'évenements futurs";
        } else if (howManyNowEvt == 0 && allEvtList.size() != 0) { // Si il n'y a QUE des events futures
            strEvtNow = "Pas d'évenements presents";
        }

        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0,strEvtNow));
        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(howManyNowEvt,strEvtFtr));

        //ajouter l'adapter à sectionAdapter
        SimpleSectionedRecyclerViewAdapter.Section[] dummy = new SimpleSectionedRecyclerViewAdapter.Section[sections.size()];
        SimpleSectionedRecyclerViewAdapter mSectionedAdapter = new SimpleSectionedRecyclerViewAdapter(this,R.layout.section,R.id.section_text,cardAdapter);
        mSectionedAdapter.setSections(sections.toArray(dummy));

        EvtRv.setAdapter(mSectionedAdapter);
    }

    // fonction de génération des clubs
    private void refreshClubs(@NonNull List<Club> allClubList) {
        clubmenu.clear();
        for (Club clubTmp : allClubList) {
            // ajoute un club, un par un
            // id Item == id Club
            clubmenu.add(0, clubTmp.getIdClub(), Menu.NONE, clubTmp.getTitleClub());
        }
    }

    // 4 methodes de callbacks pour ConnectionDataBase
    public void startAsyncTask(String query, int modeQuery) {
        new ConnectionDataBase(MainActivity.this, query, modeQuery).execute();
    }

    @Override
    public void onPreExecute(int modeQuery) {
    }

    @Override
    public void doInBackground() {

    }

    @Override
    public void onPostExecute(List result, int modeObjRtrn) {
        swipeRefreshLayout.setRefreshing(false);

        if (modeObjRtrn == ConnectionDataBase.EVENT_QUERY) {
            allEventList = new ArrayList<>();
            allEventList = result;

            // retirer les sondages déjà complétés
            //      charger la liste
            //          load
            SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SURVEY_CHECKED_NAME, MODE_PRIVATE);
            Gson gson = new Gson();
            String jsonSurvey = sharedPreferences.getString(MainActivity.LIST_SURVEY_KEY, null);
            Type typeList = new TypeToken<ArrayList<Integer>>() {}.getType();
            List<Integer> surveyList = gson.fromJson(jsonSurvey, typeList);
            if (surveyList == null)
                surveyList = new ArrayList<>();

            //          suppression de certains events, par l'intermedaire d'un autre objet
            List<Event> tempoListEvent = new ArrayList<>();
            boolean mustBeRejected = false;
            for (Event event : allEventList) {
                for(Integer idSurvey : surveyList) {
                    if(event.getIdEvent() == idSurvey) { // si les id correspondent
                        mustBeRejected = true; // montre qu'il ne doit pas être inclu dans la liste
                        // sortir de la boucle ?
                    }
                }
                if(!mustBeRejected) {
                    tempoListEvent.add(event);
                }
                mustBeRejected = false;
            }
            allEventList = tempoListEvent;

            refreshCards(allEventList);
        } else {
            allClubList = new ArrayList<>();
            allClubList = result;
            refreshClubs(result);
        }

        // enregistre les evenements pour votre plus grand confort !
        saveData();
    }

    // methode appelé lors du rafraichissement de la page, quand l'utilisateur vient/revient dessus
    @Override
    protected void onResume() {
        super.onResume();
        // IL FAUT ACCEPTER LES CGU POUR UTILISER L'APP
        SharedPreferences prefs = getSharedPreferences(DIDACTITIEL_NAME, MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean(FIRST_CO_KEY, true);
        if(firstStart){
            showStart();
        }

        // regarde si il est associé à un lycée
        // refresh si il vient de changer de lycée, sinon non
        SharedPreferences pref = getSharedPreferences(SCHOOL_NAME, MODE_PRIVATE);
        if (pref.getInt(SCHOOL_ID_KEY, 0) == 0 || pref.getInt(SCHOOL_ID_KEY, 0) != idSchool) {
            // si il vient de changer de lycée
            checkInitSchool(true);
        } else {
            checkInitSchool(true);
        }

        // regarde si il est connecté(loggé) en tant qu'utilisateur ou pas
        amIConnected();
    }

    // méthodes pour charger les events depuis la memoire
    private void saveData(){

        SharedPreferences sharedPreferences = getSharedPreferences(IF_NO_CONNECTED_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new GsonBuilder().setDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").create();
        String jsonEvent = gson.toJson(allEventList);
        String jsonClub = gson.toJson(allClubList);

        editor.putString(SAVED_EVENT_NO_CO_KEY, jsonEvent);
        editor.putString(SAVED_CLUB_NO_CO_KEY, jsonClub);
        editor.apply();
    }

    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(IF_NO_CONNECTED_NAME, MODE_PRIVATE);

        Gson gson = new GsonBuilder().setDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").create();

        String jsonEvent = sharedPreferences.getString(SAVED_EVENT_NO_CO_KEY, null);
        String jsonClub = sharedPreferences.getString(SAVED_CLUB_NO_CO_KEY,null);
        Type typeEvent = new TypeToken<ArrayList<Event>>() {}.getType();
        Type typeClub = new TypeToken<ArrayList<Club>>() {}.getType();

        allEventList = gson.fromJson(jsonEvent, typeEvent);
        allClubList = gson.fromJson(jsonClub, typeClub);

        if (allEventList == null)
            allEventList = new ArrayList<>();
        else
            refreshCards(allEventList);

        if (allClubList == null)
            allClubList = new ArrayList<>();
        else
            refreshClubs(allClubList);

    }

    // reagit par rapport à l'identification ou non de l'utilisateur ("reset connecté")
    private void amIConnected() {
        // cache ou pas le bouton de l'espace admin + bouton "connexion" ou "deconnexion" ?
        SharedPreferences preferences = getSharedPreferences(REMEMBER_USER_NAME, MODE_PRIVATE);
        if (preferences.getInt(REMEMBER_ID_KEY, -1) != -1) { // si il est reconnu "se souvenir de moi"
            fonctionnalityMenu.findItem(R.id.adminZone).setVisible(true);
            fonctionnalityMenu.findItem(R.id.connexion).setTitle("Deconnexion");
            isRemembered = true;
        } else { // sinon

            fonctionnalityMenu.findItem(R.id.adminZone).setVisible(false);
            fonctionnalityMenu.findItem(R.id.connexion).setTitle(R.string.connexion);
            isRemembered = false;
        }
    }

    private void checkInitSchool(boolean doRefreshCards) {
        SharedPreferences preferences = getSharedPreferences(SCHOOL_NAME, MODE_PRIVATE);
        idSchool = preferences.getInt(SCHOOL_ID_KEY, 0);


        // si aucun lycée n'est associé
        if (idSchool == 0){
            Intent intent = new Intent(MainActivity.this, ChooseSchoolActivity.class);
            startActivity(intent);
        } else {
            initQuery();
            setTitle(preferences.getString(SCHOOL_NAME_KEY, "Bienvenue"));
            if (doRefreshCards)
                checkIfAbleRefresh();
        }
    }

    private void initQuery() {
        // INITIALISATION DES REQUETES

        // demandes tous les events actuels + 10 events futurs sans compter les sondages
        event_Query = "(" +
                "    SELECT *" +
                "    FROM eventsdb " +
                "    WHERE ((NOW() BETWEEN dayOfEvent AND dayEndOfEvent AND dayOfEvent <> dayEndOfEvent) OR (CURRENT_DATE() = DATE(dayOfEvent))) AND idSchool = "+idSchool +
                "    ORDER BY DATE(dayOfEvent) ASC, dayOfEvent ASC" +
                ") UNION (" +
                "    SELECT * " +
                "    FROM eventsdb " +
                "    WHERE dayOfEvent > NOW() AND question IS NULL AND idSchool = "+idSchool +
                "    ORDER BY DATE(dayOfEvent) ASC, dayOfEvent ASC LIMIT 10" +
                ")";

        club_Query = "SELECT * FROM clubdb WHERE idSchool="+idSchool+" ORDER BY titleClub ASC";
    }

    public class InsertNbConnection extends AsyncTask<Void, Void,Void>{

        // variables de connexion
        Connection conn = null;
        Statement stmt = null;

        //autres variables
        private int nb_connection;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(MainActivity.DB_URL,MainActivity.USER,MainActivity.PASS);
                stmt = conn.createStatement();

                ResultSet resultSet = stmt.executeQuery("SELECT nb_connection AS nb_connection FROM schooldb WHERE idSchool=" + idSchool);
                resultSet.next();
                nb_connection = resultSet.getInt("nb_connection");
                System.out.print("Nb connection avant : " + nb_connection);
                nb_connection = nb_connection + 1;
                System.out.print("Nb connection après : " + nb_connection);


                String request = "UPDATE schooldb SET nb_connection=" + nb_connection + " WHERE idSchool=" + idSchool + "";
                stmt.executeUpdate(request);

                resultSet.close();

                stmt.close();
                conn.close();

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
            return null;

        }
    }

}

