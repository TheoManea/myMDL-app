package com.mymdl.george.myymdll;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    /*Variables pour le "Navigation Drawer"*/
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navigationView;
    /*--------------------------------------*/
    /*Variables pour la base de données*/
    String DB_URL = "jdbc:mysql://sql2.freemysqlhosting.net:3306/sql2278287";
    String USER = "sql2278287";
    String PASS = "nT8%zK6%";
    /*DatabaseHelper mDatabaseHelper;*/
    private static final String TAG = "ListDataActivity";
    private ListView mListView;
    /*--------------------------------------*/

    // Variables des CardView et de leur contenu
    private RecyclerView EvtRv;
    private List<Event> nowEvt;
    private List<Event> futureEvt;
    private List<Event> allEvtList;
    private CardAdapter CardAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Bienvenue");

        // Menu hamburger
        Toast.makeText(MainActivity.this, "Bienvenue ! ",Toast.LENGTH_LONG).show();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);



        /*populateCards();*/

        //https://gist.github.com/gabrielemariotti/4c189fb1124df4556058#file-simpleadapter-java
        // Pour les évènements actuels
        int howManyNowEvt = 5;
        int howManyFutureEvt = 5;
            //Repérage du RecyclerView
        EvtRv = (RecyclerView) findViewById(R.id.EvtRv);

            // Génération grace à CardGenerator de :
        CardGenerator cardGenerator = new CardGenerator();
                //5 évènements actuels et futurs
        allEvtList = new ArrayList<>();
        allEvtList.addAll(cardGenerator.nowEvents(howManyNowEvt));
        allEvtList.addAll(cardGenerator.futureEvents(howManyFutureEvt));
        // Création CardAdapter
        CardAdapter = new CardAdapter(allEvtList);
        EvtRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        // Création de la section de la list
        List<SimpleSectionedRecyclerViewAdapter.Section> sections =
                new ArrayList<SimpleSectionedRecyclerViewAdapter.Section>();

        //Configurer les sections
        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0,"Evenements Actuels"));
        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(howManyNowEvt,"Evenements futurs"));

        //ajouter l'adapter à sectionAdapter
        SimpleSectionedRecyclerViewAdapter.Section[] dummy = new SimpleSectionedRecyclerViewAdapter.Section[sections.size()];
        SimpleSectionedRecyclerViewAdapter mSectionedAdapter = new
                SimpleSectionedRecyclerViewAdapter(this,R.layout.section,R.id.section_text,CardAdapter);
        mSectionedAdapter.setSections(sections.toArray(dummy));

        EvtRv.setAdapter(mSectionedAdapter);
    }

    // Pour le menu hamberger
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
                Intent connexion = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(connexion);
                break;
            case R.id.infos:
                Intent infos = new Intent(MainActivity.this,Infos.class);
                startActivity(infos);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void populateCards(){
    }

    public String getDB_URL() {
        return DB_URL;
    }

    public String getUSER() {
        return USER;
    }

    public String getPASS() {
        return PASS;
    }
}
